package org.example.task2restapi.repository;

import org.example.task2restapi.entity.ExecutionFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExecutionFactRepository extends JpaRepository<ExecutionFact, UUID> {
}
