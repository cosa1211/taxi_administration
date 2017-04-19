package listAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cosicervin.administration.R;

import java.util.ArrayList;
import java.util.List;

import domain.Sum;

/**
 * Created by ervincosic on 4/14/16.
 */
public class SumAdapter extends ArrayAdapter {
    ArrayList list = new ArrayList();
    public SumAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void addAll(List<Sum> sums) {
        list.addAll(sums);
        notifyDataSetChanged();
    }

    static  class Holder{
        TextView DRIVER;
        TextView SUM;

    }
    public void add(Sum sum) {
        list.add(sum);
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
            row = layoutInflater.inflate(R.layout.sum_list, parent, false);
            holder = new Holder();
            holder.DRIVER = (TextView) row.findViewById(R.id.sum_name);
            holder.SUM = (TextView) row.findViewById(R.id.sum);

            row.setTag(holder);
        }else {
            holder  = (Holder) row.getTag();

        }
        Sum sum = (Sum)getItem(position);
        holder.DRIVER.setText(sum.getDriver());
        holder.SUM.setText(Integer.toString(sum.getSum())+ " €");

        return  row;

    }
}
