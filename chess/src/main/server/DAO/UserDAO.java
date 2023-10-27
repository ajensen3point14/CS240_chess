package server.DAO;

import server.MyServerException;
import server.models.User;

import java.util.HashMap;

/**
 * Interactions between the users and the database will occur here.
 * For now, the database is just a hashMap
 */
public class UserDAO implements DAO{
    HashMap<String, User> users = new HashMap<String, User>();

    // Singleton pattern for users
    private static UserDAO single_instance = null;

    public static synchronized UserDAO getInstance(){
        if (single_instance == null)
            single_instance = new UserDAO();

        return single_instance;
    }

    /**
     * Insert a user into the database. Names must be unique, so throw an error if a duplicate
     * is found
     * @param name string name identifier. This must be unique
     * @param password string password to use at login
     * @param email string email. This parameter is never used
     * @throws MyServerException bad request if any parameter is not given
     * @throws MyServerException already taken if the user is already in the database
     */
    public void insert(String name, String password, String email) {
        if (name == null || name.isEmpty()
                || password == null ||password.isEmpty()
                || email == null ||email.isEmpty()) {
            throw new MyServerException("bad request", 400);
        }
        if (users.containsKey(name)) {
            throw new MyServerException("already taken", 403);
        }

        users.put(name, new User(name, password, email));
    }

    /**
     * Find a specific user in the database. If none is found, return "unauthorized".
     * Use the username and password key-value pair to search the HashMap
     * @param name string username
     * @param password string password
     * @throws MyServerException bad request if there is no provided user to search for
     * @throws MyServerException unauthorized if there is no returned user
     * @return the found user
     */
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

    /**
     * Clear the user database
     */
    @Override
    public void clear() {
        users.clear();
    }
}
