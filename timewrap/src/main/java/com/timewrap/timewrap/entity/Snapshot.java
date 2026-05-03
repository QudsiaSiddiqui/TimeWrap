package com.timewrap.timewrap.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "snapshots")
public class Snapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String state; // full JSON state

    private Long lastEventId;

    private LocalDateTime timestamp;

    private Long baseEventId;

    public Snapshot() {}

    public Snapshot(String entityId, String state, Long lastEventId, LocalDateTime timestamp, Long baseEventId) {
        this.entityId = entityId;
        this.state = state;
        this.lastEventId = lastEventId;
        this.timestamp = timestamp;
        this.baseEventId=baseEventId;
    }

    // getters & setters
    public Long getId() { return id; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Long getLastEventId() { return lastEventId; }
    public void setLastEventId(Long lastEventId) { this.lastEventId = lastEventId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getBaseEventId() { return baseEventId; }
    public void setBaseEventId(Long baseEventId) { this.baseEventId = baseEventId; }

}
