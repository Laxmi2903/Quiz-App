package com.quizapp.controller;

import com.quizapp.dto.QuestionDTO;
import com.quizapp.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuizService quizService;

    public QuestionController(QuizService quizService) {
        this.quizService = quizService;
    }

    /** GET /api/questions/quiz/{quizId}  →  all questions for a quiz */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDTO>> getByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuestionsByQuizId(quizId));
    }

    /** GET /api/questions/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuestionById(id));
    }

    /** POST /api/questions  (ADMIN only — enforced in security config) */
    @PostMapping
    public ResponseEntity<QuestionDTO> addQuestion(@RequestBody QuestionDTO dto) {
        QuestionDTO created = quizService.addQuestion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/questions/{id}  (ADMIN only) */
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long id,
                                                       @RequestBody QuestionDTO dto) {
        return ResponseEntity.ok(quizService.updateQuestion(id, dto));
    }

    /** DELETE /api/questions/{id}  (ADMIN only) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        quizService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
