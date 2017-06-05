package com.cosicervin.administration.fragments.dialogs;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDriverFragment extends DialogFragment {

    String serverUrl;

    String serverToken;

    RequestQueue queue;

    String name,email,phone,car;

    EditText name_edit,email_edit,phone_edit,car_edit;

    public AddDriverFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v;

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        v = inflater.inflate(R.layout.fragment_add_driver, container, false);

        serverUrl = ((MainActivity)getActivity()).server_url;

        serverToken = ((MainActivity)getActivity()).server_request_token;

        name_edit = (EditText) v.findViewById(R.id.add_name);

        email_edit = (EditText)v.findViewById(R.id.add_email);

        phone_edit = (EditText)v.findViewById(R.id.add_phone);

        car_edit = (EditText)v.findViewById(R.id.add_car);

        addDriver(v);

        return  v;
    }


    public  void getValues(){
        name = name_edit.getText().toString();
        email = email_edit.getText().toString();
        phone = email_edit.getText().toString();
        car = car_edit.getText().toString();

    }
    public void addDriver(View v ){

        Button button = (Button)v.findViewById(R.id.addDriverToDb);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValues();
                if((name != null)||(email != null)||(phone != null)) {

                    saveNewDriver();
                    dismiss();
                }
            }
        });

    }


  public void saveNewDriver(){

      Map<String, String > params = new HashMap<>();
      params.put("token", serverToken);
      params.put("service", "11");
      params.put("driver_name", name);
      params.put("driver_email", email);
      params.put("driver_phone", phone);
      params.put("driver_car", car);

      final String URL = serverUrl + "/administration_services.php";

      CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
          @Override
          public void onResponse(JSONObject response) {

          }
      }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
      });

      queue.add(customRequest);


  }



}
