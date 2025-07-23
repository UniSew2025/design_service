package com.unisew.design_service.repositories;

import com.unisew.design_service.models.SampleImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleImageRepo extends JpaRepository<SampleImage, Integer> {
    List<SampleImage> findAllByDesignItem_Id(Integer clothId);
}
