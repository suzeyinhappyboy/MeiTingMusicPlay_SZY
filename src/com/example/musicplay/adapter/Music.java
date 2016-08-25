package com.example.musicplay.adapter;

public class Music {
	private String title;
	private String path;
	private String singer;
	public Music(String title, String singer, String path){
		 setTitle(title);
		 setSinger(singer);
		 setPath(path);
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	
}
