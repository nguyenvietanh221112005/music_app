package com.example.music_app.model;

import java.util.Date;

public class Users {
    private boolean success;

    private Integer user_id;

    private String ten ;

    private String email ;

    private String password ;


    public Users() {
    }
    public Users(String ten, String email, String password) {
        this.ten = ten;
        this.email = email;
        this.password = password;
    }

    public Users(Integer user_id,boolean success, String email, String password) {
        this.user_id = user_id;
        this.success = success;
        this.email = email;
        this.password = password;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getUser_id(){
        return user_id ;
    }
    public boolean isSuccess() {
        return success;
    }

    public String getTen() {
        return ten;
    }

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }
}