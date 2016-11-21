package matrians.instapaysam;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import matrians.instapaysam.Schemas.Product;

/**
 * Team Matrians
 */
public class RVProductsAdapter
        extends RecyclerView.Adapter<RVProductsAdapter.ViewHolder>
        implements Parcelable {

    private List<Product> dataset;

    /**
     * Constructor to initialize the dataset.
     * @param dataset - set of the data to show in RecyclerView
     */
    RVProductsAdapter(List<Product> dataset) {
        this.dataset = dataset;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,
                tvProductPrice;
        ViewHolder(View v) {
            super(v);
            tvProductName = (TextView) v.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) v.findViewById(R.id.tvProductPrice);
        }
    }

    /** Create new views (invoked by the layout manager)
     * @param parent - Parent ViewGroup
     * @param viewType - int
     * @return custom ViewHolder
     */
    @Override
    public RVProductsAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_products, parent, false);
        return new RVProductsAdapter.ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataset
     */
    @Override
    public void onBindViewHolder(RVProductsAdapter.ViewHolder holder, int position) {
        holder.tvProductName.setText(dataset.get(position).name);
        holder.tvProductPrice.setText(String.valueOf(dataset.get(position).price));
    }

    public void addProduct (Product product) {
        Log.d("ADAPTER", product.name);
        dataset.add(product);
        notifyDataSetChanged();
    }

    /** Return the size of your dataset (invoked by the layout manager)
     * @return size of the dataset
     */
    @Override
    public int getItemCount() {
        if (dataset != null)
            return dataset.size();
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.dataset);
    }

    private RVProductsAdapter(Parcel in) {
        this.dataset = new ArrayList<>();
        in.readList(this.dataset, Product.class.getClassLoader());
    }

    public static final Parcelable.Creator<RVProductsAdapter> CREATOR =
            new Parcelable.Creator<RVProductsAdapter>() {
                @Override
                public RVProductsAdapter createFromParcel(Parcel source) {
                    return new RVProductsAdapter(source);
                }
                @Override
                public RVProductsAdapter[] newArray(int size) {
                    return new RVProductsAdapter[size];
                }
            };
}
