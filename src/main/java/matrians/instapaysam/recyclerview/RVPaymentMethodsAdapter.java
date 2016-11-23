package matrians.instapaysam.recyclerview;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import matrians.instapaysam.R;
import matrians.instapaysam.schemas.MCard;

/**
 * Team Matrians
 */
public class RVPaymentMethodsAdapter extends RecyclerView.Adapter<RVPaymentMethodsAdapter.ViewHolder> implements Parcelable {

    private List<MCard> dataSet;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVPaymentMethodsAdapter(List<MCard> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardName, cardLast4Digits;
        ViewHolder(View v) {
            super(v);
            cardName = (TextView) v.findViewById(R.id.tvCardName);
            cardLast4Digits = (TextView) v.findViewById(R.id.tvLast4Digits);
        }
    }

    /** Create new views (invoked by the layout manager)
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

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(RVPaymentMethodsAdapter.ViewHolder holder, int position) {
        holder.cardName.setText(dataSet.get(position).cardName);
        holder.cardLast4Digits.setText(dataSet.get(position).cardLast4Digits);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.dataSet);
    }

    private RVPaymentMethodsAdapter(Parcel in) {
        this.dataSet = in.createTypedArrayList(MCard.CREATOR);
    }

    public static final Parcelable.Creator<RVPaymentMethodsAdapter> CREATOR = new Parcelable.Creator<RVPaymentMethodsAdapter>() {
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
