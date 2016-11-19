package matrians.instapaysam;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashSet;

import matrians.instapaysam.camera.CameraFrag;

/**
 Team Matrians
 */
public class ScanActivity extends AppCompatActivity {

    static boolean flashOn = false;
    public static RenderScript renderScript;

    void initCameraFrag() {
        getFragmentManager().beginTransaction().
                replace(R.id.camera, CameraFrag.newInstance()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(actionBar.getTitle() + " - " +
                    getIntent().getStringExtra(getString(R.string.keyVendor)));
        }

        renderScript = RenderScript.create(this);

        if (savedInstanceState == null) {
            ensurePermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else initCameraFrag();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                showAlertDialog(permissions);
                return;
            }
        }
        initCameraFrag();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        if (flashOn) {
            menu.findItem(R.id.action_flash).setIcon(R.drawable.ic_action_flash_on);
        } else {
            menu.findItem(R.id.action_flash).setIcon(R.drawable.ic_action_flash_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_flash:
                if (flashOn = !flashOn) {
                    item.setIcon(R.drawable.ic_action_flash_on);
                } else {
                    item.setIcon(R.drawable.ic_action_flash_off);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Check for permissions and showDialog if not granted.
     * @param permissions - permissions to be checked
     */
    private void ensurePermissions(String... permissions) {
        HashSet<String> deniedPermissionList = new HashSet<>();

        for (String permission : permissions) {
            if (PackageManager.PERMISSION_GRANTED !=
                    ContextCompat.checkSelfPermission(this, permission)) {
                deniedPermissionList.add(permission);
            }
        }

        if (!deniedPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, deniedPermissionList.toArray(new String[0]), 0);
        } else initCameraFrag();
    }

    /**
     * Shows alert dialog for permissions.
     */
    private void showAlertDialog(final String... permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alertPermissionRequest);
        builder.setPositiveButton(R.string.alertBtnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ensurePermissions(permissions);
            }
        });
        builder.setNegativeButton(R.string.alertBtnNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }
}
