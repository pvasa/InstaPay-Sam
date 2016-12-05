package matrians.instapaysam;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.stripe.android.model.Card;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import matrians.instapaysam.pojo.EncryptedMCard;
import matrians.instapaysam.pojo.MCard;
import matrians.instapaysam.pojo.Vendor;
import matrians.instapaysam.recyclerview.RVFrag;
import matrians.instapaysam.recyclerview.RVVendorsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class VendorsActivity extends AppCompatActivity {

    private String TAG = this.getClass().getName();
    private boolean backPressedOnce = false;
    private ProgressDialog dialog;
    private final int CODE_PAYMENT_METHODS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.titleVendors));
        }

        dialog = Utils.showProgress(this, getString(R.string.dialogFetchingCards));

        EncryptedMCard encryptedMCard = new EncryptedMCard(this);

        Call<List<EncryptedMCard>> callC = Server.connect().getCards(encryptedMCard);
        callC.enqueue(new Callback<List<EncryptedMCard>>() {
            @Override
            public void onResponse(Call<List<EncryptedMCard>> call, Response<List<EncryptedMCard>> response) {
                dialog.dismiss();
                if (204 == response.code()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(VendorsActivity.this);
                    builder.setTitle(R.string.titleNoCards);
                    builder.setMessage(R.string.messageNoCards);
                    builder.setPositiveButton(R.string.btnAdd, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(
                                    new Intent(VendorsActivity.this, CardEditActivity.class),
                                    PaymentMethodsActivity.CODE_ADD_CARD);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.btnNoThanks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.show();
                }
            }
            @Override
            public void onFailure(Call<List<EncryptedMCard>> call, Throwable t) {
                dialog.dismiss();
                Log.d(TAG, t.toString());
            }
        });

        Call<List<Vendor>> call = Server.connect().getVendors();
        call.enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                Parcelable adapter = new RVVendorsAdapter(response.body());
                Fragment fragment = new RVFrag();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.keyAdapter), adapter);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(
                        R.id.content, fragment).commitAllowingStateLoss();
            }
            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                Log.d(TAG, t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_payment_methods:
                Intent intent = new Intent(this, PaymentMethodsActivity.class);
                intent.putExtra(getString(R.string.keyEditMode), true);
                startActivityForResult(intent, CODE_PAYMENT_METHODS);
                break;
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.remove(getString(R.string.prefEmail));
                editor.remove(getString(R.string.prefLoginStatus));
                editor.apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed();
            return;
        }
        backPressedOnce = true;
        Toast.makeText(this, R.string.tostBackPressed, Toast.LENGTH_SHORT).show();
        new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
            @Override
            public void run() {
                backPressedOnce = false;
            }
        }, 2, TimeUnit.SECONDS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_PAYMENT_METHODS) {
            if (resultCode == 1)
                finish();
        }
        if (requestCode == PaymentMethodsActivity.CODE_ADD_CARD && resultCode == RESULT_OK) {
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

            dialog = Utils.showProgress(this, getString(R.string.dialogSavingCard));

            MCard mCard = new MCard(this, cardName, cardNumber, expMonth, expYear, cvv);

            Call<JSONObject> call = Server.connect().addCard(mCard.encrypt(this));
            call.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    Log.d(TAG, response.body().toString());
                    dialog.dismiss();
                    if (200 == response.code()) {
                        Toast.makeText(VendorsActivity.this,
                                R.string.toastCardAddSuccess, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VendorsActivity.this,
                                getString(R.string.snackErrAddCard), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(VendorsActivity.this,
                            getString(R.string.snackErrAddCard), Toast.LENGTH_LONG).show();
                    Log.d(TAG, t.toString());
                }
            });
        }
    }
}