package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignDraftRepo extends JpaRepository<DesignDraft, Integer> {
    List<DesignDraft> findAllByCloth_Id(Integer cloth_id);

    Optional<DesignDraft> findByIdAndCloth_Id(Integer id, Integer cloth_id);

    boolean existsByCloth_IdAndIsFinalTrue(Integer cloth_id);

    List<DesignDraft> findAllByCloth_DesignRequest_Id(Integer cloth_design_request_id);
}
