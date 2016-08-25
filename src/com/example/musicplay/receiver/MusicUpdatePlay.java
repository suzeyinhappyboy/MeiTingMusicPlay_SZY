package com.example.musicplay.receiver;

import java.util.ArrayList;

import com.example.musicplay.MusicPlayActivity;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.utils.Constant;
import com.example.musicplay.view.LyricView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MusicUpdatePlay extends BroadcastReceiver 
{
	ImageView iv_play;
	public static int status = Constant.STATUS_STOP;
	public boolean seek_play_touch=true;
	MusicPlayActivity pa;
	ArrayList<String> musicinfo;
	ArrayList<String[]> lyric;
	int oldmusicid;
	int duration = 0;
	int current = 0;
	
	public MusicUpdatePlay(MusicPlayActivity pa) 
	{
		this.pa = pa;
		oldmusicid = -1;
	}

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//SeekBar sb = (SeekBar) pa.findViewById(R.id.player_seekBar_w);
		iv_play = (ImageView) pa.findViewById(R.id.player_iv_play);
		TextView lyricabove = (TextView) pa.findViewById(R.id.player_textview_above);
		TextView lyricbelow = (TextView) pa.findViewById(R.id.player_textview_below);
		LyricView lyricView = (LyricView) pa.findViewById(R.id.lyricview);
		
		//得到播放歌曲的id		
		int musicid=getShared(Constant.SHARED_ID);
		//System.out.println("MusicUpdatePlay.musicid="+musicid);
		//获得当前播放歌曲的信息 ;
		ArrayList<String> musicinfo = DatabaseUtil.getMusicInfo(pa,getPlaylistShared("playlist"),musicid);
		String music = musicinfo.get(1);
		String singer = musicinfo.get(2);

		//尝试更新歌词、歌曲以的信息
		try 
		{
			TextView tv_gequ = (TextView) pa.findViewById(R.id.player_textView_gequ_w);
			TextView tv_geshou = (TextView) pa.findViewById(R.id.player_textView_geshou_w);
			tv_gequ.setText(music);
			tv_geshou.setText(singer);
			//是否存在于我喜欢列表
			ImageView iv_like = (ImageView) pa.findViewById(R.id.player_imageView_like_w);
			if(DatabaseUtil.getILikeStatus(pa,musicid))
			{
				iv_like.setImageResource(R.drawable.player_liked_w);
				pa.like=true;
			}
			else
			{
				iv_like.setImageResource(R.drawable.player_like_w);
				pa.like=false;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		int tempStatus = intent.getIntExtra("status", -1);
		
		switch (tempStatus) 
		{
			case Constant.STATUS_STOP:
				status = Constant.STATUS_STOP;
				break;
			case Constant.STATUS_PLAY:
				//切换歌时。进度在歌词第一句之前则显示歌名歌手信息
				lyricabove.setText(music);
				lyricbelow.setText(singer);
				status = Constant.STATUS_PLAY;
				break;

			case Constant.STATUS_PAUSE:

				status = Constant.STATUS_PAUSE;
				break;
			case Constant.COMMAND_SEEKBAR:
				//System.out.println("Constant.COMMAND_SEEKBAR");				
				if (seek_play_touch) 
				{
					//设置播放进度
					SeekBar sb = (SeekBar) pa.findViewById(R.id.player_seekBar_w);
					duration = intent.getIntExtra("duration", 0);
					current = intent.getIntExtra("current_time", 0);
					sb.setMax(duration);
					sb.setProgress(current);
					
					//计算本首歌长度和播放进度
					TextView tv_current = (TextView) pa.findViewById(R.id.play_textView_time_current);
					TextView tv_duration = (TextView) pa.findViewById(R.id.play_textView_time_duration);
					tv_current.setText(fromMsToMinuteStr(current));
					tv_duration.setText(fromMsToMinuteStr(duration));
	
					// 如果播放id和记录的id不符时初始化歌词。目的减少计算量
					if (musicid != oldmusicid) 
					{
						oldmusicid = musicid;
						lyric = null;
						lyric = DatabaseUtil.getLyric(DatabaseUtil.getLyricPath(pa,musicid,getPlaylistShared("playlist")));
						lyricabove.setText(music);
						lyricbelow.setText(singer);
						lyricView.setLyric(pa,musicid);
					}
					
					lyricView.setMusictime(current, duration);
						
					// 获取歌词控件。设置默认
					if (lyric == null) {
						lyricabove.setText("当前歌曲没有歌词");
						lyricbelow.setText("");
					} else {
						if(lyric.size()==1)
						{
							lyricabove.setText(music);
							lyricbelow.setText(singer);
						}
						for (int i = 0; i < lyric.size(); i++)
						{
							//计算当前时间歌词显示的内容以及下一句显示的内容
							String lyric1[] = lyric.get(i);
							//System.out.println("lyric1[0]"+lyric1[0]);
							//System.out.println("lyric1[1]"+lyric1[1]);
							int time1 = (Integer.parseInt(lyric1[0]) * 60 + Integer.parseInt(lyric1[1])) * 1000;
							//System.out.println("time1"+time1);
							String lyric2[] = { "", "", "" };//当歌词播放到最后一句是。下一句为空
							int time2;
							if (i != lyric.size() - 1) 
							{
								lyric2 = lyric.get(i + 1);
								time2 = (Integer.parseInt(lyric2[0]) * 60 + Integer.parseInt(lyric2[1])) * 1000;
							} 
							else 
							{
								time2 = duration;
							}
							//System.out.println("lyric2[0]"+lyric2[0]);
							//System.out.println("lyric2[1]"+lyric2[1]);
							//System.out.println("time2"+time2);
							int time = current;
							if (time > time1 && time < time2) //当本首歌的进度位于当前句和下一句之间时的显示
							{
								// 判断
								if (i % 2 == 0)//控制歌词双行显示的上行只显示奇数句、下行只显示偶数句。
								{
									lyricabove.setText(lyric1[2]);
									lyricabove.setTextColor(Color.GREEN);
									lyricbelow.setText(lyric2[2]);
									lyricbelow.setTextColor(Color.WHITE);
								} 
								else 
								{
									lyricbelow.setText(lyric1[2]);
									lyricbelow.setTextColor(Color.GREEN);
									lyricabove.setText(lyric2[2]);
									lyricabove.setTextColor(Color.WHITE);
								}
								break;
							}
						}
					}					
				}
				break;
			}
		
		if (status == Constant.STATUS_PLAY) //改变musicactivityplay界面开始\暂停按钮。设置歌词控件的显示。单双行切换
		{
			iv_play.setImageResource(R.drawable.player_pause_w);
			pa.findViewById(R.id.player_textview_default).setVisibility(View.INVISIBLE);
			pa.findViewById(R.id.player_textview_above).setVisibility(View.VISIBLE);
			pa.findViewById(R.id.player_textview_below).setVisibility(View.VISIBLE);
		}
		else 
		{
			iv_play.setImageResource(R.drawable.player_play_w);
			pa.findViewById(R.id.player_textview_default).setVisibility(View.VISIBLE);
			pa.findViewById(R.id.player_textview_above).setVisibility(View.INVISIBLE);
			pa.findViewById(R.id.player_textview_below).setVisibility(View.INVISIBLE);
		}
		
	}
	public int getShared(String key)
	{
		SharedPreferences sp = pa.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = pa.getSharedPreferences("music",
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