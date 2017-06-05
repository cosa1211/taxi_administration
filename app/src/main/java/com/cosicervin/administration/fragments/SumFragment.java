package com.cosicervin.administration.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.domain.Driver;
import com.cosicervin.administration.domain.Ride;
import com.cosicervin.administration.listAdapters.RideListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SumFragment extends Fragment implements GeneralFragment {

    ListView sumList;

    EditText start,end;

    TextView insgesamt;

    View  view;

    RideListAdapter adapter;

    int year,month,day;

    Calendar calendar;

    RequestQueue queue;

    String startDate;

    String endDate;


    Spinner selectDriverSpinner;

    String serverToken;

    String serverURL;

    ArrayList<Driver> drivers;

    ArrayList<Ride> rides;

    String driverName;

    int focusCount = 0;

    public SumFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sum, container, false);

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        init();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(date1);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(date2);

            }
        });

        sumList = (ListView) view.findViewById(R.id.driver_sum_list);

        adapter = new RideListAdapter(getContext(), R.layout.rides_list_layout, null, null);

        sumList.setAdapter(adapter);

        selectDriverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(position != 0) {
                   onDriverSelected();
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return  view;
    }

    /**
     * initialization
     */
    public void init(){

        rides = new ArrayList<>();
        start = (EditText)view.findViewById(R.id.start_date);
        start.setKeyListener(null);
        start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && focusCount != 0){
                    showDatePicker(date1);
                }
                focusCount ++;
            }
        });

        end = (EditText) view.findViewById(R.id.end_date);
        end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDatePicker(date2);
                }
            }
        });
        end.setKeyListener(null);

        insgesamt = (TextView)view.findViewById(R.id.sum_label);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        selectDriverSpinner = (Spinner) view.findViewById(R.id.select_driver_for_sum_spinner);

        serverToken = ((MainActivity)getActivity()).server_request_token;
        serverURL = ((MainActivity)getActivity()).server_services;

        drivers = new ArrayList<>();

        fetchAllDrivers();


    }

    private void onDriverSelected(){

        if(selectDriverSpinner.getSelectedItemPosition() == 0) return;

        driverName = selectDriverSpinner.getSelectedItem().toString();

        startDate = start.getText().toString();

        endDate = end.getText().toString();

        if(startDate == null || endDate == null){
            Toast.makeText(getContext(), "Bitte Datum wählen.", Toast.LENGTH_SHORT).show();
            return;
        }

        fetchDrivenRidesFromServer();

    }


    private void fetchDrivenRidesFromServer(){

        Map<String ,String> params = new HashMap<>();
        params.put("token", serverToken);
        params.put("service", "13");
        params.put("start_date", startDate);
        params.put("end_date", endDate);
        params.put("driver_name", driverName);



        CustomRequest customRequest = new CustomRequest(Request.Method.POST, serverURL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                Log.i("response", response.toString());
                try {
                    if(response.getInt("code") == -1){
                        Toast.makeText(getActivity().getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.deleteAll();

                rides.clear();

                adapter.notifyDataSetChanged();

                rides = new ArrayList<>();
                rides = parseJSONObjetToRidesArrayList(response);

                adapter.addAll(rides);
                adapter.notifyDataSetChanged();

                insgesamt.setText("Insgesamt: "+ calculateSum() + "€");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Eror response","Error "+error.getMessage() + serverURL);

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

    DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            start.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear+1)
                    + "." + String.valueOf(year));
        }
    };
    DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            end.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear+1)
                    + "." + String.valueOf(year));
        }
    };

    /**
     * this method fetchs all drivers from server and calls the populate spinner method to fill the spinner
     */
    private void fetchAllDrivers(){
        Map<String, String> params = new HashMap<>();
        params.put("token", serverToken);
        params.put("service","5");

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, serverURL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray jsonArray = response.getJSONArray("drivers");

                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        Driver driver = new Driver();
                        driver.setId(object.getString("driver_id"));
                        driver.setName(object.getString("driver_name"));
                        driver.setMail(object.getString("driver_email"));
                        driver.setPhone(object.getString("driver_phone"));
                        driver.setCar(object.getString("driver_car"));

                        drivers.add(driver);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                populateDriverSpinner();

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(customRequest);

    }

    private void populateDriverSpinner(){

        ArrayList<String> driverNames = new ArrayList<>();
        driverNames.add("Fahrer wählen");

        for(Driver d : drivers){
            driverNames.add(d.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item);

        adapter.addAll(driverNames);

        selectDriverSpinner.setAdapter(adapter);

    }
    /**
     * This method takes an JSONObject of rides and returns a ArrayList of rides
     * @param jsonObject
     * @return
     */
    private ArrayList<Ride> parseJSONObjetToRidesArrayList(JSONObject jsonObject){
        ArrayList<Ride> toReturn = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("summary");
            for(int i = 0; i<jsonArray.length();i++){

                JSONObject object = jsonArray.getJSONObject(i);

                Ride ride = new Ride();

                ride.setId(object.getInt("id"));
                ride.setDate(object.getString("date"));
                ride.setTime(object.getString("time").substring(0,5));
                ride.setPlz(object.getString("zip"));
                ride.setAddr(object.getString("address"));
                ride.setDir(object.getString("direction"));
                ride.setFlightNr(object.getString("flight_number"));
                ride.setArrival(object.getString("comes_from"));
                ride.setName(object.getString("name"));
                ride.setEmail(object.getString("email"));
                ride.setPhone(object.getString("phone"));
                ride.setPersons(object.getString("persons"));
                ride.setLugage(object.getString("lugage"));
                ride.setPrice(object.getInt("price"));

                if(object.isNull("token")){
                    ride.setPending(false);
                }else{
                    ride.setPending(true);
                }

                if(object.getInt("hasdriver") == 1) {

                    ride.setHasdriver(true);

                }else {
                    ride.setHasdriver(false);
                }

                if(object.getInt("childseat") == 1){
                    ride.setChildseat(true);
                }

                toReturn.add(ride);

            }
        } catch (JSONException e) {
            Log.d("Parse error", e.getMessage());
        }
        return  toReturn;
    }

    private int calculateSum(){
        int sum = 0;

        for(Ride r : rides){
            sum += r.getPrice();
        }

        return sum;
    }

}



