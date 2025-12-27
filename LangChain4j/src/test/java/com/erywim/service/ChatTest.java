package com.erywim.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.TokenUsage;
import org.junit.Test;

/**
 * @Date 2025/12/27
 */
public class ChatTest {
    OpenAiChatModel model = OpenAiChatModel.builder()
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
    public void testMemory(){

    }
}
