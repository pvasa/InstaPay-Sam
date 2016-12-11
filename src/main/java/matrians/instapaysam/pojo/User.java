package matrians.instapaysam.pojo;

/**
 * Team Matrians
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class User {
    private char lor;
    private String _id;
    public String firstName;
    public String lastName;
    public String email;
    public String userName;
    public String password;
    public String homeAddress;
    public String postalCode;
    public String phone;

    private User(){}

    public User(boolean login) {
        lor = login ? 'l' : 'r';
    }

    public String getId() {
        return _id;
    }
}
