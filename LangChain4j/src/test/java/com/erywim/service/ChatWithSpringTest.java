package com.erywim.service;

import com.erywim.dao.ChatMemoryDao;
import com.erywim.memory.PersistentChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
*
*@Date 2025/12/29
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ChatWithSpringTest {
    /*
    todo
    1. 记忆持久化到数据库 ———— done
    2. rag 库使用
     */
    @Autowired
    private PersistentChatMemoryStore memoryStore;
    @Test
    public void testPersistMemory() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        OpenAiStreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .modelName("deepseek-chat")
                .baseUrl("https://api.deepseek.com")
                .apiKey(System.getenv("deepseek-key"))
                .build();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(memoryStore)
                .build();

        AiServiceChat service = AiServices.builder(AiServiceChat.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        service.chatWithMemory("我是小A", 1L)
//                .doOnSubscribe((subscription) -> System.out.println(subscription + " 开始订阅"))
                .doOnNext(System.out::print)
                .doOnComplete(() -> {
                    System.out.println("\n=======================================完成=======================================");
                    countDownLatch.countDown();
                })
                .subscribe();
        countDownLatch.await();
        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        service.chatWithMemory("我是谁",1L)
//                .doOnSubscribe((subscription) -> System.out.println(subscription + " 開始订阅"))
                .doOnNext(System.out::print)
                .doOnComplete(() -> {
                    System.out.println("\n=======================================完成=======================================");
                    countDownLatch1.countDown();
                })
                .subscribe();

        countDownLatch1.await();
        CountDownLatch countDownLatch2 = new CountDownLatch(1);

        service.chatWithMemory("我是谁",2L)
//                .doOnSubscribe((subscription) -> System.out.println(subscription + " 開始订阅"))
                .doOnNext(System.out::print)
                .doOnComplete(() -> {
                    System.out.println("\n=======================================完成=======================================");
                    countDownLatch2.countDown();
                })
                .subscribe();
        countDownLatch2.await();
    }
}
