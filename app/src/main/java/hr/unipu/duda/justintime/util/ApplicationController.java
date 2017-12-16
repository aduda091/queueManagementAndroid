package hr.unipu.duda.justintime.util;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hr.unipu.duda.justintime.model.Facility;
import hr.unipu.duda.justintime.model.User;


public class ApplicationController extends Application{
    private static ApplicationController mInstance;
    public static final String PREFS_NAME = "UserData";
    public static final String API_URL = "https://justin-time.herokuapp.com";

    public static final String ID = "id";
    public static final String MAIL = "mail";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String TOKEN = "token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private RequestQueue volleyQueue;
    private List<Facility> facilities;

    public static synchronized ApplicationController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        volleyQueue = Volley.newRequestQueue(this);

        downloadFacilities();
    }

    private void downloadFacilities() {
        //dohvaćanje svih ustanova
        facilities = new ArrayList<>();
        String url = API_URL + "/facility/read-all";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0; i<response.length();i++) {
                    try {
                        JSONObject facilityObject = response.getJSONObject(i);
                        Facility facility = new Facility();
                        facility.setId(facilityObject.getString("id"));
                        facility.setName(facilityObject.getString("name"));
                        facilities.add(facility);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse", "onErrorResponse: " + error.getMessage());
                //ako je došlo do greške - vjerojatno Heroku još spava, pokušaj ponovno za 5 sekundi
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadFacilities();
                    }
                }, 5000);
            }
        });

        volleyQueue.add(request);
    }


    public void saveUser(User user) {
        editor = sharedPreferences.edit();
        editor.putString(ID, user.getId());
        editor.putString(MAIL, user.getMail());
        editor.putString(PASSWORD, user.getPassword());//todo: za potrebe lakšeg testiranja, nikako u praksi
        editor.putString(FIRSTNAME, user.getFirstName());
        editor.putString(LASTNAME, user.getLastName());
        editor.putString(TOKEN, user.getToken());

        editor.apply();
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

    public List<Facility> getFacilities() {
        return facilities;
    }
}
