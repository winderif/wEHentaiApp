package com.example.ehentaiapp.database;

import java.io.Serializable;

public class Item implements Serializable {
	private Long id;
	
	private String datetime;
	private String category;
	private String title;
	private String urlOfComic;
	private String urlOfComicCover;
	private int numOfPages;
	private boolean isFavorite;

	public Item() {
		this.title = "";
		this.category = "";
		this.isFavorite = false;
	}

	
	public Item(Long id, String datetime, String category, String title,
			String urlOfComic, String urlOfComicCover, int numOfPages,
			boolean isFavorite) {
		super();
		this.id = id;
		this.datetime = datetime;
		this.category = category;
		this.title = title;
		this.urlOfComic = urlOfComic;
		this.urlOfComicCover = urlOfComicCover;
		this.numOfPages = numOfPages;
		this.isFavorite = isFavorite;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getNumOfPages() {
		return numOfPages;
	}

	public void setNumOfPages(int numOfPages) {
		this.numOfPages = numOfPages;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrlOfComic() {
		return urlOfComic;
	}

	public void setUrlOfComic(String urlOfComic) {
		this.urlOfComic = urlOfComic;
	}

	public String getUrlOfComicCover() {
		return urlOfComicCover;
	}

	public void setUrlOfComicCover(String urlOfComicCover) {
		this.urlOfComicCover = urlOfComicCover;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}
}
