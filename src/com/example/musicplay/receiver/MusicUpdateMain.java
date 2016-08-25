package com.example.musicplay.receiver;

import java.util.ArrayList;

import com.example.musicplay.MainActivity;
import com.example.musicplay.MusicPlayActivity;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.utils.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicUpdateMain extends BroadcastReceiver 
{
	public static int status = Constant.STATUS_STOP;
	MainActivity ma;
	ArrayList<String> musicinfo;
	int updateTime = 0;
	int duration = 0;
	int current = 0;
	Context context;
	public MusicUpdateMain(MainActivity ma) 
	{
		this.ma = ma;
	}

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context=context;
		int statustemp = intent.getIntExtra("status", -1);
		ImageView iv_play = (ImageView) ma.findViewById(R.id.btnplay);

		try 
		{
			SharedPreferences sp = ma.getSharedPreferences
					("music",Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			TextView tv_gequ = (TextView) ma.findViewById(R.id.main_textview_gequ);
			TextView tv_geshou = (TextView) ma.findViewById(R.id.main_textview_geshou);
			tv_gequ.setText(DatabaseUtil.getMusicInfo(context,getPlaylistShared("playlist"),musicid).get(1));
			tv_geshou.setText(DatabaseUtil.getMusicInfo(context,getPlaylistShared("playlist"),musicid).get(2));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		switch (statustemp)
		{
		case Constant.STATUS_PLAY:
			status = statustemp;
			MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.player_pause_w);
			break;
		case Constant.STATUS_STOP:
			try 
			{
				TextView tv_gequ = (TextView) ma.findViewById(R.id.main_textview_gequ);
				TextView tv_geshou = (TextView) ma.findViewById(R.id.main_textview_geshou);
				tv_gequ.setText("美听好声音");
				tv_geshou.setText("苏泽荫");
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		case Constant.STATUS_PAUSE:
			status = statustemp;
			MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.player_play_w);
			break;
		case Constant.COMMAND_SEEKBAR:
			if(status!=intent.getIntExtra("status2", Constant.STATUS_STOP))
			{
				status = intent.getIntExtra("status2", Constant.STATUS_STOP);
				MusicUpdatePlay.status = status;
				if(status==Constant.STATUS_PLAY)
				{
					iv_play.setImageResource(R.drawable.player_pause_w);
				}
			}
			if (ma.Seekbar_touch) 
			{
				SeekBar sb = (SeekBar) ma.findViewById(R.id.seekBar1);
				duration = intent.getIntExtra("duration", 0);
				current = intent.getIntExtra("current_time", 0);
				sb.setMax(duration);
				sb.setProgress(current);
				updateTime++;
				if(updateTime>10)
				{
					updateTime=0;
					
				}
			}
			
		}
	}
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = ma.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
	
	public String fromMsToMinuteStr(int ms) 
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}
}