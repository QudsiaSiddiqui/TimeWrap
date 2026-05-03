package com.timewrap.timewrap.engine;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timewrap.timewrap.entity.Event;

@Component
public class TimeEngine {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> reconstructState(List<Event> events, LocalDateTime targetTime) {

        Map<String, Object> currentState = new HashMap<>();

        for (Event event : events) {

            // Only consider events up to target time
            if (event.getTimestamp().isAfter(targetTime)) {
                break;
            }

            // Apply event
            applyEvent(currentState, event);
        }

        return currentState;
    }
    private void applyEvent(Map<String, Object> state, Event event) {
    try {
        Map<String, Object> eventMap =
                objectMapper.readValue(event.getEventData(), Map.class);

        String type = event.getEventType();

        if ("CREATE".equals(type) || "UPDATE".equals(type)) {
            state.putAll(eventMap);  // 🔥 FLAT
        } 
        else if ("DELETE".equals(type)) {
            state.clear();
        }

    } catch (Exception e) {
        throw new RuntimeException("Error parsing event data", e);
    }
}
public Map<String, Object> reconstructFromState(
        Map<String, Object> initialState,
        List<Event> events,
        LocalDateTime targetTime) {

    Map<String, Object> currentState = new HashMap<>(initialState);

    for (Event event : events) {

        if (event.getTimestamp().isAfter(targetTime)) {
            break;
        }

        applyEvent(currentState, event);
    }

    return currentState;
}
    }