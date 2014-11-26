package com.edaviessmith.consumecontent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.edaviessmith.consumecontent.data.Group;
import com.edaviessmith.consumecontent.data.Notification;
import com.edaviessmith.consumecontent.data.User;
import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.ActionFragment;
import com.edaviessmith.consumecontent.util.Var;
import com.edaviessmith.consumecontent.view.Fab;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;


public class GroupFragment extends ActionFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final int GROUPS_LIST = 0;
    public static final int GROUPS_ALL  = 1;
    public static final int GROUP_EDIT  = 2;

    public ContentActivity act;
    private RecyclerView groups_rv;
    private LinearLayoutManager linearLayoutManager;
    private GroupAdapter groupAdapter;
    public EditGroupAdapter editGroupAdapter;
    private DragSortListView group_lv;
    private DragSortController dragSortController;
    private View userGroup_v, groupThumbnail_v, visible_v, footer;
    private Fab save_fab;
    private SwitchCompat visible_sw;
    private ImageView groupThumbnail_iv;
    private EditText groupName_edt;
    private ProgressBar groupThumbnail_pb;

    private List<Group> groupList;
    private Group editGroup;
    private List<User> users, selectedUsers;

    public int groupState = -1;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = false;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;


    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        act = (ContentActivity) getActivity();

        act.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editGroup = new Group();
        users = new ArrayList<User>();
        selectedUsers = new ArrayList<User>();

        View header = inflater.inflate(R.layout.header_group_edit, null, false);
        footer = inflater.inflate(R.layout.item_list_divider, null, false);
        footer.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, Var.getPixels(TypedValue.COMPLEX_UNIT_DIP, 48)));

        groups_rv = (RecyclerView) view.findViewById(R.id.groups_rv);
        linearLayoutManager = new LinearLayoutManager(act);
        groups_rv.setLayoutManager(linearLayoutManager);
        groups_rv.setItemAnimator(new DefaultItemAnimator());


        userGroup_v = view.findViewById(R.id.user_group_v);
        group_lv = (DragSortListView) view.findViewById(R.id.user_group_lv);
        group_lv.addHeaderView(header, null, false);
        group_lv.addFooterView(footer, null, false);

        groupThumbnail_v = header.findViewById(R.id.group_thumbnail_v);
        groupThumbnail_v.setOnClickListener(this);
        groupThumbnail_iv = (ImageView) header.findViewById(R.id.group_thumbnail_iv);
        groupThumbnail_pb = (ProgressBar) header.findViewById(R.id.group_thumbnail_pb);
        groupName_edt = (EditText) header.findViewById(R.id.group_name_edt);

        visible_v = header.findViewById(R.id.visible_v);
        visible_v.setOnClickListener(this);
        visible_sw = (SwitchCompat) header.findViewById(R.id.visible_sw);


        save_fab = (Fab) view.findViewById(R.id.save_fab);
        save_fab.setOnClickListener(this);

        toggleState(GROUPS_LIST);


        editGroupAdapter = new EditGroupAdapter(act, users);
        group_lv.setAdapter(editGroupAdapter);

        groupAdapter = new GroupAdapter(act);
        groups_rv.setAdapter(groupAdapter);


        dragSortController = buildController(group_lv);

        group_lv.setDropListener(onDrop);
        //group_lv.setRemoveListener(onRemove);
        group_lv.setFloatViewManager(dragSortController);
        group_lv.setOnTouchListener(dragSortController);
        group_lv.setDragEnabled(dragEnabled);
        group_lv.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                User item = editGroupAdapter.getItem(from);
                editGroupAdapter.remove(item);
                editGroupAdapter.insert(item, to);
                group_lv.moveCheckState(from, to);
            }
        }
    };

    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        //   dragStartMode = onDown
        //   removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private List<Group> getGroups() {
        if(getBinder() != null) {
            if (groupState == GROUPS_LIST) {
                List<Group> vis = new ArrayList<Group>();
                for (Group group : getBinder().getGroups())
                    if (group.isVisible()) vis.add(group);
                return vis;
            }

            if (groupState == GROUPS_ALL || groupState == GROUP_EDIT)
                return getBinder().getGroups();
        }
        return null;
    }


    public void toggleState(int groupState) {
        boolean changed = (this.groupState != groupState);
        this.groupState = groupState;

        if(changed) {
            groupList = getGroups();
        }

        act.actionEdit.setVisibility((groupState == GROUPS_LIST || groupState == GROUPS_ALL) ? View.VISIBLE: View.GONE);
        act.toggleEditActions(false);

        userGroup_v.setVisibility(groupState == GROUP_EDIT? View.VISIBLE: View.GONE);

        if(groupState == GROUPS_LIST){
            act.getSupportActionBar().setTitle("Groups");
            act.actionEdit.setImageResource(R.drawable.ic_create_white_24dp);

        }
        if(groupState == GROUPS_ALL) {
            act.getSupportActionBar().setTitle("Edit Groups");
            act.actionEdit.setImageResource(R.drawable.ic_check_white_24dp);
        }
        if(groupState == GROUP_EDIT){
            act.getSupportActionBar().setTitle(DB.isValid(editGroup.getId())? "Edit Group": "New Group");

            visible_sw.setChecked(editGroup.isVisible());
            act.imageLoader.DisplayImage(editGroup.getThumbnail(), groupThumbnail_iv, groupThumbnail_pb, false);

            groupName_edt.setText(editGroup.getName());

            users.clear();
            for(int i=0; i< editGroup.getUsers().size(); i++) {
                users.add( editGroup.getUsers().valueAt(i));
            }

            editGroupAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onClick(View v) {

        if(visible_v == v) {
            editGroup.setVisible(!editGroup.isVisible());
            visible_sw.setChecked(editGroup.isVisible());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e(TAG, "onoptionsItemSelected "+id +" = "+android.R.id.home);
        if(id == android.R.id.home) {
            if(groupState != GROUPS_LIST) {
                toggleState(GROUPS_LIST);
                return true;
            } else {
                act.setState(Var.LIST_USERS);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void setNotifications() {
        new NotificationDialog(this, getBinder().getNotificationList(), selectedUsers);
    }

    public void deleteConfirmation() {

    }

    public void clearSelection(int selected) {
        for (int i = 0; i < group_lv.getCount(); i++) group_lv.setItemChecked(i, (i == selected)); //Unselect all options
        if(selected < 0) act.toggleEditActions(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        selectedUsers.clear();
        SparseBooleanArray checked = group_lv.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            if(checked.valueAt(i)) {
                selectedUsers.add((User) group_lv.getItemAtPosition(checked.keyAt(i)));
            }
        }

        act.toggleEditActions(selectedUsers.size() > 0);
    }


    public class GroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

        private Context context;


        public GroupAdapter(Context context) {
            this.context = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_group, viewGroup, false);
            v.setOnClickListener(this);
            return new ViewHolderItem(v);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return groupList == null ? 0 : groupList.size();
        }

        @Override
        public void onClick(final View view) {
            int itemPosition = groups_rv.getChildPosition(view);
            if(groupState == GROUPS_LIST) {
                act.setGroup(groupList.get(itemPosition));
            }

            if(groupState == GROUPS_ALL) {
                editGroup = new Group(groupList.get(itemPosition));
                toggleState(GROUP_EDIT);

            }
            //TODO onclick

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof ViewHolderItem) {

                ViewHolderItem holder = (ViewHolderItem) viewHolder;

                Group item = groupList.get(i);

                act.imageLoader.DisplayImage(item.getThumbnail(), holder.icon_iv, holder.icon_pb, false);
                holder.name_tv.setText(item.getName());
                holder.userCount_tv.setText(item.getUsers().size() + " users");

                int watchingCount = 0;
                for (int u = 0; u < item.getUsers().size(); u++) {
                    for (int m = 0; m < item.getUsers().valueAt(m).getCastMediaFeed().size(); m++) {
                        if (DB.isValid(item.getUsers().valueAt(m).getCastMediaFeed().valueAt(m).getNotificationId())) {
                            watchingCount++;
                            break;
                        }
                    }
                }


                holder.watchingCount_tv.setText(watchingCount > 0? ("Watching " + watchingCount + " users") : "Not watching");

            }
        }


        public class ViewHolderItem extends RecyclerView.ViewHolder {
            public ImageView icon_iv;
            public ProgressBar icon_pb;
            public TextView name_tv;
            public TextView userCount_tv;
            public TextView watchingCount_tv;

            public ViewHolderItem(View itemView) {
                super(itemView);
                icon_iv = (ImageView) itemView.findViewById(R.id.icon_iv);
                icon_pb = (ProgressBar) itemView.findViewById(R.id.icon_pb);
                name_tv = (TextView) itemView.findViewById(R.id.name_tv);

                userCount_tv = (TextView) itemView.findViewById(R.id.user_count_tv);
                watchingCount_tv = (TextView) itemView.findViewById(R.id.watching_count_tv);
            }
        }
    }

    public class EditGroupAdapter extends ArrayAdapter<User> {

        private LayoutInflater inflater;
        Context context;
        List<User> users;

        public EditGroupAdapter(Context context,List<User> users) {
            super(context, R.layout.item_group_user, users);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.users = users;
        }

        @Override
        public int getCount() {
            return users == null? 0: users.size();
        }

        @Override
        public User getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            footer.setVisibility(getCount() == 0? View.GONE: View.VISIBLE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_group_user, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }

            final ViewHolder holder = (ViewHolder) convertView.getTag();
            final User user = getItem(position);

            act.imageLoader.DisplayImage(user.getThumbnail(), holder.thumbnail_iv, holder.thumbnail_pb);
            holder.name_tv.setText(user.getName());

            List<Notification> userNotifications = new ArrayList<Notification>();
            for(int i=0;i<user.getCastMediaFeed().size(); i++) {
                if(DB.isValid(user.getCastMediaFeed().valueAt(i).getNotificationId())) userNotifications.add(getBinder().getNotificationList().getNotification(user.getCastMediaFeed().valueAt(i).getNotificationId()));
            }
            if(userNotifications.size() > 0) holder.nextAlarm_tv.setText(Var.getNextNotificationTime(userNotifications, getBinder().getNotificationList().getScheduleNotification()));
            else holder.nextAlarm_tv.setText("Not watching");


            return convertView;
        }

        class ViewHolder {
            ImageView thumbnail_iv;
            ProgressBar thumbnail_pb;
            TextView name_tv;
            TextView nextAlarm_tv;

            public ViewHolder(View view) {
                thumbnail_iv = (ImageView) view.findViewById(R.id.thumbnail_iv);
                thumbnail_pb = (ProgressBar) view.findViewById(R.id.thumbnail_pb);
                name_tv = (TextView) view.findViewById(R.id.name_tv);
                nextAlarm_tv = (TextView) view.findViewById(R.id.next_alarm_tv);
            }
        }

    }


}




