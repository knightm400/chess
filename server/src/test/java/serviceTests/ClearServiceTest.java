package serviceTests;


import dataAccess.DataAccessException;
import dataAccess.IUserDataAccess;
import dataAccess.UserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private UserDataAccess userDataAccess;
    private ClearService clearService;
    @BeforeEach
    public void setUp() {
        userDataAccess = new UserDataAccess();
        clearService = new ClearService(userDataAccess);
    }

    @Test
    public void clearAllData_success() throws DataAccessException {
        userDataAccess.insertUser(new UserData("testUser", "password", "email@example.com"));
        assertEquals(1, userDataAccess.getAllUsers().size());
        clearService.clearAllData();
        assertEquals(0, userDataAccess.getAllUsers().size());
    }
}
