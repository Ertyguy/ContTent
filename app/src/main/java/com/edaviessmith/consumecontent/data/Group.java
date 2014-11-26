package com.edaviessmith.consumecontent.data;


import android.util.SparseArray;

public class Group extends Content {

    private boolean isVisible;

    private SparseArray<User> users;

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

    @Override
    public String toString() {
        return "new Group(" + getId() +
                ", " + getSort() +
                ", \"" + getName() + '\"' +
                ", \"" + getThumbnail() + '\"' +
                ", true);";
    }
}
