package com.cosicervin.administration.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.server.database.ServerDataContract;
import com.cosicervin.administration.server.database.ServerDataDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;

    EditText passwordEditText;

    EditText serverEditText;

    Button loginButton;

    View view;

    RequestQueue queue;

    MyRequestQueue myRequestQueue;

    String token;

    String server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usernameEditText = (EditText)findViewById(R.id.username_edit);

        passwordEditText = (EditText)findViewById(R.id.password_edit);

        serverEditText = (EditText)findViewById(R.id.server_edit);

        loginButton = (Button)findViewById(R.id.login_button);

        myRequestQueue = MyRequestQueue.getInstance(getApplicationContext());

        queue = myRequestQueue.getRequestQueue();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTokenFromServer();
            }
        });
        setContentView(R.layout.activity_login);
    }

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

                    }else{
                        Toast.makeText(getApplicationContext(), "Bad request.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /**
                 * Start main activity and close this one
                 */
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("server", server);
                intent.putExtra("token", token);

                startActivity(intent);
                finish();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);

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
    /**
     * This method saves the received token to the SQLite database and deletes all old tokes
     * so there is only one token at any given moment
     */
    private void saveTokenToDb(){
        ServerDataDbHelper dbHelper = new ServerDataDbHelper(getApplicationContext());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(ServerDataContract.ServerData.SERVER_URL, server);
        cv.put(ServerDataContract.ServerData.SERVER_TOKEN, token);

        long id = db.insert(ServerDataContract.ServerData.TABLE_NAME, null, cv);

        String selection = ServerDataContract.ServerData._ID + "<?";

        String[] selectionArgs = {Long.toString(id)};

        db.delete(ServerDataContract.ServerData.TABLE_NAME, selection, selectionArgs);

    }
    private void resetFields(){
        usernameEditText.setText("");
        passwordEditText.setText("");
        serverEditText.setText("");
    }

}
