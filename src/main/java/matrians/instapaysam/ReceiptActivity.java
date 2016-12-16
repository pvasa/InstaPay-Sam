package matrians.instapaysam;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import matrians.instapaysam.pojo.Order;
import matrians.instapaysam.pojo.Product;
import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVReceiptAdapter;

/**
 * Team Matrians
 * Show Receipt page after keyOrder success
 */
public class ReceiptActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private String TAG = this.getClass().getName();
    private Order order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        order = getIntent().getParcelableExtra(getString(R.string.keyOrder));
        String vendorName = getIntent().getStringExtra(getString(R.string.keyVendorName));

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(
                    getString(R.string.titleReceipt) + " - " + vendorName);
        }

        List<Product> products = order.getProductList();

        Parcelable adapter = new RVReceiptAdapter(products);
        Fragment fragment = new RVFrag();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.keyAdapter), adapter);
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss();

        float totalAmount = 0f;
        float HST;
        float payable;

        for(Product product : products) {
            totalAmount += (product.price * product.quantity);
        }
        HST = new BigDecimal(Float.toString(totalAmount * 0.13f))
                .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        payable = new BigDecimal(Float.toString(totalAmount + HST))
                .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

        ((TextView) findViewById(R.id.tvHST)).setText(String.valueOf(HST));
        ((TextView) findViewById(R.id.tvPayable)).setText(String.valueOf(payable));

        /*findViewById(R.id.fabDownloadReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
    }

    @Override
    public void onBackPressed() { // Exit or go to home?
        new AlertDialog.Builder(this)
                .setTitle(R.string.titleShopMore)
                .setPositiveButton(R.string.btnHome, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ReceiptActivity.this.finishAffinity();
                        startActivity(new Intent(ReceiptActivity.this, VendorsActivity.class));
                    }
                })
                .setNegativeButton(R.string.btnExit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ReceiptActivity.this.finishAffinity();
                    }
                }).show();
    }
}
