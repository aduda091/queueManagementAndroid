package hr.unipu.duda.justintime.requests;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginRequest extends JsonObjectRequest {
    private static final String LOGIN_REQUEST_URL = "https://justin-time.herokuapp.com/oauth/token";
    private Map<String, String> params;


    public LoginRequest(String username, String password, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        //jer nikako da proradi s params
        super(Method.GET, LOGIN_REQUEST_URL+"?grant_type=password&username="+username+"&password="+password, null, listener, errorListener);

    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError{
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String, String>();
        String creds = String.format("%s:%s","trusted-client","secret");
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.NO_WRAP);
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", auth);
        return headers;
    }
}
