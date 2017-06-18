package com.cosicervin.administration.fragments.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.domain.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ervincosic on 13/06/2017.
 */

public class EditPlaceDialogFragment extends NewPlaceFragment {

    private Place place;

    View view;

    String carPrice;

    String vanPrice;

    String busPrice;

    String placeName;

    String SERVER_URL;

    String TOKEN;

    RequestQueue queue;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);

        TOKEN = ((MainActivity)getActivity()).server_request_token;

        SERVER_URL = ((MainActivity) getActivity()).server_services;

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        if(place != null) {
            super.placeBusPriceEdit.setText(Integer.toString(place.getBusPrice()));
            super.placeCarPriceEdit.setText(Integer.toString(place.getCarPrice()));
            super.placeVanPriceEdit.setText(Integer.toString(place.getVanPrice()));
            super.placeNameEdit.setText(place.getName());
        }

        return view;
    }
    private boolean loadParams(){

        boolean validEdit = true;

        carPrice = placeCarPriceEdit.getText().toString();
        vanPrice = placeVanPriceEdit.getText().toString();
        busPrice = placeBusPriceEdit.getText().toString();
        placeName = placeNameEdit.getText().toString();

        if(carPrice == null || carPrice.length() == 0){
            validEdit = false;
        }else if(vanPrice == null || vanPrice.length() == 0){
            validEdit = false;
        }else if(busPrice == null || busPrice.length() == 0){
            validEdit = false;
        }else if(placeName == null || placeName.length() == 0){
            validEdit = false;
        }

        if(!validEdit){
            Toast.makeText(getActivity().getApplicationContext(), "Bitte alles ausfüllern", Toast.LENGTH_LONG);
        }

        return validEdit;
    }

    @Override
    protected void save() {
        boolean flag = loadParams();

        if(flag) {
            HashMap<String, String> params = new HashMap<>();
            params.put("service", "16");
            params.put("token", TOKEN);
            params.put("place_id", Integer.toString(place.getId()));
            params.put("place_name", placeName);
            params.put("car_price", carPrice);
            params.put("van_price", vanPrice);
            params.put("bus_price", busPrice);


            CustomRequest customRequest = new CustomRequest(Request.Method.POST, SERVER_URL, params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int code = 1;

                    try {
                        code = response.getInt("code");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(code == 2){//
                        // Toast.makeText(getActivity().getApplicationContext(), "Preis bearbeitet.", Toast.LENGTH_SHORT).show();
                        Log.i("Response", response.toString());
                    }else{
                        Log.i("Response", response.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            queue.add(customRequest);


        }
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;

    }
}
