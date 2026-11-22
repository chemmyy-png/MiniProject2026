package com.example.miniproject;

import java.io.Serializable;

public class QuizResultModel implements Serializable {
    private String studentName;
    private int score;
    private int totalQuestions;

    public QuizResultModel(String studentName, int score, int totalQuestions) {
        this.studentName = studentName;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    public String getStudentName() {
        return (studentName == null || studentName.trim().isEmpty()) ? "Student" : studentName;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getPercentage() {
        if (totalQuestions == 0) return 0;
        return (score * 100) / totalQuestions;
    }

    public String getPercentageText() {
        return getPercentage() + "%";
    }

    public String getScoreRatioText() {
        return score + " / " + totalQuestions + " correct";
    }

    public int getCorrectCount() {
        return score;
    }

    public int getIncorrectCount() {
        return totalQuestions - score;
    }

    public String getAchievementText() {
        int percent = getPercentage();
        if (percent >= 80) {
            return "🏆  Great work!";
        } else if (percent >= 50) {
            return "👏  Good effort!";
        } else {
            return "💪  Keep practicing!";
        }
    }
}
