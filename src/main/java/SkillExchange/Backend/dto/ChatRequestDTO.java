package SkillExchange.Backend.dto;

import java.util.List;

public class ChatRequestDTO {

    // each message has a role (user/assistant) and content
    private List<MessageDTO> messages;

    public List<MessageDTO> getMessages() { return messages; }
    public void setMessages(List<MessageDTO> messages) { this.messages = messages; }

    // inner class for a single message
    public static class MessageDTO {
        private String role;
        private String content;

        public String getRole() { return role; }
        public String getContent() { return content; }

        public void setRole(String role) { this.role = role; }
        public void setContent(String content) { this.content = content; }
    }
}