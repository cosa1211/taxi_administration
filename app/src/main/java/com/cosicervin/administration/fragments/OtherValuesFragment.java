package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherValuesFragment extends Fragment implements GeneralFragment{

    RequestQueue queue;

    String server_token;

    String server;

    View view;

    private EditText sameDistrictPriceEdit;

    private EditText otherDistrictPriceEdit;

    private Button otherDistrictPriceButton;

    private Button sameDistrictPriceButton;

    private void assignViews() {
        sameDistrictPriceEdit = (EditText) view.findViewById(R.id.same_district_price_edit);

        sameDistrictPriceButton = (Button) view.findViewById(R.id.same_district_price_button);

        otherDistrictPriceButton = (Button) view.findViewById(R.id.other_district_price_button);

        otherDistrictPriceEdit = (EditText) view.findViewById(R.id.other_district_price_edit);
    }



    public OtherValuesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_other_values, container, false);

        assignViews();

        server = ((MainActivity) getActivity()).server_url + "/administration_services.php";

        server_token = ((MainActivity)getActivity()).server_request_token;

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        fetchPrices();

        sameDistrictPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSameDistrictPriceButtonClicked();
            }
        });

        otherDistrictPriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOtherDistrictPriceButtonClicked();
            }
        });

        return view;
    }

    private void onOtherDistrictPriceButtonClicked(){
        int price = Integer.parseInt(otherDistrictPriceEdit.getText().toString());

        Map<String, String > params = new HashMap<>();
        params.put("token", server_token);
        params.put("service", "19");
        params.put("tag", "other_district");
        params.put("value", Integer.toString(price));

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, server, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("code") == 2){
                        Toast.makeText(getContext(), "Gespeichert", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(customRequest);

    }

    private void onSameDistrictPriceButtonClicked(){
        int price = Integer.parseInt(sameDistrictPriceEdit.getText().toString());

        Map<String, String > params = new HashMap<>();
        params.put("token", server_token);
        params.put("service", "19");
        params.put("tag", "same_district");
        params.put("value", Integer.toString(price));

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, server, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("code") == 2){
                        Toast.makeText(getContext(), "Gespeichert", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(customRequest);
    }

    private void fetchPrices(){
        Map<String, String > params = new HashMap<>();
        params.put("token", server_token);
        params.put("service", "20");

        CustomRequest custsom = new CustomRequest(Request.Method.POST, server, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("prices");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        if (object.getString("tag").equals("same_district")) {

                            sameDistrictPriceEdit.setText(object.getString("price"));


                        } else if (object.getString("tag").equals("other_district")) {

                            otherDistrictPriceEdit.setText(object.getString("price"));

                        }
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(custsom);


    }

}
