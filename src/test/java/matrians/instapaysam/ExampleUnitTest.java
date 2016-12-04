package matrians.instapaysam;

import org.junit.Test;

import matrians.instapaysam.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void loginTest() throws Exception {
        User user = new User(true);
        user.email = "p.v@ce.ca";
        user.password = "1337ub#r";
        Call<User> call = Server.connect().loginUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                assertEquals(400, response.code());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}