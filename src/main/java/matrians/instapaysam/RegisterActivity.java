package matrians.instapaysam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.HashSet;

import matrians.instapaysam.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class RegisterActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

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

        final TextInputEditText firstName = (TextInputEditText) findViewById(R.id.fname);
        if (firstName != null) {
            firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
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
        }

        final TextInputEditText lastName = (TextInputEditText) findViewById(R.id.lname);
        if (lastName != null) {
            lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
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
        }

        final TextInputEditText email = (TextInputEditText) findViewById(R.id.email);
        if (email != null) {
            email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!Patterns.EMAIL_ADDRESS.matcher(
                                editText.getText().toString()).matches()) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidEmail);
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
        }

        final TextInputEditText userName = (TextInputEditText) findViewById(R.id.uname);
        if (userName != null) {
            userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
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
        }

        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);
        if (password != null) {
            password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!Utils.validatePassword(password.getText().toString())) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidPassword);
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
        }

        final TextInputEditText cPassword = (TextInputEditText) findViewById(R.id.cpassword);
        if (cPassword != null) {
            cPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (password != null && !password.getText().toString().
                                equals(editText.getText().toString())) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errPasswordsMismatch);
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
        }

        final TextInputEditText address = (TextInputEditText) findViewById(R.id.address);
        if (address != null) {
            address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
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
        }

        final TextInputEditText postalCode = (TextInputEditText) findViewById(R.id.pcode);
        if (postalCode != null) {
            postalCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
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
        }

        final TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone);
        if (phone != null) {
            phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    TextInputEditText editText = (TextInputEditText) v;
                    if (!hasFocus) {
                        if (editText.getText().length() == 0) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_light));
                        } else if (!Patterns.PHONE.matcher(
                                editText.getText().toString()).matches()) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidPhone);
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
        }

        Button registerButton;
        if ((registerButton = (Button) findViewById(R.id.btn_register)) != null) {
            registerButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {

                    boolean valid = true;

                    HashSet<TextInputEditText> emptyFields = Utils.checkEmptyFields(
                            firstName, lastName, email, userName, password,
                            cPassword, address, postalCode, phone);
                    if (!emptyFields.isEmpty()) {
                        valid = false;
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errEmptyField);
                        for (TextInputEditText editText : emptyFields) {
                            editText.requestFocus();
                            editText.setHintTextColor(ContextCompat.getColor(
                                    v.getContext(), android.R.color.holo_red_dark));
                        }
                    }

                    if (!Utils.validateEmail(email.getText().toString())) {
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidEmail);
                        email.requestFocus();
                        email.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }
                    if (!Utils.validatePassword(password.getText().toString())) {
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidPassword);
                        password.requestFocus();
                        password.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }
                    if (!password.getText().toString().equals(cPassword.getText().toString())) {
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errPasswordsMismatch);
                        password.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        cPassword.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }
                    if (!Utils.validatePhone(phone.getText().toString())) {
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errInvalidPhone);
                        phone.setTextColor(ContextCompat.getColor(
                                v.getContext(), android.R.color.holo_red_dark));
                        valid = false;
                    }

                    User user = new User(false);
                    if (!valid) {
                        Utils.snackUp(findViewById(R.id.rootView), R.string.errRedFields);
                        return;
                    } else {
                        user.firstName = firstName.getText().toString();
                        user.lastName = lastName.getText().toString();
                        user.userName = userName.getText().toString();
                        user.email = email.getText().toString();
                        user.password = password.getText().toString();
                        user.homeAddress = address.getText().toString();
                        user.postalCode = postalCode.getText().toString();
                        user.phone = phone.getText().toString();
                    }

                    dialog = Utils.showProgress(v.getContext(), R.string.dialogRegistering);

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
                            } else try {
                                Utils.snackUp(findViewById(R.id.rootView),
                                        response.errorBody().string(), R.string.keyError);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            dialog.dismiss();
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errRegisterFail);
                            Log.d(TAG, t.toString());
                        }
                    });
                }
            });
        }
    }
}
