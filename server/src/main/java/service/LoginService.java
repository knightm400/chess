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

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = userDataAccess.validateUser(request.username(), request.password());
        if (user == null) {
            throw new DataAccessException("Invalid username or password.");
        }

        AuthData authData = new AuthData(user.username(), generateAuthToken());
        authDataAccess.insertAuth(authData);

        return new LoginResult(authData.username(), authData.authToken());
    }

    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}
