package matrians.instapaysam.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import matrians.instapaysam.PaymentMethodsActivity;
import matrians.instapaysam.R;
import matrians.instapaysam.ReceiptActivity;
import matrians.instapaysam.Server;
import matrians.instapaysam.Utils;
import matrians.instapaysam.pojo.EncryptedMCard;
import matrians.instapaysam.pojo.MCard;
import matrians.instapaysam.pojo.Order;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 *
 * RecyclerView.Adapter to populate available payment methods
 */
public class RVPaymentMethodsAdapter extends
        RecyclerView.Adapter<RVPaymentMethodsAdapter.ViewHolder> implements Parcelable {

    private static String TAG = RVPaymentMethodsAdapter.class.getName();
    private static List<MCard> dataSet;
    private static boolean editMode;
    private static ProgressDialog waitDialog;
    private static float amount;
    private static Parcelable productsAdapter;
    private static String vendorName, vendorID, userID, userEmail;
    private static PaymentMethodsActivity paymentMethodsActivity;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVPaymentMethodsAdapter(List<MCard> dataSet, boolean editMode,
                                   float payable, Parcelable productsAdapter,
                                   String vendorName, String vendorID, Context context) {
        RVPaymentMethodsAdapter.dataSet = dataSet;
        RVPaymentMethodsAdapter.editMode = editMode;
        amount = payable;
        RVPaymentMethodsAdapter.productsAdapter = productsAdapter;
        RVPaymentMethodsAdapter.vendorName = vendorName;
        RVPaymentMethodsAdapter.vendorID = vendorID;
        paymentMethodsActivity = (PaymentMethodsActivity) context;
        userID = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.prefUserId), null);
        userEmail = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(context.getString(R.string.prefEmail), null);
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardLast4Digits;
        String cardNumber, CVC;
        int expMonth, expYear;
        ImageView logo, deleteIcon;

        ViewHolder(View v) {
            super(v);
            cardName = (TextView) v.findViewById(R.id.tvCardName);
            cardLast4Digits = (TextView) v.findViewById(R.id.tvLast4Digits);
            logo = (ImageView) v.findViewById(R.id.logo);
            deleteIcon = (ImageView) v.findViewById(R.id.deletePM);

            if (!editMode) {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        waitDialog = Utils.showProgress(view.getContext(),
                                R.string.dialogProcessingPayment);
                        try {
                            generateTokenAndPay(
                                    view.getContext(),
                                    new Card(cardNumber, expMonth, expYear, CVC)
                            );
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent - Parent ViewGroup
     * @param viewType - int
     * @return custom ViewHolder
     */
    @Override
    public RVPaymentMethodsAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_payment_methods, parent, false);
        return new RVPaymentMethodsAdapter.ViewHolder(v);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(RVPaymentMethodsAdapter.ViewHolder holder, int position) {
        final MCard mCard = dataSet.get(position);
        holder.cardNumber = mCard.number;
        holder.expMonth = mCard.expMonth;
        holder.expYear = mCard.expYear;
        holder.CVC = mCard.CVC;
        holder.cardName.setText(mCard.name);
        holder.cardLast4Digits.setText(holder.cardNumber.substring(holder.cardNumber.length() - 4));

        switch (dataSet.get(position).brand) {
            case Card.AMERICAN_EXPRESS:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_amex));
                break;
            case Card.DINERS_CLUB:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_diners));
                break;
            case Card.DISCOVER:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_discover));
                break;
            case Card.MASTERCARD:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_master));
                break;
            case Card.VISA:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_visa));
                break;
            case Card.JCB:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_jcb));
                break;
            default:
                holder.logo.setImageDrawable(
                        ContextCompat.getDrawable(paymentMethodsActivity, R.drawable.logo_default_card));
        }

        if (editMode) {
            holder.deleteIcon.setVisibility(View.VISIBLE);
            holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitDialog = Utils.showProgress(
                            paymentMethodsActivity,
                            R.string.dialogDeletePaymentMethod);
                    deletePaymentMethod(mCard);
                }
            });
        }
    }

    /**
     * Return the size of your dataSet (invoked by the layout manager)
     * @return size of the dataSet
     */
    @Override
    public int getItemCount() {
        if (dataSet != null)
            return dataSet.size();
        return 0;
    }

    /**
     * Adds payment method to server and refreshes list
     * @param mCard - Card to be added
     */
    public void addPaymentMethod (final MCard mCard) {
        if (dataSet == null) {
            dataSet = new ArrayList<>();
        }

        waitDialog = Utils.showProgress(
                paymentMethodsActivity,
                R.string.dialogSavingCard);

        mCard.encrypt(paymentMethodsActivity, new MCard.Callback() {
            @Override
            public void encryptedOrNull(EncryptedMCard eMCard) {
                Call<JSONObject> call = Server.connect().addCard(
                        PreferenceManager.getDefaultSharedPreferences(paymentMethodsActivity)
                                .getString(paymentMethodsActivity.getString(R.string.prefUserId), null), eMCard);
                call.enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        if (waitDialog != null) waitDialog.dismiss();
                        if (HttpsURLConnection.HTTP_OK == response.code()) {
                            Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                    R.string.msgCardAddSuccess);
                            dataSet.add(mCard);
                            notifyDataSetChanged();
                        } else {
                            try {
                                Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                        response.errorBody().string(), R.string.keyError);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        waitDialog.dismiss();
                        Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                R.string.errDeletingCard);
                        Log.d(TAG, t.toString());
                    }
                });
            }
        });
    }

    /**
     * Deletes payment method from server and refreshes list
     * @param mCard - Card to be deleted
     */
    private void deletePaymentMethod (final MCard mCard) {
        if (dataSet != null) {
            mCard.encrypt(paymentMethodsActivity, new MCard.Callback() {
                @Override
                public void encryptedOrNull(EncryptedMCard eMCard) {
                    Call<JSONObject> call = Server.connect().deleteCard(
                            PreferenceManager.getDefaultSharedPreferences(paymentMethodsActivity)
                                    .getString(paymentMethodsActivity.getString(R.string.prefUserId), null),
                            eMCard.markToDelete());
                    call.enqueue(new Callback<JSONObject>() {
                        @Override
                        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                            waitDialog.dismiss();
                            if (HttpsURLConnection.HTTP_OK == response.code()) {
                                Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                        R.string.msgPaymentMethodDeleteSuccess);
                                dataSet.remove(mCard);
                                notifyDataSetChanged();
                            } else {
                                try {
                                    Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                            response.errorBody().string(), R.string.keyError);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable t) {
                            waitDialog.dismiss();
                            Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                    R.string.errNetworkError);
                            Log.d(TAG, t.toString());
                        }
                    });
                }
            });

        }
    }

    /**
     * Generate stripe token and send to server to process payment
     * @param context - Parent activity context
     * @param card - Card used for payment
     * @throws AuthenticationException
     */
    private static void generateTokenAndPay(final Context context, Card card)
            throws AuthenticationException {

        new Stripe(context.getString(R.string.stripeTestPublishableKey)).createToken(
                card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                R.string.errPaymentFailed);
                        Log.d(TAG, error.getLocalizedMessage());
                    }
                    @Override
                    public void onSuccess(Token token) {

                        final Order order = new Order(
                                vendorID, vendorName, userID, userEmail, token.getId(), amount,
                                ((RVProductsAdapter)productsAdapter).getProductList());

                        Call<Order> call = Server.connect().pay(order);
                        call.enqueue(new Callback<Order>() {
                            @Override
                            public void onResponse(Call<Order> call, Response<Order> response) {
                                waitDialog.dismiss();
                                if (HttpsURLConnection.HTTP_OK == response.code()) {
                                    Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                            R.string.msgPaymentSuccess);

                                    Intent intent = new Intent(context, ReceiptActivity.class);
                                    intent.putExtra(
                                            context.getString(R.string.keyOrder), order);
                                    intent.putExtra(
                                            context.getString(R.string.keyVendorName), vendorName);
                                    context.startActivity(intent);

                                    /*try {
                                        ResponseBody body = response.raw().body();
                                        File invoice = new File(context.getExternalFilesDir(null)
                                                + File.separator + "invoice.pdf");

                                        InputStream inputStream = null;
                                        OutputStream outputStream = null;

                                        try {
                                            byte[] fileReader = new byte[4096];

                                            long fileSize = body.contentLength();
                                            long fileSizeDownloaded = 0;

                                            inputStream = body.byteStream();
                                            outputStream = new FileOutputStream(invoice);

                                            while (true) {
                                                int read = inputStream.read(fileReader);

                                                if (read == -1) {
                                                    break;
                                                }

                                                outputStream.write(fileReader, 0, read);

                                                fileSizeDownloaded += read;

                                                Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                                            }

                                            outputStream.flush();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } finally {
                                            if (inputStream != null) {
                                                inputStream.close();
                                            }

                                            if (outputStream != null) {
                                                outputStream.close();
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }*/

                                    paymentMethodsActivity.setResult(1);
                                    paymentMethodsActivity.finish();

                                } else try {
                                    Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                            response.errorBody().string(), R.string.keyError);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<Order> call, Throwable t) {
                                waitDialog.dismiss();
                                Utils.snackUp(paymentMethodsActivity.findViewById(R.id.rootView),
                                        R.string.errPaymentFailed);
                                Log.d(TAG, t.toString());
                            }
                        });
                    }
                });
    }

    /**
     * Methods to handle parceling
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dataSet);
    }

    private RVPaymentMethodsAdapter(Parcel in) {
        dataSet = in.createTypedArrayList(MCard.CREATOR);
    }

    public static final Parcelable.Creator<RVPaymentMethodsAdapter> CREATOR =
            new Parcelable.Creator<RVPaymentMethodsAdapter>() {
        @Override
        public RVPaymentMethodsAdapter createFromParcel(Parcel source) {
            return new RVPaymentMethodsAdapter(source);
        }

        @Override
        public RVPaymentMethodsAdapter[] newArray(int size) {
            return new RVPaymentMethodsAdapter[size];
        }
    };
}
