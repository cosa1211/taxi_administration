package com.cosicervin.administration.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MyRideFragment extends Fragment implements GeneralFragment {
    private  View view;
    String INSERT_URL = Params.URL+"myRide.php";
    String date,time,addr,plz,dir,flnr,arrival,name,email,phone,pers,lugage;
    EditText dateEdit,timeEdit,plzEdit,flnrEdit,arrivalEdit,nameEdit,emailEdit,phoneEdit,persEdit,lugageEdit,addrEdit,dirEdit,priceEdit;
    int price;
    RequestQueue queue;
    Calendar calendar;
    public void init(){
        calendar = Calendar.getInstance();
        int y,d,m;
        y = calendar.get(Calendar.YEAR);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        m = calendar.get(Calendar.MONTH)+1;

        dateEdit = (EditText)view.findViewById(R.id.date);
        dateEdit.setText(d+"."+m+"."+y);
        dateEdit.setKeyListener(null);
        timeEdit = (EditText)view.findViewById(R.id.time);
        plzEdit = (EditText)view.findViewById(R.id.plz);
        flnrEdit = (EditText)view.findViewById(R.id.flnr);
        arrivalEdit = (EditText)view.findViewById(R.id.arrival);
        emailEdit = (EditText)view.findViewById(R.id.mail);
        phoneEdit = (EditText)view.findViewById(R.id.phone);
        nameEdit = (EditText)view.findViewById(R.id.name);
        persEdit = (EditText)view.findViewById(R.id.persons);
        lugageEdit = (EditText)view.findViewById(R.id.lugage);
        addrEdit = (EditText)view.findViewById(R.id.addr);
        dirEdit = (EditText)view.findViewById(R.id.dir);
        priceEdit = (EditText)view.findViewById(R.id.price);
        priceEdit.setText("0");
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_ride, container, false);
        init();
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(d);
            }
        });
        Button done = (Button)view.findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    getVals();
                if(price!=0) {
                    if ((date != "") && (date != null)) {
                        insertIntoDB();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Bitte Datum setzen", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity().getApplicationContext(),"Bitte Preis setzen",Toast.LENGTH_LONG).show();
                }
            }
        });

        return  view;
    }
    public  void getVals(){

        date = dateEdit.getText().toString();
        time = timeEdit.getText().toString();
        addr = addrEdit.getText().toString();
        plz = plzEdit.getText().toString();
        flnr = flnrEdit.getText().toString();
        dir = dirEdit.getText().toString();
        arrival = arrivalEdit.getText().toString();
        name = nameEdit.getText().toString();
        email = emailEdit.getText().toString();
        phone = phoneEdit.getText().toString();
        pers = persEdit.getText().toString();
        lugage = lugageEdit.getText().toString();
        price = Integer.parseInt(priceEdit.getText().toString());
    }
    public void insertIntoDB(){
        StringRequest request = new StringRequest(Request.Method.POST, INSERT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    Toast.makeText(getActivity().getApplicationContext(),"Fahrt wurde hinzugefugt.",Toast.LENGTH_LONG).show();

                    Fragment fragment = new MainFragment(null, null); // todo add server token and url
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    fragmentTransaction.commit();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error",error.toString());

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String > params = new HashMap<>();
                params.put("datum",date);
                params.put("zeit",time);
                params.put("plz",plz);
                params.put("adresse",addr);
                params.put("richtung",dir);
                params.put("ankunft",arrival);
                params.put("name",name);
                params.put("email",email);
                params.put("telefon",phone);
                params.put("personen",pers);
                params.put("koffer",lugage);
                params.put("price",Integer.toString(price));
                return params;
            }
        };
        queue.add(request);

    }


    private void showDatePicker(DatePickerDialog.OnDateSetListener callback) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(callback);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            dateEdit.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear+1)
                    + "." + String.valueOf(year));
        }
    };

}
