package com.timewrap.timewrap.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String eventData;

    private LocalDateTime timestamp;

    private String entityId; // which object this event belongs to

    @Column(columnDefinition="TEXT")
    private String beforeState;

    @Column(columnDefinition="TEXT")
    private String afterState;

    private Long parentEventId;
    // Constructor
    public Event() {}

    public Event(String eventType, String eventData, LocalDateTime timestamp, String entityId, String beforeState,String afterState) {
        this.eventType = eventType;
        this.eventData = eventData;
        this.timestamp = timestamp;
        this.entityId = entityId;
        this.beforeState=beforeState;
        this.afterState=afterState;
    }

    // Getters and Setters

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getEventType() { return eventType; }

    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventData() { return eventData; }

    public void setEventData(String eventData) { this.eventData = eventData; }

    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getEntityId() { return entityId; }

    public void setEntityId(String entityId) { this.entityId = entityId; }
    
    public String getBeforeState() { return beforeState; }

    public void setBeforeState(String beforeState) { this.beforeState = beforeState; }
   
    public String getAfterState() { return afterState; }

    public void setAfterState(String afterState) { this.afterState = afterState; }

    public Long getParentEventId() {
    return parentEventId;
}

public void setParentEventId(Long parentEventId) {
    this.parentEventId = parentEventId;
}
}
