package hr.unipu.duda.justintime.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Queue;

public class ReservationArrayAdapter extends ArrayAdapter<Queue> {

    Context context;
    List<Queue> queues;

    public ReservationArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Queue> queues) {
        super(context, resource, queues);

        this.context = context;
        this.queues = queues;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Queue queue = queues.get(position);

        //optimizacija
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.reservation_item, null);
        }
        TextView nameView = (TextView) view.findViewById(R.id.tvReservationName);
        nameView.setText(queue.getFacility().getName() + " - " + queue.getName());

        TextView myNumber = (TextView) view.findViewById(R.id.tvReservationMyNumber);
        myNumber.setText(String.valueOf(queue.getMyNumber()));

        TextView currentNumber = (TextView) view.findViewById(R.id.tvReservationCurrentNumber);
        currentNumber.setText(String.valueOf(queue.getCurrentNumber()));

        //todo: editirati nakon spremnih podataka o prosječnom vremenu čekanja
        TextView approximateWait = (TextView) view.findViewById(R.id.tvReservationApproximateWait);


        ImageButton exitButton = (ImageButton) view.findViewById(R.id.imgExitQueue);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Potvrda izlaza")
                        .setMessage("Sigurno želite izaći iz reda?")
                        .setNegativeButton("Ne", null)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface arg0, int arg1) {
                                //todo: pošalji API request za izlaz iz reda
                            }
                        }).create().show();
            }
        });

        return view;
    }
}
