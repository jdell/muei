package es.udc.fic.muei.apm.multimedia.common;

import android.graphics.Bitmap;

public class GalleryItem {
	private Bitmap image;
	private String name;
	private String date;
	
	public GalleryItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GalleryItem(Bitmap image, String name,String date) {
		super();
		this.setImage(image);
		this.setName(name);
		this.setDate(date);
	}
	/**
	 * @return the image
	 */
	public Bitmap getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(Bitmap image) {
		this.image = image;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
