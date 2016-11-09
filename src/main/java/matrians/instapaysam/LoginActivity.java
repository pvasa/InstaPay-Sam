package matrians.instapaysam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

        final TextInputEditText login_id = (TextInputEditText) findViewById(R.id.login_id);
        if (login_id != null)
            login_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.error_empty_field), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);
        if (password != null)
            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.error_empty_field), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle("Login to InstaPay");
        }

        if ((view = findViewById(R.id.btn_login)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    boolean valid = true;

                    User user = new User(true);
                    if ( (user.user_name = user.email = login_id.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.error_field_empty, Toast.LENGTH_SHORT).show();
                        login_id.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        login_id.requestFocus();
                        valid = false;
                    }
                    if ( password.getText().length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.error_field_empty, Toast.LENGTH_SHORT).show();
                        login_id.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        login_id.requestFocus();
                        valid = false;
                    }

                    if (!valid) {
                        Toast.makeText(v.getContext(), R.string.error_red_fields, Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        user.password = user.hashPassword(
                                password.getText().toString(), login_id.getText().toString());
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
