package com.cbre.tsandford.cbreinspector.misc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.fragments.FragmentDictation;

import java.util.List;

public class VoiceTagsListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> tags;


    public VoiceTagsListAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        this.tags = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.tag_list_row, parent, false);

        listItem.setLongClickable(true);

        TextView tv = listItem.findViewById(R.id.row_tag_text);
        tv.setText(tags.get(position));

        return listItem;
    }


}
