package com.edaviessmith.consumecontent.data;

import com.edaviessmith.consumecontent.db.DB;
import com.edaviessmith.consumecontent.util.Var;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private int id = -1;    //Placeholder for null
    private int sort;
    private String name;

    private int thumb;
    private List<String> thumbnails;
    private String thumbnail;       //Used for groups

    public Content() { }

    public Content(int id, int sort, String name,int thumb, String thumbnails) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumb = thumb;
        this.thumbnails = DB.stringToStringList(thumbnails);
    }

    public Content(int id, int sort, String name, String thumbnail) {
        this.id = id;
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.thumbnails = new ArrayList<String>();
    }

    public Content(String name, String thumbnails) {
        this.name = name;
        this.thumbnails = DB.stringToStringList(thumbnails);
    }

    public Content(int sort, String name, int thumb, String thumbnails) {
        this.sort = sort;
        this.name = name;
        this.thumb = thumb;
        this.thumbnails = DB.stringToStringList(thumbnails);
    }

    public Content(int sort, String name, String thumbnail) {
        this.sort = sort;
        this.name = name;
        this.thumbnail = thumbnail;
        this.thumbnails = new ArrayList<String>();
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
        return Var.isEmpty(thumbnail)? thumbnails.get(thumb): thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public List<String> getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(List<String> thumbnails) {
        this.thumbnails = thumbnails;
    }


}
