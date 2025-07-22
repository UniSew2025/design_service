package com.unisew.design_service.repositories;

import com.unisew.design_service.models.RevisionRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevisionRequestRepo extends JpaRepository<RevisionRequest, Integer> {
    boolean existsByDelivery_Id(Integer id);

    List<RevisionRequest> findAllById(Integer id);
}
