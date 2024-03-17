package com.example.scraps.DBModels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Households {
    public String houseID, email;
    public HashMap<String, Users> users;
    private Users adminUser;

    public Households() {}

    public Households(String houseID, String email) {
        this.houseID = houseID;
        this.email = email;
        this.users = new HashMap<>();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("houseID", houseID);
        // Add other properties to the map if needed
        return result;
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
    public String getHouseEmail() {return email;}
    public Users getAdminUser() {
        return adminUser;
    }
    public void setAdminUser(Users adminUser) {
        this.adminUser = adminUser;
    }

    public List<Users> getUsersByEmail(String userEmail) {
        List<Users> usersWithEmail = new ArrayList<>();
        for (Users user : users.values()) {
            if (user.getEmail().equals(userEmail)) {
                usersWithEmail.add(user);
            }
        }
        return usersWithEmail;
    }

}
