package service;

import dataAccess.AuthDataAccess;
import dataAccess.UserDataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;


public class RegisterService {
    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;

    public RegisterService(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (userDataAccess.getUser(request.username()) != null) {
            throw new DataAccessException("Username already taken");
        }
        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDataAccess.insertUser(newUser);

        String authToken = generateAuthToken();
        AuthData authData = new AuthData(authToken, request.username());
        authDataAccess.insertAuth(authData);

        return new RegisterResult(request.username(), authToken);
    }

    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}