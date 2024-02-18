package com.baseball.controller.websocket;

import com.baseball.vo.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(Principal principal, @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        Integer id = (Integer) headerAccessor.getSessionAttributes().get("userID");
        System.out.println("누가 보낸 메시지인가?" + id);
        chatMessage.setId(id);
        return chatMessage;
    }
}
