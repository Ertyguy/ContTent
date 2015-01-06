package com.edaviessmith.consumecontent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.util.Listener;
import com.edaviessmith.consumecontent.util.Var;

import java.util.List;


public class GroupDialog extends Dialog implements View.OnClickListener {

    private final static String TAG = "GroupDialog";

    AddActivity act;
    ListView group_lv;
    List<Group> groups, userGroups;
    GroupAdapter groupAdapter;
    View cancel_tv, set_tv;
    Group newGroup;
    int offset;



    public GroupDialog(AddActivity activity,  List<Group> groups,  List<Group> userGroups, String newGroupName) {
        super(activity);
        this.act = activity;
        this.groups = groups;
        this.userGroups = userGroups;
        if(!Var.isEmpty(newGroupName)) {
            this.newGroup = new Group(newGroupName, true);
            offset = 1;
        }
        init();
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_groups);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * (Var.isDeviceLandscape(act) ? 0.65 : 0.95));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        getWindow().setAttributes(params);



        group_lv = (ListView) findViewById(R.id.group_lv);
        groupAdapter = new GroupAdapter(act);
        group_lv.setAdapter(groupAdapter);


        for (int i = 0; i < group_lv.getCount() - offset; i++){
            group_lv.setItemChecked(i, (i == 0 && offset == 1) || (offset == 0 && userGroups.contains(groups.get(i)))); //Unselect all options
        }

        set_tv = findViewById(R.id.set_tv);
        set_tv.setOnClickListener(this);

        cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);

        show();
    }


    @Override
    public void onClick(View v) {

        if(set_tv == v) {
            userGroups.clear();
            for (int i = 0; i < group_lv.getCount(); i++){
                if(group_lv.isItemChecked(i)) userGroups.add(groups.get(i));
            }

            act.setGroups(userGroups);

            dismiss();
        }

        if(cancel_tv == v) {
            dismiss();
        }



    }


    public class GroupAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GroupAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return groups.size() + (newGroup == null? 0:1);
        }

        @Override
        public Group getItem(int position) {
            if(position == 0 && offset == 1) return newGroup;
            return groups.get(position - offset);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_group_name, parent, false);
                ViewHolder holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            final ViewHolder holder = (ViewHolder) convertView.getTag();


            Group group = getItem(position);
            holder.name_tv.setText(group.getName());

            holder.icon_iv.setImageResource(R.drawable.ic_group_grey600_36dp);
            if(!Var.isEmpty(group.getThumbnail())) {
                Listener l = Var.getGroupThumbnailListener(act.binder, group, holder.icon_iv);
                act.binder.getImageLoader().DisplayImage(l, group.getThumbnail(), holder.icon_iv, holder.icon_pb);
            }

            return convertView;

        }

        class ViewHolder {
            ImageView icon_iv;
            ProgressBar icon_pb;
            TextView name_tv;

            public ViewHolder(View view) {
                icon_iv = (ImageView) view.findViewById(R.id.icon_iv);
                icon_pb = (ProgressBar) view.findViewById(R.id.icon_pb);
                name_tv = (TextView) view.findViewById(R.id.name_tv);
            }
        }


    }


}
