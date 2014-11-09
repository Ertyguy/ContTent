package com.edaviessmith.consumecontent.data;

public class MediaFeed {


	public int type;
	public String name;

    public MediaFeed() {
        this.type = -1;
        this.name = "Placeholder";
    }

	public MediaFeed(int type, String name) {
		this.type = type;
		this.name = name;
	}


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}