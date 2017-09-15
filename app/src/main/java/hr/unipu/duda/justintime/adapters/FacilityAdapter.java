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
import hr.unipu.duda.justintime.activities.FacilityDetailActivity;
import hr.unipu.duda.justintime.activities.QueueListActivity;
import hr.unipu.duda.justintime.model.Facility;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.ViewHolder>{

    Context context;
    List<Facility> facilities;

    public FacilityAdapter(Context context, List<Facility> facilities) {
        this.context = context;
        this.facilities = facilities;
    }

    @Override
    public FacilityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityAdapter.ViewHolder(v, context, (ArrayList<Facility>)facilities);
    }

    @Override
    public void onBindViewHolder(FacilityAdapter.ViewHolder holder, int position) {
        final Facility facility = facilities.get(position);
        holder.facilityName.setText(facility.getName());
        holder.infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FacilityDetailActivity.class);
                intent.putExtra("id", facility.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView facilityName;
        public ImageView infoImage;

        public ViewHolder(View view, Context ctx, ArrayList<Facility> facilityList) {
            super(view);
            facilities = facilityList;
            context = ctx;

            facilityName = (TextView) view.findViewById(R.id.tvFacilityListName);
            infoImage = (ImageView) view.findViewById(R.id.imageButtonFacilityInfo);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Facility facility = facilities.get(position);
            Intent intent = new Intent(context, QueueListActivity.class);
            intent.putExtra("id", facility.getId());
            intent.putExtra("name", facility.getName());
            context.startActivity(intent);
        }
    }
}
