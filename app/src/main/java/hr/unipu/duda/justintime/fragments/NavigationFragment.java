package hr.unipu.duda.justintime.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.FacilityListActivity;
import hr.unipu.duda.justintime.activities.LoginActivity;
import hr.unipu.duda.justintime.activities.ProfileActivity;
import hr.unipu.duda.justintime.util.UserController;


public class NavigationFragment extends Fragment {

    Button navFacilities, navReservations, navProfile;




    public NavigationFragment() {
        // Required empty public constructor
    }


    public static NavigationFragment newInstance() {
        NavigationFragment fragment = new NavigationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container, false);
        navFacilities = (Button) view.findViewById(R.id.navFacilities);
        navReservations = (Button) view.findViewById(R.id.navReservations);
        navProfile = (Button) view.findViewById(R.id.navProfile);

        //boja teksta gumba u navigaciji ovisi o trenutnoj aktivnosti
        int defaultColor = Color.parseColor("#ff4081");//colorAccent
        int selectedColor = Color.parseColor("#303f9f");//colorPrimaryDark

        String currentActivityName = getActivity().getClass().getSimpleName();
        //Log.d("navFragment", "current activity name: " +currentActivityName);

        if(currentActivityName.equalsIgnoreCase(FacilityListActivity.class.getSimpleName())) {
            navFacilities.setTextColor(selectedColor);
            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
        }
        //todo: reservation activity
        else if(currentActivityName.equalsIgnoreCase(ProfileActivity.class.getSimpleName())) {
            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(selectedColor);
        }
        else {
            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
        }

        //gumb za popis ustanova
        navFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FacilityListActivity.class);
                startActivity(intent);
            }
        });

        //gumb za postavke profila
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(UserController.getInstance().isRemembered()) {
                    //korisnik je prijavljen
                    intent = new Intent(getActivity(), ProfileActivity.class);
                } else {
                    //korisnik nije prijavljen - odvedimo ga na zaslon prijave
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
                startActivity(intent);
            }
        });
        return view;
    }
}
