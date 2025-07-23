package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignItemRepo extends JpaRepository<DesignItem, Integer> {
    List<DesignItem> getAllByDesignRequest_Id(int id);
}
