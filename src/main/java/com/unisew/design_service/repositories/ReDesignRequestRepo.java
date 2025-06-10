package com.unisew.design_service.repositories;

import com.unisew.design_service.models.ReDesignRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReDesignRequestRepo extends JpaRepository<ReDesignRequest, Integer> {
}
