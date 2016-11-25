package matrians.instapaysam.recyclerview;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import java.util.List;

import matrians.instapaysam.R;
import matrians.instapaysam.ReceiptActivity;
import matrians.instapaysam.Server;
import matrians.instapaysam.Utils;
import matrians.instapaysam.schemas.MCard;
import matrians.instapaysam.schemas.Payment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Team Matrians
 */
public class RVPayNowAdapter extends RecyclerView.Adapter<RVPayNowAdapter.ViewHolder> implements Parcelable {

    private static List<MCard> dataSet;
    private static ProgressDialog waitDialog;
    private static String TAG = RVPayNowAdapter.class.getName();
    private static float amount;
    private static Parcelable productsAdapter;
    private static String vendorName;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVPayNowAdapter(List<MCard> dataSet,
                           float payable, Parcelable adapter,
                           String vendorName) {
        RVPayNowAdapter.dataSet = dataSet;
        amount = payable;
        productsAdapter = adapter;
        RVPayNowAdapter.vendorName = vendorName;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardLast4Digits;
        String cardNumber, CVC;
        int expMonth, expYear;
        ViewHolder(View v) {
            super(v);
            cardName = (TextView) v.findViewById(R.id.tvCardName);
            cardLast4Digits = (TextView) v.findViewById(R.id.tvLast4Digits);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    waitDialog = Utils.showProgress(view.getContext(),
                            view.getContext().getString(R.string.dialogProcessingPayment));

                    final String _id = PreferenceManager.getDefaultSharedPreferences(
                            view.getContext()).getString(
                            view.getContext().getString(R.string.prefUserId), null);
                    final String userEmail = PreferenceManager.getDefaultSharedPreferences(
                            view.getContext()).getString(
                            view.getContext().getString(R.string.prefLoginId), null);
                    try {
                        generateTokenAndPay(
                                view.getContext(),
                                new Card(cardNumber, expMonth, expYear, CVC),
                                _id, userEmail);
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static void generateTokenAndPay (
            final Context context, Card card, final String _id, final String userEmail)
            throws AuthenticationException {

        new Stripe(context.getString(R.string.stripeTestPublishableKey)).createToken(
                card, new TokenCallback() {
                    @Override
                    public void onError(Exception error) {
                        Toast.makeText(context, context.getString(
                                R.string.errPaymentFailed), Toast.LENGTH_LONG).show();
                        Log.d(TAG, error.getLocalizedMessage());
                    }
                    @Override
                    public void onSuccess(Token token) {
                        Payment payment =
                                new Payment(_id, userEmail,
                                        token.getId(), amount);
                        Call<Payment> call = Server.connect().pay(payment);
                        call.enqueue(new Callback<Payment>() {
                            @Override
                            public void onResponse(Call<Payment> call, Response<Payment> response) {
                                waitDialog.dismiss();
                                if (200 == response.code()) {
                                    Intent intent = new Intent(context, ReceiptActivity.class);
                                    intent.putExtra(context.getString(
                                            R.string.keyProducts), productsAdapter);
                                    intent.putExtra(
                                            context.getString(R.string.keyVendorName), vendorName);
                                    context.startActivity(intent);

                                    Toast.makeText(context,
                                            R.string.txtPaymentSuccess,
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(context,
                                            R.string.errPaymentFailed,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Payment> call, Throwable t) {
                                waitDialog.dismiss();
                                Toast.makeText(context,
                                        R.string.errPaymentFailed, Toast.LENGTH_LONG).show();
                                Log.d(TAG, t.toString());
                            }
                        });
                    }
                });
    }

    /** Create new views (invoked by the layout manager)
     * @param parent - Parent ViewGroup
     * @param viewType - int
     * @return custom ViewHolder
     */
    @Override
    public RVPayNowAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_payment_methods, parent, false);
        return new RVPayNowAdapter.ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(RVPayNowAdapter.ViewHolder holder, int position) {
        MCard mCard = dataSet.get(position);
        holder.cardNumber = mCard.number;
        holder.expMonth = mCard.expMonth;
        holder.expYear = mCard.expYear;
        holder.CVC = mCard.CVC;

        holder.cardName.setText(mCard.name);
        holder.cardLast4Digits.setText(holder.cardNumber.substring(holder.cardNumber.length() - 4));
    }

    /** Return the size of your dataSet (invoked by the layout manager)
     * @return size of the dataSet
     */
    @Override
    public int getItemCount() {
        if (dataSet != null)
            return dataSet.size();
        return 0;
    }

    public void addCard(MCard mCard) {
        dataSet.add(mCard);
        notifyDataSetChanged();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dataSet);
    }

    private RVPayNowAdapter(Parcel in) {
        dataSet = in.createTypedArrayList(MCard.CREATOR);
    }

    public static final Parcelable.Creator<RVPayNowAdapter> CREATOR =
            new Parcelable.Creator<RVPayNowAdapter>() {
        @Override
        public RVPayNowAdapter createFromParcel(Parcel source) {
            return new RVPayNowAdapter(source);
        }

        @Override
        public RVPayNowAdapter[] newArray(int size) {
            return new RVPayNowAdapter[size];
        }
    };
}
