package com.example.scraps.DBModels;
import java.util.HashMap;

public class Households {
    public String houseID;
    public HashMap<String, Users> users;

    public Households() {}

    public Households(String houseID) {
        this.houseID = houseID;
        this.users = new HashMap<>();
    }

    public String getHouseID() {
        return houseID;
    }

    public HashMap<String, Users> getUsers() {
        return users;
    }

    public void setAddress(String address) {
        this.houseID = houseID;
    }

    public void setUsers(HashMap<String, Users> users) {
        this.users = users;
    }
}
