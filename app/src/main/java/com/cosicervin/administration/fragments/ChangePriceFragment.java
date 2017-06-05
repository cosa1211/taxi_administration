package com.cosicervin.administration.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    Button newPlacebButton;

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

        newPlacebButton = (Button) view.findViewById(R.id.new_place_button);
        newPlacebButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPlaceFragment newPlaceFragment = new NewPlaceFragment();
                newPlaceFragment.show(getFragmentManager(),"New Place");
            }
        });


        places = new ArrayList<>();
        adapter = new PlaceListAdapter(getActivity().getApplicationContext(), R.layout.places_list_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedPlace = (Place) v.getTag(R.id.place);

                EditText placeNameEdit = (EditText) v.getTag(R.id.place_name_id);
                EditText carPriceEdit = (EditText) v.getTag(R.id.place_car_price_id);
                EditText vanPriceEdit = (EditText) v.getTag(R.id.place_van_price_id);
                EditText busPriceEdit = (EditText) v.getTag(R.id.place_bus_price_id);

                selectedPlace.setName(placeNameEdit.getText().toString());
                selectedPlace.setCarPrice(Integer.parseInt(carPriceEdit.getText().toString()));
                selectedPlace.setVanPrice(Integer.parseInt(vanPriceEdit.getText().toString()));
                selectedPlace.setBusPrice(Integer.parseInt(busPriceEdit.getText().toString()));

                changePlacePrices();

                adapter.deleteAll();
                adapter.notifyDataSetChanged();
                fetchPlacesFromServer();
                adapter.notifyDataSetChanged();


            }
        });

        placesListView.setAdapter(adapter);

        fetchPlacesFromServer();



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

    private void changePlacePrices(){
        Map<String, String> params = new HashMap<>();
        params.put("token", serverRequestToken);
        params.put("service", "16");
        params.put("place_id",Integer.toString(selectedPlace.getId()));
        params.put("place_name", selectedPlace.getName());
        params.put("car_price", Integer.toString(selectedPlace.getCarPrice()));
        params.put("van_price", Integer.toString(selectedPlace.getVanPrice()));
        params.put("bus_price", Integer.toString(selectedPlace.getBusPrice()));

        final String URL = serverUrl + "/administration_services.php";

        CustomRequest customRequest = new CustomRequest(Request.Method.POST, URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int code = 1;

                try {
                    code = response.getInt("code");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(code == 2){
                    Toast.makeText(getContext(), "Gespeichert", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(customRequest);
    }



}
