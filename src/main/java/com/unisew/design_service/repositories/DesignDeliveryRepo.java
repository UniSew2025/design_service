package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignDeliveryRepo extends JpaRepository<DesignDelivery, Integer> {
    Optional<DesignDelivery> findTopByDesignRequest_IdOrderByDeliveryNumberDesc(Integer designRequestId);

}
