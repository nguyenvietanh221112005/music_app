package com.example.music_app.model;

public class UserUpdate {

    private String ten;
    private String email;
    private String password;

    public UserUpdate() {}

    public UserUpdate(String ten, String email, String password) {
        this.ten = ten;
        this.email = email;
        this.password = password;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}