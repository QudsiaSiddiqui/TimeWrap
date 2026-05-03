package com.timewrap.timewrap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "head_state")
public class HeadState {

    @Id
    private String entityId;

    private Long currentEventId;

    public HeadState() {}

    public HeadState(String entityId, Long currentEventId) {
        this.entityId = entityId;
        this.currentEventId = currentEventId;
    }

    public String getEntityId() { return entityId; }
    public Long getCurrentEventId() { return currentEventId; }

    public void setCurrentEventId(Long id) {
        this.currentEventId = id;
    }
}
