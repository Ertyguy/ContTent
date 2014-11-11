package com.edaviessmith.consumecontent.data;


public class Group extends Content {

    private boolean isVisible;

    public Group () { }

    public Group (String name, boolean isVisible) {
        setName(name);
        setVisible(isVisible);
    }

    public Group(int id, int sort, String name, String thumbnail, boolean isVisible) {
        super(id, sort, name, thumbnail);
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
