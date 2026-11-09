package com.dsavisualizer.repository;

import com.dsavisualizer.entity.LearningHistory;
import com.dsavisualizer.enums.LearningStatus;
import com.dsavisualizer.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningHistoryRepository
        extends JpaRepository<LearningHistory, Long> {

    List<LearningHistory> findByUser_Id(Long userId);

    List<LearningHistory> findByUser_IdAndStatus(
            Long userId,
            LearningStatus status
    );

    List<LearningHistory> findByRequestType(RequestType requestType);
}