package models;

/**
 * Creates a unique user to play on the chess server
 */
public class User {
    String username;
    String password;
    String email;

    /**
     *  Create a user with their required information
     * @param username a unique username for the server
     * @param password a string password
     * @param email a string email
     */
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
