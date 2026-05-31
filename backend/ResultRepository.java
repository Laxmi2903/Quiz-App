package com.quizapp.repository;

import com.quizapp.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserId(Long userId);
    List<Result> findByQuizId(Long quizId);
    List<Result> findByUserIdAndQuizId(Long userId, Long quizId);
}
