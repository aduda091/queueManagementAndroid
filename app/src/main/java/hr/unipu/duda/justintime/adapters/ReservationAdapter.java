package hr.unipu.duda.justintime.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.ReservationsActivity;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.util.AppController;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>{

    Context context;
    List<Reservation> reservations;

    public ReservationAdapter(Context context, List<Reservation> reservations) {
        this.context = context;
        this.reservations = reservations;
    }

    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        return new ReservationAdapter.ViewHolder(v, context, (ArrayList<Reservation>) reservations);
    }

    @Override
    public void onBindViewHolder(ReservationAdapter.ViewHolder holder, int position) {
        final Reservation reservation = reservations.get(position);
        holder.nameView.setText(reservation.getFacility().getName() + " - " + reservation.getQueue().getName());
        holder.myNumber.setText(String.valueOf(reservation.getNumber()));
        holder.currentNumber.setText(String.valueOf(reservation.getQueue().getCurrent()));
        //procjena vremena: moj broj - trenutno aktivni broj
        int approx = reservation.getNumber() - reservation.getQueue().getCurrent();
        holder.approximateWait.setText("Očekivano vrijeme čekanja: " + approx * 5 + " minuta");

        holder.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Potvrda izlaza")
                        .setMessage("Sigurno želite izaći iz reda?")
                        .setNegativeButton("Ne", null)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                exitQueue(reservation);
                            }
                        }).create().show();
            }
        });

    }

    //izlaz iz reda
    private void exitQueue(Reservation reservation) {
        RequestQueue volleyQueue = Volley.newRequestQueue(context);
        String url = AppController.API_URL + "/reservations/" + reservation.getId();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("ExitQueue", "onResponse: " +response);

                new AlertDialog.Builder(context)
                        .setTitle("Rezervacija otkazana")
                        .setMessage("Uspješno ste izašli iz reda")
                        .setPositiveButton("U redu", null)
                        .create().show();

                AppController.getInstance().downloadReservations();
                //pričekaj par sekundi da se osvježe rezervacije pa preusmjeri korisnika
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(context, ReservationsActivity.class);
                        //zamjena za recreate() ReservationsActivity
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }, 2000);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ExitQueueError", "onResponse: " +error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getAuthorizationHeader();
            }
        };

        volleyQueue.add(request);
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameView;
        TextView myNumber;
        TextView currentNumber;
        TextView approximateWait;
        ImageButton exitButton;


        public ViewHolder(View view, Context ctx, ArrayList<Reservation> reservationList) {
            super(view);
            reservations = reservationList;
            context = ctx;

            nameView = (TextView) view.findViewById(R.id.tvReservationName);
            myNumber = (TextView) view.findViewById(R.id.tvReservationMyNumber);
            currentNumber = (TextView) view.findViewById(R.id.tvReservationCurrentNumber);
            approximateWait = (TextView) view.findViewById(R.id.tvReservationApproximateWait);
            exitButton = (ImageButton) view.findViewById(R.id.imgExitQueue);

        }
    }
}
