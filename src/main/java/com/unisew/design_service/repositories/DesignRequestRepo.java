package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignRequestRepo extends JpaRepository<DesignRequest, Integer> {
}
