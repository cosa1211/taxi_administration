package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import domain.Ride;
import listAdapters.RideListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignedFragment extends Fragment implements GeneralFragment {
    ListView assigned_list;
    TextView fahrer_label;
    View view;
    RequestQueue requestQueue;
    String URL = Params.URL+ "getAssignedRides.php";
    ArrayList<Ride> rides;
    RideListAdapter adapter;
    HashMap<Integer,String > assigned_drivers;
    int selectedRide;

    public AssignedFragment() {
        // Required empty public constructor
    }
    public void init(){
        assigned_list = (ListView)view.findViewById(R.id.assigned_list);
        fahrer_label = (TextView)view.findViewById(R.id.fahrer_label);
        rides = new ArrayList<>();
        assigned_drivers = new HashMap<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assigned, container, false);
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        init();

        adapter = new RideListAdapter(getActivity().getBaseContext(),R.layout.assigned_rides);
        assigned_list.setAdapter(adapter);
        getFromDb();
        assigned_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedRide = position;
                Ride r = rides.get(selectedRide);
                fahrer_label.setText("Fahrer: " + assigned_drivers.get(r.getId()));

            }
        });


        return  view;

    }
    public void getFromDb(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( Request.Method.POST,URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                rides = parseRides(response);
                adapter.addAll(rides);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public ArrayList<Ride> parseRides(JSONObject jsonObject){
        ArrayList<Ride> toReturn = new ArrayList<>();
        try {

            JSONArray jsonArray = jsonObject.getJSONArray("rides");
            JSONArray   tempArray = jsonObject.getJSONArray("rides");
            for(int i = 0; i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Ride ride = new Ride();
                ride.setId(object.getInt("id"));
                ride.setDate(object.getString("datum"));
                ride.setTime(object.getString("zeit"));
                ride.setPlz(object.getString("plz"));
                ride.setAddr(object.getString("adresse"));
                ride.setDir(object.getString("richtung"));
                ride.setFlightNr(object.getString("flugnummer"));
                ride.setArrival(object.getString("ankunft"));
                ride.setName(object.getString("name"));
                ride.setEmail(object.getString("email"));
                ride.setPhone(object.getString("telefon"));
                ride.setPersons(object.getString("personen"));
                ride.setLugage(object.getString("koffer"));
                ride.setPrice(object.getInt("price"));
                if(object.getInt("hasdriver")==1) {
                    ride.setHasdriver(true);
                }else ride.setHasdriver(false);

                toReturn.add(ride);
            }

            jsonArray = jsonObject.getJSONArray("drivers");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                assigned_drivers.put(object.getInt("ride"),object.getString("driver"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  toReturn;

    }

}
