package matrians.instapaysam;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Team Matrians
 *
 * Main activity
 */
public class HomeActivity extends AppCompatActivity {

    static final int CODE_LOGIN = 1;
    static final int CODE_REGISTER = 2;
    private boolean backPressedOnce = false;
    private static final String TAG = HomeActivity.class.getName();
    private static final String ALLOWED_DEVICE = ""; // Allowed all devices for testing.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(getString(R.string.prefEmail)) ||
                preferences.getInt(getString(R.string.prefLoginStatus), LoginActivity.STATUS_LOGGED_OUT) ==
                        LoginActivity.STATUS_LOGGED_OUT) {
            setContentView(R.layout.activity_home);

            // Allow only app to run on specific devices only
            if (!android.os.Build.MANUFACTURER.contains(ALLOWED_DEVICE)) {
                Log.d(TAG, android.os.Build.MANUFACTURER);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialogNoSamsung);
                builder.setPositiveButton(R.string.btnExit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HomeActivity.this.finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }

            // Make view full screen
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window w = getWindow();
                w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

            findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(v.getContext(), LoginActivity.class), CODE_LOGIN);
                }
            });

            findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(v.getContext(), RegisterActivity.class), CODE_REGISTER);
                }
            });

        } else {
            startActivity(new Intent(this, VendorsActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkPlayServices(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendors, menu);
        menu.findItem(R.id.action_logout).setVisible(false);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_LOGIN:
                if (resultCode == 1) {
                    startActivity(new Intent(this, VendorsActivity.class));
                    finish();
                }
                if (resultCode == 2) {
                    Utils.snackUp(findViewById(R.id.rootView), R.string.msgAccountCreated);
                }
                break;
            case CODE_REGISTER:
                if (resultCode == 1) {
                    Utils.snackUp(findViewById(R.id.rootView), R.string.msgAccountCreated);
                }
                else if (resultCode == 2) {
                    startActivity(new Intent(this, VendorsActivity.class));
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }
        backPressedOnce = true;
        Utils.snackUp(findViewById(R.id.rootView), R.string.msgBackPressed);
        new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2, TimeUnit.SECONDS);
    }
}
