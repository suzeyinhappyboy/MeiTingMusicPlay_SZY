package com.example.musicplay.utils;


import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

//
public class GetBitmap 
{
	static Bitmap bitmap;                   //���һ��ͼƬBitmap
	static byte[] bb;                       //ͼƬ����
	static String CACHE="/css";             //�浽SD�����ļ�����
	static String filePath;					//�ļ���·��
	static String picFilePath;				//ͼƬ�����ļ��м�ͼƬ����
	static File file;
	
	//�õ�����ͼƬ
	public static Bitmap getOneBitmap(String aid,String picStr)
	{
		//�ж�SD���Ƿ����
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			filePath=Environment.getExternalStorageDirectory().toString()+CACHE;
			file=new File(filePath);              //��ȡ�����ļ���ĿA¼������������򴴽�
			if(!file.exists())
			{
				file.mkdirs();
			}
			
			picStr+=".jpg";
			picFilePath=Environment.getExternalStorageDirectory().toString()+CACHE+"/"+picStr;   //��ȡSD���ļ�
			file=new File(picFilePath);
			if(file.exists())
			{
				bitmap=BitmapFactory.decodeFile(picFilePath);
				return bitmap;
			}
			bb=NetInfoUtil.getPicture(aid);             //�������ϻ�ȡ  �������ݲ���ӵ�SD����	
			bitmap=BitmapFactory.decodeByteArray(bb, 0,bb.length);
    	    FileOutputStream fos=null;
            file=new File(filePath,picStr);
            try
   	        {
   	        	fos = new FileOutputStream(file);      //����SD����
   	        	if(fos!=null)
   	        	{
	        		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);	    	        		
	        		fos.flush();
    	            fos.close();
    	        }
    	    }
    	    catch(Exception e)
            {
            	e.printStackTrace();
  	        }
    	    return bitmap;
	    }
	    return null;
	}
}
