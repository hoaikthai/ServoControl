package com.example.minimous.servocontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.minimous.servocontrol.Models.History;

import java.util.List;

/**
 * Created by minimous on 16/05/2016.
 */
public class ListAdapter extends ArrayAdapter<History> {

    public ListAdapter(Context context, int resource, List<History> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.activity_custom_layout,null);
        }

        History history = getItem(position);

        if (history != null) {
            TextView username = (TextView) v.findViewById(R.id.usernameText);
            TextView action = (TextView) v.findViewById(R.id.actionText);
            TextView datetime = (TextView) v.findViewById(R.id.datetimeText);
            username.setText(history.getUsername());
            action.setText(history.getAction());
            datetime.setText(history.getDate().toString());
        }

        return v;
    }
}
