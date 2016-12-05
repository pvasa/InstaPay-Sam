package matrians.instapaysam;

import android.support.v7.app.AppCompatActivity;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Team Matrians
 */
public class CardValidateTest extends AppCompatActivity {

    @Test
    public void validatePassword() {
        assertTrue(Utils.validatePassword("1337ub#r"));
    }

    @Test
    public void validateEmail() {
        assertTrue(Utils.validateEmail("pv.ryan14@gmail.com"));
    }

    @Test
    public void validatePhone() {
        assertTrue(Utils.validatePhone("6476189379"));
    }
}
