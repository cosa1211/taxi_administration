package com.cosicervin.administration.listAdapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ervincosic on 04/05/2017.
 */

public class PlaceListAdapter extends ArrayAdapter {

    ArrayList<Place> data = new ArrayList<>();


    public PlaceListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }


    public void addAll(List<Place> places) {
        data.addAll(places);

    }

    static  class Holder{
        TextView PLACE_NAME;
        TextView CAR_PRICE;
        TextView VAN_PRICE;
        TextView BUS_PRICE;
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

            holder.PLACE_NAME = (TextView) row.findViewById(R.id.place_name_edit);

            holder.CAR_PRICE = (TextView) row.findViewById(R.id.place_car_price_edit);

            holder.VAN_PRICE = (TextView) row.findViewById(R.id.place_van_price_edit);

            holder.BUS_PRICE = (TextView) row.findViewById(R.id.place_bus_price_edit);


            row.setTag(holder);
        }else {
            holder  = (PlaceListAdapter.Holder) row.getTag();

        }


        Place place = (Place)getItem(position);

        holder.PLACE_NAME.setText(place.getName());

        holder.CAR_PRICE.setText(Integer.toString(place.getCarPrice()));

        holder.VAN_PRICE.setText(Integer.toString(place.getVanPrice()));

        holder.BUS_PRICE.setText(Integer.toString(place.getBusPrice()));



        return  row;

    }



}
