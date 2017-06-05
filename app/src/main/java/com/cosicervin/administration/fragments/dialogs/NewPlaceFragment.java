package com.cosicervin.administration.fragments.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.domain.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewPlaceFragment extends DialogFragment {

    View view;

    private EditText placeNameEdit;

    private EditText placeCarPriceEdit;

    private EditText placeVanPriceEdit;

    private EditText placeBusPriceEdit;

    private Button savePlaceButton;

    String token;

    String server;

    Place placeToSave;

    RequestQueue queue;

    private void assignViews() {
        placeNameEdit = (EditText) view.findViewById(R.id.place_name_edit);

        placeCarPriceEdit = (EditText) view.findViewById(R.id.place_car_price_edit);

        placeVanPriceEdit = (EditText) view.findViewById(R.id.place_van_price_edit);

        placeBusPriceEdit = (EditText) view.findViewById(R.id.place_bus_price_edit);

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        savePlaceButton = (Button) view.findViewById(R.id.save_place_button);
        savePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String carPricesString = placeCarPriceEdit.getText().toString();

                String vanPriceString = placeVanPriceEdit.getText().toString();

                String busPriceString = placeBusPriceEdit.getText().toString();

                String placeName = placeNameEdit.getText().toString();




                if(carPricesString != "" && vanPriceString != "" && busPriceString != "" && placeName != "") {

                    placeToSave = new Place();

                    placeToSave.setCarPrice(Integer.parseInt(carPricesString));
                    placeToSave.setVanPrice(Integer.parseInt(vanPriceString));
                    placeToSave.setBusPrice(Integer.parseInt(busPriceString));
                    placeToSave.setName(placeName);

                    saveNewPlace();
                    dismiss();
                }
            }
        });
    }


    public NewPlaceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_place, container, false);

        token = ((MainActivity)getActivity()).server_request_token;
        server = ((MainActivity) getActivity()).server_url;



        assignViews();


        return view;
    }

    private void saveNewPlace(){

        final String URL = server + "/administration_services.php";

        Map<String, String > params = new HashMap<>();
        params.put("token", token);
        params.put("service", "17");
        params.put("place_name", placeToSave.getName());
        params.put("car_price", Integer.toString(placeToSave.getCarPrice()));
        params.put("van_price", Integer.toString(placeToSave.getVanPrice()));
        params.put("bus_price", Integer.toString(placeToSave.getBusPrice()));

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int code = 0;

                try {
                    code = response.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("ResponseCode",Integer.toString(code));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ResponseError", error.getMessage());
            }
        });

        queue.add(customRequest);


    }

}
