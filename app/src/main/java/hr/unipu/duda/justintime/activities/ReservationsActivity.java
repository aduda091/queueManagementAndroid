package hr.unipu.duda.justintime.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.adapters.ReservationAdapter;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.util.AppController;

public class ReservationsActivity extends AppCompatActivity {

    //svakih koliko milisekundi se osvježavaju rezervacije - za potrebe testiranja 10 sekundi,
    //u produkciji nikako toliko često
    public static final int DELAY = 10000;
    RequestQueue volleyQueue;
    TextView emptyView;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<Reservation> reservations;
    SwipeRefreshLayout swipeContainer;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations);
        setTitle("Moje rezervacije");

        volleyQueue = Volley.newRequestQueue(this);
        emptyView = findViewById(R.id.empty_view);

        //reservations = AppController.getInstance().getReservations();
        recyclerView = (RecyclerView) findViewById(R.id.reservationRecyclerView);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReservationsActivity.this));

        swipeContainer.setSize(SwipeRefreshLayout.LARGE);
        swipeContainer.setColorSchemeResources(R.color.colorPrimary);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateReservations();
            }
        });


        //handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateReservations();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            populateReservations();
        }
    };

    private void populateReservations() {
        swipeContainer.setRefreshing(true);
        //dohvaćanje svih rezervacija trenutnog korisnika

        reservations = new ArrayList<>();
        String url = AppController.API_URL + "/users/me";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Reservations response", "onResponse: " + response.toString());
                try {
                    JSONArray reservationsArray = response.getJSONObject("user").getJSONArray("reservations");
                    if (reservationsArray.length() == 0) {
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);

                        //handler.removeCallbacks(runnable);
                    } else {
                        for (int i = 0; i < reservationsArray.length(); i++) {
                            JSONObject object = reservationsArray.getJSONObject(i);
                            JSONObject queueObject = object.getJSONObject("queue");
                            JSONObject facilityObject = queueObject.getJSONObject("facility");

                            Facility facility = new Facility();
                            facility.setName(facilityObject.getString("name"));

                            Queue queue = new Queue();
                            queue.setName(queueObject.getString("name"));
                            queue.setId(queueObject.getString("_id"));
                            queue.setCurrent(queueObject.getInt("current"));

                            Reservation reservation = new Reservation();
                            reservation.setQueue(queue);
                            reservation.setFacility(facility);
                            reservation.setNumber(object.getInt("number"));
                            reservation.setId(object.getString("_id"));

                            reservations.add(reservation);
                            //osjveži rezervaciju u singletonu (istovremeno provjerava izmjene reda)
                            AppController.getInstance().updateReservations(reservation);

                            FirebaseMessaging.getInstance().subscribeToTopic(queue.getId());
                        }

                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);

                        //handler.postDelayed(runnable, DELAY);
                    }
                    adapter = new ReservationAdapter(ReservationsActivity.this, reservations);
                    recyclerView.setAdapter(adapter);

                    swipeContainer.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("reservations error", "onErrorResponse: " + error.getMessage());

                swipeContainer.setRefreshing(false);
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getAuthorizationHeader();
            }
        };

        volleyQueue.add(request);
    }
}
