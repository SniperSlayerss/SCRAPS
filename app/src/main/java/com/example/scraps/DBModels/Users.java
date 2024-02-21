package com.example.scraps.DBModels;

public class Users {
    private String username, password, email;
    private int houseID;

    public Users() {}

    public Users(String username, String email, String password, int houseID) {
        this.username = username;
        this.email = email;
        this.houseID = houseID;
        this.password = password;

    }

    public String getUsername() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public int getHouseID() {return houseID;}
    public void setHouseID(int houseID) {this.houseID = houseID;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
