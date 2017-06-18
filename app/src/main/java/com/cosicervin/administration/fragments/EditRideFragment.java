package com.cosicervin.administration.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.activities.MainActivity;
import com.cosicervin.administration.domain.Ride;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditRideFragment extends DialogFragment {

    EditText dateEdit, timeEdit, plzEdit, flnrEdit, arrivalEdit, nameEdit, emailEdit, phoneEdit, persEdit, lugageEdit, addrEdit, dirEdit, priceEdit;

    View view;

    Calendar calendar;

    View imageView;

    Ride ride;

    Date rideDate;

    Button doneButton;

    RequestQueue requestQueue;

    String serviceUrl;

    String serverRequestToken;

    Spinner directionSpinner;

    public EditRideFragment(View view) {
        this.imageView = view;


        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_ride, container, false);

        ride = (Ride) imageView.getTag(R.id.ride_in_image_view);

        requestQueue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        serviceUrl = ((MainActivity) getActivity()).server_services;
        serverRequestToken = ((MainActivity) getActivity()).server_request_token;

        doneButton = (Button) view.findViewById(R.id.done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneButtonClicked();
            }
        });

        initilize();



        return view;
    }

    private void initilize(){

        directionSpinner = (Spinner) view.findViewById(R.id.dir);

        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.support_simple_spinner_dropdown_item);
        adapter.add("Zum Flughafen");
        adapter.add("Vom Flughafen");

        directionSpinner.setAdapter(adapter);


        dateEdit = (EditText)view.findViewById(R.id.date_ride_edit);
        dateEdit.setText(ride.getDate());

        dateEdit.setKeyListener(null);

        timeEdit = (EditText)view.findViewById(R.id.time);
        timeEdit.setText(ride.getTime());

        plzEdit = (EditText)view.findViewById(R.id.plz);
        plzEdit.setText(ride.getPlz());

        flnrEdit = (EditText)view.findViewById(R.id.flnr);
        flnrEdit.setText(ride.getFlightNr());

        arrivalEdit = (EditText)view.findViewById(R.id.arrival);
        arrivalEdit.setText(ride.getArrival());

        emailEdit = (EditText)view.findViewById(R.id.mail);
        emailEdit.setText(ride.getEmail());

        phoneEdit = (EditText)view.findViewById(R.id.phone);
        phoneEdit.setText(ride.getPhone());

        nameEdit = (EditText)view.findViewById(R.id.name);
        nameEdit.setText(ride.getName());

        persEdit = (EditText)view.findViewById(R.id.persons);
        persEdit.setText(ride.getPersons());

        lugageEdit = (EditText)view.findViewById(R.id.lugage);
        lugageEdit.setText(ride.getLugage());

        addrEdit = (EditText)view.findViewById(R.id.addr);
        addrEdit.setText(ride.getAddr());



        priceEdit = (EditText)view.findViewById(R.id.price);
        priceEdit.setText(Integer.toString(ride.getPrice()));
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener callback) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();

        calendar = Calendar.getInstance();

        args.putInt("year", calendar.get(Calendar.YEAR));

        args.putInt("month", calendar.get(Calendar.MONTH));

        args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));

        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(callback);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener dateCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            dateEdit.setText(String.valueOf(dayOfMonth) + "." + String.valueOf(monthOfYear + 1)
                    + "." + String.valueOf(year));
        }
    };

    private void onDoneButtonClicked(){
        Ride editedRide = getEditedRide();

        Map<String, String> params = new HashMap<>();

        params.put("token", serverRequestToken);
        params.put("service", "10");
        params.put("ride_id", Integer.toString(ride.getId()));
        params.put("ride_time", editedRide.getTime());
        params.put("ride_zip", editedRide.getPlz());
        params.put("flight_number", editedRide.getFlightNr());
        params.put("comes_from", editedRide.getArrival());
        params.put("name", editedRide.getName());
        params.put("phone", editedRide.getPhone());
        params.put("email", editedRide.getEmail());
        params.put("persons", editedRide.getPersons());
        params.put("lugage", editedRide.getLugage());
        params.put("address", editedRide.getAddr());
        params.put("direction", editedRide.getDir());
        params.put("price", Integer.toString(editedRide.getPrice()));

        System.out.println(params.get("ride_date"));
        CustomRequest customRequest = new CustomRequest(Request.Method.POST, serviceUrl, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int code = 0;

                try {
                    code = response.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(customRequest);
        dismiss();

    }

    private Ride getEditedRide(){
        Ride editedRide = new Ride();

        editedRide.setId(ride.getId());
        editedRide.setDate(dateEdit.getText().toString());
        editedRide.setTime(timeEdit.getText().toString());
        editedRide.setPlz(plzEdit.getText().toString());
        editedRide.setFlightNr(flnrEdit.getText().toString());
        editedRide.setArrival(arrivalEdit.getText().toString());
        editedRide.setName(nameEdit.getText().toString());
        editedRide.setEmail(emailEdit.getText().toString());
        editedRide.setPhone(phoneEdit.getText().toString());
        editedRide.setPersons(persEdit.getText().toString());
        editedRide.setLugage(lugageEdit.getText().toString());
        editedRide.setAddr(addrEdit.getText().toString());
        editedRide.setDir(directionSpinner.getSelectedItem().toString());
        editedRide.setPrice(Integer.parseInt(priceEdit.getText().toString()));

        return editedRide;
    }




}
