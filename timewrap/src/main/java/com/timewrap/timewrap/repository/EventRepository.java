package com.timewrap.timewrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timewrap.timewrap.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEntityIdOrderByTimestampAsc(String entityId);
    List<Event> findByEntityId(String entityId);
}

