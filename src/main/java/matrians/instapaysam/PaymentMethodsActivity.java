package matrians.instapaysam;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import matrians.instapaysam.payments.RVPaymentMethodsAdapter;
import matrians.instapaysam.schemas.Card;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class PaymentMethodsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titlePaymentMethods));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String _id = preferences.getString(getString(R.string.prefUserId), null);
        String email = preferences.getString(getString(R.string.prefLoginId), null);

        Call<List<Card>> call = Server.connect().getCards(_id, email);
        call.enqueue(new Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                RVPaymentMethodsAdapter adapter = new RVPaymentMethodsAdapter(response.body());
                Fragment fragment = new RVFrag();
                //Bundle args = new Bundle();
                //args.putParcelable(getString(R.string.keyAdapter), adapter);
                //fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss();
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                Log.d("Retrofit error", t.toString());
            }
        });
    }
}
