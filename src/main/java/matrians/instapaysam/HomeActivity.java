package matrians.instapaysam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 Team Matrians
 */
public class HomeActivity extends AppCompatActivity {

    private final int CODE_LOGIN = 1;
    private final int CODE_REGISTER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(getString(R.string.prefLoginId)) ||
                preferences.getInt(getString(R.string.prefLoginStatus), LoginActivity.STATUS_LOGGED_OUT) ==
                        LoginActivity.STATUS_LOGGED_OUT) {
            setContentView(R.layout.activity_home);

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

        /*if (ContextCompat.checkSelfPermission(this,
                "com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"}, 2);
        } else loadCamera();*/
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
                break;
            case CODE_REGISTER:
                if (resultCode == 1) {
                    Toast.makeText(this, "Account created. Proceed to login.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
