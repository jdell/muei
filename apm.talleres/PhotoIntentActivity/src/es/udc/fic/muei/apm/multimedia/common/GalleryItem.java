package es.udc.fic.muei.apm.multimedia.common;

import android.graphics.Bitmap;

public class GalleryItem {
	private Bitmap image;
	private String title;
	private String date;
	
	public GalleryItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GalleryItem(Bitmap image, String title,String date) {
		super();
		this.setImage(image);
		this.setTitle(title);
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
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
