package hr.unipu.duda.justintime.util;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.unipu.duda.justintime.R;
import hr.unipu.duda.justintime.activities.ReservationsActivity;
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
    private MediaPlayer player;

    public static synchronized AppController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        volleyQueue = Volley.newRequestQueue(this);

//        if(isRemembered()) updateReservations();
        player = MediaPlayer.create(this, R.raw.chime);

    }

    public void saveUser(User user) {
        editor = sharedPreferences.edit();
        editor.putString(ID, user.getId());
        editor.putString(MAIL, user.getMail());
        editor.putString(FIRSTNAME, user.getFirstName());
        editor.putString(LASTNAME, user.getLastName());
        editor.putString(TOKEN, user.getToken());

        editor.apply();
        //korisnik je sad prijavljen, dohvatiti njegove rezervacije
//        updateReservations();
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

        this.reservations.clear();
    }

    public void setReservations(List<Reservation> reservations) {
        try {
            if (this.reservations != null && !reservations.isEmpty()) {
                for (int i = 0; i < this.reservations.size(); i++) {

                    Reservation oldReservation = this.reservations.get(i);
                    Reservation newReservation = reservations.get(i);//indexOutOfBounds exception kad nema više rezervacija

                    if (newReservation.getQueue().getCurrent() == newReservation.getNumber()) {
                        //korisnik je upravo na redu, prikaži notifikaciju

                        //ali samo ako već nije bila prikazana
                        if(oldReservation.getQueue().getCurrent() != newReservation.getQueue().getCurrent()) {
                            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.hashtag_white)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                    .setContentTitle(newReservation.getFacility().getName() + " - " + newReservation.getQueue().getName())
                                    .setContentText("Vi ste na redu!");
                            notificationBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_LIGHTS);
                            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                                    new Intent(this, ReservationsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                            notificationBuilder.setContentIntent(contentIntent);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

                            notificationManagerCompat.notify(1, notificationBuilder.build());
                        }

                    } else if (oldReservation.getQueue().getCurrent() != newReservation.getQueue().getCurrent()) {
                        //promijenio se trenutni broj u redu
                        player.start();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        this.reservations = reservations;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}
