package hr.unipu.duda.justintime.util;

import android.app.Application;
import android.content.SharedPreferences;

import hr.unipu.duda.justintime.model.User;


public class UserController extends Application{
    private static UserController mInstance;
    public static final String PREFS_NAME = "UserData";

    public static final String ID = "id";
    public static final String MAIL = "mail";
    public static final String PASSWORD = "password";
    public static final String FIRSTNAME = "firstName";
    public static final String LASTNAME = "lastName";
    public static final String TOKEN = "token";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static synchronized UserController getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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
}
