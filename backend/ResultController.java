package com.quizapp.controller;

import com.quizapp.model.Result;
import com.quizapp.service.ResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    /**
     * POST /api/results/submit/{quizId}
     * Body: { "<questionId>": "A", "<questionId>": "C", ... }
     * Submits a quiz attempt and returns the scored Result.
     */
    @PostMapping("/submit/{quizId}")
    public ResponseEntity<Result> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody Map<Long, String> answers,
            @AuthenticationPrincipal UserDetails userDetails) {

        Result result = resultService.submitQuiz(userDetails.getUsername(), quizId, answers);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * GET /api/results/user/{userId}
     * Returns all results for a given user.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Result>> getResultsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(resultService.getResultsByUser(userId));
    }

    /**
     * GET /api/results/quiz/{quizId}
     * Returns all results for a given quiz (useful for admins / leaderboards).
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Result>> getResultsByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsByQuiz(quizId));
    }

    /**
     * GET /api/results/{resultId}
     * Returns a single result by ID.
     */
    @GetMapping("/{resultId}")
    public ResponseEntity<Result> getResultById(@PathVariable Long resultId) {
        return ResponseEntity.ok(resultService.getResultById(resultId));
    }

    /**
     * GET /api/results/user/{userId}/quiz/{quizId}
     * Returns all attempts by a specific user on a specific quiz.
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<List<Result>> getResultsByUserAndQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        return ResponseEntity.ok(resultService.getResultsByUserAndQuiz(userId, quizId));
    }
}
