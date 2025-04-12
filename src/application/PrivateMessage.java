package application;

import java.time.LocalDateTime;

public class PrivateMessage {
    private int id;
    private String sender;
    private String recipient;
    private String messageText;
    private LocalDateTime timestamp;

    // Constructor used when retrieving from the database (with id)
    public PrivateMessage(int id, String sender, String recipient, String messageText, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }
    
    // Constructor used when creating a new message (id will be auto-generated)
    public PrivateMessage(String sender, String recipient, String messageText) {
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public String getMessageText() {
        return messageText;
    }
    
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "PrivateMessage{" +
                "id=" + id +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", messageText='" + messageText + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
