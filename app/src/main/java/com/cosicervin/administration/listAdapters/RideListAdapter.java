package com.cosicervin.administration.listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cosicervin.administration.R;

import java.util.ArrayList;

import com.cosicervin.administration.domain.Ride;

/**
 * Created by ervincosic on 4/7/16.
 */
public class RideListAdapter extends ArrayAdapter {
    ArrayList data = new ArrayList();
    int resource;

    public RideListAdapter(Context context, int resource) {
        super(context, resource);
        this.resource = resource;


    }

    public ArrayList getData() {
        return data;
    }

    public void add(Ride ride) {
        data.add(ride);
        notifyDataSetChanged();
    }
    public  void addAll(ArrayList<Ride> toAdd){
        data.addAll(toAdd);
    }
    public  void remove(int position){
        this.data.remove(position);
        notifyDataSetChanged();
    }
    public  void deleteAll(){
        for (int i=0; i<this.data.size();i++){
            this.data.remove(i);
        }
    }

    @Override
    public Object getItem(int position) {
        return this.data.get(position);
    }

    public  int getCount(){
        return  this.data.size();
    }
    static  class Holder{
        TextView DATE;
        TextView TIME;
        TextView PLZ;
        TextView ADDR;
        TextView DIR;
        TextView PRICE;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        Holder holder;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(resource, parent, false);
            holder = new Holder();
            holder.DATE = (TextView) row.findViewById(R.id.ride_date);
            holder.TIME = (TextView) row.findViewById(R.id.ride_time);
            holder.PLZ = (TextView) row.findViewById(R.id.ride_plz);
            holder.ADDR = (TextView)row.findViewById(R.id.ride_addr);
            holder.DIR = (TextView)row.findViewById(R.id.ride_direction);
            holder.PRICE = (TextView)row.findViewById(R.id.ride_price);
            row.setTag(holder);
        }else {
            holder  = (Holder) row.getTag();

        }


        Ride ride = (Ride)getItem(position);
        holder.DATE.setText(ride.getDate());
        holder.TIME.setText(ride.getTime());
        holder.PLZ.setText(ride.getPlz());
        holder.ADDR.setText(ride.getAddr());
        holder.DIR.setText(ride.getDir());
        holder.PRICE.setText(Integer.toString(ride.getPrice()));



        return  row;

    }

}
