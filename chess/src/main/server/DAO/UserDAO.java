package server.DAO;

import server.MyServerException;
import server.models.User;

import java.util.HashMap;

public class UserDAO extends DAO{
    HashMap<String, User> users = new HashMap<String, User>();
    private static UserDAO single_instance = null;
    public static synchronized UserDAO getInstance(){
        if (single_instance == null)
            single_instance = new UserDAO();

        return single_instance;
    }

    public void insert(String name, String password, String email) {
        if (name.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        if (users.containsKey(name)) {
            throw new MyServerException("already taken", 403);
        }

        users.put(name, new User(name, password, email));
    }

    public User find(String name, String password) {
        if (name.isEmpty() || password.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        User foundUser = users.get(name);
        // foundUser check comes first as a guard against illegal dereferencing
        if (foundUser == null || !password.equals(foundUser.getPassword())) {
            throw new MyServerException("unauthorized", 401);
        }

        return foundUser;
    }
    @Override
    public void clear() {
        users.clear();
    }
}
