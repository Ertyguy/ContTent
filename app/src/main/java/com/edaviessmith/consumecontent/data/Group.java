package com.edaviessmith.consumecontent.data;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Group extends Content {

    private boolean isVisible;

    private LinkedHashMap<Integer, User> users;
    List<User> removed;


    public Group () {
        users = new LinkedHashMap<Integer, User>();
    }

    public Group (String name, boolean isVisible) {
        setName(name);
        setVisible(isVisible);
        users = new LinkedHashMap<Integer, User>();
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

    public LinkedHashMap<Integer, User> getUsers() {
        return users;
    }

    public void setUsers(LinkedHashMap<Integer, User> users) {
        this.users = users;
    }


    public List<User> getRemoved() {
        return removed;
    }

    public void clearRemoved() {
        removed = null;
    }

    //Utility method to update users and set removed users
    public void setUserList(List<User> newUsers) {
        removed = new ArrayList<User>(getUsers().values());
        LinkedHashMap<Integer, User> us = new LinkedHashMap<Integer, User>();
        int i = 0;
        for(User user : newUsers) { //Places user in order of sort not by ID
            removed.remove(user);
            user.setSort(i++);
            us.put(user.getId(), user);
        }
        setUsers(us);
    }


    @Override
    public String toString() {

        return "}});\n\n"+getName().toLowerCase()+"Group"+".setUsers(new SparseArray<User>() {{";

    }


}
