package com.edaviessmith.contTent;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edaviessmith.contTent.data.Group;
import com.edaviessmith.contTent.data.User;
import com.edaviessmith.contTent.db.DB;
import com.edaviessmith.contTent.util.Listener;
import com.edaviessmith.contTent.util.Var;

import java.util.ArrayList;
import java.util.List;


public class GroupUserDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemClickListener {

    private final static String TAG = "GroupDialog";

    GroupFragment fragment;
    ListView group_lv;
    List<Group> groups;
    List<User> users, currentUsers;
    GroupAdapter groupAdapter;
    TextView title_tv;
    View cancel_tv, set_tv, title_ll, back_iv;

    int selectedGroup = -1;



    public GroupUserDialog(GroupFragment fragment, List<Group> groups, List<User> currentUsers) {
        super(fragment.getActivity());
        this.fragment = fragment;
        this.groups = groups;
        this.currentUsers = currentUsers;

        init();
    }

    private void init() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.dialog_group_users);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        fragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = (int) (displaymetrics.widthPixels * (Var.isDeviceLandscape(fragment.getActivity()) ? 0.65 : 0.95));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = width;
        getWindow().setAttributes(params);


        back_iv = findViewById(R.id.back_iv);

        title_ll = findViewById(R.id.title_ll);
        title_ll.setOnClickListener(this);

        group_lv = (ListView) findViewById(R.id.group_lv);
        groupAdapter = new GroupAdapter(fragment.getActivity());
        group_lv.setAdapter(groupAdapter);
        group_lv.setOnItemClickListener(this);

        title_tv = (TextView) findViewById(R.id.title_tv);
        title_tv.setText("Select Group");

        set_tv = findViewById(R.id.set_tv);
        set_tv.setOnClickListener(this);

        cancel_tv = findViewById(R.id.cancel_tv);
        cancel_tv.setOnClickListener(this);

        show();
    }


    @Override
    public void onClick(View v) {

        if(title_ll == v) {
            if(DB.isValid(selectedGroup)) {
                clearSelection();
                back_iv.setVisibility(View.GONE);
                group_lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
                selectedGroup = -1;
                groupAdapter.notifyDataSetChanged();
                title_tv.setText("Select Group");
                set_tv.setVisibility(View.INVISIBLE);
            }
        }

        if(set_tv == v) {

            List<User> addUsers = new ArrayList<User>();
            for (int i = 0; i < group_lv.getCount(); i++){
                if(group_lv.isItemChecked(i)) addUsers.add(users.get(i));
            }

            fragment.addUsers(addUsers);


            dismiss();
        }

        if(cancel_tv == v) {
            dismiss();
        }



    }

    private void clearSelection() {
        for (int i = 0; i < group_lv.getCount(); i++){
            group_lv.setItemChecked(i,false); //Unselect all options
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(!DB.isValid(selectedGroup)) {
            clearSelection();
            back_iv.setVisibility(View.VISIBLE);
            set_tv.setVisibility(View.VISIBLE);
            selectedGroup = groups.get(position).getId();
            group_lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

            users = new ArrayList<User>(fragment.getBinder().getGroup(selectedGroup).getUsers().values());
            groupAdapter.notifyDataSetChanged();
            title_tv.setText(groups.get(position).getName());
        }

    }


    public class GroupAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        public GroupAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return DB.isValid(selectedGroup) ? users.size(): groups.size();
        }

        @Override
        public Object getItem(int position) {
            return DB.isValid(selectedGroup) ? users.get(position): groups.get(position);
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


            if(DB.isValid(selectedGroup)) {
                User user = (User) getItem(position);
                holder.name_tv.setText(user.getName());

                Listener l = Var.getUserThumbnailListener(fragment.getBinder(), user, holder.icon_iv);
                fragment.getBinder().getImageLoader().DisplayImage(l, user.getThumbnail(), holder.icon_iv, holder.icon_pb);
            } else {
                Group group = (Group) getItem(position);
                holder.name_tv.setText(group.getName());

                Listener l = Var.getGroupThumbnailListener(fragment.getBinder(), group, holder.icon_iv);
                fragment.getBinder().getImageLoader().DisplayImage(l, group.getThumbnail(), holder.icon_iv, holder.icon_pb);
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
