package matrians.instapaysam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
            firstName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        firstName.setError(getString(R.string.errEmptyField));
                    else firstName.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText lastName = (TextInputEditText) findViewById(R.id.lname);
        if (lastName != null) {
            lastName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        lastName.setError(getString(R.string.errEmptyField));
                    else lastName.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText email = (TextInputEditText) findViewById(R.id.email);
        if (email != null) {
            email.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        email.setError(getString(R.string.errEmptyField));
                    else if (!Patterns.EMAIL_ADDRESS.matcher(charSequence).matches())
                        email.setError(getString(R.string.errInvalidEmail));
                    else email.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText userName = (TextInputEditText) findViewById(R.id.uname);
        if (userName != null) {
            userName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        userName.setError(getString(R.string.errEmptyField));
                    else userName.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);
        if (password != null) {
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        password.setError(getString(R.string.errEmptyField));
                    else if (!Utils.validatePassword(charSequence.toString()))
                        password.setError(getString(R.string.errInvalidPassword));
                    else password.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText cPassword = (TextInputEditText) findViewById(R.id.cpassword);
        if (cPassword != null) {
            cPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        cPassword.setError(getString(R.string.errEmptyField));
                    else if (password != null &&
                            !(charSequence.toString()).equals(password.getText().toString()))
                        cPassword.setError(getString(R.string.errPasswordsMismatch));
                    else cPassword.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText address = (TextInputEditText) findViewById(R.id.address);
        if (address != null) {
            address.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        address.setError(getString(R.string.errEmptyField));
                    else address.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText postalCode = (TextInputEditText) findViewById(R.id.pcode);
        if (postalCode != null) {
            postalCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        postalCode.setError(getString(R.string.errEmptyField));
                    else postalCode.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        final TextInputEditText phone = (TextInputEditText) findViewById(R.id.phone);
        if (phone != null) {
            phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        phone.setError(getString(R.string.errEmptyField));
                    else if (!Patterns.PHONE.matcher(charSequence).matches())
                        phone.setError(getString(R.string.errInvalidPhone));
                    else phone.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        Button registerButton;
        if ((registerButton = (Button) findViewById(R.id.btn_register)) != null) {
            registerButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(
                                        v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } catch (Exception e) {e.printStackTrace();}

                    boolean valid = true;

                    HashSet<TextInputEditText> emptyFields = Utils.checkEmptyFields(
                            firstName, lastName, email, userName, password,
                            cPassword, address, postalCode, phone);
                    if (!emptyFields.isEmpty()) {
                        valid = false;
                        for (TextInputEditText editText : emptyFields) {
                            editText.setError(getString(R.string.errEmptyField));
                        }
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        email.requestFocus();
                        email.setError(getString(R.string.errInvalidEmail));
                        valid = false;
                    }
                    if (!Utils.validatePassword(password.getText().toString())) {
                        password.requestFocus();
                        password.setError(getString(R.string.errInvalidPassword));
                        valid = false;
                    }
                    if (!password.getText().toString().equals(cPassword.getText().toString())) {
                        cPassword.setError(getString(R.string.errPasswordsMismatch));
                        valid = false;
                    }
                    if (!Patterns.PHONE.matcher(phone.getText().toString()).matches()) {
                        phone.setError(getString(R.string.errInvalidPhone));
                        valid = false;
                    }

                    User user = new User(false);
                    if (valid) {
                        user.firstName = firstName.getText().toString();
                        user.lastName = lastName.getText().toString();
                        user.userName = userName.getText().toString();
                        user.email = email.getText().toString();
                        user.password = password.getText().toString();
                        user.homeAddress = address.getText().toString();
                        user.postalCode = postalCode.getText().toString();
                        user.phone = phone.getText().toString();
                    } else return;

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
