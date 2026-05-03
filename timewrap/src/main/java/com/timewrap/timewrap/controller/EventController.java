package com.timewrap.timewrap.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.timewrap.timewrap.entity.Branch;
import com.timewrap.timewrap.entity.Event;
import com.timewrap.timewrap.entity.MergeStrategy;
import com.timewrap.timewrap.model.MergeResult;
import com.timewrap.timewrap.service.EventService;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping
    public Event createEvent(@RequestBody Event event) {
        return eventService.saveEvent(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{entityId}")
    public List<Event> getEventsByEntity(@PathVariable String entityId) {
        return eventService.getEventsByEntity(entityId);
    }
    @GetMapping("/{entityId}/state")
public Map<String, Object> getState(
        @PathVariable String entityId,
        @RequestParam(required = false) String timestamp,
        @RequestParam(required = false) Long eventId,
        @RequestParam(required = false) Integer version) {

    int count = 0;

    if (timestamp != null) count++;
    if (eventId != null) count++;
    if (version != null) count++;

    if (count == 0) {
        throw new RuntimeException("Provide at least one parameter");
    }

    if (count > 1) {
        throw new RuntimeException("Only one parameter allowed");
    }

    if (timestamp != null) {
        return eventService.getStateAtTime(
                entityId,
                LocalDateTime.parse(timestamp)
        );
    }

    if (eventId != null) {
        return eventService.getStateAtEventId(entityId, eventId);
    }

    if (version != null) {
        return eventService.getStateAtVersion(entityId, version);
    }

    throw new RuntimeException("Invalid request");
}
@GetMapping("/{entityId}/timeline")
public List<Map<String, Object>> getTimeline(@PathVariable String entityId) {
    return eventService.getTimeline(entityId);
}
@PostMapping("/{entityId}/merge")
public MergeResult merge(
        @PathVariable String entityId,
        @RequestParam Long sourceEventId,
        @RequestParam Long targetEventId,@RequestParam MergeStrategy strategy) {

    return eventService.mergeBranches(
            entityId,
            sourceEventId,
            targetEventId,
            strategy);

}
@PostMapping("/{entityId}/checkout")
public String checkout(
        @PathVariable String entityId,
        @RequestParam Long eventId) {

    eventService.checkout(entityId, eventId);
    return "Switched timeline";
}
@GetMapping("/{entityId}/head")
public Long getHead(@PathVariable String entityId) {
    return eventService.getHead(entityId);
}
@PostMapping("/{entityId}/branch")
public Branch createBranch(
        @PathVariable String entityId,
        @RequestParam String name,
        @RequestParam Long eventId) {

    return eventService.createBranch(entityId, name, eventId);
}
@GetMapping("/{entityId}/diff")
public Map<String, Object> diff(
        @PathVariable String entityId,
        @RequestParam Long fromEventId,
        @RequestParam Long toEventId) {

    Map<String, Object> before =
            eventService.getStateAtEventId(entityId, fromEventId);

    Map<String, Object> after =
            eventService.getStateAtEventId(entityId, toEventId);

    return eventService.computeDiff(before, after);
}
}