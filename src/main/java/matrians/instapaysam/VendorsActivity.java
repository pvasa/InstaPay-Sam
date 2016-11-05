package matrians.instapaysam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.camera.SCamera;
import com.samsung.android.sdk.camera.SCameraManager;

/**
 Team Matrians
 **/

public class VendorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.collapsing_toolbar_title));
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(getString(R.string.login_id)) &&
                preferences.getInt(getString(R.string.login_status), 0) == 1) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new RVFrag()).commit();
        }
        else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content, new HomeFrag()).commit();
        }

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.remove(getString(R.string.login_id));
                editor.remove(getString(R.string.login_status));
                editor.apply();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content, new HomeFrag()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
