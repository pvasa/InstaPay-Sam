package matrians.instapaysam;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

public class VendorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);
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

        /*RVFrag recyclerViewFrag = new RVFrag();
        getFragmentManager().beginTransaction()
                .replace(R.id.content, recyclerViewFrag).addToBackStack(null).commit();
*/
        if (ContextCompat.checkSelfPermission(this,
                "com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY")
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{"com.samsung.android.providers.context.permission.WRITE_USE_APP_FEATURE_SURVEY"}, 2);
        } else loadCamera();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
