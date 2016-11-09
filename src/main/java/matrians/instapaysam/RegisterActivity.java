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
import android.telephony.PhoneNumberUtils;
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

public class RegisterActivity extends AppCompatActivity {

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final TextInputEditText first_name;
        if ( (first_name = (TextInputEditText) findViewById(R.id.fname)) != null &&
                first_name.getText().length() == 0) {
            Snackbar.make((View)first_name.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            first_name.requestFocus();
        }

        final TextInputEditText last_name;
        if ( (last_name = (TextInputEditText) findViewById(R.id.lname)) != null &&
                last_name.getText().length() == 0) {
            Snackbar.make((View)last_name.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            last_name.requestFocus();
        }

        final TextInputEditText email;
        if ( (email = (TextInputEditText) findViewById(R.id.email)) != null &&
                email.getText().length() != 0)
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (!Utils.isValidEmail(email.getText().toString())) {
                            Snackbar.make((View)v.getParent(),
                                    R.string.snack_invalid_email, Snackbar.LENGTH_LONG).show();
                            v.requestFocus();
                        }
                    }
                }
            });
        else {
            Snackbar.make((View)email.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            email.requestFocus();
        }

        final TextInputEditText user_name;
        if ( (user_name = (TextInputEditText) findViewById(R.id.uname)) != null &&
                user_name.getText().length() == 0) {
            Snackbar.make((View)user_name.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            user_name.requestFocus();
        }

        final TextInputEditText password;
        if ( (password = (TextInputEditText) findViewById(R.id.password)) != null &&
                password.getText().length() != 0)
            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (!Utils.isValidPassword(password.getText().toString())) {
                            Snackbar.make((View)v.getParent(),
                                    R.string.snack_invalid_password, Snackbar.LENGTH_LONG).show();
                            v.requestFocus();
                        }
                    }
                }
            });
        else {
            Snackbar.make((View)password.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            password.requestFocus();
        }

        final TextInputEditText c_password;
        if ( (c_password = (TextInputEditText) findViewById(R.id.cpassword)) != null &&
                c_password.getText().length() != 0)
            c_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (!c_password.getText().toString().equals(password.getText().toString())) {
                            Snackbar.make((View)v.getParent(),
                                    R.string.snack_passwords_unmatch, Snackbar.LENGTH_LONG).show();
                            v.requestFocus();
                        }
                    }
                }
            });
        else {
            Snackbar.make((View)c_password.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            c_password.requestFocus();
        }

        final TextInputEditText address;
        if ( (address = (TextInputEditText) findViewById(R.id.address)) != null &&
                address.getText().length() == 0) {
            Snackbar.make((View)address.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            address.requestFocus();
        }

        final TextInputEditText postal_code;
        if ( (postal_code = (TextInputEditText) findViewById(R.id.pcode)) != null &&
                postal_code.getText().length() == 0) {
            Snackbar.make((View)postal_code.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            postal_code.requestFocus();
        }

        final TextInputEditText phone;
        if ( (phone = (TextInputEditText) findViewById(R.id.phone)) != null &&
                phone.getText().length() != 0)
            phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!PhoneNumberUtils.isGlobalPhoneNumber(phone.getText().toString()))
                        Snackbar.make((View)v.getParent(),
                                getString(R.string.snack_invalid_phone),
                                Snackbar.LENGTH_LONG).show();
                    v.requestFocus();
                }
            });
        else {
            Snackbar.make((View)phone.getParent(),
                    R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
            phone.requestFocus();
        }

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.title_create_account));
        }

        if ((view = findViewById(R.id.btn_register)) != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    View view;
                    if ( (view = getCurrentFocus()) != null ) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    User user = new User(false);
                    if ( (user.first_name = first_name.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)first_name.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        first_name.requestFocus();
                        return;
                    }

                    if ( (user.last_name = last_name.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)last_name.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        last_name.requestFocus();
                        return;
                    }

                    if ( (user.email = email.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)email.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        email.requestFocus();
                        return;
                    }

                    if ( (user.user_name = user_name.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)user_name.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        user_name.requestFocus();
                        return;
                    }

                    if ( password.getText().length() == 0 ) {
                        Snackbar.make((View)password.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        password.requestFocus();
                        return;
                    } else if ( c_password.getText().length() == 0 ) {
                        Snackbar.make((View)c_password.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        c_password.requestFocus();
                        return;
                    } else if (!password.getText().toString().equals(c_password.getText().toString())) {
                        Snackbar.make((View)v.getParent(),
                                R.string.snack_passwords_unmatch,
                                Snackbar.LENGTH_LONG).show();
                        password.requestFocus();
                        return;
                    }
                    user.password = user.hashPassword(
                            password.getText().toString(), email.getText().toString());

                    if ( (user.home_addr = address.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)address.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        address.requestFocus();
                        return;
                    }

                    if ( (user.postal_code = postal_code.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)postal_code.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        postal_code.requestFocus();
                        return;
                    }

                    if ( (user.phone = phone.getText().toString()).length() == 0 ) {
                        Snackbar.make((View)phone.getParent(),
                                R.string.snack_field_empty, Snackbar.LENGTH_LONG).show();
                        phone.requestFocus();
                        return;
                    }

                    Call<User> call = Server.connect().createUser(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.body().success) {
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putInt(getString(R.string.login_status), 1);
                                editor.putString(getString(R.string.login_id), response.body().email);
                                editor.apply();
                                setResult(1);
                                finish();
                            } else Snackbar.make((View)v.getParent(),
                                    R.string.snack_invalid_login, Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Snackbar.make((View)v.getParent(),
                                    R.string.snack_register_fail, Snackbar.LENGTH_LONG).show();
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
