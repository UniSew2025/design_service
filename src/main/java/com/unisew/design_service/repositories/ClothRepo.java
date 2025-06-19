package com.unisew.design_service.repositories;

import com.unisew.design_service.models.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClothRepo extends JpaRepository<Cloth, Integer> {
    List<Cloth> getAllByDesignRequest_Id(int id);
}
