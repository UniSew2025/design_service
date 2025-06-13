package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignDraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DesignDraftRepo extends JpaRepository<DesignDraft, Integer> {
}
