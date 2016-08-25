package com.example.musicplay.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.service.voice.VoiceInteractionService;

public class DatabaseHelper extends SQLiteOpenHelper{

		private static final String DATABASE_NAME = "MusicData";
		private static final int DATABASE_VERSION = 1;
		private static DatabaseHelper dbUtil;
		
		public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
			super(context, name, null, version);

		}
		//file MP3文件名
		@Override
		public void onCreate(SQLiteDatabase db) {
			String sqlcreate = "create table musicdata(";
			sqlcreate += "id INTEGER PRIMARY KEY,file varchar(100),music varchar(50),singer varchar(50),";
			sqlcreate += "path varchar(200),lyric varchar(100),lpath varchar(200),";
			sqlcreate += "ilike integer);";
			db.execSQL(sqlcreate);
			
			String sqldownload="create table networkmusic(";
			sqldownload +="id integer PRIMARY KEY,file varchar(100),music varchar(50),singer varchar(50),";
			sqldownload += "path varchar(200),lyric varchar(100),lpath varchar(200),";
			sqldownload += "ilike integer);";
			db.execSQL(sqldownload);
			
			String sqllastplay="create table lastplay(id integer PRIMARY KEY)";
			db.execSQL(sqllastplay);			
			String sqlplaylist="create table playlist(id integer PRIMARY KEY,name varchar(20))";
			db.execSQL(sqlplaylist);
			String sqllistinfo="create table listinfo(id integer ,musicid integer,";
			sqllistinfo+="FOREIGN KEY(id) REFERENCES playlist(id), FOREIGN KEY(musicid) REFERENCES musicdata(id));";
			db.execSQL(sqllistinfo); 
			System.out.println("Create succeeded");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			System.out.println("-----onUpgrade Called-----" + oldVersion + "--->"  
	                + newVersion);
			
		}
		
		public static SQLiteDatabase getInstance(Context context) {  
	       // if (dbUtil == null) {  
	        	 // 指定数据库名为MusicData，需修改时在此修改；此处使用默认工厂；指定版本为1  
	            dbUtil = new DatabaseHelper(context,DATABASE_NAME, null, DATABASE_VERSION);  
	      //  }  
	        return dbUtil.getReadableDatabase();  
	    }  
		
		
}
