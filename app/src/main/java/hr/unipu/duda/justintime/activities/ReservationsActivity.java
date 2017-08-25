package hr.unipu.duda.justintime.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.ReservationArrayAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;

public class ReservationsActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue volleyQueue;
    ListView reservationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);

        /*progressDialog = new ProgressDialog(ReservationsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Dohvaćanje podataka u tijeku...");
        progressDialog.setCancelable(false);
        if(!progressDialog.isShowing()) progressDialog.show();
        */

        List<Queue> reservations = new ArrayList<>();
        Facility facility = new Facility();
        facility.setName("Općina");
        Queue queue = new Queue("bla","Carina");
        queue.setFacility(facility);
        queue.setCurrentNumber(5);
        queue.setMyNumber(7);
        reservations.add(queue);

        Facility facility1 = new Facility();
        facility1.setName("Konzum");
        Queue queue1 = new Queue();
        queue1.setFacility(facility1);
        queue1.setName("Brza blagajna");
        queue1.setCurrentNumber(2);
        queue1.setMyNumber(3);
        reservations.add(queue1);


        reservationListView = (ListView) findViewById(R.id.reservationListView);
        reservationListView.setAdapter(new ReservationArrayAdapter(this, 0, reservations));

    }
}
