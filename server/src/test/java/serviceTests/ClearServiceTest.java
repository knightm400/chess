package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.UserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class ClearServiceTest {
    private ClearService clearService;
    private UserDataAccess userDataAccess;

    @BeforeEach
    public void setUp() {
        userDataAccess = new UserDataAccess();
        clearService = new ClearService(userDataAccess);
    }

    @Test
    public void testClearAll() throws DataAccessException {
        userDataAccess.insertUser(new UserData("testUser", "password", "email@example.com"));
        assertFalse(userDataAccess.isDatabaseEmpty());
        clearService.clearAll();
        assertTrue(userDataAccess.isDatabaseEmpty());
    }
}