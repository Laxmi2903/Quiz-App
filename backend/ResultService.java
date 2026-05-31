package com.quizapp.service;

import com.quizapp.model.Question;
import com.quizapp.model.Quiz;
import com.quizapp.model.Result;
import com.quizapp.model.User;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.ResultRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.util.AppConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResultService {

    private final ResultRepository resultRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public ResultService(ResultRepository resultRepository,
                         QuizRepository quizRepository,
                         QuestionRepository questionRepository,
                         UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submit a quiz attempt.
     *
     * @param username   the authenticated user's username
     * @param quizId     the quiz being attempted
     * @param answers    map of questionId -> chosen answer ("A" / "B" / "C" / "D")
     * @return the persisted Result
     */
    public Result submitQuiz(String username, Long quizId, Map<Long, String> answers) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException(AppConstants.UNAUTHORIZED));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException(AppConstants.QUIZ_NOT_FOUND));

        List<Question> questions = questionRepository.findByQuizId(quizId);

        int score = 0;
        int totalMarks = 0;
        int correctAnswers = 0;

        for (Question q : questions) {
            totalMarks += q.getMarks();
            String submitted = answers.get(q.getId());
            if (submitted != null && submitted.equalsIgnoreCase(q.getCorrectAnswer())) {
                score += q.getMarks();
                correctAnswers++;
            }
        }

        Result result = new Result(user, quiz, score, totalMarks,
                correctAnswers, questions.size(), LocalDateTime.now());
        return resultRepository.save(result);
    }

    @Transactional(readOnly = true)
    public List<Result> getResultsByUser(Long userId) {
        return resultRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Result> getResultsByQuiz(Long quizId) {
        return resultRepository.findByQuizId(quizId);
    }

    @Transactional(readOnly = true)
    public Result getResultById(Long resultId) {
        return resultRepository.findById(resultId)
                .orElseThrow(() -> new RuntimeException(AppConstants.RESULT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Result> getResultsByUserAndQuiz(Long userId, Long quizId) {
        return resultRepository.findByUserIdAndQuizId(userId, quizId);
    }
}
