package SkillExchange.Backend.dto;

public class ChatResponseDTO {

    private String reply;
    private String model;
    private boolean success;

    // getters
    public String getReply() { return reply; }
    public String getModel() { return model; }
    public boolean isSuccess() { return success; }

    // setters
    public void setReply(String reply) { this.reply = reply; }
    public void setModel(String model) { this.model = model; }
    public void setSuccess(boolean success) { this.success = success; }
}