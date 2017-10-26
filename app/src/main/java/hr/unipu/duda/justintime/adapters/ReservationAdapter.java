package hr.unipu.duda.justintime.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Queue;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder>{

    Context context;
    List<Queue> queues;

    public ReservationAdapter(Context context, List<Queue> queues) {
        this.context = context;
        this.queues = queues;
    }

    @Override
    public ReservationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
        return new ReservationAdapter.ViewHolder(v, context, (ArrayList<Queue>)queues);
    }

    @Override
    public void onBindViewHolder(ReservationAdapter.ViewHolder holder, int position) {
        final Queue queue = queues.get(position);
        holder.nameView.setText(queue.getFacility().getName() + " - " + queue.getName());
        holder.myNumber.setText(String.valueOf(queue.getMyNumber()));
        holder.currentNumber.setText(String.valueOf(queue.getCurrentNumber()));

    }

    @Override
    public int getItemCount() {
        return queues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nameView;
        TextView myNumber;
        TextView currentNumber;
        TextView approximateWait;
        ImageButton exitButton;


        public ViewHolder(View view, Context ctx, ArrayList<Queue> queueList) {
            super(view);
            queues = queueList;
            context = ctx;

            nameView = (TextView) view.findViewById(R.id.tvReservationName);
            myNumber = (TextView) view.findViewById(R.id.tvReservationMyNumber);
            currentNumber = (TextView) view.findViewById(R.id.tvReservationCurrentNumber);
            //todo: editirati nakon spremnih podataka o prosječnom vremenu čekanja
            approximateWait = (TextView) view.findViewById(R.id.tvReservationApproximateWait);
            exitButton = (ImageButton) view.findViewById(R.id.imgExitQueue);

            exitButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
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
    }
}
