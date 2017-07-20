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

import hr.unipu.duda.justintime.activities.FacilityDetailActivity;
import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.model.Facility;

public class FacilityArrayAdapter extends ArrayAdapter<Facility> {

    Context context;
    List<Facility> facilities;

    public FacilityArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Facility> facilities) {
        super(context, resource, facilities);

        this.context = context;
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Facility facility = facilities.get(position);

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.facility_item, null);
        }
        TextView textView = (TextView) view.findViewById(R.id.tvFacilityListName);
        textView.setText(facility.getName());

        //info gumbić treba prikazati detalje o odabranoj ustanovi
        ImageView infoImage = (ImageView) view.findViewById(R.id.imageButtonFacilityInfo);
        infoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FacilityDetailActivity.class);
                intent.putExtra("id", facility.getId());
                context.startActivity(intent);
            }
        });


        return view;
    }
}
