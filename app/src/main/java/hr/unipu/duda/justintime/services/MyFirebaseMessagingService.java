package hr.unipu.duda.justintime.services;



import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import hr.unipu.duda.justintime.model.Queue;
import hr.unipu.duda.justintime.model.Reservation;
import hr.unipu.duda.justintime.util.AppController;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Messaging Service";
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            updateReservation(remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }

    }

    private void updateReservation(Map<String, String> messageData) {
        try {
            //dohvati ID reda i trenutni broj iz notifikacije
            String queueId = messageData.get("queueId");
            String currentStr = messageData.get("current");

//            Log.d(TAG, "updateReservation: queueId: " + queueId + " , current:" + currentStr);

            //dohvati korisnikovu rezervaciju po ID-u reda iz kontrolera
            Reservation oldReservation = AppController.getInstance().getReservationByQueueId(queueId);

//            Log.d(TAG, "oldReservation: " +oldReservation);
            //stvaranje kopija bitnih polja - inače se po referenci mijenja stara rezervacija
            Queue queue = new Queue();
            queue.setId(queueId);
            queue.setName(oldReservation.getQueue().getName());
            queue.setCurrent(Integer.parseInt(currentStr));

            Reservation reservation = new Reservation();
            reservation.setId(oldReservation.getId());
            reservation.setFacility(oldReservation.getFacility());
            reservation.setNumber(oldReservation.getNumber());
            reservation.setQueue(queue);

            //oldReservation.getQueue().setCurrent(Integer.parseInt(currentStr)); // ovo bi mijenjalo original
            //ažuriraj rezervaciju u kontroleru
            AppController.getInstance().updateReservations(reservation);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
