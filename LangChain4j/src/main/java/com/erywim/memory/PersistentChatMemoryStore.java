package com.erywim.memory;

import com.erywim.dao.ChatMemoryDao;
import com.erywim.entity.ChatMemory;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2025/12/29
 */
@Component
@Slf4j
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private ChatMemoryDao memoryDao;


    @Override
    public List<ChatMessage> getMessages(Object o) {
        return memoryDao.queryByUserId(o);
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {

        memoryDao.deletaByUserId(o);

        List<ChatMemory> chatMemoryList = new ArrayList<>();
        System.out.println("list = " + list);
        for (ChatMessage chatMessage : list) {
            if (chatMessage.type() == ChatMessageType.USER) {
                UserMessage message = (UserMessage) chatMessage;
                message.contents().forEach(content -> {
                    TextContent textContent = (TextContent) content;
                    String text = textContent.text();
                    ChatMemory chatMemory = new ChatMemory(Long.valueOf((String) o), text, ChatMessageType.USER.name());
                    chatMemoryList.add(chatMemory);
                });
            }else if(chatMessage.type() == ChatMessageType.AI){
                AiMessage message = (AiMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf((String) o), message.text(), ChatMessageType.AI.name());
                chatMemoryList.add(chatMemory);
            }else if(chatMessage.type() == ChatMessageType.TOOL_EXECUTION_RESULT){
                ToolExecutionResultMessage message = (ToolExecutionResultMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf((String) o), message.text(), ChatMessageType.TOOL_EXECUTION_RESULT.name());
                chatMemoryList.add(chatMemory);
            }else if (chatMessage.type() == ChatMessageType.CUSTOM){
                CustomMessage message = (CustomMessage) chatMessage;
                System.out.println("message.attributes() = " + message.attributes());
            }else if (chatMessage.type() == ChatMessageType.SYSTEM){
                //系统提示词应该只保存一次
                SystemMessage message = (SystemMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf((String) o), message.text(), ChatMessageType.SYSTEM.name());
                chatMemoryList.add(chatMemory);
            }
        }

        //保存或更新

        memoryDao.save(o, chatMemoryList);
        log.info("更新用户 {} 的对话记录", o);
    }

    @Override
    public void deleteMessages(Object o) {
        memoryDao.deletaByUserId(o);
        log.info("删除用户 {} 的对话记录", o);
    }
}
