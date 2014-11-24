package com.edaviessmith.consumecontent.data;


import java.util.ArrayList;
import java.util.List;

public class Group extends Content {

    private boolean isVisible;

    private List<User> users;

    public Group () { }

    public Group (String name, boolean isVisible) {
        setName(name);
        setVisible(isVisible);
        users = new ArrayList<User>();
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

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
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
