package matrians.instapaysam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import matrians.instapaysam.pojo.Order;
import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVOrdersAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 * Load recycler view showing Order history
 */
public class OrdersActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private ProgressDialog dialog;
    private Parcelable ordersAdapter;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.keyAdapter), ordersAdapter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((ordersAdapter = savedInstanceState.getParcelable(getString(R.string.keyAdapter))) != null) {
            ((RVOrdersAdapter) ordersAdapter).notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Load toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titleOrders));
        }

        if (savedInstanceState != null) return;

        dialog = Utils.showProgress(this, R.string.dialogLoadingOrders);

        // Fetch orders list from server
        Call<List<Order>> callV = Server.connect().getOrders(
                PreferenceManager.getDefaultSharedPreferences(
                        OrdersActivity.this).getString(getString(R.string.prefUserId), null));
        callV.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                dialog.dismiss();
                if (200 == response.code()) {
                    ordersAdapter = new RVOrdersAdapter(response.body());
                    Fragment fragment = new RVFrag();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.keyAdapter), ordersAdapter);
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction().replace(
                            R.id.content, fragment).commitAllowingStateLoss();
                } else if (204 == response.code()) {
                    Utils.snackUp(findViewById(R.id.rootView), R.string.msgNoOrders);
                } else {
                    Utils.snackUp(findViewById(R.id.rootView), R.string.errNetworkError);
                }
            }
            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                dialog.dismiss();
                Log.d(TAG, t.toString());
            }
        });

        // Define pull down to refresh functionality
        final SwipeRefreshLayout swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call<List<Order>> callV = Server.connect().getOrders(
                        PreferenceManager.getDefaultSharedPreferences(
                                OrdersActivity.this).getString(getString(R.string.prefUserId), null));
                callV.enqueue(new Callback<List<Order>>() {
                    @Override
                    public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                        if (200 == response.code()) {
                            if (ordersAdapter != null)
                                ((RVOrdersAdapter) ordersAdapter).addDataSet(response.body());
                        } else if (204 == response.code()) {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.msgNoOrders);
                        } else {
                            Utils.snackUp(findViewById(R.id.rootView), R.string.errNetworkError);
                        }
                        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                    }
                    @Override
                    public void onFailure(Call<List<Order>> call, Throwable t) {
                        Log.d(TAG, t.toString());
                        if (swipeRefresh.isRefreshing()) swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });

    }
}