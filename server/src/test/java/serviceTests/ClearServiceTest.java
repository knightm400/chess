package serviceTests;


import dataAccess.IUserDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    static final ClearService service = new ClearService(new MemoryDataAcess());

    @BeforeEach
    void clear() {
        service.clearAllData();
    }

    @Test
    void clearAllData() {
        service.addData(new UserData("user1", "pass1", "email1"));
        service.addData(new UserData("user2", "pass2", "email2"));
        service.clearAllData();;
        assertTrue(service.isDataEmpty());
    }
}
