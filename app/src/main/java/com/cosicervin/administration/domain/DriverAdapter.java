package com.cosicervin.administration.domain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cosicervin.administration.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ervincosic on 3/24/16.
 */
public class DriverAdapter extends ArrayAdapter {
    ArrayList list = new ArrayList();

    public DriverAdapter(Context context, int resource) {
        super(context, resource);

    }
    public void addAll(List<Driver> drivers) {
        list.addAll(drivers);
        notifyDataSetChanged();
    }


    static  class Holder{
        TextView NAME;
        TextView EMAIL;
        TextView PHONE;
        TextView CAR;
    }
    public void add(Driver driver) {
        list.add(driver);
        notifyDataSetChanged();
    }
    public  void remove(int position){
        this.list.remove(position);
        notifyDataSetChanged();
    }
    public  void deleteAll(){
        for (int i=0; i<this.list.size();i++){
            this.list.remove(i);
        }
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    public  int getCount(){
        return  this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        Holder holder;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.driver_listview, parent, false);
            holder = new Holder();
            holder.NAME = (TextView) row.findViewById(R.id.driverName);
            holder.EMAIL = (TextView) row.findViewById(R.id.driverMail);
            holder.PHONE = (TextView) row.findViewById(R.id.driverPhone);
            holder.CAR = (TextView) row.findViewById(R.id.driverCar);
            row.setTag(holder);
        }else {
            holder  = (Holder) row.getTag();

        }
        Driver driver = (Driver)getItem(position);
        holder.NAME.setText(driver.getName());
        holder.EMAIL.setText(driver.getMail());
        holder.PHONE.setText(driver.getPhone());
        holder.CAR.setText(driver.getCar());

        return  row;

    }
}
