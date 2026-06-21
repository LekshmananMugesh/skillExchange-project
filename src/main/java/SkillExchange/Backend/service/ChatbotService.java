package SkillExchange.Backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {

    @Value("${chatbot.mode}")
    private String mode;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    @Value("${ollama.api.url}")
    private String ollamaUrl;

    @Value("${ollama.model}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String SYSTEM_PROMPT =
            "You are a study assistant for SkillExchange " +
                    "platform. Only answer educational and technical " +
                    "questions related to programming, mathematics, " +
                    "sciences, languages, or any skill. If asked " +
                    "off-topic questions politely decline. " +
                    "Be concise, clear and helpful.";

    public String chat(String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("model", ollamaModel);
            body.put("prompt", SYSTEM_PROMPT + "\n\nUser question: " + userMessage);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            String url = ollamaUrl + "/api/generate";

            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("response");
            }

            return "Sorry, I could not process your question.";
        } catch (Exception e) {
            System.out.println("Ollama error: " + e.getMessage());
            return "Study assistant is currently unavailable. Please try again later.";
        }
    }

    public String getResponse(String userMessage) {
        if ("production".equalsIgnoreCase(mode)) {
            return callGemini(userMessage);
        } else {
            return callOllama(userMessage);
        }
    }

    private String callOllama(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", ollamaModel);
        body.put("prompt", message);
        body.put("stream", false);

        ResponseEntity<Map> response = restTemplate.postForEntity(ollamaUrl, body, Map.class);
        return (String) response.getBody().get("response");
    }

    private String callGemini(String message) {
        String url = geminiUrl + "?key=" + geminiApiKey;

        Map<String, Object> part = Map.of("text", message);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

        Map responseBody = response.getBody();
        List candidates = (List) responseBody.get("candidates");
        Map firstCandidate = (Map) candidates.get(0);
        Map contentMap = (Map) firstCandidate.get("content");
        List parts = (List) contentMap.get("parts");
        Map firstPart = (Map) parts.get(0);
        return (String) firstPart.get("text");
    }

}
