package com.unisew.design_service.repositories;

import com.unisew.design_service.enums.Status;
import com.unisew.design_service.models.DesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignRequestRepo extends JpaRepository<DesignRequest, Integer> {
    List<DesignRequest> findAllByStatus(Status status);
}
