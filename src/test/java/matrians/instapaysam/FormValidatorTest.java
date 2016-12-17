package matrians.instapaysam;

import android.support.v7.app.AppCompatActivity;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Team Matrians
 * Test form validation
 */
public class FormValidatorTest extends AppCompatActivity {

    private String email, password;

    @Before
    public void setup() {
        email = "test@mail.com";
        password = "@test123";
    }

    @Test
    public void validateEmail() {
        Assert.assertTrue(Utils.validateEmail(email));
    }

    @Test
    public void validatePassword() {
        Assert.assertTrue(Utils.validatePassword(password));
    }
}
