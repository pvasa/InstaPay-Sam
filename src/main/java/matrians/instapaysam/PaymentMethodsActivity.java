package matrians.instapaysam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.stripe.android.model.Card;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVPayNowAdapter;
import matrians.instapaysam.schemas.MCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class PaymentMethodsActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private ProgressDialog waitDialog;
    private RVPayNowAdapter payNowAdapter;
    private float payable;
    private Parcelable productsAdapter;

    static final int CODE_ADD_CARD = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productsAdapter = getIntent().getParcelableExtra(getString(R.string.keyProducts));

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                startActivityForResult(
                        new Intent(PaymentMethodsActivity.this,
                        CardEditActivity.class), CODE_ADD_CARD);
            }
        });

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titlePaymentMethods));
            float totalAmount = getIntent().getFloatExtra(getString(R.string.keyTotalAmount), 0f);
            float hst = new BigDecimal(Float.toString(totalAmount * 0.13f))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            payable = new BigDecimal(Float.toString(totalAmount + hst))
                    .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            ((TextView) view.findViewById(R.id.tvTotalAmount)).setText(String.valueOf(totalAmount));
            ((TextView) view.findViewById(R.id.tvHST)).setText(String.valueOf(hst));
            ((TextView) view.findViewById(R.id.tvPayable)).setText(String.valueOf(payable));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String _id = preferences.getString(getString(R.string.prefUserId), null);
        String email = preferences.getString(getString(R.string.prefLoginId), null);

        JSONObject object = new JSONObject();
        try {
            object.put("_id", _id);
            object.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        waitDialog = Utils.showProgress(this, getString(R.string.dialogFetchingCards));
        Call<List<MCard>> call = Server.connect().getCards(object);
        call.enqueue(new Callback<List<MCard>>() {
            @Override
            public void onResponse(Call<List<MCard>> call, Response<List<MCard>> response) {
                waitDialog.dismiss();
                if (response.code() == 204) {
                    Snackbar.make(findViewById(R.id.rootView),
                            R.string.snackNoPaymentMethods,
                            Snackbar.LENGTH_LONG).show();
                }
                Parcelable adapter = new RVPayNowAdapter(
                        response.body(), payable, productsAdapter,
                        getIntent().getStringExtra(getString(R.string.keyVendorName)), PaymentMethodsActivity.this);
                payNowAdapter = (RVPayNowAdapter) adapter;
                Fragment fragment = new RVFrag();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.keyAdapter), adapter);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(
                        R.id.content, fragment).commitAllowingStateLoss();
            }
            @Override
            public void onFailure(Call<List<MCard>> call, Throwable t) {
                waitDialog.dismiss();
                Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_ADD_CARD && resultCode == RESULT_OK) {
            String cardName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            if (cardName.isEmpty()) {
                Toast.makeText(this,
                        R.string.errCardName, Toast.LENGTH_LONG).show();
                return;
            }
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            if (cardNumber.isEmpty()) {
                Toast.makeText(this,
                        R.string.errCardNumber, Toast.LENGTH_LONG).show();
                return;
            }
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            if (expiry.isEmpty()) {
                Toast.makeText(this,
                        R.string.errExpDate, Toast.LENGTH_LONG).show();
                return;
            }
            int expMonth = Integer.parseInt(expiry.split("/")[0]);
            int expYear = Integer.parseInt(expiry.split("/")[1]);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
            if (cvv.isEmpty()) {
                Toast.makeText(this,
                        R.string.errCVC, Toast.LENGTH_LONG).show();
                return;
            }
            Card card = new Card(cardNumber, expMonth, expYear, cvv);
            if (!card.validateCard()) {
                waitDialog.dismiss();
                if (!card.validateNumber()) {
                    Toast.makeText(this,
                            R.string.errCardNumber, Toast.LENGTH_LONG).show();
                } else if (!card.validateExpiryDate()) {
                    Toast.makeText(this,
                            R.string.errExpDate, Toast.LENGTH_LONG).show();
                } else if (!card.validateCVC()) {
                    Toast.makeText(this,
                            R.string.errCVC, Toast.LENGTH_LONG).show();
                }
                return;
            }

            waitDialog = Utils.showProgress(this, getString(R.string.dialogSavingCard));

            String email = PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString(getString(R.string.prefLoginId), null);
            final MCard mCard = new MCard(email, cardName, cardNumber, expMonth, expYear, cvv);
            Call<MCard> call = Server.connect().addCard(mCard);
            call.enqueue(new Callback<MCard>() {
                @Override
                public void onResponse(Call<MCard> call, Response<MCard> response) {
                    waitDialog.dismiss();
                    if (200 == response.code()) {
                        Toast.makeText(PaymentMethodsActivity.this,
                                R.string.toastCardAddSuccess, Toast.LENGTH_LONG).show();
                        payNowAdapter.addCard(mCard);
                    } else {
                        Toast.makeText(PaymentMethodsActivity.this,
                                getString(R.string.snackErrAddCard), Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<MCard> call, Throwable t) {
                    waitDialog.dismiss();
                    Toast.makeText(PaymentMethodsActivity.this,
                            getString(R.string.snackErrAddCard), Toast.LENGTH_LONG).show();
                    Log.d(TAG, t.toString());
                }
            });
        }
    }
}
