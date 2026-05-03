package com.timewrap.timewrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.timewrap.timewrap.entity.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findByEntityId(String entityId);

}
