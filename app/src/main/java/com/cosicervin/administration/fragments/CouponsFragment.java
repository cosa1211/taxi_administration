package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;
import com.cosicervin.administration.fragments.GeneralFragment;

import java.util.HashMap;
import java.util.Map;

import QRIntegrator.IntentIntegrator;


/**
 * A simple {@link Fragment} subclass.
 */
public class CouponsFragment extends Fragment implements GeneralFragment {
    View view;

    RequestQueue queue;

    String couponCode;

    EditText couponEdit;

    String URL = Params.URL + "validateCoupon.php";
    String DELETE_URL = Params.URL + "deleteCoupon.php";

    TextView validMsg, invalidMsg;


    public CouponsFragment() {
        // Required empty public constructor
    }
    public CouponsFragment(String couponCode){
        this.couponCode = couponCode;
    }

    IntentIntegrator integrator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        queue = Volley.newRequestQueue(getActivity());

        view = inflater.inflate(R.layout.fragment_coupons, container, false);
        couponEdit = (EditText)view.findViewById(R.id.coupon);
        validMsg = (TextView)view.findViewById(R.id.validMsg);
        invalidMsg = (TextView) view.findViewById(R.id.invalidMsg);
        validateCouponCode(view);

        return view;
    }


    private  void validateCouponCode(View view){
        Button validateButton = (Button) view.findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                integrator = new IntentIntegrator(getActivity());
                integrator.initiateScan();
                couponEdit.setText(couponCode);
                couponCode = couponEdit.getText().toString();
               // validateCodeInDb(couponCode);
            }
        });
        final Button deleteButton = (Button) view.findViewById(R.id.useCouponButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponCode = couponEdit.getText().toString();
                deleteCoupon(couponCode);
            }
        });
    }

    private void validateCodeInDb(final String code){
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")){
                    validMsg.setVisibility(View.VISIBLE);
                    invalidMsg.setVisibility(View.INVISIBLE);
                }else if(response.equals("0")){
                    invalidMsg.setVisibility(View.VISIBLE);
                    validMsg.setVisibility(View.INVISIBLE);
                }
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }){
            @Override
            public Map<String,String> getParams()throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("coupon",code);
                return  headers;

            }
        };
        queue.add(request);

    }
    private void deleteCoupon(final String code){
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")) {
                    Toast toast = Toast.makeText(getActivity(), "Gutschein eingelöst", Toast.LENGTH_LONG);
                    toast.show();
                }else {
                    Toast toast = Toast.makeText(getActivity(),"Server Error",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }){
            @Override
            public Map<String,String> getParams()throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("coupon",code);
                return  headers;

            }
        };
        queue.add(request);
    }


}
