package hr.unipu.duda.justintime.adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.FacilityDetailActivity;
import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;

public class QueueArrayAdapter extends ArrayAdapter<Queue> {

    Context context;
    List<Queue> queues;

    public QueueArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Queue> queues) {
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
            view = inflater.inflate(R.layout.queue_item, null);
        }
        TextView nameView = (TextView) view.findViewById(R.id.tvQueueListName);
        nameView.setText(queue.getName());

        TextView priorityView = (TextView) view.findViewById(R.id.tvQueueListPriority);
        priorityView.setText(String.valueOf(queue.getPriority()));

        return view;
    }
}
