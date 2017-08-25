package hr.unipu.duda.justintime.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import hr.unipu.duda.justintime.activities.ReservationsActivity;
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
        int defaultColor = Color.parseColor("#303f9f");//colorAccent #ff4081
        int selectedColor = Color.parseColor("#ff4081");//colorPrimaryDark #303f9f

        String currentActivityName = getActivity().getClass().getSimpleName();

        if(currentActivityName.equalsIgnoreCase(FacilityListActivity.class.getSimpleName())) {
            //odabran popis ustanova
            navFacilities.setTextColor(selectedColor);
            navFacilities.setTypeface(Typeface.DEFAULT_BOLD);

            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
            navReservations.setTypeface(Typeface.DEFAULT);
            navProfile.setTypeface(Typeface.DEFAULT);
        }
        else if(currentActivityName.equalsIgnoreCase(ReservationsActivity.class.getSimpleName())) {
            //odabran popis rezervacija
            navReservations.setTextColor(selectedColor);
            navReservations.setTypeface(Typeface.DEFAULT_BOLD);

            navFacilities.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
            navFacilities.setTypeface(Typeface.DEFAULT);
            navProfile.setTypeface(Typeface.DEFAULT);
        }
        else if(currentActivityName.equalsIgnoreCase(ProfileActivity.class.getSimpleName())) {
            //odabrane postavke profila
            navProfile.setTextColor(selectedColor);
            navProfile.setTypeface(Typeface.DEFAULT_BOLD);

            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
            navFacilities.setTypeface(Typeface.DEFAULT);
            navReservations.setTypeface(Typeface.DEFAULT);
        }
        else {
            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);

            navFacilities.setTypeface(Typeface.DEFAULT);
            navReservations.setTypeface(Typeface.DEFAULT);
            navProfile.setTypeface(Typeface.DEFAULT);
        }

        //gumb za popis ustanova
        navFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FacilityListActivity.class);
                startActivity(intent);
            }
        });

        //gumb za rezervacije
        navReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(UserController.getInstance().isRemembered()) {
                    //korisnik je prijavljen
                    intent = new Intent(getActivity(), ReservationsActivity.class);
                } else {
                    //korisnik nije prijavljen - odvedimo ga na zaslon prijave
                    intent = new Intent(getActivity(), LoginActivity.class);
                }
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
