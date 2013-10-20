package yuan.model;

import java.io.Serializable;

public class ImageInfo implements Serializable{
	
	private static final long serialVersionUID = 1L;
	String picUrl;
	int width;
	int heigth;	
	
	public ImageInfo() {
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
