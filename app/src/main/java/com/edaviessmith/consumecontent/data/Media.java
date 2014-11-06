package com.edaviessmith.consumecontent.data;

import java.io.Serializable;

public class Media implements Serializable{
	private static final long serialVersionUID = -2403529325960234150L;
	public int type;
	public String name;
	public Media (int type, String name) {
		this.type = type;
		this.name = name;
	}
}