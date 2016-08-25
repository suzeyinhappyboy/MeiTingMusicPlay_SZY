package com.example.musicplay.utils;
import java.io.*;

import com.example.musicplay.MainActivity;

import android.database.DatabaseUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;



public class DownloadMP3 
{
	static String filePath;
	static String CACHESONG="/mp3/song";
	static String CACHELYRIC="/mp3/lyric";
	static File file;
	MainActivity mainActivity;
	public static int download(String lyric, String fileName,Handler handler) throws Exception
	{
		int musicid=-1;
		try
		{
			NetInfoUtil.connect();
			NetInfoUtil.dos.writeUTF("<#DOWNLOAD_MP3#>"+fileName);
			filePath=Environment.getExternalStorageDirectory().toString()+CACHESONG;
	        //��ȡ�����ļ���Ŀ¼������������򴴽�
			file=new File(filePath);
			if(!file.exists())
			{
				file.mkdirs();
			}
			//���ܷ��������صĳ���
			long lengthTotal=NetInfoUtil.din.readLong();
			if(lengthTotal<100)
			{
				return musicid;
			}
			//���ܷ�����������ֱ�����
			byte[] buf=new byte[4096];
			//��ʱ����
			int tempLength=0;
			//��ǰ�ܳ���
			long currLength=0;
			File musicPath=new File(filePath+"/"+fileName);
			if(musicPath.exists())
			{
				musicPath.delete();
			}
			FileOutputStream fos=new FileOutputStream(musicPath);
			int i=0;
			while(currLength<lengthTotal)
			{
				tempLength=NetInfoUtil.din.read(buf);
				currLength=currLength+tempLength;
				fos.write(buf,0,tempLength);
				int download=(int) ((currLength*100)/lengthTotal);
				if(download==i)
				{
					i+=2;
					Bundle b=new Bundle();
					b.putInt("download", download);
					Message msg=new Message();
					msg.what=Constant.DOWNLOAD_UPDATE;
					msg.setData(b);
					handler.sendMessage(msg);
				}
			}
			fos.close();
			NetInfoUtil.disConnect();
			NetInfoUtil.connect();
			String lyricPath="";
			if(!lyric.equals("��"))
			{
				lyric=fileName.substring(0,fileName.length()-4)+".lrc";
				NetInfoUtil.dos.writeUTF("<#DOWNLOAD_LYRIC#>"+lyric);
				lyricPath=Environment.getExternalStorageDirectory().toString()+CACHELYRIC;
		        //��ȡ�����ļ���Ŀ¼������������򴴽�
				file=new File(lyricPath);
				if(!file.exists())
				{
					file.mkdirs();
				}
				
				//���ܷ��������صĳ���
				long lengthTotal2=NetInfoUtil.din.readLong();
				
				System.out.println(lengthTotal);
				//���ܷ�����������ֱ�����
				byte[] buf2=new byte[4096];
				//��ʱ����
				int tempLength2=0;
				//��ǰ�ܳ���
				long currLength2=0;
				File lyricFile=new File(lyricPath+"/"+lyric);
				
				if(lyricFile.exists())
				{
					lyricFile.delete();
				}
				FileOutputStream fos2=new FileOutputStream(lyricFile);
				while(currLength2<lengthTotal2)
				{
					tempLength2=NetInfoUtil.din.read(buf2);
					currLength2=currLength2+tempLength2;
					fos2.write(buf2,0,tempLength2);
				}
				fos2.close();
				
			}else{
				lyric="";
				lyricPath="";
			}
			String music[]=fileName.split("-");
			String musicName=music[1].substring(0, music[1].length()-4);
			String singerName=music[0].trim();
			String[] musicinfo={fileName, musicName, singerName, filePath+"/"+fileName,lyric,lyricPath+"/"+lyric};
			//musicid=DatabaseUtils.setMusic(mainActivity,musicinfo);
			//DBUtil.setMusicInDownload(musicid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			NetInfoUtil.disConnect();
		}
		return musicid;
	}
}
