package matrians.instapaysam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.stripe.android.model.Card;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import matrians.instapaysam.pojo.EncryptedMCard;
import matrians.instapaysam.pojo.MCard;
import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVPaymentMethodsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class PaymentMethodsActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private ProgressDialog waitDialog;
    private RVPaymentMethodsAdapter paymentMethodsAdapter;
    private float payable;
    private Parcelable productsAdapter;
    private boolean editMode;

    public static final int CODE_ADD_CARD = 1;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.keyAdapter), paymentMethodsAdapter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if ((paymentMethodsAdapter =
                savedInstanceState.getParcelable(getString(R.string.keyAdapter))) != null) {
             paymentMethodsAdapter.notifyDataSetChanged();
        }
    }

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

        CollapsingToolbarLayout toolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (toolbarLayout != null) {
            toolbarLayout.setTitle(getString(R.string.titlePaymentMethods));
            if (editMode = getIntent().getBooleanExtra(getString(R.string.keyEditMode), false)) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                toolbarLayout.findViewById(R.id.toolbarBackground).setVisibility(View.VISIBLE);
            } else {
                toolbarLayout.findViewById(R.id.toolbarAmounts).setVisibility(View.VISIBLE);
                float totalAmount = getIntent().getFloatExtra(getString(R.string.keyTotalAmount), 0f);
                float hst = new BigDecimal(Float.toString(totalAmount * 0.13f))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                payable = new BigDecimal(Float.toString(totalAmount + hst))
                        .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                ((TextView) toolbarLayout.findViewById(R.id.tvTotalAmount)).setText(String.valueOf(totalAmount));
                ((TextView) toolbarLayout.findViewById(R.id.tvHST)).setText(String.valueOf(hst));
                ((TextView) toolbarLayout.findViewById(R.id.tvPayable)).setText(String.valueOf(payable));
            }
        }

        if (savedInstanceState != null) return;

        waitDialog = Utils.showProgress(this, R.string.dialogFetchingCards);
        Call<List<EncryptedMCard>> call = Server.connect().getCards(
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .getString(getString(R.string.prefUserId), null));
        call.enqueue(new Callback<List<EncryptedMCard>>() {
            @Override
            public void onResponse(
                    Call<List<EncryptedMCard>> call,
                    Response<List<EncryptedMCard>> response) {

                List<MCard> mCards = new ArrayList<>();
                if (200 == response.code()) {
                    for (EncryptedMCard eMCard : response.body()) {
                        mCards.add(eMCard.decrypt(PaymentMethodsActivity.this));
                    }
                } else if (response.code() == 204) {
                    Utils.snackUp(findViewById(R.id.rootView), R.string.errNoPaymentMethods);
                } else try {
                    Utils.snackUp(findViewById(R.id.rootView),
                            response.errorBody().string(), R.string.keyError);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                waitDialog.dismiss();
                Parcelable adapter = new RVPaymentMethodsAdapter(
                        mCards, editMode, payable, productsAdapter,
                        getIntent().getStringExtra(getString(R.string.keyVendorName)),
                        PaymentMethodsActivity.this);
                paymentMethodsAdapter = (RVPaymentMethodsAdapter) adapter;
                Fragment fragment = new RVFrag();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.keyAdapter), adapter);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(
                        R.id.content, fragment).commitAllowingStateLoss();
            }

            @Override
            public void onFailure(Call<List<EncryptedMCard>> call, Throwable t) {
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
                if (waitDialog != null) waitDialog.dismiss();
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

            MCard mCard = new MCard(
                    this, cardName, cardNumber, expMonth, expYear, cvv, card.getBrand());
            paymentMethodsAdapter.addPaymentMethod(mCard);
        }
    }
}
