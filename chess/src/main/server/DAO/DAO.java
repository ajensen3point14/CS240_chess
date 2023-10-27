package server.DAO;

import dataAccess.DataAccessException;

public class DAO {
    private static DAO single_instance = null;
    public static synchronized DAO getInstance(){
        if (single_instance == null)
            single_instance = new DAO();

        return single_instance;
    }
    public void clear() throws DataAccessException {

    }
}
