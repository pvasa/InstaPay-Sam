package matrians.instapaysam;

import android.support.v7.app.AppCompatActivity;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Team Matrians
 */
public class FormValidatorTest extends AppCompatActivity {

    private String email, password, data;

    @Before
    public void setup() {
        email = "test@mail.com";
        password = "@test123";
        data = "Secure data to be encrypted.";
    }

    @Test
    public void areNotEmpty() {
        Secure secure = Secure.getDefault(password, email, new byte[16]);
        assertEquals(null, secure != null ? secure.encryptOrNull(data) : null);
    }
}
