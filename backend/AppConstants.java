package com.quizapp.util;

public class AppConstants {

    private AppConstants() {}

    // JWT
    public static final long JWT_EXPIRATION_MS = 86_400_000L; // 24 hours

    // Roles
    public static final String ROLE_USER  = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // Pagination defaults
    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE   = 10;

    // Success / Error messages
    public static final String USER_REGISTERED_SUCCESS = "User registered successfully.";
    public static final String USER_ALREADY_EXISTS     = "Username or email already in use.";
    public static final String QUIZ_NOT_FOUND          = "Quiz not found.";
    public static final String QUESTION_NOT_FOUND      = "Question not found.";
    public static final String RESULT_NOT_FOUND        = "Result not found.";
    public static final String UNAUTHORIZED             = "Unauthorized access.";
}
