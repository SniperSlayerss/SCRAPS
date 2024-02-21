package com.example.scraps.DBModels;
import java.util.HashMap;

public class Households {
    public String houseID;
    public HashMap<String, Users> users;
    private Users adminUser;

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

    public Users getAdminUser() {
        return adminUser;
    }
    public void setAdminUser(Users adminUser) {
        this.adminUser = adminUser;
    }
}
