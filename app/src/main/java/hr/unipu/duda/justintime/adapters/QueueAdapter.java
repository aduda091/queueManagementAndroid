package hr.unipu.duda.justintime.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.QueueDetailActivity;
import hr.unipu.duda.justintime.activities.QueueListActivity;
import hr.unipu.duda.justintime.activities.ReservationsActivity;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.util.AppController;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder>{

    Context context;
    List<Queue> queues;

    public QueueAdapter(Context context, List<Queue> queues) {
        this.context = context;
        this.queues = queues;
    }

    @Override
    public QueueAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_item, parent, false);
        return new QueueAdapter.ViewHolder(v, context, (ArrayList<Queue>)queues);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Queue queue = queues.get(position);
        holder.queueName.setText(queue.getName());
        holder.queuePriority.setText(String.valueOf(queue.getCurrent()));
    }

    @Override
    public int getItemCount() {
        return queues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView queueName;
        public TextView queuePriority;

        public ViewHolder(View view, Context ctx, ArrayList<Queue> queueList) {
            super(view);
            queues = queueList;
            context = ctx;

            queueName = (TextView) view.findViewById(R.id.tvQueueListName);
            queuePriority = (TextView) view.findViewById(R.id.tvQueueListPriority);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Queue queue = queues.get(position);
            //korisnik je već u redu kojeg je dodirnuo
            if(AppController.getInstance().hasReservation(queue.getId())) {
                Intent intent = new Intent(context, ReservationsActivity.class);
                context.startActivity(intent);
            } else {
                //korisnik nije već u tom redu
                Intent intent = new Intent(context, QueueDetailActivity.class);
                //todo: zamijeniti sa serijalizacijom/bundle
                intent.putExtra("queueId", queue.getId());
                intent.putExtra("queueName", queue.getName());
                intent.putExtra("facilityId", queue.getFacility().getId());
                intent.putExtra("facilityName", queue.getFacility().getName());
                intent.putExtra("queuePriority", queue.getCurrent());
                intent.putExtra("queueNext", queue.getNext());
                context.startActivity(intent);
            }
        }
    }
}
