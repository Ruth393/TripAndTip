package com.example.trip.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AIChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private static final String PACKING_INSTRUCTION = """
        אתה עוזר AI שמכין רשימת ציוד לטיולים.
        1. קודם קרא לכלי getTripDetailsForPacking
        2. השתמש בפרטי הטיול כדי להכין רשימת ציוד
        3. החזר רשימה מחולקת לקטגוריות
        """;

    public AIChatService(ChatClient.Builder chatClientBuilder,
                         ChatMemory chatMemory,
                         AgentTools agentTools) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClientBuilder
                .defaultTools(agentTools)
                .build();
    }

    public Flux<String> getPackingListForTrip(long tripId) {
        return chatClient.prompt()
                .system(PACKING_INSTRUCTION)
                .user("הכן רשימת ציוד לטיול עם tripId=" + tripId)
                .stream()
                .content()
                .timeout(Duration.ofSeconds(60))
                .onErrorResume(e -> Flux.just("Error retrieving packing list. Please check connection."));
    }

    public Flux<String> getResponse2(String prompt, String conversationId) {
        List<Message> history = chatMemory.get(conversationId);
        List<Message> limitedHistory = history.size() > 6 ? history.subList(history.size() - 6, history.size()) : history;

        UserMessage userMessage = new UserMessage(prompt);
        AtomicReference<String> fullContent = new AtomicReference<>("");

        return chatClient.prompt()
                .messages(limitedHistory)
                .user(prompt)
                .stream()
                .content()
                .onBackpressureBuffer()
                .timeout(Duration.ofSeconds(60))
                .doOnNext(chunk -> fullContent.updateAndGet(c -> c + chunk))
                .doOnComplete(() -> {
                    if (!fullContent.get().isEmpty()) {
                        AssistantMessage aiMessage = new AssistantMessage(fullContent.get());
                        chatMemory.add(conversationId, List.of(userMessage, aiMessage));
                    }
                })
                .onErrorResume(e -> Flux.just("Connection error. Please try again."));
    }
}