package com.bailcompany.model;

// TODO: Auto-generated Javadoc
/**
 * The Class Feed is a simple Java Bean that is used to hold Name, Detail and
 * image pairs.
 */
public class Feed
{
	private int index;

	/** The title. */
	private String title;

	/** The description. */
	private String desc;

	/** The image resource id. */
	private int image_normal;
	private int image_selected;

	/** The is online. */
	private boolean isOnline;
	private String distance;

	public String getDistance() {
		return distance;
	}

	/**
	 * Instantiates a new feed class.
	 * 
	 * @param title
	 *            the title
	 * @param desc
	 *            the desc
	 * @param image
	 *            the image
	 */
	public Feed(String title, String desc, int image_normal , int image_selected ,int index)
	{
		this.title = title;
		this.desc = desc;
		this.image_normal = image_normal;
		this.image_selected = image_selected;
		this.index = index;
	}

	/**
	 * Instantiates a new feed.
	 * 
	 * @param title
	 *            the title
	 * @param desc
	 *            the desc
	 * @param image
	 *            the image
	 * @param online
	 *            the online
	 */
	public Feed(String title, String desc,String distance, int image, boolean online)
	{
		this.title = title;
		this.desc = desc;
		this.image_normal = image;
		this.distance = distance;
		isOnline = online;
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle()
	{

		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 *            the new title
	 */
	public void setTitle(String title)
	{

		this.title = title;
	}

	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDesc()
	{

		return desc;
	}

	/**
	 * Sets the description.
	 * 
	 * @param desc
	 *            the new description
	 */
	public void setDesc(String desc)
	{

		this.desc = desc;
	}

	/**
	 * Gets the image resource id..
	 * 
	 * @return the image resource id.
	 */
	public int getImageNormal()
	{

		return image_normal;
	}

	/**
	 * Sets the image resource id..
	 * 
	 * @param image
	 *            the new image resource id.
	 */
	public void setImageNormal(int image)
	{

		this.image_normal = image;
	}

	/**
	 * Checks if is online.
	 * 
	 * @return true, if is online
	 */
	public boolean isOnline()
	{
		return isOnline;
	}

	/**
	 * Sets the online.
	 * 
	 * @param isOnline
	 *            the new online
	 */
	public void setOnline(boolean isOnline)
	{
		this.isOnline = isOnline;
	}

	public int getImageSelected() {
		return image_selected;
	}

	public void setImageSelected(int image_selected) {
		this.image_selected = image_selected;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
