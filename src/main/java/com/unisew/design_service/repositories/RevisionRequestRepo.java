package com.unisew.design_service.repositories;

import com.unisew.design_service.models.RevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevisionRequestRepo extends JpaRepository<RevisionRequest, Integer> {
}
