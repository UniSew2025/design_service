package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignResultRepo extends JpaRepository<DesignResult, Integer> {
}
