package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessInterface;

public class ClearService {
    private DataAccessInterface dao;

    public ClearService() {
        dao = new DataAcess();
    }

    public void clearData() {
        dao.clearAll();
    }
}
