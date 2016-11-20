package matrians.instapaysam.Schemas;

/**
 * Team Matrians
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class User {
    private char lor;
    public boolean success;
    public String err;
    public String firstName;
    public String lastName;
    public String email;
    public String userName;
    public String password;
    public String homeAddr;
    public String postalCode;
    public String phone;

    private User(){}

    public User(boolean login) {
        lor = login ? 'l' : 'r';
        success = false;
        err = "none";
    }
}
