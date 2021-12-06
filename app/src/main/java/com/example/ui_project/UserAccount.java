package com.example.ui_project;

//사용자 계정 정보 객체
public class UserAccount {

    private String idToken; //Firebase Uid(고유 키값)
    private String emailId;
    private String password;

    public UserAccount() { }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
