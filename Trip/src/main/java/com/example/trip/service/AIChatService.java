package com.example.trip.service;


import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;


import java.util.ArrayList;
import java.util.List;



@Service
public class AIChatService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    private final static String SYSYEM_INSTRUCTION= """
             אתה עוזר AI שנועד לעזור בעניני טיולים בארץ בלבד
            אתה אמור לומר אם זה מתאים לצאת בעונה שביקשו מבחינת התחזית
            וכן מפה של דרך הגעה למיקום ואיזה קווי אוטובוס יוכלו להגיע הכי קרוב למיקום
           תביא רעיונות מגובנים ונגישים
            """;

    public AIChatService(ChatClient.Builder chatClient, ChatMemory chatMemory) {
        this.chatClient=chatClient.build();
        this.chatMemory=chatMemory;
    }
    public String getResponse(String prompt){
        SystemMessage systemMessage = new SystemMessage(prompt);
        UserMessage userMessage = new UserMessage(prompt);

        List<Message> messageList= List.of(systemMessage,userMessage);

        return chatClient.prompt().messages(messageList).call().content();
    }
    //שמירת השיחה עם הזרמות
//    public Flux<String> getResponse2(String prompt, String conversationId){
//        List<Message> messageList=new ArrayList<>();
//
//        messageList.add(new SystemMessage(SYSYEM_INSTRUCTION));
//
//        messageList.addAll(chatMemory.get(conversationId));
//        UserMessage userMessage=new UserMessage(prompt);
//        messageList.add(userMessage);
//
//        Flux<String> aiResponse=chatClient.prompt().messages(messageList).stream().content();
//        AssistantMessage aiMessage=new AssistantMessage(aiResponse.toString());
//
//        List<Message> messageList1=List.of(userMessage,aiMessage);
//
//        chatMemory.add(conversationId,messageList1);
//        return aiResponse;
//
//    }
    //בלי הזרמה
    public String getResponse2(String prompt, String conversationId){
        List<Message> messageList=new ArrayList<>();

        messageList.add(new SystemMessage(SYSYEM_INSTRUCTION));

        messageList.addAll(chatMemory.get(conversationId));
        UserMessage userMessage=new UserMessage(prompt);
        messageList.add(userMessage);

        String aiResponse=chatClient.prompt().messages(messageList).call().content();
        AssistantMessage aiMessage=new AssistantMessage(aiResponse);

        List<Message> messageList1=List.of(userMessage,aiMessage);

        chatMemory.add(conversationId,messageList1);
        return aiResponse;

    }
}
