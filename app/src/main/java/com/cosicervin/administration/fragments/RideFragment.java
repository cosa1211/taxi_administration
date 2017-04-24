package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Driver;
import com.cosicervin.administration.domain.DriverAdapter;
import com.cosicervin.administration.domain.Ride;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RideFragment extends Fragment {
    Ride ride;
    RequestQueue requestQueue;
    ArrayList drivers;
    View view;
    String URL = Params.URL+"getdrivers.php";
    String SEND_URL=Params.URL+"assignRide.php";
    ListView listView;
    DriverAdapter driverAdapter;
    TextView details;
    int selectedDriver;
    Button button;


    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public RideFragment() {
        // Required empty public constructor
    }
    public  void initalize(){
        drivers = new ArrayList();
        listView = (ListView)view.findViewById(R.id.drivers_ride);
        details = (TextView)view.findViewById(R.id.detailLabel);
        details.setText("Preis: " + ride.getPrice() + "€  Adresse: " + ride.getAddr() + " Richtung: " + ride.getDir());
        button = (Button)view.findViewById(R.id.zuordnen_ride);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ride, container, false);
        requestQueue = Volley.newRequestQueue(getActivity());
        initalize();

        getFromDB();
        driverAdapter = new DriverAdapter(getActivity().getApplicationContext(),R.layout.driver_listview);
        listView.setAdapter(driverAdapter);

        //listview Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDriver = position;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
                MainFragment fragment = new MainFragment(null, null); //todo add server token and url
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
                fragmentTransaction.commit();
            }
        });


        return view;
    }

    public  void getFromDB(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray jArray;
                try {
                    jArray = response.getJSONArray("drivers");
                    for (int i=0; i<jArray.length();i++){
                        JSONObject o = jArray.getJSONObject(i);
                        Driver dr = new Driver();
                        dr.setName(o.getString("name"));
                        dr.setMail(o.getString("email"));
                        dr.setPhone(o.getString("phone"));
                        dr.setPhone(o.getString("car"));
                        drivers.add(dr);
                        driverAdapter.add(dr);
                        driverAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    Log.e("JSON Error", e.toString());
                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);

    }

    /**
     *
     *   public Map<String,String> getParams()throws AuthFailureError {
     HashMap<String,String> params = new HashMap<>();

     Driver d =(Driver) driverAdapter.getItem(selectedDriver);
     params.put("driver", d.getName());
     params.put("mail", d.getMail());

     params.put("ride", Integer.toString(ride.getId()));
     params.put("date", ride.getDate());
     params.put("time", ride.getTime());
     params.put("plz", ride.getPlz());
     params.put("addr", ride.getAddr());
     params.put("flnr", ride.getFlightNr());
     params.put("arrival", ride.getArrival());
     params.put("name", ride.getName());
     params.put("email", ride.getEmail());
     params.put("phone", ride.getPhone());
     params.put("persons", ride.getPersons());
     params.put("lugage", ride.getLugage());
     params.put("price", Integer.toString(ride.getPrice()));
     params.put("dir", ride.getDir());


     return  params;


     }
     * Assinging a ride to a driver in the data base
     */
    public  void send() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SEND_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("RESPONSE: ",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR: ", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> params = new HashMap<>();

                    Driver d =(Driver) driverAdapter.getItem(selectedDriver);
                    params.put("driver", d.getName());
                    params.put("mail", d.getMail());

                    params.put("ride", Integer.toString(ride.getId()));
                    params.put("date", ride.getDate());
                    params.put("time", ride.getTime());
                    params.put("plz", ride.getPlz());
                    params.put("addr", ride.getAddr());
                    params.put("flnr", ride.getFlightNr());
                    params.put("arrival", ride.getArrival());
                    params.put("name", ride.getName());
                    params.put("email", ride.getEmail());
                    params.put("phone", ride.getPhone());
                    params.put("persons", ride.getPersons());
                    params.put("lugage", ride.getLugage());
                    params.put("price", Integer.toString(ride.getPrice()));
                    params.put("dir", ride.getDir());


                    return  params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
