package com.cosicervin.administration.listAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Ride;

import java.util.ArrayList;

/**
 * Created by ervincosic on 4/7/16.
 */
public class RideListAdapter extends ArrayAdapter {
    ArrayList data = new ArrayList();
    int resource;
    Activity activity;

    View.OnClickListener assignDriverListener;

    View.OnClickListener editRideListener;

    public RideListAdapter(Context context, int resource, View.OnClickListener assignListener, View.OnClickListener editListener) {
        super(context, resource);
        this.resource = resource;
        this.assignDriverListener = assignListener;
        this.editRideListener = editListener;

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
        data.clear();
        for (int i=0; i<this.data.size();i++){
            this.data.remove(i);
        }
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
        ImageView DIR;
        TextView PRICE;
        ImageView DRIVER;
        ImageView EDIT;
        TextView EMAIL;
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
            holder.DIR = (ImageView) row.findViewById(R.id.direction_image);
            holder.PRICE = (TextView)row.findViewById(R.id.ride_price);
            holder.DRIVER = (ImageView) row.findViewById(R.id.image_driver);
            holder.EDIT = (ImageView) row.findViewById(R.id.image_edit);
            holder.EMAIL = (TextView) row.findViewById(R.id.email_layout);
            row.setTag(holder);
        }else {
            holder  = (Holder) row.getTag();

        }


        final Ride ride = (Ride)getItem(position);
        holder.DATE.setText(ride.getDate());
        holder.TIME.setText(ride.getTime());
        holder.PLZ.setText(ride.getPlz());
        holder.ADDR.setText(ride.getAddr());

        if(ride.getDir().equals("Zum Flughafen")){
            holder.DIR.setImageResource(R.drawable.ic_flight_takeoff_black_24dp);
        }else {
            holder.DIR.setImageResource(R.drawable.ic_flight_land_black_24dp);
        }

        holder.PRICE.setText(Integer.toString(ride.getPrice()) +"€");

            holder.DRIVER.setImageResource(R.drawable.ic_person_outline_black_24dp);


        if(ride.isPending()){
            holder.DRIVER.setImageResource(R.drawable.ic_person_no_background_yellow);
        }

        if(ride.isHasdriver()){
            holder.DRIVER.setImageResource(R.drawable.ic_person_green_no_background);

        }
        /**
         * Store the current ride in the imageView as a tag
         */
        holder.DRIVER.setTag(R.id.ride_in_image_view, ride);
        if(editRideListener !=null) {
            holder.EDIT.setImageResource(R.drawable.ic_create_black_24dp);
        }else{
            holder.EDIT.setImageResource(0);
        }

        holder.EDIT.setOnClickListener(editRideListener);

        holder.EDIT.setTag(R.id.ride_in_image_view, ride);

        holder.EMAIL.setText(ride.getEmail());

        holder.DRIVER.setOnClickListener(assignDriverListener);


        return  row;

    }

}
