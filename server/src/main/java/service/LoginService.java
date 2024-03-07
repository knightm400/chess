package service;

import dataAccess.AuthDataAccess;
import dataAccess.DataAccessException;
import dataAccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import com.google.gson.Gson;

public class LoginService {

    private final AuthDataAccess authDataAccess;
    private final UserDataAccess userDataAccess;

    public LoginService(AuthDataAccess authDataAccess, UserDataAccess userDataAccess) {
        this.authDataAccess = authDataAccess;
        this.userDataAccess = userDataAccess;
    }

    public LoginResult login(LoginRequest request) {
        try {
            UserData user = userDataAccess.validateUser(request.username(), request.password());
            AuthData authData = new AuthData(user.username(), generateAuthToken());
            authDataAccess.insertAuth(authData);
            return new LoginResult(user.username(), authData.authToken());
        } catch (DataAccessException e) {
            return null;
        }
    }

    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}
