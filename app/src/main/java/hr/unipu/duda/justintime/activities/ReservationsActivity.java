package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.ReservationAdapter;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.util.AppController;

public class ReservationsActivity extends AppCompatActivity {
//    ProgressDialog progressDialog;
//    RequestQueue volleyQueue;
//    JsonObjectRequest request;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List <Reservation> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        setTitle("Moje rezervacije");

        //reservations = AppController.getInstance().getReservations();
        recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReservationsActivity.this));
        updateReservations();

//        progressDialog = new ProgressDialog(ReservationsActivity.this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
//        progressDialog.setCancelable(false);
//        if(!progressDialog.isShowing()) progressDialog.show();


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateReservations();
    }

    private void updateReservations() {
        reservations = AppController.getInstance().getReservations();
        adapter = new ReservationAdapter(ReservationsActivity.this, reservations);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
