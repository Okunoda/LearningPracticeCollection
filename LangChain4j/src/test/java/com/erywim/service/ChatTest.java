package com.erywim.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import javax.xml.transform.stream.StreamResult;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Date 2025/12/27
 */
public class ChatTest {
    OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("deepseek-key"))
            .modelName("deepseek-chat")
            .baseUrl("https://api.deepseek.com")
            .build();
    OpenAiStreamingChatModel streamingModel = OpenAiStreamingChatModel.builder()
            .apiKey(System.getenv("deepseek-key"))
            .modelName("deepseek-chat")
            .baseUrl("https://api.deepseek.com")
            .build();
    @Test
    public void chat1(){
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from("你好，你是谁，你的模型名称是什么"))
                .modelName("deepseek-reasoner").build();
//        System.out.println("model.chat(\"你好，你是谁\") = " + model.chat("你好，你是谁"));
        ChatResponse chatResponse = model.chat(request);
        System.out.println("model.chat(request) = " + chatResponse);
        TokenUsage tokenUsage = chatResponse.tokenUsage();
        System.out.println("输入token = " + tokenUsage.inputTokenCount());
        System.out.println("输出token = " + tokenUsage.outputTokenCount());
        System.out.println("总token = " + tokenUsage.totalTokenCount());

        AiMessage aiMessage = chatResponse.aiMessage();
        System.out.println("aiMessage.thinking() = " + aiMessage.thinking());
        System.out.println("aiMessage.type() = " + aiMessage.type());
    }

    @Test
    public void testChatRequestStream() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AiServiceChat service = AiServices.builder(AiServiceChat.class)
                .streamingChatModel(streamingModel)
                .chatRequestTransformer((chatRequest, object) ->
                        chatRequest.toBuilder()
                                .modelName("deepseek-reasoner")
                                .parameters(null)
                                .build())
                .build();
        Flux<String> chat = service.chat("你好，你是谁");

        chat.doOnSubscribe(subscription -> System.out.println("开始订阅"))
                .doOnNext(token -> System.out.print(token))
                .doOnError(error -> System.err.println("错误: " + error))
                .doOnComplete(() -> {
                    System.out.println("完成");
                    countDownLatch.countDown();
                })
                .subscribe();
        countDownLatch.await();
    }

    @Test
    public void testChatStreamResponse() throws InterruptedException {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        AtomicReference<ChatResponse> metadataHolder = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 2. 手动调底层 API（仅在这个方法回落）
        streamingModel.chat("你好，你是谁", new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
//                System.out.println("token = " + token);
                sink.tryEmitNext(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("\n1. 进入 onCompleteResponse");
                sink.tryEmitComplete();
                System.out.println("2. tryEmitComplete 返回");
                // 3. 在这里才能拿到元数据
                metadataHolder.set(ChatResponse.builder()
                        .aiMessage(response.aiMessage())
                        .tokenUsage(response.tokenUsage())
                        .finishReason(response.finishReason())
                        .build());
                System.out.println("3. metadataHolder 已设置");
                // 3. 最后 countDown（确保 set 已完成）
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                sink.tryEmitError(error);
            }
        });

        sink.asFlux()
                .doOnSubscribe(subscription -> System.out.println("开始订阅"))
                .doOnNext(System.out::print)
                .doOnError(error -> System.err.println("错误: " + error))
                .doOnComplete(() -> {
                    System.out.println("完成");
                })
                .subscribe();
        countDownLatch.await();
        System.out.println("metadataHolder.get() = " + metadataHolder.get());
    }
    /*
    todo
     1. 结构化输出
     2. 模型切换
     */

    @Test
    public void testMemory(){

    }

}
