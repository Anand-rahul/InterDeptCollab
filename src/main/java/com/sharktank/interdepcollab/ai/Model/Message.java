package com.sharktank.interdepcollab.ai.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    public UUID chatGuid;
    @Column(columnDefinition = "TEXT")
    public String messageType;
    @Column(columnDefinition = "TEXT")
    public String messageText;
    public Instant timestamp;
    public String metaData;  // Store JSON data as String

    public Message() {
    }

    public Message(long id, UUID chatGuid, String messageType, String messageText, Instant timestamp, String metaData) {
        this.id = id;
        this.chatGuid = chatGuid;
        this.messageType = messageType;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.metaData = metaData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UUID getChatGuid() {
        return chatGuid;
    }

    public void setChatGuid(UUID chatGuid) {
        this.chatGuid = chatGuid;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
