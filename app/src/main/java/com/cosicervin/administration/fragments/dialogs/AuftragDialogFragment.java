package com.cosicervin.administration.fragments.dialogs;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cosicervin.administration.R;
import com.cosicervin.administration.domain.Driver;
import com.cosicervin.administration.domain.Ride;


/**
 * A simple {@link Fragment} subclass.
 */
public class AuftragDialogFragment extends DialogFragment {
    Ride ride;

    TextView tDate,tTime,tPLZ,tAddr,tDir,tFlnr,tArrival,tName,tMail,tPhone,tPers,tLug,tPrice,tChildSeat, tComment;

    View view;

    String title;

    Driver driver;

    private void init(){

        if(driver == null){
            title = "Es ist noch kein Fahrer für diese Fahrt zugeordnet.";
        }else {
            title = "Zugeordneter Fahrer für diese Fahrt ist: " + driver.getName();
        }

        getDialog().setTitle(title);

        tTime = (TextView) view.findViewById(R.id.time_label);
        tTime.setText(ride.getTime());

        tDate = (TextView) view.findViewById(R.id.date_label);
        tDate.setText(ride.getDate());

        tPLZ = (TextView) view.findViewById(R.id.zip_label);
        tPLZ.setText(ride.getPlz());

        tAddr = (TextView) view.findViewById(R.id.address_label);
        tAddr.setText(ride.getAddr());

        tDir = (TextView) view.findViewById(R.id.direction_label);
        tDir.setText(ride.getDir());

        tFlnr = (TextView) view.findViewById(R.id.flight_number_label);
        tFlnr.setText(ride.getFlightNr());

        tArrival = (TextView) view.findViewById(R.id.comes_from_label);
        tArrival.setText(ride.getArrival());

        tName = (TextView) view.findViewById(R.id.name_label);
        tName.setText(ride.getName());

        tMail = (TextView) view.findViewById(R.id.email_label);
        tMail.setText(ride.getEmail());

        tPhone = (TextView) view.findViewById(R.id.phone_label);
        tPhone.setText(ride.getPhone());

        tPers = (TextView) view.findViewById(R.id.persons_label);
        tPers.setText(ride.getPersons());

        tLug = (TextView) view.findViewById(R.id.luagege_label);
        tLug.setText(ride.getLugage());

        tPrice  = (TextView) view.findViewById(R.id.price_label);
        tPrice.setText(Integer.toString(ride.getPrice())+ "€");

        tChildSeat = (TextView) view.findViewById(R.id.child_seat_label);

        tComment = (TextView) view.findViewById(R.id.comment_label);
        if(ride.getComment() != null){
            tComment.setText(ride.getComment());
        }else{
            tComment.setText("");
        }

        if(ride.isChildseat()){
            tChildSeat.setText("Ja");
        }else{
            tChildSeat.setText("Nein");
        }


    }

    public AuftragDialogFragment() {
        // Required empty public constructor
    }

    public AuftragDialogFragment(Ride ride, Driver driver) {
        this.ride = ride;
        this.driver = driver;
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
