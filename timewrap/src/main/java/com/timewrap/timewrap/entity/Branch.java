package com.timewrap.timewrap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue
    private Long id;

    private String entityId;
    private String name;
    private Long headEventId;

    public Branch() {}
    public String getEntityId() { return entityId; }
    public String getName() { return name; }        
    public Long getHeadEventId() { return headEventId; }
    public void setHeadEventId(Long headEventId) { this.headEventId = headEventId; }
    public Branch(String entityId, String name, Long headEventId) {
        this.entityId = entityId;
        this.name = name;
        this.headEventId = headEventId;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    public void setName(String name) {
        this.name = name;
    }
}
   
