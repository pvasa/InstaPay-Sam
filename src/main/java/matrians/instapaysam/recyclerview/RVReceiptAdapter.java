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
import matrians.instapaysam.pojo.Product;

/**
 * Team Matrians
 */
public class RVReceiptAdapter
        extends RecyclerView.Adapter<RVReceiptAdapter.ViewHolder> implements Parcelable {
    private List<Product> dataSet;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVReceiptAdapter(List<Product> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,
                tvProductPrice,
                tvQuantity;
        ViewHolder(View v) {
            super(v);
            tvProductName = (TextView) v.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) v.findViewById(R.id.tvProductPrice);
            tvQuantity = (TextView) v.findViewById(R.id.tvQuantity);
        }
    }

    /** Create new views (invoked by the layout manager)
     * @param parent - Parent ViewGroup
     * @param viewType - int
     * @return custom ViewHolder
     */
    @Override
    public RVReceiptAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_receipt, parent, false);
        return new RVReceiptAdapter.ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(final RVReceiptAdapter.ViewHolder holder, int position) {
        final Product currentProduct = dataSet.get(holder.getAdapterPosition());
        holder.tvProductName.setText(currentProduct.name);
        holder.tvProductPrice.setText(String.valueOf(
                currentProduct.price * currentProduct.quantity));
        holder.tvQuantity.setText(String.valueOf(currentProduct.quantity));
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

    private RVReceiptAdapter(Parcel in) {
        this.dataSet = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Parcelable.Creator<RVReceiptAdapter> CREATOR = new Parcelable.Creator<RVReceiptAdapter>() {
        @Override
        public RVReceiptAdapter createFromParcel(Parcel source) {
            return new RVReceiptAdapter(source);
        }

        @Override
        public RVReceiptAdapter[] newArray(int size) {
            return new RVReceiptAdapter[size];
        }
    };
}
