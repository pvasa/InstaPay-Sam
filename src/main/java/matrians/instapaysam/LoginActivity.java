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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import java.io.IOException;
import java.util.HashSet;

import javax.net.ssl.HttpsURLConnection;

import matrians.instapaysam.pojo.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 * User login page
 */
public class LoginActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    static final int STATUS_LOGGED_IN = 1;
    static final int STATUS_LOGGED_OUT = 0;
    private ProgressDialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            setResult(2);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Load toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Load FAB
        FloatingActionButton fab;
        if ((fab = (FloatingActionButton) findViewById(R.id.fab)) != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(v.getContext(), RegisterActivity.class),
                            HomeActivity.CODE_REGISTER);
                    finish();
                }
            });
        }

        final TextInputEditText loginId = (TextInputEditText) findViewById(R.id.login_id);
        if (loginId != null)
            loginId.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        loginId.setError(getString(R.string.errEmptyField));
                    else loginId.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

        final TextInputEditText password = (TextInputEditText) findViewById(R.id.password);
        if (password != null)
            password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (0 == charSequence.length())
                        password.setError(getString(R.string.errEmptyField));
                    else password.setError(null);
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

        Button loginButton;
        if ((loginButton = (Button) findViewById(R.id.btn_login)) != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("ConstantConditions")
                @Override
                public void onClick(final View v) {
                    try {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(
                                        v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } catch (Exception e) {e.printStackTrace();}
                    User user = new User(true);
                    HashSet<TextInputEditText> emptyEditTexts = Utils.checkEmptyFields(loginId, password);
                    if ( !emptyEditTexts.isEmpty() ) {
                        for (TextInputEditText editText : emptyEditTexts) {
                            editText.setError(getString(R.string.errEmptyField));
                            editText.requestFocus();
                        }
                        return;
                    }
                    user.email = user.userName = loginId.getText().toString();
                    user.password = password.getText().toString();

                    dialog = Utils.showProgress(LoginActivity.this,
                            R.string.dialogVerifyingCredentials);
                    Call<User> call = Server.connect().loginUser(user);
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            dialog.dismiss();
                            if (HttpsURLConnection.HTTP_OK == response.code()) {
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(getApplicationContext()).edit();
                                editor.putInt(getString(R.string.prefLoginStatus), STATUS_LOGGED_IN);
                                editor.putString(getString(R.string.prefEmail), response.body().email);
                                editor.putString(getString(R.string.prefUserId), response.body().getId());
                                editor.apply();
                                setResult(1);
                                finish();
                            } else {
                                try {
                                    Utils.snackUp(findViewById(R.id.rootView),
                                            response.errorBody().string(), R.string.keyError);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            dialog.dismiss();
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errNetworkError);
                            Log.d(TAG, t.toString());
                        }
                    });
                }
            });
        }
        /*View linkForgotPassword;
        if ((linkForgotPassword = findViewById(R.id.link_forgot_password)) != null) {
            linkForgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(v.getContext(), PasswordReset.class));
                    finish();
                }
            });
        }*/
    }
}
