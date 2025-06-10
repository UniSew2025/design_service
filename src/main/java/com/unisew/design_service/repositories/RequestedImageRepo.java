package com.unisew.design_service.repositories;

import com.unisew.design_service.models.RequestedImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestedImageRepo extends JpaRepository<RequestedImage, Integer> {
}
