package com.cosicervin.administration.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.activities.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class MyRideFragment extends Fragment implements GeneralFragment {
    private  View view;

    String serverUrl;

    String token;

    String date, time, addr, plz, dir, flnr, arrival, name, email, phone, pers, lugage;

    EditText dateEdit, timeEdit, plzEdit, flnrEdit, arrivalEdit, nameEdit, emailEdit, phoneEdit, persEdit, lugageEdit, addrEdit, priceEdit;

    Spinner dirSpinner;

    String selectedDate;

    int price;

    RequestQueue queue;

    Calendar calendar;

    ArrayAdapter<String> adapter;

    int focusCount = 0;

    public void init(){
        calendar = Calendar.getInstance();

        int y,d,m;

        y = calendar.get(Calendar.YEAR);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        m = calendar.get(Calendar.MONTH)+1;

        dateEdit = (EditText)view.findViewById(R.id.date_ride_edit);

        selectedDate = d + "." + m + "." + y;
        dateEdit.setText(selectedDate);
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

        dirSpinner = (Spinner)view.findViewById(R.id.dir); // todo should me dd spinner

        adapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item);
        adapter.add("Zum Flughafen");
        adapter.add("Vom Flughafen");

        dirSpinner.setAdapter(adapter);



        priceEdit = (EditText)view.findViewById(R.id.price);

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        serverUrl = ((MainActivity) getActivity()).server_services;

        token = ((MainActivity)getActivity()).server_request_token;
    }
    private void resetFields(){
        timeEdit.setText("");
        plzEdit.setText("");
        flnrEdit.setText("");
        arrivalEdit.setText("");
        emailEdit.setText("");
        phoneEdit.setText("");
        nameEdit.setText("");
        persEdit.setText("");
        lugageEdit.setText("");
        addrEdit.setText("");
        priceEdit.setText("");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_ride, container, false);
        init();
        dateEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && focusCount != 0){
                    showDatePicker(d);
                }
                focusCount++;
            }
        });
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

        dir = dirSpinner.getSelectedItem().toString();

        arrival = arrivalEdit.getText().toString();

        name = nameEdit.getText().toString();

        email = emailEdit.getText().toString();

        phone = phoneEdit.getText().toString();

        pers = persEdit.getText().toString();

        lugage = lugageEdit.getText().toString();

        price = Integer.parseInt(priceEdit.getText().toString());
    }

    public void insertIntoDB(){
        HashMap<String,String > params = new HashMap<>();

        params.put("ride_date", date);
        params.put("ride_time", time);
        params.put("ride_zip", plz);
        params.put("address", addr);
        params.put("direction", dir);
        params.put("comes_from", arrival);
        params.put("name", name);
        params.put("email", email);
        params.put("phone", phone);
        params.put("persons", pers);
        params.put("lugage", lugage);
        params.put("flight_number", flnr);
        params.put("price", Integer.toString(price));
        params.put("token", token);
        params.put("service", "14");

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, serverUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("code") == 2){
                        Toast.makeText(getContext(), "Fahrt hinzugefügt.", Toast.LENGTH_SHORT).show();
                        resetFields();
                    }else {
                        Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
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
           // selectedDate =(String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1) + "-" +String.valueOf(dayOfMonth));

        }
    };

}
