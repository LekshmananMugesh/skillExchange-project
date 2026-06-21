package SkillExchange.Backend.controller;

import SkillExchange.Backend.dto.ChatRequestDTO;
import SkillExchange.Backend.dto.ChatResponseDTO;
import SkillExchange.Backend.service.ChatbotService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(
            ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(
            @RequestBody ChatRequestDTO request) {

        String userMessage =
                request.getMessages()
                        .get(0)
                        .getContent();

        String reply =
                chatbotService.chat(userMessage);

        ChatResponseDTO response =
                new ChatResponseDTO();

        response.setReply(reply);
        response.setModel("gemini");
        response.setSuccess(true);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> request) {
        String reply = chatbotService.getResponse(request.get("message"));
        return ResponseEntity.ok(Map.of("response", reply));
    }
}