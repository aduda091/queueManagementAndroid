package hr.unipu.duda.justintime.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.FacilityListActivity;
import hr.unipu.duda.justintime.activities.LoginActivity;
import hr.unipu.duda.justintime.activities.ProfileActivity;
import hr.unipu.duda.justintime.activities.ReservationsActivity;
import hr.unipu.duda.justintime.util.AppController;


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
        int defaultColor = 0;
        int selectedColor = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            defaultColor = getResources().getColor(R.color.colorNavigationDefault, null);
            selectedColor = getResources().getColor(R.color.colorNavigationSelected, null);
        } else {
            defaultColor = getResources().getColor(R.color.colorNavigationDefault);
            selectedColor = getResources().getColor(R.color.colorNavigationSelected);
        }


        final String currentActivityName = getActivity().getClass().getSimpleName();

        if (currentActivityName.equalsIgnoreCase(FacilityListActivity.class.getSimpleName())) {
            //odabran popis ustanova
            navFacilities.setTextColor(selectedColor);

            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
        } else if (currentActivityName.equalsIgnoreCase(ReservationsActivity.class.getSimpleName())) {
            //odabran popis rezervacija
            navReservations.setTextColor(selectedColor);

            navFacilities.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
        } else if (currentActivityName.equalsIgnoreCase(ProfileActivity.class.getSimpleName())) {
            //odabrane postavke profila
            navProfile.setTextColor(selectedColor);

            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
        } else {
            navFacilities.setTextColor(defaultColor);
            navReservations.setTextColor(defaultColor);
            navProfile.setTextColor(defaultColor);
        }

        //gumb za popis ustanova
        navFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentActivityName.equalsIgnoreCase(FacilityListActivity.class.getSimpleName())) {

                    Intent intent = new Intent(getActivity(), FacilityListActivity.class);
                    startActivity(intent);
                }
            }
        });

        //gumb za rezervacije
        navReservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentActivityName.equalsIgnoreCase(ReservationsActivity.class.getSimpleName())) {
                    Intent intent;
                    if (AppController.getInstance().isRemembered()) {
                        //korisnik je prijavljen
                        intent = new Intent(getActivity(), ReservationsActivity.class);
                    } else {
                        //korisnik nije prijavljen - odvedimo ga na zaslon prijave
                        if (currentActivityName.equalsIgnoreCase(LoginActivity.class.getSimpleName())) {
                            //osim ako već je na zaslonu prijave, prikaži poruku
                            Snackbar.make(view, R.string.login_first, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        intent = new Intent(getActivity(), LoginActivity.class);
                    }
                    startActivity(intent);
                }
            }
        });

        //gumb za postavke profila
        navProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentActivityName.equalsIgnoreCase(ProfileActivity.class.getSimpleName())) {
                    Intent intent;
                    if (AppController.getInstance().isRemembered()) {
                        //korisnik je prijavljen
                        intent = new Intent(getActivity(), ProfileActivity.class);
                    } else {
                        //korisnik nije prijavljen - odvedimo ga na zaslon prijave
                        if (currentActivityName.equalsIgnoreCase(LoginActivity.class.getSimpleName())) {
                            //osim ako već je na zaslonu prijave, prikaži poruku
                            Snackbar.make(v, R.string.login_first, Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        intent = new Intent(getActivity(), LoginActivity.class);
                    }
                    startActivity(intent);
                }
            }
        });
        return view;
    }
}
