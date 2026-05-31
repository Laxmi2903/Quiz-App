package com.quizapp.service;

import com.quizapp.dto.QuestionDTO;
import com.quizapp.dto.QuizDTO;
import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    // ── Quiz CRUD ────────────────────────────────────────────────────────────────

    public QuizDTO createQuiz(QuizDTO dto) {
        Quiz quiz = new Quiz(dto.getTitle(), dto.getDescription(),
                dto.getCategory(), dto.getTimeLimitMinutes());
        Quiz saved = quizRepository.save(quiz);
        return toQuizDTO(saved);
    }

    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.QUIZ_NOT_FOUND));
        return toQuizDTO(quiz);
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(this::toQuizDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuizDTO> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category)
                .stream()
                .map(this::toQuizDTO)
                .collect(Collectors.toList());
    }

    public QuizDTO updateQuiz(Long id, QuizDTO dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.QUIZ_NOT_FOUND));
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setCategory(dto.getCategory());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        return toQuizDTO(quizRepository.save(quiz));
    }

    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new RuntimeException(AppConstants.QUIZ_NOT_FOUND);
        }
        quizRepository.deleteById(id);
    }

    // ── Question CRUD ────────────────────────────────────────────────────────────

    public QuestionDTO addQuestion(QuestionDTO dto) {
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new RuntimeException(AppConstants.QUIZ_NOT_FOUND));
        Question question = new Question(
                dto.getQuestionText(), dto.getOptionA(), dto.getOptionB(),
                dto.getOptionC(), dto.getOptionD(), dto.getCorrectAnswer(),
                dto.getMarks(), quiz);
        return toQuestionDTO(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public QuestionDTO getQuestionById(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.QUESTION_NOT_FOUND));
        return toQuestionDTO(q);
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId)
                .stream()
                .map(this::toQuestionDTO)
                .collect(Collectors.toList());
    }

    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(AppConstants.QUESTION_NOT_FOUND));
        q.setQuestionText(dto.getQuestionText());
        q.setOptionA(dto.getOptionA());
        q.setOptionB(dto.getOptionB());
        q.setOptionC(dto.getOptionC());
        q.setOptionD(dto.getOptionD());
        q.setCorrectAnswer(dto.getCorrectAnswer());
        q.setMarks(dto.getMarks());
        return toQuestionDTO(questionRepository.save(q));
    }

    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new RuntimeException(AppConstants.QUESTION_NOT_FOUND);
        }
        questionRepository.deleteById(id);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private QuizDTO toQuizDTO(Quiz quiz) {
        return new QuizDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getCategory(),
                quiz.getTimeLimitMinutes(),
                quiz.getQuestions().size()
        );
    }

    private QuestionDTO toQuestionDTO(Question q) {
        return new QuestionDTO(
                q.getId(),
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                q.getCorrectAnswer(),
                q.getMarks(),
                q.getQuiz().getId()
        );
    }
}
