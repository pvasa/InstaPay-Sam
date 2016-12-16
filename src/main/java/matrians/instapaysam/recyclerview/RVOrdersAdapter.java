package matrians.instapaysam.recyclerview;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import matrians.instapaysam.OrdersActivity;
import matrians.instapaysam.R;
import matrians.instapaysam.pojo.Order;
import matrians.instapaysam.pojo.Product;

/**
 * Team Matrians
 * Fetches and renders list of previous orders from server
 */
public class RVOrdersAdapter
        extends RecyclerView.Adapter<RVOrdersAdapter.ViewHolder> implements Parcelable {

    private List<Order> dataSet; // The list of orders
    private Context context;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVOrdersAdapter(List<Order> dataSet, Context context) {
        this.dataSet = dataSet;
        this.context = context;
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
        CardView cardView;
        ViewHolder(View v) {
            super(v);
            tvVendorName = (TextView) v.findViewById(R.id.tvVendorName);
            tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
            tvTotalAmount = (TextView) v.findViewById(R.id.tvTotalAmount);
            cardView = (CardView) v.findViewById(R.id.cardView);
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
    public void onBindViewHolder(final RVOrdersAdapter.ViewHolder holder, int position) {
        holder.tvVendorName.setText(dataSet.get(position).getVendorName());
        holder.tvTotalAmount.setText(String.valueOf(dataSet.get(position).getAmount()));
        holder.tvTimeStamp.setText(dataSet.get(position).getTimeStamp());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new OrderDetailsFrag();
                Bundle args = new Bundle();
                args.putParcelable(
                        context.getString(R.string.keyOrder),
                        dataSet.get(holder.getAdapterPosition()));
                fragment.setArguments(args);
                if (context.getResources().getBoolean(R.bool.isTablet))
                    ((OrdersActivity)context).getFragmentManager().beginTransaction().replace(
                            R.id.orderDetailsFrag, fragment)
                            .addToBackStack("OrderDetails").commitAllowingStateLoss();
                else ((OrdersActivity)context).getFragmentManager().beginTransaction().replace(
                        R.id.content, fragment)
                        .addToBackStack("OrderDetails").commitAllowingStateLoss();
            }
        });
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

    @SuppressWarnings("WeakerAccess")
    public static class OrderDetailsFrag extends Fragment {

        private static final String TAG = OrderDetailsFrag.class.getName();

        Order order;
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            order = getArguments().getParcelable(getString(R.string.keyOrder));
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_order_details, container, false);
            ((TextView) view.findViewById(R.id.tvReceiptNumber)).setText(order.getReceiptNumber());
            ((TextView) view.findViewById(R.id.tvVendorName)).setText(order.getVendorName());
            ((TextView) view.findViewById(R.id.tvVendorId)).setText(order.getVendorID());
            ((TextView) view.findViewById(R.id.tvUserEmail)).setText(order.getUserEmail());
            ((TextView) view.findViewById(R.id.tvUserId)).setText(order.getUserID());
            ((TextView) view.findViewById(R.id.tvTotalAmount)).setText(String.valueOf(order.getAmount()));
            ((TextView) view.findViewById(R.id.tvTimeStamp)).setText(order.getTimeStamp());
            for (Product product : order.getProductList()) {
                Log.d(TAG, product.name);
                Log.d(TAG, String.valueOf(product.quantity));
            }
            return view;
        }
    }
}
