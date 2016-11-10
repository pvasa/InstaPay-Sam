package matrians.instapaysam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.camera.SCamera;
import com.samsung.android.sdk.camera.SCameraManager;

/**
 Team Matrians
 **/

public class HomeActivity extends AppCompatActivity {

    private final int CODE_LOGIN = 1;
    private final int CODE_REGISTER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains(getString(R.string.login_id)) ||
                preferences.getInt(getString(R.string.login_status), 0) == 0) {
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

        /*View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.collapsing_toolbar_title));
        }*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }*/

        /*if (ContextCompat.checkSelfPermission(this,
                "com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"}, 2);
        } else loadCamera();*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loadCamera();
    }

    void loadCamera() {
        SCamera sCamera = new SCamera();
        try {
            sCamera.initialize(this);
        } catch (SsdkUnsupportedException e) {
            if (e.getType() == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED) {
                e.printStackTrace();
            } else if (e.getType() == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
                e.printStackTrace();
            }
        }
        SCameraManager sCameraManager = sCamera.getSCameraManager();
        int versionCode = sCamera.getVersionCode();
        String versionName = sCamera.getVersionName();
        Toast.makeText(this, versionName + ' ' + versionCode, Toast.LENGTH_LONG).show();
        Log.d("CAMERA", versionName + ' ' + versionCode);
        if (sCamera.isFeatureEnabled(SCamera.SCAMERA_PROCESSOR))
            Log.d("CAMERA", "SCAMERA_PROCESSOR ENABLED");
        if (sCamera.isFeatureEnabled(SCamera.SCAMERA_IMAGE))
            Log.d("CAMERA", "SCAMERA_IMAGE ENABLED");
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
                View view = findViewById(R.id.content);
                if (resultCode == 1) {
                    findViewById(R.id.btn_register).setVisibility(View.INVISIBLE);
                    if (view != null)
                        Snackbar.make(view, R.string.snack_register_success,
                                Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }
}
