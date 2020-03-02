package com.westjonathan.quizapp;

public class QuizAttempt {
    private String name;
    private int time;
    private int score;
    private String id;
    public QuizAttempt() {
        // Default constructor required for calls to DataSnapshot.getValue(QuizAttempt.class)
    }

    public QuizAttempt(String id, String name, int score, int time) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.score = score;
    }
    public String getName(){ return name; }
    public int getTime(){ return time; }
    public int getScore(){ return score; }
}