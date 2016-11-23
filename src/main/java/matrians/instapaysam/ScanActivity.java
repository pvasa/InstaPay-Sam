package matrians.instapaysam;

import android.Manifest;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

import matrians.instapaysam.camera.CameraFrag;
import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVProductsAdapter;
import matrians.instapaysam.schemas.Product;

/**
 * Team Matrians
 */
public class ScanActivity extends AppCompatActivity {

    int FLASH_OFF = 0;
    int FLASH_ON = 1;
    int FLASH_AUTO = 2;
    int currentFlash = 0;
    int[] flashes = new int[] {
            R.drawable.ic_flash_off_black_24dp,
            R.drawable.ic_flash_on_black_24dp,
            R.drawable.ic_flash_auto_black_24dp
    };

    void init() {
        Parcelable adapter = new RVProductsAdapter(new ArrayList<Product>());
        Bundle rvArgs = new Bundle();
        rvArgs.putParcelable(getString(R.string.keyAdapter), adapter);
        Fragment rvFrag = new RVFrag();
        rvFrag.setArguments(rvArgs);
        getFragmentManager().beginTransaction().
                replace(R.id.rvFrag, rvFrag).commitAllowingStateLoss();

        Fragment cameraFrag = CameraFrag.newInstance();
        Bundle cameraArgs = new Bundle(getIntent().getExtras());
        cameraArgs.putAll(rvArgs);
        cameraFrag.setArguments(cameraArgs);
        getFragmentManager().beginTransaction().
                replace(R.id.cameraFrag, cameraFrag).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(actionBar.getTitle() + " - " +
                    getIntent().getStringExtra(getString(R.string.keyVendorName)));
        }

        ImageView flash = (ImageView) findViewById(R.id.flash);
        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ImageView) view).setImageResource(flashes[currentFlash]);
                currentFlash = currentFlash == 2 ? 0 : ++currentFlash;
            }
        });

        findViewById(R.id.fabCheckout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float totalAmount = Float.parseFloat(
                        ((TextView)findViewById(R.id.tvTotalAmount)).getText().toString());
                if (0.0 == totalAmount) {
                    Snackbar.make((View) view.getParent(),
                            R.string.snackNoProducts, Snackbar.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ScanActivity.this, PaymentMethodsActivity.class);
                intent.putExtra(getString(R.string.keyTotalAmount),
                        totalAmount);
                startActivity(intent);
            }
        });

        if (savedInstanceState == null) {
            ensurePermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else init();
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
        init();
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
        } else init();
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
