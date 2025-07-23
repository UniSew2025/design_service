package com.unisew.design_service.repositories;

import com.unisew.design_service.models.FinalImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FinalImageRepo extends JpaRepository<FinalImage, Integer> {

    List<FinalImage> findAllByDesignItem_Id(Integer id);
}
