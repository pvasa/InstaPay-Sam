package matrians.instapaysam.recyclerview;

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

import matrians.instapaysam.R;
import matrians.instapaysam.ScanActivity;
import matrians.instapaysam.pojo.Vendor;

/**
 * Team Matrians
 */
public class RVVendorsAdapter
        extends RecyclerView.Adapter<RVVendorsAdapter.ViewHolder>
        implements Parcelable {

    private List<Vendor> dataSet;

    /**
     * Constructor to initialize the dataSet.
     * @param dataSet - set of the data to show in RecyclerView
     */
    public RVVendorsAdapter(List<Vendor> dataSet) {
        this.dataSet = dataSet;
    }

    public void addDataSet(List<Vendor> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    /**
     * Provide a reference to the views for each data item
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCompanyName,
                tvCompanyAddress,
                tvEmail,
                tvPhone;
        String vid;
        ViewHolder(View v) {
            super(v);
            tvCompanyName = (TextView) v.findViewById(R.id.tvCompanyName);
            tvCompanyAddress = (TextView) v.findViewById(R.id.tvCompanyAddr);
            tvEmail = (TextView) v.findViewById(R.id.tvCompanyEmail);
            tvPhone = (TextView) v.findViewById(R.id.tvPhone);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ScanActivity.class);
                    intent.putExtra(v.getContext().getString(R.string.keyVendorName),
                            tvCompanyName.getText().toString());
                    intent.putExtra(v.getContext().getString(R.string.keyVendorID), vid);
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
                .inflate(R.layout.card_view_vendors, parent, false);
        return new ViewHolder(v);
    }

    /** Replace the contents of a view (invoked by the layout manager)
     * @param holder - CardView
     * @param position - position of current element in dataSet
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCompanyName.setText(dataSet.get(position).companyName);
        holder.tvCompanyAddress.setText(dataSet.get(position).companyAddr);
        holder.tvEmail.setText(dataSet.get(position).email);
        holder.tvPhone.setText(dataSet.get(position).phone);
        holder.vid = dataSet.get(position)._id;
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
        dest.writeList(this.dataSet);
    }

    private RVVendorsAdapter(Parcel in) {
        this.dataSet = new ArrayList<>();
        in.readList(this.dataSet, Vendor.class.getClassLoader());
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
