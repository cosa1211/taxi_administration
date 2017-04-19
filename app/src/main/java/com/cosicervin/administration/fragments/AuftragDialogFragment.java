package com.cosicervin.administration.fragments;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosicervin.administration.R;

import domain.Ride;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuftragDialogFragment extends DialogFragment {
    Ride ride;
    TextView tDate,tTime,tPLZ,tAddr,tDir,tFlnr,tArrival,tName,tMail,tPhone,tPers,tLug,tPrice,tChildSeat;
    View view;

    private void init(){
        getDialog().setTitle("Fahrt");

        tDate = (TextView)view.findViewById(R.id.datum_auftrag);
        tDate.setText(tDate.getText()+ride.getDate());

        tTime = (TextView)view.findViewById(R.id.zeit_auftrag);
        tTime.setText(tTime.getText()+ride.getTime());

        tPLZ = (TextView)view.findViewById(R.id.plz_auftrag);
        tPLZ.setText(tPLZ.getText()+ride.getPlz());

        tAddr = (TextView)view.findViewById(R.id.addr_auftrag);
        tAddr.setText(tAddr.getText()+ride.getAddr());

        tDir = (TextView)view.findViewById(R.id.dir_a);
        tDir.setText(tDir.getText()+ride.getDir());

        tFlnr = (TextView)view.findViewById(R.id.flnr_a);
        tFlnr.setText(tFlnr.getText()+ride.getFlightNr());

        tArrival = (TextView)view.findViewById(R.id.arrival_a);
        tArrival.setText(tArrival.getText()+ride.getArrival());

        tName = (TextView)view.findViewById(R.id.name_a);
        tName.setText(tName.getText()+ride.getName());

        tMail = (TextView)view.findViewById(R.id.mail_a);
        tMail.setText(tMail.getText()+ride.getEmail());

        tPhone = (TextView)view.findViewById(R.id.phone_a);
        tPhone.setText(tPhone.getText()+ride.getPhone());

        tPers = (TextView)view.findViewById(R.id.pers_a);
        tPers.setText(tPers.getText()+ride.getPersons());

        tLug = (TextView)view.findViewById(R.id.lugage_a);
        tLug.setText(tLug.getText()+ride.getLugage());

        tPrice = (TextView) view.findViewById(R.id.price);
        tPrice.setText(tPrice.getText().toString()+ride.getPrice());

        tChildSeat = (TextView)view.findViewById(R.id.childseat);
        String chseat = "Nein";
        if(ride.isChildseat()){
            chseat = "Ja";
        }
        tChildSeat.setText(tChildSeat.getText().toString()+chseat);


    }

    public AuftragDialogFragment() {
        // Required empty public constructor
    }

    public AuftragDialogFragment(Ride ride) {
        this.ride = ride;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println(ride.toString());
        view = inflater.inflate(R.layout.fragment_auftrag, container, false);
        init();

        return view;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }
}
