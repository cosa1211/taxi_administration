package com.cosicervin.administration.listAdapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ervincosic on 04/05/2017.
 */

public class PlaceListAdapter extends ArrayAdapter {

    ArrayList<Place> data = new ArrayList<>();

    View.OnClickListener onButtonClickedListener;

    public PlaceListAdapter(@NonNull Context context, @LayoutRes int resource, View.OnClickListener listener) {
        super(context, resource);
        onButtonClickedListener = listener;
    }


    public void addAll(List<Place> places) {
        data.addAll(places);

    }

    static  class Holder{
        EditText PLACE_NAME;
        EditText CAR_PRICE;
        EditText VAN_PRICE;
        EditText BUS_PRICE;
        Button BUTTON;
    }

    public void add(Place place) {
        data.add(place);
    }

    public  void remove(int position){
        this.data.remove(position);
    }
    public  void deleteAll(){
        data.clear();
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    public  int getCount(){
        return  this.data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        row = convertView;

        PlaceListAdapter.Holder holder;

        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflater.inflate(R.layout.places_list_layout, parent, false);

            holder = new PlaceListAdapter.Holder();

            holder.PLACE_NAME = (EditText) row.findViewById(R.id.place_name_edit);

            holder.CAR_PRICE = (EditText) row.findViewById(R.id.place_car_price_edit);

            holder.VAN_PRICE = (EditText) row.findViewById(R.id.place_van_price_edit);

            holder.BUS_PRICE = (EditText) row.findViewById(R.id.place_bus_price_edit);

            holder.BUTTON = (Button) row.findViewById(R.id.save_place_button);

            row.setTag(holder);
        }else {
            holder  = (PlaceListAdapter.Holder) row.getTag();

        }


        Place place = (Place)getItem(position);

        holder.PLACE_NAME.setText(place.getName());

        holder.CAR_PRICE.setText(Integer.toString(place.getCarPrice()));

        holder.VAN_PRICE.setText(Integer.toString(place.getVanPrice()));

        holder.BUS_PRICE.setText(Integer.toString(place.getBusPrice()));

        holder.BUTTON.setTag(R.id.place, place);

        holder.BUTTON.setTag(R.id.place_name_id, holder.PLACE_NAME);
        holder.BUTTON.setTag(R.id.place_car_price_id, holder.CAR_PRICE);
        holder.BUTTON.setTag(R.id.place_van_price_id, holder.VAN_PRICE);
        holder.BUTTON.setTag(R.id.place_bus_price_id, holder.BUS_PRICE);

        holder.BUTTON.setOnClickListener(onButtonClickedListener);


        return  row;

    }



}
