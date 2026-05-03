package com.timewrap.timewrap.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timewrap.timewrap.entity.Snapshot;

public interface SnapshotRepository extends JpaRepository<Snapshot, Long> {

    Optional<Snapshot> findTopByEntityIdOrderByLastEventIdDesc(String entityId);
    Optional<Snapshot> findTopByEntityIdAndBaseEventIdOrderByLastEventIdDesc(
        String entityId,
        Long baseEventId
);
}