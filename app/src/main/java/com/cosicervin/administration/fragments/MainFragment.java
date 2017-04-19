package com.cosicervin.administration.fragments;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cosicervin.administration.Params;
import com.cosicervin.administration.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import domain.Ride;
import listAdapters.RideListAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GeneralFragment {
    ListView listView;
    View view;
    RideListAdapter listAdapter;
    RequestQueue requestQueue;
    String URL = Params.URL+ "getAllRides.php";
    String delURL=Params.URL+"deleteRide.php";
    ArrayList <Ride> ridesInDataBase;
    JSONArray tempArray;
    int selectedRide;


    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initializing

        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        view = inflater.inflate(R.layout.fragment_main, container, false);
        listView  = (ListView) view.findViewById(R.id.rides_list);
        listAdapter = new RideListAdapter(getActivity().getApplicationContext(),R.layout.rides_list_template);
        ridesInDataBase = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRide = position;
            }
        });

        //Order the adapter to the ListView
        listView.setAdapter(listAdapter);
        getFromDB();
        registerForContextMenu(listView);
        listView.setSelector(R.drawable.gratis_selector);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return  view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.rides_list){
            /*
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Ride obj = (Ride) lv.getItemAtPosition(acmi.position);
            */
            menu.add("Anzeigen");
            menu.add("Zuordnen");
            menu.add("Löschen");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "Löschen":

                if(ridesInDataBase.get(selectedRide)!=null){
                    new AlertDialog.Builder(getContext())
                            .setTitle("Fahrt Entfernen")
                            .setMessage("Sind Sie sicher das Sie diese Fahrt entfernen wollen ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteRideFromDb();
                                    System.out.println(ridesInDataBase.size());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }

                return true;
            case "Anzeigen":

                if(ridesInDataBase.get(selectedRide)!=null) {
                    AuftragDialogFragment a = new AuftragDialogFragment(ridesInDataBase.get(selectedRide));
                    a.show(getActivity().getFragmentManager(), "Auftrag");
                }

                return true;
            case "Zuordnen":

                RideFragment fragment = new RideFragment();
                fragment.setRide(ridesInDataBase.get(selectedRide));
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
                fragmentTransaction.commit();

                return true;
        }

        return true;
    }

    public ArrayList<Ride> parse(JSONObject jsonObject){
        ArrayList<Ride> toReturn = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("rides");
            tempArray = jsonObject.getJSONArray("rides");
            for(int i = 0; i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Ride ride = new Ride();
                ride.setId(object.getInt("id"));
                ride.setDate(object.getString("datum"));
                ride.setTime(object.getString("zeit").substring(0,5));
                ride.setPlz(object.getString("plz"));
                ride.setAddr(object.getString("adresse"));
                ride.setDir(object.getString("richtung"));
                ride.setFlightNr(object.getString("flugnummer"));
                ride.setArrival(object.getString("ankunft"));
                ride.setName(object.getString("name"));
                ride.setEmail(object.getString("email"));
                ride.setPhone(object.getString("telefon"));
                ride.setPersons(object.getString("personen"));
                ride.setLugage(object.getString("koffer"));
                ride.setPrice(object.getInt("price"));
                if(object.getInt("hasdriver") == 1) {
                    ride.setHasdriver(true);
                }else ride.setHasdriver(false);
                if(object.getInt("childseat") == 1){
                    ride.setChildseat(true);
                }
                toReturn.add(ride);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  toReturn;

    }

    public void getFromDB(){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,URL,(String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                ridesInDataBase = parse(response);
                listAdapter.addAll(ridesInDataBase);
                listAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void deleteRideFromDb(){
        StringRequest request = new StringRequest(Request.Method.POST, delURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("1")){
                    Toast toast = Toast.makeText(getActivity(),"Fahrt ist gelöscht",Toast.LENGTH_LONG);
                    toast.show();
                }else{
                    Toast toast = Toast.makeText(getActivity(),"Server Fehler ! ",Toast.LENGTH_LONG);
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
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap <String, String> params = new HashMap<String, String>();
                Ride rideToDelete = new Ride();
                rideToDelete = ridesInDataBase.get(selectedRide);
                params.put("ride",Integer.toString(rideToDelete.getId()));
                return  params;
            }
        };
        requestQueue.add(request);
        listAdapter.remove(selectedRide);
        listAdapter.notifyDataSetChanged();
    }


}

