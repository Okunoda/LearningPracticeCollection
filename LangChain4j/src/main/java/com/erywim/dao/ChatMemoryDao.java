package com.erywim.dao;

import com.erywim.entity.ChatMemory;
import dev.langchain4j.data.message.ChatMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//@Mapper
public interface ChatMemoryDao {

    ChatMessage queryById(Object o);

    List<ChatMessage> queryByUserId(Object o);

    void updateByUserId(@Param("id") Object o, @Param("chatMemoryList") List<ChatMemory> list);

    void deletaByUserId(Object o);

    void save(Object o, List<ChatMemory> chatMemoryList);

}
