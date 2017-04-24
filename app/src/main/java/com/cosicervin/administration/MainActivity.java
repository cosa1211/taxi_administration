package com.cosicervin.administration;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.fragments.AssignedFragment;
import com.cosicervin.administration.fragments.CouponsFragment;
import com.cosicervin.administration.fragments.DriverFragment;
import com.cosicervin.administration.fragments.GeneralFragment;
import com.cosicervin.administration.fragments.LoginFragment;
import com.cosicervin.administration.fragments.MainFragment;
import com.cosicervin.administration.fragments.MyRideFragment;
import com.cosicervin.administration.fragments.SumFragment;
import com.cosicervin.administration.server.database.ServerDataContract;
import com.cosicervin.administration.server.database.ServerDataDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import QRIntegrator.IntentIntegrator;
import QRIntegrator.IntentResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    /**
     * Token that is sent to the server on every request
     */
    public static String server_request_token = null;
    public static String server_url = null;

    ServerDataDbHelper dbHelper;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ServerDataDbHelper(getApplicationContext());

        db = dbHelper.getWritableDatabase();

        getServerInfo();

        MainFragment fragment = new MainFragment(server_request_token, server_url);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            //todo implement back button pressed on active fragment

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {

            Fragment fragment = new LoginFragment();

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container,(Fragment) fragment);
            fragmentTransaction.commit();

        }else if(id == R.id.action_log_out){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sind Sie sicher das Sie sich abmleden wollen?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        deleteToken();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

        }
        //todo implement paswword change
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        GeneralFragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            fragment = (MainFragment) new MainFragment(server_request_token, server_url);

        } else if (id == R.id.nav_driver) {
            fragment =  new DriverFragment();
        }else if (id == R.id.nav_zfahrten){
            fragment = new AssignedFragment();
        }else if (id == R.id.nav_sum){
            fragment = new SumFragment();
        }else  if(id ==R.id.nav_ride){
            fragment = new MyRideFragment();
        }//else if (id == R.id.nav_coupon){
           // fragment = new CouponsFragment();
       // }
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            // handle scan result
            Toast t = Toast.makeText(getApplicationContext(),scanResult.getContents(),Toast.LENGTH_LONG);
            t.show();
            GeneralFragment fragment = new CouponsFragment(scanResult.getContents());
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
            fragmentTransaction.commit();
        }
        // else continue with any other code you need in the method
    }

    /**
     * This method is used for retrieving the server data from the database
     */
    private void getServerInfo(){
        ServerDataDbHelper dbHelper = new ServerDataDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {ServerDataContract.ServerData.SERVER_URL, ServerDataContract.ServerData.SERVER_TOKEN};

        Cursor cursor = db.query(ServerDataContract.ServerData.TABLE_NAME, projection, null, null, null, null, null);

        cursor.moveToNext();
        try {
            server_url = cursor.getString(cursor.getColumnIndex(ServerDataContract.ServerData.SERVER_URL));
            server_request_token = cursor.getString(cursor.getColumnIndex(ServerDataContract.ServerData.SERVER_TOKEN));
        }catch (Exception e){

        }

        cursor.close();
    }

    private void deleteTokenFromDatabase(){
        String selection = ServerDataContract.ServerData._ID + ">?";
        String selection_args[]= {"0"};


        db.delete(ServerDataContract.ServerData.TABLE_NAME, selection, selection_args);

        server_request_token = null;
    }

    private void deleteToken(){
        if(server_url == null) return;
        RequestQueue requestQueue = MyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();

        Map<String, String> params = new HashMap<>();
        params.put("service", "2");
        params.put("token",server_request_token);

        String url = server_url + "server_authentication.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int responseCode = 0;


                try {
                     responseCode = response.getInt("code");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(responseCode == 1){
                    deleteTokenFromDatabase();

                    finish();

                }else if(responseCode == -1) {
                    Toast.makeText(MainActivity.this, "Authentication error\n", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(customRequest);
    }


}
