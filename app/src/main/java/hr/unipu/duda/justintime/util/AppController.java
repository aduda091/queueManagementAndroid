package hr.unipu.duda.justintime.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.model.User;


public class AppController extends Application {
    private static AppController mInstance;
    public static final String PREFS_NAME = "UserData";
    public static final String API_URL = "https://dustin-time.herokuapp.com";


    public static final String ID = "id";
    public static final String MAIL = "mail";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String TOKEN = "token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private RequestQueue volleyQueue;
    private List<Reservation> reservations;

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        volleyQueue = Volley.newRequestQueue(this);

        if(isRemembered()) downloadReservations();

    }


    public void downloadReservations() {
        reservations = new ArrayList<>();
        String url = AppController.API_URL + "/users/me";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Reservations response", "onResponse: " +response.toString());
                try {
                    JSONArray reservationsArray = response.getJSONObject("user").getJSONArray("reservations");
                    for(int i=0; i<reservationsArray.length();i++) {
                        JSONObject object = reservationsArray.getJSONObject(i);
                        JSONObject queueObject = object.getJSONObject("queue");
                        JSONObject facilityObject = queueObject.getJSONObject("facility");

                        Facility facility = new Facility();
                        facility.setName(facilityObject.getString("name"));

                        Queue queue = new Queue();
                        queue.setName(queueObject.getString("name"));
                        queue.setId(queueObject.getString("_id"));
                        queue.setCurrent(queueObject.getInt("current"));

                        Reservation reservation = new Reservation();
                        reservation.setQueue(queue);
                        reservation.setFacility(facility);
                        reservation.setNumber(object.getInt("number"));
                        reservation.setId(object.getString("_id"));

                        reservations.add(reservation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("reservations error", "onErrorResponse: " +error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getAuthorizationHeader();
            }
        };

        volleyQueue.add(request);
    }

    public void saveUser(User user) {
        editor = sharedPreferences.edit();
        editor.putString(ID, user.getId());
        editor.putString(MAIL, user.getMail());
        //editor.putString(PASSWORD, user.getPassword());//todo: za potrebe lakšeg testiranja, nikako u praksi
        editor.putString(FIRSTNAME, user.getFirstName());
        editor.putString(LASTNAME, user.getLastName());
        editor.putString(TOKEN, user.getToken());

        editor.apply();
        //korisnik je sad prijavljen, dohvatiti njegove rezervacije
        downloadReservations();
    }

    public void updateUser(User user) {
        editor = sharedPreferences.edit();
        editor.putString(MAIL, user.getMail());
        editor.putString(FIRSTNAME, user.getFirstName());
        editor.putString(LASTNAME, user.getLastName());

        editor.apply();
    }

    public User getUser() {
        String id = sharedPreferences.getString(ID, "");
        String mail = sharedPreferences.getString(MAIL, "");
        String password = sharedPreferences.getString(PASSWORD, "");
        String firstName = sharedPreferences.getString(FIRSTNAME, "");
        String lastName = sharedPreferences.getString(LASTNAME, "");
        String token = sharedPreferences.getString(TOKEN, "");

        User user = new User(firstName, lastName, mail, password, token);
        user.setId(id);
        return user;
    }

    public String getToken() {
        return sharedPreferences.getString(TOKEN, "");
    }
    public Map getAuthorizationHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", getToken());
        return header;


    }

    public boolean isRemembered() {
        return sharedPreferences.contains(MAIL);
    }

    public void logout() {
        editor = sharedPreferences.edit();
        editor.remove(ID);
        editor.remove(MAIL);
        editor.remove(PASSWORD);
        editor.remove(FIRSTNAME);
        editor.remove(LASTNAME);
        editor.remove(TOKEN);
        editor.apply();
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}
