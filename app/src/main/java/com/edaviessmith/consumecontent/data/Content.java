package com.edaviessmith.consumecontent.data;

public class Content {
    private int id = -1;    //Placeholder for null
    private int sort;
    private String name;
    private String thumbnail;

    public Content() { }

    public Content(int id, int sort, String name, String thumbnail) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
