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
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Invalid registration information provided"); // Use specific code for bad request
        }

        if (userDataAccess.getUser(request.username()) != null) {
            throw new DataAccessException("Username already taken");
        }

        UserData newUser = new UserData(request.username(), request.password(), request.email());
        userDataAccess.insertUser(newUser);

        String authToken = authDataAccess.generateAuthToken();
        authDataAccess.insertAuth(new AuthData(authToken, request.username()));

        return new RegisterResult(request.username(), authToken);
    }



    private String generateAuthToken() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }
}