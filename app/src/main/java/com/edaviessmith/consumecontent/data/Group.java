package com.edaviessmith.consumecontent.data;


import android.util.SparseArray;

import java.util.List;

public class Group extends Content {

    private boolean isVisible;

    private SparseArray<User> users;
    SparseArray<User> removed;


    public Group () { }

    public Group (String name, boolean isVisible) {
        setName(name);
        setVisible(isVisible);
        users = new SparseArray<User>();
    }

    public Group(int sort, String name, String thumbnail, boolean isVisible) {
        super(sort, name, thumbnail);
        this.isVisible = isVisible;
    }

    public Group(int id, int sort, String name, String thumbnail, boolean isVisible) {
        super(id, sort, name, thumbnail);
        this.isVisible = isVisible;
    }

    public Group(Group group) {
        super(group.getId(), group.getSort(), group.getName(), group.getThumbnail());
        this.isVisible = group.isVisible();
        this.users = group.getUsers();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public SparseArray<User> getUsers() {
        return users;
    }

    public void setUsers(SparseArray<User> users) {
        this.users = users;
    }


    public SparseArray<User> getRemoved() {
        return removed;
    }

    public void clearRemoved() {
        removed = null;
    }

    //Utility method to update users and set removed users
    public void setUserList(List<User> newUsers) {
        removed = getUsers().clone();
        SparseArray<User> us = new SparseArray<User>();
        for(int i=0; i < newUsers.size(); i++) {
            removed.remove(newUsers.get(i).getId());
            us.put(i, newUsers.get(i));        //Places user in order of sort not by ID

        }
        setUsers(us);
    }


    @Override
    public String toString() {

        return "}});\n\n"+getName().toLowerCase()+"Group"+".setUsers(new SparseArray<User>() {{";

    }


}
