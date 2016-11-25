package matrians.instapaysam;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVProductsAdapter;
import matrians.instapaysam.recyclerview.RVReceiptAdapter;
import matrians.instapaysam.schemas.Product;

/**
 * Team Matrians
 */
public class ReceiptActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private List<Product> products;
    private String vendorName;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vendorName = getIntent().getStringExtra(getString(R.string.keyVendorName));

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titleReceipt));
        }

        products = ((RVProductsAdapter)getIntent().getParcelableExtra(
                getString(R.string.keyProducts))).getProductList();

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


        PrintAttributes.Builder builder = new PrintAttributes.Builder();
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
        }
    }
}
