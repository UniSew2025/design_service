package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignDraftRepo extends JpaRepository<DesignDraft, Integer> {
    List<DesignDraft> findAllByCloth_Id(Integer cloth_id);

    boolean existsByCloth_IdAndIsFinalTrue(Integer cloth_id);
}
