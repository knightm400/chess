package model;

import com.google.gson.Gson;

public class UserData {
    private String username;
    private String password;
    private String email;

    public UserData() {

    }

    public UserData(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public static UserData fromJson(String json) {
        return new Gson().fromJson(json, UserData.class);
    }


}
