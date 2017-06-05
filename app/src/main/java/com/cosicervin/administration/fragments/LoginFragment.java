package com.cosicervin.administration.fragments;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.server.database.ServerDataContract;
import com.cosicervin.administration.server.database.ServerDataDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    EditText usernameEditText;

    EditText passwordEditText;

    EditText serverEditText;

    Button loginButton;

    View view;

    RequestQueue queue;

    MyRequestQueue myRequestQueue;

    String token;

    String server;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_login, container, false);

        usernameEditText = (EditText) view.findViewById(R.id.username_edit);

        passwordEditText = (EditText) view.findViewById(R.id.password_edit);

        serverEditText = (EditText) view.findViewById(R.id.server_edit);

        loginButton = (Button) view.findViewById(R.id.login_button);

        myRequestQueue = MyRequestQueue.getInstance(getContext());

        queue = myRequestQueue.getRequestQueue();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenFromServer();
            }
        });

        return view;
    }

    /**
     * This method is responsible for authenticating the username and password and receiving the server token
     */
    private void getTokenFromServer(){

        String username = usernameEditText.getText().toString();

        String password = passwordEditText.getText().toString();

        server = serverEditText.getText().toString();

        final String url = server + "/server_authentication.php";

        String passwordHash = sha256(password);

        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", passwordHash);
        params.put("service", "1");

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST ,url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {



                try {
                    int responseCode = response.getInt("code");

                    if(responseCode == 1){
                        token = response.getString("token");
                        saveTokenToDb();
                        resetFields();


                        ((MainActivity)getActivity()).server_url = server;
                        ((MainActivity)getActivity()).server_request_token = token;

                    }else{
                        Toast.makeText(getActivity().getApplicationContext(), "Bad request.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                GeneralFragment fragment = new MainFragment(token, url);
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.fragment_container, (Fragment) fragment);
                fm.commit();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);



    }

    /**
     * Deleting values from input fields
     */
    private void resetFields(){
        usernameEditText.setText("");
        passwordEditText.setText("");
        serverEditText.setText("");
    }

    /**
     * This method saves the received token to the SQLite database and deletes all old tokes
     * so there is only one token at any given moment
     */
    private void saveTokenToDb(){
        ServerDataDbHelper dbHelper = new ServerDataDbHelper(getActivity().getApplicationContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(ServerDataContract.ServerData.SERVER_URL, server);
        cv.put(ServerDataContract.ServerData.SERVER_TOKEN, token);

        long id = db.insert(ServerDataContract.ServerData.TABLE_NAME, null, cv);

        String selection = ServerDataContract.ServerData._ID + "<?";

        String[] selectionArgs = {Long.toString(id)};

        db.delete(ServerDataContract.ServerData.TABLE_NAME, selection, selectionArgs);

    }

    /**
     * Method for getting a sha-2 signature
     * @param base - String that you want to get the signature
     * @return sha-2 signature of the given string
     */
    private static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
