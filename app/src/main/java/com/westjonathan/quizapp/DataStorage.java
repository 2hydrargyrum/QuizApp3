package com.westjonathan.quizapp;

public class DataStorage {
    private String name;
    public DataStorage(){
    }
    public DataStorage(String nombre){
        name = nombre;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return name;
    }
}