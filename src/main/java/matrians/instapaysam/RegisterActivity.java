package matrians.instapaysam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Pattern;

import matrians.instapaysam.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class RegisterActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private final String PASSWORD_REGEX =
            "(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@!%*#?&])[A-Za-z\\d$@!%*#?&]{8,}";
    private ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            setResult(2);
            finish();
        }
    }

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

        FloatingActionButton fab;
        if ((fab = (FloatingActionButton) findViewById(R.id.fab)) != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(v.getContext(), LoginActivity.class), HomeActivity.CODE_LOGIN);
                }
            });
        }

        final TextInputEditText first_name = (TextInputEditText) findViewById(R.id.fname);
        if (first_name != null)
            first_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
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

        final TextInputEditText last_name = (TextInputEditText) findViewById(R.id.lname);
        if (last_name != null)
            last_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
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

        final TextInputEditText email = (TextInputEditText) findViewById(R.id.email);
        if (email != null)
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                editText.getText().toString()).matches()) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errInvalidEmail), Toast.LENGTH_SHORT).show();
                            editText.setTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        final TextInputEditText user_name = (TextInputEditText) findViewById(R.id.uname);
        if (user_name != null)
            user_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
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
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!(Pattern.compile(PASSWORD_REGEX)).
                                matcher(password.getText().toString()).matches()) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errInvalidPassword), Toast.LENGTH_SHORT).show();
                            editText.setTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        final TextInputEditText c_password = (TextInputEditText) findViewById(R.id.cpassword);
        if (c_password != null)
            c_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (password != null && !password.getText().toString().
                                equals(editText.getText().toString())) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errPasswordsMismatch), Toast.LENGTH_SHORT).show();
                            password.setTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                            editText.setTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        final TextInputEditText address = (TextInputEditText) findViewById(R.id.address);
        if (address != null)
            address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
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

        final TextInputEditText postal_code = (TextInputEditText) findViewById(R.id.pcode);
        if (postal_code != null)
            postal_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
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

        final TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone);
        if (phone != null)
            phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errEmptyField), Toast.LENGTH_SHORT).show();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!Patterns.PHONE.matcher(
                                editText.getText().toString()).matches()) {
                            Toast.makeText(v.getContext(), getResources().getString(
                                    R.string.errInvalidPhone), Toast.LENGTH_SHORT).show();
                            editText.setTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                        }
                    } else {
                        editText.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorSecondaryText));
                        editText.setTextColor(ContextCompat.getColor(
                                v.getContext(), R.color.colorPrimaryText));
                    }
                }
            });

        Button registerButton;
        if ((registerButton = (Button) findViewById(R.id.btn_register)) != null) {
            registerButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    dialog = Utils.showProgress(v.getContext(), getString(R.string.processRegistering));

                    boolean valid = true;

                    User user = new User(false);
                    if ( (user.firstName = first_name.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        first_name.requestFocus();
                        first_name.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    }

                    if ( (user.lastName = last_name.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        last_name.requestFocus();
                        last_name.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    }

                    if ( (user.email = email.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        email.requestFocus();
                        email.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email).matches()) {
                        Toast.makeText(v.getContext(),
                                R.string.errInvalidEmail, Toast.LENGTH_SHORT).show();
                        email.requestFocus();
                        email.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }

                    if ( (user.userName = user_name.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        user_name.requestFocus();
                        user_name.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    }

                    if ( password.getText().length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        password.requestFocus();
                        password.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    } else if (!(Pattern.compile(PASSWORD_REGEX)).
                            matcher(password.getText().toString()).matches()) {
                        Toast.makeText(v.getContext(),
                                R.string.errInvalidPassword, Toast.LENGTH_SHORT).show();
                        password.requestFocus();
                        password.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }
                    if ( c_password.getText().length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        c_password.requestFocus();
                        c_password.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    } else if (!password.getText().toString().equals(c_password.getText().toString())) {
                        Toast.makeText(v.getContext(),
                                R.string.errPasswordsMismatch,
                                Toast.LENGTH_SHORT).show();
                        password.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        c_password.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }

                    if ( (user.homeAddress = address.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        address.requestFocus();
                        address.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    }

                    if ( (user.postalCode = postal_code.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        postal_code.requestFocus();
                        postal_code.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    }

                    if ( (user.phone = phone.getText().toString()).length() == 0 ) {
                        Toast.makeText(v.getContext(),
                                R.string.errFieldEmpty, Toast.LENGTH_SHORT).show();
                        phone.requestFocus();
                        phone.setHintTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_light));
                        valid = false;
                    } else if (!Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
                        Toast.makeText(v.getContext(),
                                R.string.errInvalidPhone,
                                Toast.LENGTH_SHORT).show();
                        phone.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }

                    if (!valid) {
                        Toast.makeText(v.getContext(), R.string.errRedFields, Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        user.password = password.getText().toString();
                    }

                    Call<User> call = Server.connect().createUser(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            dialog.dismiss();
                            if (200 == response.code()) {
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putInt(getString(R.string.prefLoginStatus), LoginActivity.STATUS_LOGGED_OUT);
                                editor.putString(getString(R.string.prefEmail), response.body().email);
                                editor.apply();
                                setResult(1);
                                finish();
                            } else Snackbar.make((View) v.getParent(),
                                    response.body().err, Snackbar.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            dialog.dismiss();
                            Snackbar.make((View) v.getParent(),
                                    R.string.errRegisterFail, Snackbar.LENGTH_LONG).show();
                            Log.d(TAG, t.toString());
                        }
                    });
                }
            });
        }
    }
}
