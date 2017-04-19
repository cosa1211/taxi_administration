package com.cosicervin.administration.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;
import com.cosicervin.administration.fragments.AddDriverFragment;
import com.cosicervin.administration.fragments.GeneralFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Driver;
import domain.DriverAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriverFragment extends Fragment implements GeneralFragment {
    int toDelete;
    ListView listView;
    DriverAdapter driverAdapter;
    RequestQueue queue=null;
    List<Driver> drivers;
    int c = 0;
    String URLget = Params.URL+"getdrivers.php";
    String URLdel =Params.URL+ "deleteDriver.php";
    public DriverFragment() {

        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_driver, container, false);

        queue = Volley.newRequestQueue(getActivity());

        toDelete=-1;

        listView = (ListView) view.findViewById(R.id.driverList);

        drivers = new ArrayList();

        driverAdapter = new DriverAdapter(getActivity().getApplicationContext(),R.layout.driver_listview);
        listView.setAdapter(driverAdapter);

        getFromDb();

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



    public void getFromDb(){

        driverAdapter.deleteAll();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, URLget,(String)null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("Response:",jsonObject.toString());
                JSONArray jArray;
                try {
                    jArray = jsonObject.getJSONArray("drivers");
                    for (int i=0; i<jArray.length();i++){
                        JSONObject o = jArray.getJSONObject(i);
                        Driver dr = new Driver();
                        dr.setName(o.getString("name"));
                        dr.setMail(o.getString("email"));
                        dr.setPhone(o.getString("phone"));
                        dr.setCar(o.getString("car"));
                        driverAdapter.add(dr);

                        driverAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    Log.e("JSON Error",e.toString());
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put(Integer.toString(c),Integer.toString(c));
                c++;
                return params;
            }

        };
        jsonObjectRequest.setShouldCache(false);
        queue.add(jsonObjectRequest);


    }

    @Override
    public void onResume() {

        super.onResume();

    }

    public  void deleteDriver(int position) {



        final Driver driver = (Driver) driverAdapter.getItem(position);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLdel, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",error.toString());

            }
        }){
            @Override
            public Map<String,String> getParams()throws AuthFailureError {
                HashMap<String,String> headers = new HashMap<>();
                headers.put("name",driver.getName());
                headers.put("mail",driver.getMail());
                headers.put("phone", driver.getPhone());
                return  headers;


            }
        };

        queue.add(jsonObjectRequest);
        driverAdapter.notifyDataSetChanged();
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
                                    deleteDriver(toDelete);
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
                AddDriverFragment fragment = new AddDriverFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
                fragmentTransaction.commit();
            }
        });

    }




}
