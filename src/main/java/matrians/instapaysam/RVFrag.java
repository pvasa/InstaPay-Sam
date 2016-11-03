package matrians.instapaysam;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("WeakerAccess")
public class RVFrag extends Fragment {

    final String BASE_URL = "https://instapay-animus.rhcloud.com/";
    private final int PERMISSION_INTERNET = 1;

    private RecyclerView recyclerView;
    private RVAdapter mAdapter;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        if (recyclerView != null) {

            if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.INTERNET}, PERMISSION_INTERNET);
            } else loadVendors();

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    void loadVendors() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InstaPayEndpointInterface instaPayService =
                retrofit.create(InstaPayEndpointInterface.class);

        Call<List<Vendor>> call = instaPayService.getVendors();
        call.enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {

                // specify an adapter (see also next example)
                mAdapter = new RVAdapter(response.body());
                recyclerView.setAdapter(mAdapter);

                for (Vendor vendor: response.body()) {
                    Log.d(vendor.first_name, vendor.last_name);
                    Log.d(vendor.company_name, vendor.company_addr);
                    Log.d(vendor.email, vendor.postal_code);
                    Log.d(vendor.phone, vendor.phone);
                }
            }

            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                Log.d("Retrofit error", t.toString());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_INTERNET &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadVendors();
        }
    }
}
