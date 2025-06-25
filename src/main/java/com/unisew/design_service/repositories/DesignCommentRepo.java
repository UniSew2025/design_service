package com.unisew.design_service.repositories;

import com.unisew.design_service.models.DesignComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignCommentRepo extends JpaRepository<DesignComment, Integer> {
    List<DesignComment> findAllByDesignRequest_Id(int designId);
}
