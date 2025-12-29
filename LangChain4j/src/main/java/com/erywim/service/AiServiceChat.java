package com.erywim.service;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import dev.langchain4j.service.spring.AiService;
import opennlp.tools.langdetect.Language;
import reactor.core.publisher.Flux;

import javax.xml.transform.stream.StreamResult;

/**
 * @Date 2025/12/20
 */
public interface AiServiceChat extends ChatMemoryAccess {
    Flux<String> chat(@UserMessage String question);


}
