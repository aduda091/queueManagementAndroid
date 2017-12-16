package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.FacilityAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.util.ApplicationController;

public class FacilityListActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Facility> facilities;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list);
        setTitle("Ustanove");

        recyclerView = (RecyclerView) findViewById(R.id.facilityRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(FacilityListActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        if(!progressDialog.isShowing()) progressDialog.show();

        populateFacilities();




    }

    private void populateFacilities() {
        facilities = ApplicationController.getInstance().getFacilities();
        adapter = new FacilityAdapter(FacilityListActivity.this, facilities);
        recyclerView.setAdapter(adapter);

        //nisu se stigle učitati ustanove, pokušaj ponovno
        if(facilities.isEmpty()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (facilities.isEmpty()) {
                        facilities = ApplicationController.getInstance().getFacilities();
                    } else {
                        //sakrij progress dialog i osvježi popis
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        adapter.notifyDataSetChanged();
                    }
                }
            }, 2500);
        } else {
            //učitale su se ustanove, sakrij dialog
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}
