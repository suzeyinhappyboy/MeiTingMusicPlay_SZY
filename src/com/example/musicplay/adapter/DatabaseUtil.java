package com.example.musicplay.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplay.MainActivity;
import com.example.musicplay.utils.Constant;

import android.R.anim;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtil {
	
	 private static SQLiteDatabase db; 
	 //private static boolean flag=false; 
  //  public DatabaseUtil(Context context) {  
    //    db = DatabaseHelper.getInstance(context);  
  //  }  
    public static void deleteTable(Context context)
	{
    	db = DatabaseHelper.getInstance(context); 
		try
		{
			db.delete("listinfo", null, null);
			db.delete("playlist", null, null);
			db.delete("lastplay", null, null);
			db.delete("musicdata", null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("delete success!");
			db.close();
		}
	}
    
    public static void deleteNetmusic(Context context)
	{
    	SQLiteDatabase musicData = null;
		musicData = DatabaseHelper.getInstance(context);
    	musicData.delete("networkmusic",null,null);
    	musicData.close();
    	System.out.println("deleteNetmusic");
	}
    
    public static int setMusic(Context context,String[] musicInfo,String playlistid) {
	
    	db = DatabaseHelper.getInstance(context); 
    	Cursor cur = null;
		final ContentValues cv = new ContentValues();
		int musicId=-1;
		//System.out.println("setMusic->"+musicInfo[0]);
		//System.out.println("setMusic->"+musicInfo[2]);
		//System.out.println("setMusic->"+musicInfo[3]);
		
		try {
			if (playlistid.equals(Constant.Netmusicid)) {
				String sql = "select max(id) from networkmusic";
				cur = db.rawQuery(sql, null);
			} else {
				String sql = "select max(id) from musicdata";
				cur = db.rawQuery(sql, null);
			}			
			if(cur.moveToFirst())
			{
				musicId=cur.getInt(0)+1;
				
				
			}else
			{
				musicId=0;
				System.out.println("setMusic-> First Song!");
				
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

			
		try 
		{
			cv.put("id", musicId); 			
            cv.put("file", musicInfo[0]);
            cv.put("music", musicInfo[1]);  
            cv.put("singer", musicInfo[2]);
            cv.put("path", musicInfo[3]);  
            cv.put("lyric", musicInfo[4]);
            cv.put("lpath", musicInfo[5]);
            cv.put("ilike", 0);
            // name和phone为列名  
            if (playlistid.equals(Constant.Netmusicid)) {
            	long res = db.insert("networkmusic", null, cv);// 插入数据  
            	if (res == -1) {  
                    System.out.println("添加失败");  
      
                } else {  
                	System.out.println("添加成功");
                }   
			}else if (playlistid.equals(Constant.Localmusicid)) {
				long res = db.insert("musicdata", null, cv);// 插入数据 
				if (res == -1) {  
	                System.out.println("添加失败");  
	  
	            } else {  
	            	System.out.println("添加成功");
	            }   
			}	
            	 
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(cur!=null){cur.close();}
			db.close();
		}
		System.out.println("setMusic.musicId->"+musicId);		
		System.out.println("setMusic->"+musicInfo[2]);
		System.out.println("setMusic->"+musicInfo[3]);
		return musicId;
	}
    
    public ArrayList<Song> getAllSongs(Context context,String playlistid) {
   	
    	SQLiteDatabase musicData = null;
    	Cursor cursor = null;
		musicData = DatabaseHelper.getInstance(context);
    	final ArrayList<Song> songslist = new ArrayList<Song>();
    	Song song = null;
    	//String[] selectionArgs={playlistid};
    	if (playlistid.equals(Constant.Netmusicid)) {
    		cursor = musicData.query("networkmusic", new String[] { "id",  
                    "music", "singer" ,"path"}, null, null, null,null, null);
    	}
    	else if (playlistid.equals(Constant.Localmusicid)) {
    		cursor = musicData.query("musicdata", new String[] { "id",  
                    "music", "singer" ,"path"},null, null, null,null, null);
			}		    		
    	/*String sql = "select * from musicdata where playlistid=? and id=?";
        String[] selectionArgs={playlistid,String.valueOf(1)};
    	//String sql = "select * from musicdata where playlistid='" + playlistid + "';";
    	Cursor cursor = musicData.rawQuery(sql, selectionArgs);*/
		while (cursor.moveToNext()){
			song = new Song();
	    	// 歌曲名
	        song.setTitle(cursor.getString(cursor.getColumnIndex("music")));
	        // 歌手名
	        song.setSinger(cursor.getString(cursor.getColumnIndex("singer")));
	        song.setFileUrl(cursor.getString(cursor.getColumnIndex("path")));
	        songslist.add(song);
	       // System.out.println("getAllSongs->"+song.getTitle());
	        //System.out.println("getAllSongs->"+song.getSinger());
	       // System.out.println(song.getFileUrl());
		}
	    if(cursor!=null)
	    {
	    	cursor.close();
	    }
	    musicData.close();
    	return songslist;
		
    }

    public static boolean setILikeStatus(Context context,int id)
	{
		boolean flag=false;
		SQLiteDatabase musicData = null;
		musicData = DatabaseHelper.getInstance(context);
		Cursor cur=null;
		String iLikeStr="0";
		try {
			
			String sql = "select ilike from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				iLikeStr = (cur.getInt(0)==0)?"1":"0";
				String sql2 = "update musicdata set ilike="+iLikeStr;
				sql2+=" where id="+id+";";
				musicData.execSQL(sql2);
				flag=true;
			}
			else
			{
				flag=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return flag;
	}
    
 // 获取歌曲信息
 	public static ArrayList<String> getMusicInfo(Context context,String playlistid,int id) {
 		SQLiteDatabase musicData = null;
 		musicData = DatabaseHelper.getInstance(context);
 		Cursor cursor = null;
 		ArrayList<String> musicInfo = new ArrayList<String>();
 		try {
 			if (playlistid.equals(Constant.Netmusicid)) {
 				cursor = musicData.query("networkmusic", new String[] {
 			            "file","music","singer","path","lyric","lpath","ilike" }, "id=? ", 
 			 					new String[]{String.valueOf(id)},null, null, null);
			} else if(playlistid.equals(Constant.Localmusicid)){
				cursor = musicData.query("musicdata", new String[] {
			            "file","music","singer","path","lyric","lpath","ilike" }, "id=?", 
			 					new String[]{String.valueOf(id)},null, null, null);
			}
 			
 			//System.out.println("getMusicInfo="+cursor.getString(cursor.getColumnIndex("music")));
 			/*cursor = musicData.rawQuery("select * from musicData where playlistid=? and id =?",
 					new String[]{playlistid,String.valueOf(id)});*/
 			
 			/*cursor = musicData.query("musicdata", new String[] {
		            "file","music","singer","path","lyric","lpath","ilike" }, "id=? and playlistid=?", 
		 					new String[]{String.valueOf(id),playlistid},null, null, null);*/
 			if (cursor.moveToFirst()) {
 				for (int i = 0; i < cursor.getColumnCount(); i++) {
 					musicInfo.add(cursor.getString(i));
 					//System.out.println("getMusicInfo="+cursor.getString(cursor.getColumnIndex("id")));
 				}
 			} else {
 				musicInfo.add("中国好音乐-传播好声音");
 				musicInfo.add("中国好音乐");
 				musicInfo.add("传播好声音");
 				musicInfo.add("0");
 				musicInfo.add("0");
 				musicInfo.add("0");
 				musicInfo.add("0");
 				musicInfo.add("0");
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 			musicInfo.add("中国好音乐-传播好声音");
 			musicInfo.add("中国好音乐");
 			musicInfo.add("传播好声音");
 			musicInfo.add("0");
 			musicInfo.add("0");
 			musicInfo.add("0");
 			musicInfo.add("0");
 			musicInfo.add("0");
 			return musicInfo;
 		} finally {
 			if(cursor!=null){cursor.close();}
 			musicData.close();
 		}
 		//System.out.println("musicInfo.get(0)="+musicInfo.get(0));
 		//System.out.println("musicInfo.get(1)="+musicInfo.get(1));
 		//System.out.println("getMusicInfo.musicInfo.size()="+musicInfo.size());
 		return musicInfo;
 	}
 	public static String getLyricPath(Context context,int id,String playlistid) {
		
 		if (id == -1) {
			return "";
		}
		SQLiteDatabase musicData = null;
		musicData = DatabaseHelper.getInstance(context);
		Cursor cur = null;
		String lyricpath = null;
		try {
			if (playlistid.equals(Constant.Netmusicid)) {
 				cur = db.query("networkmusic", new String[] {
 						"lpath" }, "id=?",new String[]{String.valueOf(id)},null, null, null);
			} else if(playlistid.equals(Constant.Localmusicid)){
				cur = musicData.query("musicdata", new String[] {  
	            		"lpath" }, "id=" + id + "", null, null, null, null);
			}		
			/*String sql = "select lpath from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);*/
			
			if (cur.moveToFirst()) {
				lyricpath = cur.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return lyricpath;
	}
    public static ArrayList<String[]> getLyric(String lyricPath) {
		if(lyricPath==null)
		{
			return null;
		}
		ArrayList<String[]> mp3Lyric = new ArrayList<String[]>();
		File file = new File(lyricPath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedReader reader = null;
		if (file.exists()) {
			try {
				if (file.exists()) {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bis.mark(4);
					byte[] first3bytes = new byte[3];
					// System.out.println("");
					// 找到文档的前三个字节并自动判断文档类型。
					bis.read(first3bytes);
					bis.reset();
					if (first3bytes[0] == (byte) 0xEF
							&& first3bytes[1] == (byte) 0xBB
							&& first3bytes[2] == (byte) 0xBF) {// utf-8

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-8"));

					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFE) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"unicode"));
					} else if (first3bytes[0] == (byte) 0xFE
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16be"));
					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16le"));
					} else {

						reader = new BufferedReader(new InputStreamReader(bis,
								"GBK"));
					}
//Pattern实例订制了一个所用语法与PERL的类似的正则表达式经编译后的模式，然后一个Matcher实例在这个给定的Pattern实例的模式控制下进行字符串的匹配工作
					String strTemp = "";
					while ((strTemp = reader.readLine()) != null) {
						String patternStr = "(.)*([0-9]{2}):([0-9]{2}).([0-9]{2})(.)*";
						Pattern p = Pattern.compile(patternStr);
						Matcher m = p.matcher(strTemp);
						if (m.matches()) {
							String lyric[] = { strTemp.substring(2, 3),
									strTemp.substring(4, 6),
									strTemp.substring(10, strTemp.length()) };
							mp3Lyric.add(lyric);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (bis != null) {
					try {
						bis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return mp3Lyric;
		} else {
			return null;
		}
	}
    //db.query("表名", new String[]{"字段1，字段2"}, "条件1=? and 条件2=?", new String[]{"条件1的值，条件2的值"},null,null,null)
    public static String getMusicPath(Context context,String playlistid,int id) {
    	db = DatabaseHelper.getInstance(context);
    	if (id == -1) {
			return null;
		}
		//setLastPlay(context,id);
    	Cursor cursor = null;
		String path = null;
		try {
			if (playlistid.equals(Constant.Netmusicid)) {
 				cursor = db.query("networkmusic", new String[] {
 						"path" }, "id=?",new String[]{String.valueOf(id)},null, null, null);
			} else if(playlistid.equals(Constant.Localmusicid)){
				cursor = db.query("musicdata", new String[] {  
                		"path" }, "id=?", new String[]{String.valueOf(id)}, null,null, null);
			}			
			
			if (cursor.moveToFirst()) {
				path = cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cursor!=null){cursor.close();}
			db.close();
		}
		System.out.println("getMusicPath="+path);
		if(playlistid.equals(Constant.Netmusicid)){
			path=MainActivity.BaseURL+path;
		}
		return path;
	}
    
    public static ArrayList<Integer> getMusicList(Context context,String listNumber) {
    	SQLiteDatabase musicData = null;
 		musicData = DatabaseHelper.getInstance(context);
 		Cursor cursor = null;
		ArrayList<Integer> file = null;
		
		if(listNumber.equals(Constant.Localmusicid))
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				
				//String sql = "select id from musicdata order by singer DESC;"; 
				String sql = "select id from musicdata";
				cursor = musicData.rawQuery(sql, null);
				//cursor = musicData.query("musicdata", new String[] {"id"}, null,null, null, null, null);
				//cursor.moveToFirst();
				System.out.println("getMusicList.cursor.getCount()="+cursor.getCount());
				while (cursor.moveToNext()) {
					music = cursor.getInt(0);
					//System.out.println("music="+music);
					file.add(music);					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor!=null){cursor.close();}
				musicData.close();
			}
			System.out.println("getMusicList Constant.Localmusicid="+listNumber);
		}
		
		else if(listNumber.equals(Constant.Netmusicid))
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				
				//String sql = "select id from musicdata order by singer DESC;";
				String sql = "select id from networkmusic";
				cursor = musicData.rawQuery(sql,null);
				//cursor = musicData.query("musicdata", new String[] {"id"}, null,null, null, null, null);
				//cursor.moveToFirst();
				System.out.println("getMusicList.cursor.getCount()="+cursor.getCount());
				while (cursor.moveToNext()) {
					music = cursor.getInt(0);
					//System.out.println("music="+music);
					file.add(music);					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cursor!=null){cursor.close();}
				musicData.close();
			}
			System.out.println("getMusicList Constant.Netmusicid="+listNumber);
		}		
		
		return file;
	}
    
    public static int getNextMusic(ArrayList<Integer> musicList, int id) 
	{
		if(id==-1)
		{
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == musicList.size() - 1) {
					return musicList.get(0);
				} else {
					++i;
					System.out.println("getNextMusic musicList.get(i)="+musicList.get(i));
					return musicList.get(i);
				}
			}
		}
		
		return -1;
	}

	// ��ȡ��һ�׸���
	public static int getPreviousMusic(ArrayList<Integer> musicList, int id) 
	{
		if(id==-1)
		{
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == 0) {
					return musicList.get(musicList.size() - 1);
				} else {
					--i;
					System.out.println("getPreviousMusic musicList.get(i)="+musicList.get(i));
					return musicList.get(i);
				}
			}
		}
		//System.out.println("getPreviousMusic");
		return -1;
	}
	
	public static int getRandomMusic(ArrayList<Integer> musicList, int id)
	{
		int musicid = -1;
		if(id==-1)
		{
			return -1;
		}
		if(musicList.isEmpty())
		{
			return -1;
		}
		if(musicList.size()==1)
		{
			return id;
		}
		do
		{
			int count = (int) (Math.random() * musicList.size());
			musicid = musicList.get(count);
		}while(musicid==id);
		System.out.println("getRandomMusic"+musicid);
		return musicid;
	}
	
	public static boolean getILikeStatus(Context context,int id)
	{
		boolean flag=false;
		SQLiteDatabase musicData = null;
 		musicData = DatabaseHelper.getInstance(context);

		Cursor cur=null;
		try {

			String sql = "select ilike from musicdata where id='" + id + "';";
			cur = musicData.query("musicdata", new String[]{"ilike"}, "id=" + id + "", null, null,  null, null);
			if (cur.moveToFirst()) {
				flag = (cur.getInt(0)==0)?false:true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return flag;
	}
	
	
	
}
