package matrians.instapaysam;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import matrians.instapaysam.Schemas.Vendor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 Team Matrians
 **/

public class VendorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendors);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View view;
        if ((view = findViewById(R.id.toolbar_layout)) != null) {
            ((CollapsingToolbarLayout) view).setTitle(getString(R.string.title_vendors));
        }

        Call<List<Vendor>> call = Server.connect().getVendors();
        call.enqueue(new Callback<List<Vendor>>() {
            @Override
            public void onResponse(Call<List<Vendor>> call, Response<List<Vendor>> response) {
                Parcelable adapter = new RVVendorsAdapter(response.body());
                Fragment fragment = new RVFrag();
                Bundle args = new Bundle();
                args.putParcelable(getString(R.string.keyAdapter), adapter);
                fragment.setArguments(args);
                getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
            }

            @Override
            public void onFailure(Call<List<Vendor>> call, Throwable t) {
                Log.d("Retrofit error", t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendors, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_logout:
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.remove(getString(R.string.login_id));
                editor.remove(getString(R.string.login_status));
                editor.apply();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}