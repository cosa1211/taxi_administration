package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddDriverFragment extends Fragment {

    String URLaddDriver = Params.URL+"addDriver.php";
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

        queue = Volley.newRequestQueue(getActivity());
        v = inflater.inflate(R.layout.fragment_add_driver, container, false);
        name_edit = (EditText) v.findViewById(R.id.add_name);
        email_edit = (EditText)v.findViewById(R.id.add_email);
        phone_edit = (EditText)v.findViewById(R.id.add_phone);
        car_edit = (EditText)v.findViewById(R.id.add_car);
        addDriver(v);
        return  v;
    }


    public  void getVals(){
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
                getVals();
                if((name!=null)||(email!=null)||(phone!=null)) {

                    addDrivertoDb();
                    DriverFragment fragment = new DriverFragment();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
                    fragmentTransaction.commit();
                }
            }
        });

    }


  public void addDrivertoDb(){

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLaddDriver, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String,String> getParams()throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("name",name);
                headers.put("mail",email);
                headers.put("phone",phone);
                headers.put("car",car);
                Log.e("header", headers.toString());
                return  headers;


            }
        };

      queue.add(jsonObjectRequest);


    }



}
