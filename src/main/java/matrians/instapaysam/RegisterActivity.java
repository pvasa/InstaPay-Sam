package matrians.instapaysam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Objects;

import matrians.instapaysam.Schemas.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 Team Matrians
 **/

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextInputEditText first_name = (TextInputEditText) findViewById(R.id.fname);
        final TextInputEditText last_name = (TextInputEditText) findViewById(R.id.lname);
        final TextInputEditText email = (TextInputEditText) findViewById(R.id.email);
        final TextInputEditText user_name = (TextInputEditText) findViewById(R.id.uname);
        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);
        final TextInputEditText c_password = (TextInputEditText) findViewById(R.id.cpassword);
        final TextInputEditText address = (TextInputEditText) findViewById(R.id.address);
        final TextInputEditText postal_code = (TextInputEditText) findViewById(R.id.pcode);
        final TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone);

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle("Create an account");
        }

        if ((view = findViewById(R.id.btn_register)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    User user = new User(false);
                    user.first_name = first_name.getText().toString();
                    user.last_name = last_name.getText().toString();
                    user.email = email.getText().toString();
                    user.user_name = user_name.getText().toString();
                    if (!Objects.equals(password.getText().toString(),
                            c_password.getText().toString())) {
                        Snackbar.make(v.getRootView(),
                                "Passwords do not match.",
                                Snackbar.LENGTH_LONG).show();
                        password.requestFocus();
                        return;
                    }
                    user.password = user.hashPassword(
                            password.getText().toString(), email.getText().toString());
                    user.home_addr = address.getText().toString();
                    user.postal_code = postal_code.getText().toString();
                    user.phone = phone.getText().toString();

                    Call<User> call = Server.connect().createUser(user);
                    Log.d("REGISTER CALL", call.toString());
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            Log.d("REGISTER RES", "Response received: " + response.toString());
                            if (response.body().success) {
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putInt(getString(R.string.login_status), 1);
                                editor.putString(getString(R.string.login_id), response.body().email);
                                editor.apply();
                                setResult(1);
                                finish();
                            } else Snackbar.make(v.getRootView(),
                                    "Invalid login. Try again.", Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(v, "Registration failed. Try again.", Snackbar.LENGTH_LONG).show();
                            Log.d("RETROFIT ERROR", t.toString());
                        }
                    });
                }
            });
        }
        if ((view = findViewById(R.id.link_login)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(), LoginActivity.class));
                    finish();
                }
            });
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
