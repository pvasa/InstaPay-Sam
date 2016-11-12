package matrians.instapaysam;

/**
 Team Matrians
 **/

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import matrians.instapaysam.Schemas.Vendor;

class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<Vendor> dataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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
                    intent.putExtra("vendor", tvCompanyName.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RVAdapter(List<Vendor> dataset) {
        this.dataset = dataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.tvCompanyName.setText(dataset.get(position).company_name);
        holder.tvCompanyAddr.setText(dataset.get(position).company_addr);
        holder.tvPhone.setText(dataset.get(position).phone);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
