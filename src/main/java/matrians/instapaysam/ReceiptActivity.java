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

        order = getIntent().getParcelableExtra(getString(R.string.keyPayment));
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

        findViewById(R.id.fabDownloadReceipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /*PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME);
        builder.setMediaSize(PrintAttributes.MediaSize.NA_LETTER);
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

        // open a new document
        PrintedPdfDocument document = new PrintedPdfDocument(this, builder.build());
        // start a page
        PdfDocument.Page page = document.startPage(1);

        // draw something on the page
        View content = findViewById(R.id.receipt);
        content.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);

        String DocumentsDir =
                Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

        String instaPayDir = DocumentsDir + File.separator + getString(R.string.app_name);

        String vendorDir = instaPayDir + File.separator + vendorName;

        File vendorDirF = new File(vendorDir);
        if (!vendorDirF.exists()) vendorDirF.mkdirs();

        @SuppressLint("SimpleDateFormat")
        String receiptNumber =
                new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(Calendar.getInstance().getTime());

        File receipt = new File(vendorDir + File.separator + receiptNumber + ".pdf");

        FileOutputStream fos = null;
        try {
            receipt.createNewFile();
            fos = new FileOutputStream(receipt);
            document.writeTo(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            document.close();
        }*/
    }

    @Override
    public void onBackPressed() {
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
