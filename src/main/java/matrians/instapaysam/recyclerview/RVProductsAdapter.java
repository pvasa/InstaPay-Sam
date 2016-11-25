package matrians.instapaysam.recyclerview;

import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.List;

import matrians.instapaysam.R;
import matrians.instapaysam.schemas.Product;

/**
 * Team Matrians
 */
public class RVProductsAdapter
        extends RecyclerView.Adapter<RVProductsAdapter.ViewHolder> implements Parcelable {

    private List<Product> dataSet;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVProductsAdapter(List<Product> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName,
                tvProductPrice,
                tvQuantity;
        View close;
        ViewHolder(View v) {
            super(v);
            tvProductName = (TextView) v.findViewById(R.id.tvProductName);
            tvProductPrice = (TextView) v.findViewById(R.id.tvProductPrice);
            tvQuantity = (TextView) v.findViewById(R.id.tvQuantity);
            close = v.findViewById(R.id.close);
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
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(final RVProductsAdapter.ViewHolder holder, int position) {
        final Product currentProduct = dataSet.get(holder.getAdapterPosition());
        holder.tvProductName.setText(currentProduct.name);
        holder.tvProductPrice.setText(String.valueOf(currentProduct.price));
        holder.tvQuantity.setText(String.valueOf(currentProduct.quantity));
        holder.tvQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NumberPicker picker = new NumberPicker(view.getContext());
                picker.setMinValue(1);
                picker.setMaxValue(currentProduct.maxQuantity);
                picker.setValue(currentProduct.quantity);
                new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.titleQuantity)
                .setView(picker)
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dataSet.get(holder.getAdapterPosition()).quantity = picker.getValue();
                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });
        holder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSet.remove(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
    }

    public String isProductPresent (String id) {
        for (Product product : dataSet) {
            if (id.equals(String.valueOf(product.id)))
                return product.name;
        }
        return "";
    }

    public void addProduct (Product product) {
        dataSet.add(product);
        notifyDataSetChanged();
    }

    public List<Product> getProductList() {
        return dataSet;
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

    private RVProductsAdapter(Parcel in) {
        this.dataSet = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Parcelable.Creator<RVProductsAdapter> CREATOR = new Parcelable.Creator<RVProductsAdapter>() {
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
