package com.timewrap.timewrap.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timewrap.timewrap.engine.TimeEngine;
import com.timewrap.timewrap.entity.Branch;
import com.timewrap.timewrap.entity.Event;
import com.timewrap.timewrap.entity.HeadState;
import com.timewrap.timewrap.entity.MergeStrategy;
import com.timewrap.timewrap.model.MergeConflict;
import com.timewrap.timewrap.model.MergeResult;
import com.timewrap.timewrap.repository.EventRepository;
import com.timewrap.timewrap.repository.HeadStateRepository;
import com.timewrap.timewrap.repository.SnapshotRepository;
import com.timewrap.timewrap.repository.BranchRepository;
@Service
public class EventService {


    private final EventRepository eventRepository;

    private final TimeEngine timeEngine;
    @Autowired
    private TimeControlService timeControlService;
    @Autowired
     private HeadStateRepository headRepo;
    @Autowired
     private BranchRepository branchRepo;   
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private SnapshotService snapshotService;
    private SnapshotRepository snapshotRepository;
    private Map<String, Long> currentHeadMap = new HashMap<>();
public EventService(EventRepository eventRepository, TimeEngine timeEngine) {
    this.eventRepository = eventRepository;
    this.timeEngine = timeEngine;
}
public Event saveEvent(Event event) {
    try {
        String entityId = event.getEntityId();

        Map<String, Object> currentState =
                getStateAtTime(entityId, LocalDateTime.now());

        if (currentState == null) {
            currentState = new HashMap<>();
        }

        Map<String, Object> newData = new HashMap<>();

        if (event.getEventData() != null) {
            newData = objectMapper.readValue(event.getEventData(), Map.class);
        }

       if ("CREATE".equalsIgnoreCase(event.getEventType())) {

    event.setBeforeState("{}");
    event.setAfterState(objectMapper.writeValueAsString(newData));
}

        else if ("UPDATE".equalsIgnoreCase(event.getEventType())) {

            String beforeJson = objectMapper.writeValueAsString(currentState);

            currentState.putAll(newData);

            String afterJson = objectMapper.writeValueAsString(currentState);

            event.setBeforeState(beforeJson);
            event.setAfterState(afterJson);
        }

        else if ("DELETE".equalsIgnoreCase(event.getEventType())) {

            String beforeJson = objectMapper.writeValueAsString(currentState);

            event.setBeforeState(beforeJson);
            event.setAfterState("{}");
        }

        // SAFETY CHECK (THIS WILL SAVE YOU HOURS)
        if (event.getBeforeState() == null || event.getAfterState() == null) {
            throw new RuntimeException("beforeState/afterState NOT SET");
        }

        event.setTimestamp(LocalDateTime.now());

        timeControlService.recordEvent(event, false);

        Event saved = eventRepository.save(event);

        List<Event> events = eventRepository
                .findByEntityIdOrderByTimestampAsc(event.getEntityId());

        if (events.size() % 5 == 0) {
            Map<String, Object> latestState =
            getStateAtEventId(event.getEntityId(), saved.getId());

            Long baseEventId = findBranchRoot(saved.getId());

            snapshotService.createSnapshot(
                    event.getEntityId(),
                    latestState,
                    saved,baseEventId
            );
        }
        Event latest = getLatestEvent(event.getEntityId());

        if (latest != null) {
            event.setParentEventId(latest.getId());
        }

        return saved;

    } catch (Exception e) {
        throw new RuntimeException("Error saving event", e);
    }
}
    public List<Event> getEventsByEntity(String entityId) {
        return eventRepository.findByEntityIdOrderByTimestampAsc(entityId);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

  public Map<String, Object> getStateAtTime(String entityId, LocalDateTime time) {

    //  Get latest snapshot
    var snapshot = snapshotService.getLatestSnapshot(entityId);

    try {

        // If snapshot exists
        if (snapshot != null) {

            // Load snapshot state
            Map<String, Object> baseState =
                    objectMapper.readValue(snapshot.getState(), Map.class);

            // Get only events AFTER snapshot
            List<Event> events = eventRepository
                    .findByEntityIdOrderByTimestampAsc(entityId)
                    .stream()
                    .filter(e -> e.getId() > snapshot.getLastEventId())
                    .toList();

            // Use new engine method (IMPORTANT)
            return timeEngine.reconstructFromState(baseState, events, time);
        }

        // Fallback (no snapshot)
        List<Event> events = eventRepository
                .findByEntityIdOrderByTimestampAsc(entityId);

        return timeEngine.reconstructState(events, time);

    } catch (Exception e) {
        throw new RuntimeException("State reconstruction failed", e);
    }
}

public Event saveSystemEvent(Event event) {
    try {
        String entityId = event.getEntityId();

        // 🔥 Get current state BEFORE applying redo/undo
        Map<String, Object> currentState =
                getStateAtTime(entityId, LocalDateTime.now());

        if (currentState == null) {
            currentState = new HashMap<>();
        }

        // Convert incoming eventData
        Map<String, Object> newData = new HashMap<>();

        if (event.getEventData() != null) {
            newData = objectMapper.readValue(event.getEventData(), Map.class);
        }

        String beforeJson = objectMapper.writeValueAsString(currentState);

        // APPLY change
        if ("DELETE".equalsIgnoreCase(event.getEventType())) {
            currentState.clear();
        } else {
            currentState.putAll(newData);
        }

        String afterJson = objectMapper.writeValueAsString(currentState);

        event.setBeforeState(beforeJson);
        event.setAfterState(afterJson);

        event.setTimestamp(LocalDateTime.now());

        timeControlService.recordEvent(event, true);

        return eventRepository.save(event);

    } catch (Exception e) {
        throw new RuntimeException("Error saving system event", e);
    }
}

public Map<String, Object> getStateAtEventId(String entityId, Long eventId) {

    Event targetEvent = eventRepository.findById(eventId)
            .orElseThrow(() -> new RuntimeException("Event not found"));

    if (!targetEvent.getEntityId().equals(entityId)) {
        throw new RuntimeException("Event does not belong to this entity");
    }

    return reconstructFromEvent(entityId, targetEvent);
}
public Map<String, Object> getStateAtVersion(String entityId, int version) {

    List<Event> events = eventRepository
            .findByEntityIdOrderByTimestampAsc(entityId);

    if (version <= 0 || version > events.size()) {
        throw new RuntimeException("Invalid version");
    }

    Event targetEvent = events.get(version - 1);

    return reconstructFromEvent(entityId, targetEvent);
}
private Event getLatestEvent(String entityId) {

    if (currentHeadMap.containsKey(entityId)) {
        Long headId = currentHeadMap.get(entityId);
        return eventRepository.findById(headId).orElse(null);
    }

    List<Event> events = eventRepository
            .findByEntityIdOrderByTimestampAsc(entityId);

    if (events.isEmpty()) return null;

    return events.get(events.size() - 1);
}
private List<Event> buildEventPath(Long eventId) {

    Map<Long, Event> eventMap = eventRepository.findAll()
            .stream()
            .collect(Collectors.toMap(Event::getId, e -> e));

    List<Event> path = new ArrayList<>();

    Event current = eventMap.get(eventId);

    while (current != null) {
        path.add(0, current); // insert at beginning
        current = eventMap.get(current.getParentEventId());
    }

    return path;
}
private Long findBranchRoot(Long eventId) {

    Map<Long, Event> eventMap = eventRepository.findAll()
            .stream()
            .collect(Collectors.toMap(Event::getId, e -> e));

    Event current = eventMap.get(eventId);

    while (current.getParentEventId() != null) {
        current = eventMap.get(current.getParentEventId());
    }

    return current.getId(); // root event
}
public List<Map<String, Object>> getTimeline(String entityId) {

    List<Event> events = eventRepository.findByEntityId(entityId);

    List<Map<String, Object>> result = new ArrayList<>();

    for (Event e : events) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", e.getId());
        node.put("parentId", e.getParentEventId());
        node.put("type", e.getEventType());
        node.put("timestamp", e.getTimestamp());

        result.add(node);
    }

    return result;
}
public MergeResult mergeBranches(
        String entityId,
        Long sourceEventId,
        Long targetEventId,
        MergeStrategy strategy) {

    Map<String, Object> sourceState =
            getStateAtEventId(entityId, sourceEventId);

    Map<String, Object> targetState =
            getStateAtEventId(entityId, targetEventId);

    return mergeStates(sourceState, targetState, strategy);
}private Map<String, Object> reconstructFromEvent(String entityId, Event targetEvent) {

    try {
        //  1. Find branch root
        Long baseEventId = findBranchRoot(targetEvent.getId());

        // 2. Get snapshot for this branch
        var snapshotOpt = snapshotRepository
                .findTopByEntityIdAndBaseEventIdOrderByLastEventIdDesc(
                        entityId,
                        baseEventId
                );

        Map<String, Object> baseState = new HashMap<>();
        Long startEventId = 0L;

        if (snapshotOpt.isPresent()) {
            var snapshot = snapshotOpt.get();

            baseState = objectMapper.readValue(snapshot.getState(), Map.class);
            startEventId = snapshot.getLastEventId();
        }

        // 3. Build path ONLY up to target
        List<Event> path = buildEventPath(targetEvent.getId());

        // 4. Filter only needed events
        Long finalStartEventId = startEventId;

        List<Event> eventsToReplay = path.stream()
                .filter(e -> e.getId() > finalStartEventId)
                .toList();

        return timeEngine.reconstructFromState(
                baseState,
                eventsToReplay,
                targetEvent.getTimestamp()
        );

    } catch (Exception e) {
        throw new RuntimeException("Reconstruction failed", e);
    }
}
public void checkout(String entityId, Long eventId) {

    HeadState head = headRepo
            .findById(entityId)
            .orElse(new HeadState(entityId, eventId));

    head.setCurrentEventId(eventId);

    headRepo.save(head);
}
public MergeResult mergeStates(
        Map<String, Object> source,
        Map<String, Object> target,
        MergeStrategy strategy) {

    Map<String, Object> result = new HashMap<>(target);
    List<MergeConflict> conflicts = new ArrayList<>();

    for (String key : source.keySet()) {

        Object sourceValue = source.get(key);
        Object targetValue = target.get(key);

        // No conflict
        if (targetValue == null || sourceValue.equals(targetValue)) {
            result.put(key, sourceValue);
            continue;
        }

        // Conflict detected
        conflicts.add(new MergeConflict(key, sourceValue, targetValue));

        switch (strategy) {

            case SOURCE_WINS:
                result.put(key, sourceValue);
                break;

            case TARGET_WINS:
                // do nothing
                break;

            case MANUAL:
                // leave unresolved
                break;
        }
    }

    return new MergeResult(result, conflicts);
}
public Long getHead(String entityId) {
    return headRepo.findById(entityId)
            .map(HeadState::getCurrentEventId)
            .orElse(null);
}
public Branch createBranch(String entityId, String name, Long eventId) {

    Branch b = new Branch();
    b.setEntityId(entityId);
    b.setName(name);
    b.setHeadEventId(eventId);

    return branchRepo.save(b);
}
public void switchBranch(Long branchId) {

    Branch b = branchRepo.findById(branchId)
            .orElseThrow();

    checkout(b.getEntityId(), b.getHeadEventId());
}
public Map<String, Object> computeDiff(
        Map<String, Object> before,
        Map<String, Object> after) {

    Map<String, Object> diff = new HashMap<>();

    for (String key : after.keySet()) {

        Object b = before.get(key);
        Object a = after.get(key);

        if (!Objects.equals(b, a)) {

            Map<String, Object> change = new HashMap<>();
            change.put("before", b);
            change.put("after", a);

            diff.put(key, change);
        }
    }

    return diff;
}
}