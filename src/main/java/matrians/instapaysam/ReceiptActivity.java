package matrians.instapaysam;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    }
}
