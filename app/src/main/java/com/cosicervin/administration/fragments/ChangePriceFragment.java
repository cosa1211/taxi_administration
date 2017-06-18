package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.cosicervin.administration.domain.Place;
import com.cosicervin.administration.fragments.dialogs.EditPlaceDialogFragment;
import com.cosicervin.administration.fragments.dialogs.NewPlaceFragment;
import com.cosicervin.administration.listAdapters.PlaceListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePriceFragment extends Fragment implements GeneralFragment{

    ListView placesListView;

    String serverRequestToken;

    String serverUrl;

    View view;

    ArrayList<Place> places;

    PlaceListAdapter adapter;

    RequestQueue requestQueue;

    Place selectedPlace;

    SwipeRefreshLayout swipe;

    Button newPlaceButton;

    public ChangePriceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_change_price, container, false);

        serverUrl = ((MainActivity)getActivity()).server_url;

        serverRequestToken = ((MainActivity)getActivity()).server_request_token;

        requestQueue = MyRequestQueue.getInstance(getContext()).getRequestQueue();

        placesListView = (ListView) view.findViewById(R.id.places_listView);

        newPlaceButton = (Button) view.findViewById(R.id.new_place_button);
        newPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPlaceFragment newPlaceFragment = new NewPlaceFragment();
                newPlaceFragment.show(getFragmentManager(),"New Place");
            }
        });

        swipe =(SwipeRefreshLayout) view.findViewById(R.id.price_swipe_layout);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPlacesFromServer();
                swipe.setRefreshing(false);
            }
        });

        places = new ArrayList<>();
        adapter = new PlaceListAdapter(getActivity().getApplicationContext(), R.layout.places_list_layout);

        placesListView.setAdapter(adapter);
        placesListView.setSelection(R.drawable.gratis_selector);

        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPlace = places.get(position);
            }
        });

        fetchPlacesFromServer();

        registerForContextMenu(placesListView);



        return view;
    }
  

    private void fetchPlacesFromServer(){
        final Map<String, String> params = new HashMap<>();
        params.put("token", serverRequestToken);
        params.put("service","15");

        final String URL = serverUrl + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                adapter.deleteAll();
                adapter.notifyDataSetChanged();
                places.clear();
                try {
                    JSONArray array = response.getJSONArray("places");

                    for(int i = 0; i < array.length(); i++){
                        JSONObject object = array.getJSONObject(i);

                        Place place = new Place();

                        place.setId(object.getInt("id"));
                        place.setName(object.getString("place_name"));
                        place.setCarPrice(object.getInt("car_price"));
                        place.setVanPrice(object.getInt("van_price"));
                        place.setBusPrice(object.getInt("bus_price"));

                        places.add(place);
                        adapter.add(place);
                        adapter.notifyDataSetChanged();

                        Log.i("Place", place.toString());

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.places_listView){
            menu.add("Bearbeiten");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "Bearbeiten":{

                if(selectedPlace != null) {

                    EditPlaceDialogFragment editPlaceDialogFragment = new EditPlaceDialogFragment();

                    editPlaceDialogFragment.setPlace(selectedPlace);
                    editPlaceDialogFragment.show(getFragmentManager(), "Edit place price");


                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Bitte ein ort wählen.", Toast.LENGTH_SHORT).show();
                }
               return true;
            }

            default: return false;
        }
    }


}
