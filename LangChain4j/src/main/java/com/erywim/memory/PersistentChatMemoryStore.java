package com.erywim.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Date 2025/12/29
 */
@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public List<ChatMessage> getMessages(Object o) {

        return List.of();
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {

    }

    @Override
    public void deleteMessages(Object o) {

    }
}
