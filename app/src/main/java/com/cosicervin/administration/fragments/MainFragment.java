package com.cosicervin.administration.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cosicervin.administration.CustomRequest;
import com.cosicervin.administration.MyRequestQueue;
import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Driver;
import com.cosicervin.administration.domain.Ride;
import com.cosicervin.administration.fragments.dialogs.AuftragDialogFragment;
import com.cosicervin.administration.listAdapters.RideListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements GeneralFragment {

    String server_request_token;

    String server_url;

    ListView listView;

    View view;

    RideListAdapter listAdapter;

    RequestQueue requestQueue;

    SwipeRefreshLayout swipeRefreshLayout;


    ArrayList <Ride> ridesInDataBase;

    ArrayList <Driver> drivers;

    HashMap<Integer, Integer> assignedRides;


    int selectedRide;

    public MainFragment(String token, String url) {
        server_request_token = token;
        server_url = url;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestQueue = MyRequestQueue.getInstance(getActivity().getApplicationContext()).getRequestQueue();
        view = inflater.inflate(R.layout.fragment_main, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshListView();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        checkIfTokenExists(); // Check if user is logged in

        assignedRides = new HashMap<>();
        drivers = new ArrayList<>();
        ridesInDataBase = new ArrayList<>();

        fetchRidesFromServer();
        fetchAllDrivers();

        /** Populate list view **/
        listView  = (ListView) view.findViewById(R.id.rides_list);

        /**
         * Lsitener for selecting rides while clickig on the lsit view
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRide = position;
            }
        });



        listAdapter = new RideListAdapter(getActivity().getApplicationContext(), R.layout.rides_list_layout, new View.OnClickListener() {
            /**
             * On click listener for the driver selection
             *
             * @param v
             */
            @Override
            public void onClick(final View v) {

                int assignedDriverId = -1;
                /**
                 * Get the ride encoded in the imageView in the row
                 */
                final Ride selectedRideFromList = (Ride) v.getTag(R.id.ride_in_image_view);

                if (selectedRideFromList != null) {
                    if (selectedRideFromList.isHasdriver()) {
                        assignedDriverId = assignedRides.get(selectedRideFromList.getId());
                    }
                }
                /**
                 * Find the allready assigned driver if he exists so he can be shown as already selected in the spinner
                 */
                Driver driver = new Driver();

                for (Driver d : drivers) {
                    if (Integer.parseInt(d.getId()) == assignedDriverId) {
                        driver = drivers.get(drivers.indexOf(d));
                    }
                }

                AssignDriverFragment fragment = new AssignDriverFragment(selectedRideFromList, drivers, driver, v);

                fragment.show(getFragmentManager(), "Fahrer auswählen.");

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditRideFragment editRideFragment = new EditRideFragment(v);

                editRideFragment.show(getFragmentManager(), "Fahrt bearbeiten.");


            }
        });

        listAdapter.setActivity(getActivity());


        //Order the adapter to the ListView
        listView.setAdapter(listAdapter);

        registerForContextMenu(listView);
        listView.setSelector(R.drawable.gratis_selector);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return  view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.rides_list){

            menu.add("Anzeigen");

            menu.add("Löschen");

            menu.add("E-Mail schicken");

        }
    }

    /**
     *
     * Press and hold on menu implementation
     * @param item
     * @return
     */
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
                                    deleteSelectedRide();
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

                    Driver driver = null;

                    if(ridesInDataBase.get(selectedRide).isHasdriver()){
                       int driverId = assignedRides.get(ridesInDataBase.get(selectedRide).getId());
                        for(Driver dr: drivers){
                            if(Integer.parseInt(dr.getId()) == driverId){
                                driver = drivers.get(drivers.indexOf(dr));
                            }
                        }
                    }

                    AuftragDialogFragment a = new AuftragDialogFragment(ridesInDataBase.get(selectedRide), driver);//

                    a.show(getActivity().getFragmentManager(), "Auftrag");

                }

                return true;

            case "E-Mail schicken":
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, ridesInDataBase.get(selectedRide).getEmail());

                startActivity(Intent.createChooser(intent, "Schicke E-Mail"));

                return true;
        }

        return true;
    }



    /**
     * This method is called every time the MainFragment is opened and sends a request for all rides to the server
     */
    private void fetchRidesFromServer(){
        ridesInDataBase = new ArrayList<>();


        Map<String ,String> params = new HashMap<>();
        params.put("token", server_request_token);
        params.put("service", "3");



        final String URL = server_url + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
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
                ridesInDataBase = new ArrayList<>();
                ridesInDataBase = parseJSONObjetToRidesArrayList(response);

                listAdapter.addAll(ridesInDataBase);
                listAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Eror response","Error "+error.getMessage() + URL);

            }
        });


        requestQueue.add(customRequest);


    }

    /**
     * This method takes an JSONObject of rides and returns a ArrayList of rides
     * @param jsonObject
     * @return
     */
    private ArrayList<Ride> parseJSONObjetToRidesArrayList(JSONObject jsonObject){
        ArrayList<Ride> toReturn = new ArrayList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("rides");
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
                ride.setComment(object.getString("comment"));

                if(object.isNull("token")){
                    ride.setPending(false);
                }else{
                    ride.setPending(true);
                }

                if(object.getInt("hasdriver") == 1) {

                    ride.setHasdriver(true);

                    int driverId = object.getInt("driver_id");

                    assignedRides.put(ride.getId(), driverId);

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

    /**
     * This method is used for deleting rides
     */
    private void deleteSelectedRide(){
        Map<String, String> params = new HashMap<>();
        Ride rideToDelete = ridesInDataBase.get(selectedRide);
        params.put("ride_id", Integer.toString(rideToDelete.getId()));
        params.put("token", server_request_token);
        params.put("service", "4");

        String URL = server_url + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int code = response.getInt("code");

                    if(code == 2){

                        listAdapter.remove(selectedRide);
                        listAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), "Fahrt gelöscht.", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getActivity(), "Server error.", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),"delete ride error", Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(customRequest);
    }

    /**
     * This method fetches all drivers and saves them in the drivers ArrayList
     */
    private void fetchAllDrivers(){

        drivers = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("token", server_request_token);
        params.put("service", "5");

        String url = server_url + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
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
                        Log.i("drivers", driver.toString());
                        drivers.add(driver);
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

        requestQueue.add(customRequest);

    }

    private void checkIfTokenExists(){
        if(server_request_token != null) return;
        GeneralFragment fragment = new NotLoggedInFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, (Fragment) fragment);
        fragmentTransaction.commit();
    }

    private void refreshListView(){

        listAdapter.deleteAll();
        listAdapter.notifyDataSetChanged();
        fetchAllDrivers();
        fetchRidesFromServer();


    }


}

