package matrians.instapaysam.Schemas;

import android.support.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 Team Matrians
 */

public class User {
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String lor;
    public boolean success;
    public String first_name;
    public String last_name;
    public String email;
    public String user_name;
    public String password;
    public String home_addr;
    public String postal_code;
    public String phone;

    public User(boolean login) {
        lor = login ? "login" : "register";
    }

    @Nullable
    public String hashPassword(String password, String salt) {

        int iterations = 10000;
        int keyLength = 512;
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);

        PBEKeySpec spec = new PBEKeySpec(passwordChars, saltBytes, iterations, keyLength);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            StringBuilder passwordHash = new StringBuilder();
            for (Byte aByte : hash) {
                passwordHash.append(String.format("%02x", aByte));
            }
            return passwordHash.toString();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
