package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DraftImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DraftImageRepo extends JpaRepository<DraftImage, Integer> {
    List<DraftImage> findAllByDesignDraft_Id(Integer designDraft_id);
}
