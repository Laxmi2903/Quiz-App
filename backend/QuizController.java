package com.quizapp.controller;

import com.quizapp.dto.QuizDTO;
import com.quizapp.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /** GET /api/quizzes  →  list all quizzes (optional ?category=) */
    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(quizService.getQuizzesByCategory(category));
        }
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    /** GET /api/quizzes/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    /** POST /api/quizzes  (ADMIN only — enforced in security config) */
    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO dto) {
        QuizDTO created = quizService.createQuiz(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/quizzes/{id}  (ADMIN only) */
    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id,
                                               @RequestBody QuizDTO dto) {
        return ResponseEntity.ok(quizService.updateQuiz(id, dto));
    }

    /** DELETE /api/quizzes/{id}  (ADMIN only) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
