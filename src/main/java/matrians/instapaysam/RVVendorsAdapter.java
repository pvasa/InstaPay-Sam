package matrians.instapaysam;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import matrians.instapaysam.Schemas.Vendor;

/**
 Team Matrians
 */

class RVVendorsAdapter extends RecyclerView.Adapter<RVVendorsAdapter.ViewHolder> implements Parcelable {

    private List<Vendor> dataset;

    /**
     * Constructor to initialize the dataset.
     * @param dataset - set of the data to show in RecyclerView
     */
    RVVendorsAdapter(List<Vendor> dataset) {
        this.dataset = dataset;
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCompanyName,
                tvCompanyAddr,
                tvPhone;
        ViewHolder(View v) {
            super(v);
            tvCompanyName = (TextView) v.findViewById(R.id.tvCompanyName);
            tvCompanyAddr = (TextView) v.findViewById(R.id.tvCompanyAddr);
            tvPhone = (TextView) v.findViewById(R.id.tvPhone);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ScanActivity.class);
                    intent.putExtra(v.getContext().getString(R.string.keyVendor),
                            tvCompanyName.getText().toString());
                    v.getContext().startActivity(intent);
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
    public RVVendorsAdapter.ViewHolder onCreateViewHolder (
            ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataset
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCompanyName.setText(dataset.get(position).company_name);
        holder.tvCompanyAddr.setText(dataset.get(position).company_addr);
        holder.tvPhone.setText(dataset.get(position).phone);
    }

    /** Return the size of your dataset (invoked by the layout manager)
     * @return size of the dataset
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.dataset);
    }

    private RVVendorsAdapter(Parcel in) {
        this.dataset = new ArrayList<>();
        in.readList(this.dataset, Vendor.class.getClassLoader());
    }

    public static final Parcelable.Creator<RVVendorsAdapter> CREATOR =
            new Parcelable.Creator<RVVendorsAdapter>() {
        @Override
        public RVVendorsAdapter createFromParcel(Parcel source) {
            return new RVVendorsAdapter(source);
        }

        @Override
        public RVVendorsAdapter[] newArray(int size) {
            return new RVVendorsAdapter[size];
        }
    };
}
