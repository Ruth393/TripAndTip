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
    private final TripTools tripTools;
    private final WeatherTools weatherTools;

    private static final String AGENT_INSTRUCTION = """
        אתה עוזר AI חכם של אתר טיולים.
        אתה מחובר למערכת הטיולים ויכול להשתמש בכלים הזמינים לך:
        - searchTrips / getTripDetails - חיפוש ומידע על טיולים במערכת
        - getWeatherByCity - תחזית מזג אוויר

        כאשר המשתמש שואל על טיולים, יעדים או מקומות:
        - חפש קודם במערכת הטיולים באמצעות הכלים הזמינים.
        - אל תמציא מידע שלא קיים במערכת - אם הכלי לא החזיר תוצאות, אמור זאת בכנות.

        כאשר המשתמש מבקש המלצה לטיול:
        - השתמש ב-searchTrips לפי מה שהמשתמש ביקש (תקציב, התאמה לילדים, רמת קושי).
        - אם השאלה קשורה לזמן קרוב ("מחר", "השבוע"), בדוק גם מזג אוויר עם getWeatherByCity.
        - התאם את ההמלצה לתוצאות שקיבלת מהכלים.

        כאשר המשתמש שואל רק על מזג האוויר - השתמש ב-getWeatherByCity.

        ענה תמיד בעברית, בקצרה ובבהירות. אם אינך יודע תשובה מדויקת, אמור זאת בכנות.
        """;

    public AIChatService(ChatClient.Builder chatClientBuilder,
                         ChatMemory chatMemory,
                         TripTools tripTools,
                         WeatherTools weatherTools) {
        this.chatMemory = chatMemory;
        this.tripTools = tripTools;
        this.weatherTools = weatherTools;
        this.chatClient = chatClientBuilder.build();
    }

    public Flux<String> getResponse2(String prompt, String conversationId) {
        List<Message> history = chatMemory.get(conversationId);
        List<Message> limitedHistory =
                history.size() > 2
                        ? history.subList(history.size() - 2, history.size())
                        : history;

        UserMessage userMessage = new UserMessage(prompt);
        AtomicReference<String> fullContent = new AtomicReference<>("");

        return chatClient.prompt()
                .system(AGENT_INSTRUCTION)
                .messages(limitedHistory)
                .user(prompt)
                .tools(tripTools, weatherTools)
                .stream()
                .content()
                .onBackpressureBuffer()
                .timeout(Duration.ofSeconds(60))

                // בדיקה: האם Gemini מחזיר chunks?
                .doOnNext(chunk -> {
                    System.out.println("========== GEMINI CHUNK ==========");
                    System.out.println(chunk);
                    System.out.println("==================================");

                    fullContent.updateAndGet(c -> c + chunk);
                })

                // בדיקה: האם הזרם הסתיים בהצלחה?
                .doOnComplete(() -> {
                    System.out.println("========== GEMINI COMPLETE ==========");
                    System.out.println("Full response length: " + fullContent.get().length());
                    System.out.println("======================================");

                    if (!fullContent.get().isEmpty()) {
                        AssistantMessage aiMessage =
                                new AssistantMessage(fullContent.get());

                        chatMemory.add(
                                conversationId,
                                List.of(userMessage, aiMessage)
                        );
                    }
                })

                // בדיקה: האם התרחשה שגיאה?
                .doOnError(e -> {
                    System.out.println("========== GEMINI ERROR ==========");
                    e.printStackTrace();
                    System.out.println("==================================");
                })

                .onErrorResume(e ->
                        Flux.just("Connection error. Please try again.")
                );
    }
}