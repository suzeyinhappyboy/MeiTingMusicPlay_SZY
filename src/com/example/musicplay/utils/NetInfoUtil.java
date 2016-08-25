package com.example.musicplay.utils;

import java.io.*;
import java.net.*;
import java.util.*;

public class NetInfoUtil 
{
	public static Socket ss = null;
	public static DataInputStream din = null;
	public static DataOutputStream dos = null;
	public static String message="";
	public static byte[] data;
	
	//ͨ�Ž���
	public static void connect() throws Exception
	{
		ss = new Socket();//����һ��ServerSocket����
		SocketAddress socketAddress = new InetSocketAddress(MusicApplication.socketIp, 8888); //�󶨵�ָ��IP�Ͷ˿�
		ss.connect(socketAddress, 5000);//�������ӳ�ʱʱ��
		din = new DataInputStream(ss.getInputStream());//����������������
		dos = new DataOutputStream(ss.getOutputStream());//���������������
	}
	
	//��������
	public static List<String[]> searchSong(String info) 
	{
		try 
		{
			connect();
			dos.writeUTF("<#SEARCH_SONG#>" + info);
			message = din.readUTF();
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally 
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//ͨ�Źر�
	public static void disConnect()
	{
		if(dos!=null)
		{
			try{dos.flush();}catch(Exception e){e.printStackTrace();}
		}
		if(din!=null)
		{
			try{din.close();}catch(Exception e){e.printStackTrace();}
		}
		if(ss!=null)
		{
			try{ss.close();}catch(Exception e){e.printStackTrace();}
		}
	}
	
	//��ȡͼƬ��ͼƬ����
	public static byte[] getPicture(String aid)
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_PICTURE#>"+aid);
			data=IOUtil.readBytes(din);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return data;
	}
	
	//��ø���Ŀ¼
	public static List<String[]> getSongList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SONGLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//��ø���Ŀ¼
	public static List<String[]> getSingerList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SINGERLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//���ר��Ŀ¼
	public static List<String[]> getAlbumsList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_ALBUMLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//���ǰ��������
	public static List<String[]> getSingerListTop()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SINGERLISTTOP#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//���ǰ����ר��
	public static List<String[]> getAlbumsListTop()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_ALBUMLISTTOP#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
}