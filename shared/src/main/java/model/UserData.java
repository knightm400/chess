package model;

import com.google.gson.Gson;

public record UserData(String username, String password, String email) {
    public String toJson() {
        return new Gson().toJson(this);
    }

    public static UserData fromJson(String json) {
        return new Gson().fromJson(json, UserData.class);
    }
}
