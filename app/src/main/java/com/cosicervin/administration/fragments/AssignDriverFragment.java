package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Driver;
import com.cosicervin.administration.domain.Ride;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignDriverFragment extends DialogFragment {

    View view;

    Button assignDriverButton;

    Spinner allDriversSpinner;

    String serverUrl;

    String serverRequestToken;

    ArrayList<Driver> drivers;

    RequestQueue queue;

    Ride ride;

    Driver driver;

    ImageView imageView;



    public AssignDriverFragment(Ride ride, ArrayList<Driver> allDrivers, Driver assignedDriver, View imageView) {
        this.ride = ride;
        this.drivers = new ArrayList<>(allDrivers);
        this.driver = assignedDriver;
        this.imageView = (ImageView) imageView;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_assign_driver, container, false);

        queue = MyRequestQueue.getInstance(getActivity()).getRequestQueue();

        serverUrl = ((MainActivity)getActivity()).server_services;
        serverRequestToken = ((MainActivity)getActivity()).server_request_token;

        assignDriverButton = (Button) view.findViewById(R.id.assign_driver_button);
        allDriversSpinner = (Spinner) view.findViewById(R.id.drivers_spinner);

        populateDriverSpinner();

        assignDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignButtonClicked();
            }
        });


        return view;
    }

    /**
     * This method is used for populating the driver spinner
     */
    private void populateDriverSpinner(){
        ArrayList<String> driverNames = new ArrayList<>();
        driverNames.add("Fahrer entfernen");
        for(Driver d : drivers){
            driverNames.add(d.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item);

        adapter.addAll(driverNames);

        allDriversSpinner.setAdapter(adapter);
        /**
         * set the assigned driver as selected
         */
        if(ride.isHasdriver()) {
            allDriversSpinner.setSelection(driverNames.indexOf(driver.getName()));
        }else{
            allDriversSpinner.setSelection(0);
        }

    }

    private void assignButtonClicked(){

        int selectedDriverPosition = allDriversSpinner.getSelectedItemPosition();

        if(selectedDriverPosition != 0){
            /**
             * the selected driver position is the spinner id - 1 because the first one is empty
             */
            driver = drivers.get(selectedDriverPosition - 1);
        }else{
            selectedDriverPosition = -1;
        }




        /**
         * Check if there is a driver selected if he is update it on the server
         */
        if(selectedDriverPosition != -1) {
            Map<String, String> params = new HashMap<>();
            params.put("token", serverRequestToken);
            params.put("service", "6");
            params.put("ride_id", Integer.toString(ride.getId()));
            params.put("driver_id", driver.getId());
            params.put("driver_email", driver.getMail());



            CustomRequest customRequest = new CustomRequest(Request.Method.POST, serverUrl, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    int responseCode = 0;

                    try {
                        responseCode = response.getInt("code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (responseCode == 2) {
                        Toast.makeText(getActivity(), "Fahrt gesendet.", Toast.LENGTH_SHORT).show();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(customRequest);
            dismiss();

        }else{
            /**
             * In case of deleteing assigned driver
             */
            Map<String, String > params = new HashMap<>();
            params.put("token", serverRequestToken);
            params.put("service", "8");
            params.put("ride_id", Integer.toString(ride.getId()));

            CustomRequest customRequest = new CustomRequest(Request.Method.POST, serverUrl, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int code = -1;

                    try {
                        code = response.getInt("code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(code == 2){
                        imageView.setImageResource(R.drawable.ic_person_outline_black_24dp);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(customRequest);

            dismiss();
            Toast.makeText(getActivity(), "Fahrer wurde von der Fahrt entfernt.", Toast.LENGTH_LONG).show();
        }


    }



}
