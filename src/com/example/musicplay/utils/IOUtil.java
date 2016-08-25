package com.example.musicplay.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class IOUtil 
{
	//��ȡͼƬ����
	public static byte[] readBytes(DataInputStream din)
	{
		byte[] data=null;
		ByteArrayOutputStream out=new ByteArrayOutputStream(1024);
		
		try
		{
			int length=0,temRev=0,size;
			length=din.readInt();
			byte[] buf=new byte[length-temRev];
			while((size=din.read(buf))!=-1)
			{
				temRev+=size;
				out.write(buf,0,size);
				if(temRev>=length)
				{
					break;
				}
				buf=new byte[length-temRev];
			}
			data=out.toByteArray();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try{out.close();} catch(IOException e){e.printStackTrace();}
		}
		return data;
	}
}
