package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DraftImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DraftImageRepo extends JpaRepository<DraftImage, Integer> {
}
