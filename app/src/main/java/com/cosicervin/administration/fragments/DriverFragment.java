package com.cosicervin.administration.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.cosicervin.administration.fragments.dialogs.AddDriverFragment;
import com.cosicervin.administration.listAdapters.DriverAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverFragment extends Fragment implements GeneralFragment {
    int toDelete;

    ListView listView;

    DriverAdapter driverAdapter;

    RequestQueue queue = null;

    List<Driver> drivers;

    int c = 0;

    String server_url;

    String serverToken;

    SwipeRefreshLayout swipeLayout;

    View view;

    public DriverFragment() {

        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_driver, container, false);

        swipeLayout =(SwipeRefreshLayout) view.findViewById(R.id.swipe_drivers);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                drivers.clear();
                driverAdapter.deleteAll();
                driverAdapter.notifyDataSetChanged();
                fetchAllDrivers();
                driverAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

        queue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        toDelete = -1;

        listView = (ListView) view.findViewById(R.id.driverList);

        drivers = new ArrayList();

        serverToken = ((MainActivity) getActivity()).server_request_token;

        server_url = ((MainActivity)getActivity()).server_url;

        driverAdapter = new DriverAdapter(getActivity().getApplicationContext(),R.layout.driver_listview);
        listView.setAdapter(driverAdapter);

        fetchAllDrivers();

        driverAdapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                toDelete = position;

            }
        });

        driverAdapter.notifyDataSetChanged();


        newDriver(view);

        deleteDriver(view);

        driverAdapter.notifyDataSetChanged();

        queue.cancelAll("END END END");

        driverAdapter.notifyDataSetChanged();

        Log.e("size",Integer.toString(driverAdapter.getCount()));

        return view;
    }



    public void fetchAllDrivers(){
        Map<String, String> params = new HashMap<>();
        params.put("token", serverToken);
        params.put("service","5");

        final String URL = server_url + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
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
                        Log.i("drivers", driver.toString());
                        drivers.add(driver);
                    }

                    driverAdapter.deleteAll();
                    driverAdapter.notifyDataSetChanged();
                    driverAdapter.addAll(drivers);
                    driverAdapter.notifyDataSetChanged();

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

    @Override
    public void onResume() {

        super.onResume();

    }

    public  void deleteDriver() {
        Map<String, String> params = new HashMap<>();
        params.put("token", serverToken);
        params.put("service", "12");
        params.put("driver_id", drivers.get(toDelete).getId());

        final String URL = server_url + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int code = -1;

                try {
                    code = response.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(code == 2){
                    Toast.makeText(getActivity(), "Fahrer entfernt.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Error.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(customRequest);
    }


    public  void deleteDriver (View v){
        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toDelete != -1) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Fahrer Entfernen")
                            .setMessage("Sind Sie sicher das Sie diesen Fahrer entfernen wollen ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteDriver();
                                    Log.e("todelete", Integer.toString(driverAdapter.getCount()));
                                    driverAdapter.remove(toDelete);

                                    driverAdapter.notifyDataSetChanged();
                                    toDelete = -1;

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                } else if (toDelete == -1) {
                    Toast.makeText(getActivity(), "Bitte Fahrer wählen !", Toast.LENGTH_LONG);
                }

            }
        });

    }

    public void newDriver(View v){
        Button newButton = (Button)v.findViewById(R.id.addButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AddDriverFragment addDriverFragment = new AddDriverFragment();
                addDriverFragment.show(getFragmentManager(), "Neuer Fahrer");
            }
        });

    }




}
