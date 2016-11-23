package matrians.instapaysam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVPaymentMethodsAdapter;
import matrians.instapaysam.schemas.MCard;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class PaymentMethodsActivity extends AppCompatActivity {

    private final String TAG = "PAYMENT_METHODS";
    private ProgressDialog waitDialog;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.fabAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                dialog = new AlertDialog.Builder(view.getContext())
                        .setView(R.layout.dialog_add_card)
                        .setTitle("Add card")
                        .setCancelable(false)
                        .setPositiveButton("Done", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onClick(final View view) {

                        waitDialog = Utils.showProgress(view.getContext(), getString(R.string.processAddingCard));

                        String cardName = ((TextView) dialog.findViewById(R.id.cardName))
                                .getText().toString();
                        String cardNumber =
                                ((TextView) dialog.findViewById(R.id.cardNumber))
                                        .getText().toString();
                        Integer expMonth = Integer.parseInt(
                                ((TextView) dialog.findViewById(R.id.expMonth))
                                        .getText().toString());
                        Integer expYear = Integer.parseInt(
                                ((TextView) dialog.findViewById(R.id.expYear))
                                        .getText().toString());
                        String cvc =
                                ((TextView) dialog.findViewById(R.id.cvc))
                                        .getText().toString();
                        Card card = new Card(cardNumber, expMonth, expYear, cvc);
                        card.setName(cardName);
                        if (card.getName().isEmpty()) {
                            Toast.makeText(view.getContext(),
                                    "Card name cannot be empty.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (!card.validateCard()) {
                            waitDialog.dismiss();
                            if (!card.validateNumber()) {
                                Toast.makeText(view.getContext(),
                                        "Invalid card number.", Toast.LENGTH_LONG).show();
                            } else if (!card.validateExpiryDate()) {
                                Toast.makeText(view.getContext(),
                                        "Invalid exp date.", Toast.LENGTH_LONG).show();
                            } else if (!card.validateCVC()) {
                                Toast.makeText(view.getContext(),
                                        "Invalid cvc.", Toast.LENGTH_LONG).show();
                            }
                            return;
                        }
                        sendToStripe(card, dialog);
                    }
                });
            }
        });

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titlePaymentMethods));
            float totalAmount = getIntent().getFloatExtra(getString(R.string.keyTotalAmount), 0f);
            float hst = totalAmount * 0.13f;
            float payable = totalAmount + hst;
            ((TextView) view.findViewById(R.id.tvTotalAmount)).setText(String.valueOf(totalAmount));
            ((TextView) view.findViewById(R.id.tvHST)).setText(String.valueOf(hst));
            ((TextView) view.findViewById(R.id.tvPayable)).setText(String.valueOf(payable));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String _id = preferences.getString(getString(R.string.prefUserId), null);
        String email = preferences.getString(getString(R.string.prefLoginId), null);

        Log.d(TAG, _id);
        Log.d(TAG, email);
        JSONObject object = new JSONObject();
        try {
            object.put("_id", _id);
            object.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<List<MCard>> call = Server.connect().getCards(object);
        call.enqueue(new Callback<List<MCard>>() {
            @Override
            public void onResponse(Call<List<MCard>> call, Response<List<MCard>> response) {
                if (response.code() == 204) {
                    Snackbar.make(findViewById(R.id.rootView),
                            R.string.snackNoPaymentMethods,
                            Snackbar.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, response.body().toString());
                Parcelable adapter = new RVPaymentMethodsAdapter(response.body());
                Fragment fragment = new RVFrag();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.keyAdapter), adapter);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.content, fragment).commitAllowingStateLoss();
            }

            @Override
            public void onFailure(Call<List<MCard>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    private void sendToStripe(final Card card, final AlertDialog dialog) {
        Stripe stripe;
        try {
            stripe = new Stripe("pk_test_B04KoH4BztCSTDWiC8XyXiBF");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            token.getId();
                            String userEmail = PreferenceManager
                                    .getDefaultSharedPreferences(PaymentMethodsActivity.this)
                                    .getString(getString(R.string.prefLoginId), null);
                            MCard mCard = new MCard(
                                    userEmail,
                                    card.getName(),
                                    card.getLast4(),
                                    token.getId()
                            );
                            Call<MCard> call = Server.connect().addCard(mCard);
                            call.enqueue(new Callback<MCard>() {
                                @Override
                                public void onResponse(Call<MCard> call, Response<MCard> response) {
                                    waitDialog.dismiss();
                                    if (200 == response.code()) {
                                        dialog.dismiss();
                                        Toast.makeText(PaymentMethodsActivity.this,
                                                R.string.toastCardAddSuccess, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(PaymentMethodsActivity.this,
                                                getString(R.string.snackErrAddCard) + response.code(), Toast.LENGTH_LONG).show();
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
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(PaymentMethodsActivity.this,
                                    error.getLocalizedMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }
    }
}
