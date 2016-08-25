package com.example.musicplay.utils;

import java.util.ArrayList;
import java.util.List;

public class StrListChange 
{
	  //���ַ���ת����List
	public static List<String[]> StrToList(String info)
	{
		List<String[]> list = new ArrayList<String[]>();
		if(info.isEmpty())
		{
			return null;
		}
		String[] s = info.split("\\|");
		int num = 0;
		for(String ss:s)
		{
			num = 0;
			String[] temp = ss.split("<#>");
			String[] midd = new String[temp.length];
			for(String a:temp)
			{
				midd[num++] = a;
			}
			list.add(midd);
		}
	    return list;
	}
}