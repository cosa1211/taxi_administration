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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import domain.Sum;
import listAdapters.SumAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SumFragment extends Fragment implements GeneralFragment {
    ListView sumList;
    EditText start,end;
    TextView insgesamt;
    View  view;
    SumAdapter adapter;
    int year,month,day;
    Calendar calendar;
    RequestQueue queue;
    ArrayList<Sum> sumen;

    String URL = Params.URL+"getFinishedRides.php";
    String sdate;
    String edate;
    Button goButton;
    public void init(){

        start = (EditText)view.findViewById(R.id.start_date);
        start.setKeyListener(null);
        end = (EditText) view.findViewById(R.id.end_date);
        end.setKeyListener(null);
        insgesamt = (TextView)view.findViewById(R.id.insgesamt);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sumen = new ArrayList<>();
        goButton = (Button) view.findViewById(R.id.go_button);


    }
    public SumFragment() {
        // Required empty public constructor

    }

    /**
     * todo do a string request
     */
    public void  getFromDB(){


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    sumen = new ArrayList<>();

                    JSONArray array = response.getJSONArray("finished");
                    for (int i = 0; i < array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        Sum sum = new Sum(object.getString("driver"),object.getInt("price"));
                        sumen.add(sum);

                    }
                    adapter.addAll(sumen);
                    calcSum();
                    adapter.notifyDataSetChanged();
                }catch (JSONException e ){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ",error.toString());

            }
        });


        queue.add(request);

    }
    public void calcSum(){
        int toReturn = 0;
        for (int i = 0;i<sumen.size();i++){
            Sum s = sumen.get(i);
            toReturn+=s.getSum();
        }
        insgesamt.setText("Insgesamt: " + Integer.toString(toReturn) + " €");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sum, container, false);
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
        adapter = new SumAdapter(getActivity().getApplicationContext(),R.layout.sum_list);
        sumList.setAdapter(adapter);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.deleteAll();
                sdate = start.getText().toString();
                edate = end.getText().toString();
                adapter.notifyDataSetChanged();
                URL += "?start=" + sdate + "&end=" + edate;
                getFromDB();

            }
        });

        return  view;
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

}



