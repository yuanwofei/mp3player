package com.music.factory.model;

import java.io.Serializable;

public class Image implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String picUrl;
	
	private int width;
	
	private int heigth;	
	
	public Image() {
		super();
	}
	
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeigth() {
		return heigth;
	}
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}
}
