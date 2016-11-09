package matrians.instapaysam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import matrians.instapaysam.Schemas.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
    Team Matrians
**/

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextInputEditText email = (TextInputEditText) findViewById(R.id.login_id);
        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle("Login to InstaPay");
        }

        if ((view = findViewById(R.id.btn_login)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    User user = new User(true);
                    user.email = email.getText().toString();
                    user.password = user.hashPassword(
                            password.getText().toString(), email.getText().toString());

                    View view;
                    if ( (view = getCurrentFocus()) != null ) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    Call<User> call = Server.connect().loginUser(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {

                            Log.d("LOGIN RESPONSE", response.message() + ' ' +
                                    response.code() + ' ' + response.body().success + ' ' +
                                    response.body().email + ' ' + response.body().password);
                            if (response.body().success) {
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putInt(getString(R.string.login_status), 1);
                                editor.putString(getString(R.string.login_id), response.body().email);
                                editor.apply();
                                setResult(1);
                                finish();
                            } else Snackbar.make((View)v.getParent(),
                                    "Invalid login. Try again.", Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make(v.getRootView(), "Login failed. Try again.", Snackbar.LENGTH_LONG).show();
                            Log.d("RETROFIT ERROR", t.toString());
                        }
                    });
                }
            });
        }
        if ((view = findViewById(R.id.link_register)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(), RegisterActivity.class));
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
