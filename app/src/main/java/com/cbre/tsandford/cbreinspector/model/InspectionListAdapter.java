package com.cbre.tsandford.cbreinspector.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.model.Inspection;

import java.text.SimpleDateFormat;
import java.util.List;

public class InspectionListAdapter extends ArrayAdapter<Inspection>{

    private Context context;
    private List<Inspection> inspections;

    public InspectionListAdapter(@NonNull Context context, @NonNull List<Inspection> objects) {
        super(context, 0, objects);
        this.inspections = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null)
             listItem = LayoutInflater.from(context).inflate(R.layout.inspection_list_row, parent, false);

        Inspection currentInspection = inspections.get(position);

        TextView address_tv = listItem.findViewById(R.id.row_text_view_address);
        TextView date_tv = listItem.findViewById(R.id.row_text_view_date);

        address_tv.setText(currentInspection.property_name_formatted);

        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy");
        String formattedDate = format.format(currentInspection.inspection_date);

        date_tv.setText(formattedDate);


        return listItem;
    }


}
