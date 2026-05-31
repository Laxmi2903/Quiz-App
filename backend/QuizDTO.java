package com.quizapp.dto;

public class QuizDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private int timeLimitMinutes;
    private int totalQuestions;

    public QuizDTO() {}

    public QuizDTO(Long id, String title, String description,
                   String category, int timeLimitMinutes, int totalQuestions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.timeLimitMinutes = timeLimitMinutes;
        this.totalQuestions = totalQuestions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public void setTimeLimitMinutes(int timeLimitMinutes) { this.timeLimitMinutes = timeLimitMinutes; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
}
