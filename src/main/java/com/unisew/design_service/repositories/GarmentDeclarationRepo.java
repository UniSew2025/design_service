package com.unisew.design_service.repositories;

import com.unisew.design_service.models.GarmentDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarmentDeclarationRepo extends JpaRepository<GarmentDeclaration, Integer> {
}
