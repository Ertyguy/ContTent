package com.edaviessmith.consumecontent.data;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
	private static final long serialVersionUID = 6856742212195516377L;
	public String name;
	public List<Media> media;
	public User (String name, List<Media> media) {
		this.name = name;
		this.media = media;
	}
}