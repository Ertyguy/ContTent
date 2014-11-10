package com.edaviessmith.consumecontent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class IconAdapter extends ArrayAdapter<Integer> {

    Integer objects[];

    //Things don't work moving out of the way

    public IconAdapter(Context context, int textViewResourceId,   Integer[] objects) {
        super(context, textViewResourceId, objects);

        this.objects = objects;
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=null;//getLayoutInflater();
        View row = inflater.inflate(R.layout.item_image, parent, false);
        ImageView image = (ImageView) row.findViewById(R.id.image_iv);
        image.setImageResource(R.drawable.ic_action_new);
        return row;

    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=null;//getLayoutInflater();
        View row = inflater.inflate(R.layout.item_image, parent, false);
        ImageView image = (ImageView) row.findViewById(R.id.image_iv);
        image.setImageResource(objects[position]);

        return row;
    }
}
