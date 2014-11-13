package com.edaviessmith.consumecontent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.edaviessmith.consumecontent.util.ImageLoader;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;

public class IconAdapter  extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<String> thumbnails;
    private ImageLoader imageLoader;

    public IconAdapter(Context context, List<String> thumbnails, ImageLoader imageLoader) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.thumbnails = thumbnails;
        this.imageLoader= imageLoader;
    }


    @Override
    public int getCount() {
        return thumbnails.size();
    }

    @Override
    public String getItem(int position) {
        return thumbnails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_image, parent, false);
            holder = new ViewHolder();
            holder.image_iv = (ImageView) convertView.findViewById(R.id.thumbnail_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image_iv.setImageDrawable(null);
        if (!Var.isEmpty(getItem(position)))
            imageLoader.DisplayImage(getItem(position), holder.image_iv);

        return convertView;
    }

    class ViewHolder {
        ImageView image_iv;
    }
}

