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
import matrians.instapaysam.pojo.Order;

/**
 * Team Matrians
 * Fetches and renders list of previous orders from server
 */
public class RVOrdersAdapter
        extends RecyclerView.Adapter<RVOrdersAdapter.ViewHolder> implements Parcelable {

    private List<Order> dataSet; // The list of orders

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVOrdersAdapter(List<Order> dataSet) {
        this.dataSet = dataSet;
    }

    public void addDataSet(List<Order> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVendorName,
                tvTimeStamp,
                tvTotalAmount;
        ViewHolder(View v) {
            super(v);
            tvVendorName = (TextView) v.findViewById(R.id.tvVendorName);
            tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
            tvTotalAmount = (TextView) v.findViewById(R.id.tvTotalAmount);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    /** Create new views (invoked by the layout manager)
     * @param parent - Parent ViewGroup
     * @param viewType - int
     * @return custom ViewHolder
     */
    @Override
    public RVOrdersAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_orders, parent, false);
        return new RVOrdersAdapter.ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(RVOrdersAdapter.ViewHolder holder, int position) {
        holder.tvVendorName.setText(dataSet.get(position).vendorName);
        holder.tvTotalAmount.setText(String.valueOf(dataSet.get(position).amount));
        holder.tvTimeStamp.setText(dataSet.get(position).timeStamp);
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

    private RVOrdersAdapter(Parcel in) {
        this.dataSet = in.createTypedArrayList(Order.CREATOR);
    }

    public static final Parcelable.Creator<RVOrdersAdapter> CREATOR = new Parcelable.Creator<RVOrdersAdapter>() {
        @Override
        public RVOrdersAdapter createFromParcel(Parcel source) {
            return new RVOrdersAdapter(source);
        }

        @Override
        public RVOrdersAdapter[] newArray(int size) {
            return new RVOrdersAdapter[size];
        }
    };
}
