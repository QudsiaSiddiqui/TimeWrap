package com.timewrap.timewrap.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timewrap.timewrap.entity.Event;
import com.timewrap.timewrap.entity.Snapshot;
import com.timewrap.timewrap.repository.SnapshotRepository;

@Service
public class SnapshotService {

    private final SnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SnapshotService(SnapshotRepository snapshotRepository) {
        this.snapshotRepository = snapshotRepository;
    }

    // ✅ Create snapshot
    public void createSnapshot(String entityId,
                               Map<String, Object> state,
                               Event lastEvent, Long baseEventId) {

        try {
            String stateJson = objectMapper.writeValueAsString(state);

            Snapshot snapshot = new Snapshot(
                    entityId,
                    stateJson,
                    lastEvent.getId(),
                    lastEvent.getTimestamp(), baseEventId
            );
            snapshot.setBaseEventId(baseEventId);
            snapshotRepository.save(snapshot);

        } catch (Exception e) {
            throw new RuntimeException("Snapshot creation failed", e);
        }
    }

    // ✅ Get latest snapshot
    public Snapshot getLatestSnapshot(String entityId) {
        return snapshotRepository
                .findTopByEntityIdOrderByLastEventIdDesc(entityId)
                .orElse(null);
    }
}