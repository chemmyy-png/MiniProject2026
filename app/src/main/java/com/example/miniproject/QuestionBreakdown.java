package com.example.miniproject;

import java.io.Serializable;

public class QuestionBreakdown implements Serializable {
    private String number;
    private String category;
    private String userAnswer;
    private String correctAnswer;
    private boolean isCorrect;

    public QuestionBreakdown(String number, String category, String userAnswer, String correctAnswer, boolean isCorrect) {
        this.number = number;
        this.category = category;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
    }

    public String getNumber() {
        return number;
    }

    public String getCategory() {
        return category;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
