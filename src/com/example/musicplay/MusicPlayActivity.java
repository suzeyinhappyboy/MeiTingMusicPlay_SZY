package com.example.musicplay;

import java.util.ArrayList;

import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.adapter.MusiclistAdapter;
import com.example.musicplay.adapter.Song;
import com.example.musicplay.receiver.MusicUpdatePlay;
import com.example.musicplay.receiver.MusicUpdateVisualizer;
import com.example.musicplay.service.MusicPlayService;
import com.example.musicplay.utils.Constant;
import com.example.musicplay.view.VisualizerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayActivity extends Activity implements OnClickListener {

	SQLiteDatabase databse; 
	public VisualizerView mVisualizerView;
	MusicUpdateVisualizer muv;
	MusicUpdatePlay pur;
	ImageView iv_play;
	ImageView iv_next;
	ImageView iv_pre;	
	TextView tv_play_gequ;
	TextView tv_play_geshou;
	String path;
	int seek_progress;
	private PopupWindow popupWindow;
	public boolean like = false;
	
	MusiclistAdapter adapter;
	DatabaseUtil dbUtil;
	public static ArrayList<Song> musiclist;
	int music_position;
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music_play);
		
		//databse = DatabaseHelper.getInstance(this);
		
		mVisualizerView=(VisualizerView)findViewById(R.id.visualizerview);
		muv = new MusicUpdateVisualizer(this);
		pur = new MusicUpdatePlay(this);
		muv.visualzerFlag=true;
		
		ImageView iv_list = (ImageView) findViewById(R.id.player_imageView_list_w);
		ImageView iv_back = (ImageView) findViewById(R.id.imageView_back);
		final ImageView iv_like = (ImageView) findViewById(R.id.player_imageView_like_w);
		final SeekBar sb = (SeekBar) this.findViewById(R.id.player_seekBar_w);
		ImageView iv_shunxu = (ImageView) findViewById(R.id.player_iv_shunxu);
		ImageView iv_scanmusic = (ImageView) findViewById(R.id.iv_scanmusic);
		iv_play = (ImageView) findViewById(R.id.player_iv_play);
		iv_next = (ImageView) findViewById(R.id.player_iv_next);
		iv_pre = (ImageView) findViewById(R.id.player_iv_pre);
		tv_play_gequ = (TextView) findViewById(R.id.player_textView_gequ_w);
		tv_play_geshou = (TextView) findViewById(R.id.player_textView_geshou_w);
		final RelativeLayout rl_lyric_1 = (RelativeLayout) findViewById(R.id.player_relativelayout_change_1);
		final RelativeLayout rl_lyric_2 = (RelativeLayout) findViewById(R.id.player_relativelayout_change_2);
		ImageView iv_lyric_1 = (ImageView) findViewById(R.id.player_imageview_change_1);
		ImageView iv_lyric_2 = (ImageView) findViewById(R.id.player_imageview_change_2);
		tv_play_gequ.setText("Love You");
		tv_play_geshou.setText("Su");
		
		iv_play.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		iv_pre.setOnClickListener(this);
		
		pur = new MusicUpdatePlay(this);				
		
		Intent intent = new Intent(this, MusicPlayService.class);
		// 启动后台Service
		startService(intent);
		
		dbUtil = new DatabaseUtil();
        musiclist = new ArrayList<Song>();		
		//初始化播放顺序
		initPlayMode();	
		iv_scanmusic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater.from(MusicPlayActivity.this);
				RelativeLayout mpopupwindow = (RelativeLayout) 
						inflater.inflate(R.layout.fragment_localmusic_popup, null);

				mpopupwindow.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						popupWindow.dismiss();
					}
				});
				
				Animation popAnim=AnimationUtils.loadAnimation(MusicPlayActivity.this, R.anim.pop_menu);
				LinearLayout ll_main=(LinearLayout)mpopupwindow.getChildAt(0);
				ll_main.setAnimation(popAnim);
				
				LinearLayout ll_scan = (LinearLayout) 
						mpopupwindow.findViewById(R.id.localmusic_popup_linearlayout_scan);
				ll_scan.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						popupWindow.dismiss();
						Intent intent = new Intent(MusicPlayActivity.this,MusicActivityScan.class);
						startActivity(intent);
					}
				});
				popupWindow = new PopupWindow(mpopupwindow, LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT, true);
				popupWindow.setOutsideTouchable(true);
				popupWindow.setFocusable(true);
				popupWindow.setAnimationStyle(R.anim.none);
				popupWindow.setBackgroundDrawable(new ColorDrawable(0));
				popupWindow.showAsDropDown(v, 0, 0);				
			}
		});

		iv_lyric_1.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				rl_lyric_1.setVisibility(View.INVISIBLE);
				rl_lyric_2.setVisibility(View.VISIBLE);
				muv.visualzerFlag=false;
			}
		});
		iv_lyric_2.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				rl_lyric_1.setVisibility(View.VISIBLE);
				rl_lyric_2.setVisibility(View.INVISIBLE);
				muv.visualzerFlag=true;
			}
		});		
		// 返回
		iv_back.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				MusicPlayActivity.this.finish();
			}
		});
		iv_shunxu.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				ImageView iv_shunxu = (ImageView) findViewById(R.id.player_iv_shunxu);
				SharedPreferences sp = getSharedPreferences("music",
						Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor=sp.edit();
				int bofang=sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);
				switch (bofang) {
				case Constant.PLAYMODE_REPEATSINGLE:
					spEditor.putInt("playmode", Constant.PLAYMODE_SEQUENCE);
					spEditor.commit();
					iv_shunxu.setImageResource(R.drawable.player_shunxu_w);
					Toast.makeText(MusicPlayActivity.this, "顺序播放",
							Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_SEQUENCE:
					spEditor.putInt("playmode", Constant.PLAYMODE_REPEATALL);
					spEditor.commit();
					iv_shunxu.setImageResource(R.drawable.player_liebiao_w);
					Toast.makeText(MusicPlayActivity.this, "列表循环",
							Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_REPEATALL:
					spEditor.putInt("playmode", Constant.PLAYMODE_RANDOM);
					spEditor.commit();
					iv_shunxu.setImageResource(R.drawable.player_suiji_w);
					Toast.makeText(MusicPlayActivity.this, "随机播放",
							Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_RANDOM:
					spEditor.putInt("playmode", Constant.PLAYMODE_REPEATSINGLE);
					spEditor.commit();
					iv_shunxu.setImageResource(R.drawable.player_danqu_w);
					Toast.makeText(MusicPlayActivity.this, "单曲循环",
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		iv_list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showDialog();		
			}
		});

		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
		{
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) 
			{
				// 发送指令
				pur.seek_play_touch = true;
				int musicid=getShared(Constant.SHARED_ID);
				if (musicid == -1) 
				{
					sb.setProgress(0);
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MusicPlayActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
				intent.putExtra("current_progress", seek_progress);
				MusicPlayActivity.this.sendBroadcast(intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) 
			{
				pur.seek_play_touch = false;
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) 
			{
				// 一旦改变进度
				seek_progress = progress;
				TextView tv_timetiao = (TextView) findViewById(R.id.tv_timetiao);
				if (fromUser) 
				{
					String time = pur.fromMsToMinuteStr(progress);
					tv_timetiao.setText(time);
					tv_timetiao.setVisibility(View.VISIBLE);
				} 
				else 
				{
					tv_timetiao.setVisibility(View.INVISIBLE);
				}

			}
		});
		iv_like.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				int musicid=getShared(Constant.SHARED_ID);
				if(DatabaseUtil.setILikeStatus(MusicPlayActivity.this,musicid))
				{
					if (like) 
					{
						iv_like.setImageResource(R.drawable.player_like_w);
						Toast.makeText(MusicPlayActivity.this, "不喜欢",
								Toast.LENGTH_SHORT).show();
						like = false;
					} 
					else 
					{
						iv_like.setImageResource(R.drawable.player_liked_w);
						Toast.makeText(MusicPlayActivity.this, "喜欢",
								Toast.LENGTH_SHORT).show();
						like = true;
					}
				}
				else
				{
					Toast.makeText(MusicPlayActivity.this, "添加“我喜欢”失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		ImageView iv_shared=(ImageView)findViewById(R.id.player_imageView_share_w);
		iv_shared.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Toast.makeText(MusicPlayActivity.this, "分享", Toast.LENGTH_SHORT).show();
			}
		});
		//切换频谱
		mVisualizerView.setClickable(true);
		mVisualizerView.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(muv.visualzerMode)
				{
					muv.visualzerMode=false;
				}else
				{
					muv.visualzerMode=true;
				}
			}
		});
	}
	
	@Override
	public void onClick(View source)
	{
		int playMode;
		int musicid;
		ArrayList<String> musicinfo;
		String path;
		//setPlaylistShared("playlist",Constant.Localmusicid);
		// 创建Intent
		Intent intent = new Intent(Constant.MUSIC_CONTROL);
		TextView tv_play_gequ = (TextView) findViewById(R.id.player_textView_gequ_w);
		TextView tv_play_geshou = (TextView) findViewById(R.id.player_textView_geshou_w);
		System.out.println("getPlaylistShared(playlist)="+getPlaylistShared("playlist"));
		ArrayList<Integer> songlist = DatabaseUtil.getMusicList(MusicPlayActivity.this,getPlaylistShared("playlist"));
		switch (source.getId())
		{
			// 按下播放/暂停按钮
			case R.id.player_iv_play:
				System.out.println("MusicUpdatePlay.status=="+MusicUpdatePlay.status);
				musicid = getShared(Constant.SHARED_ID);
				musicinfo = DatabaseUtil.getMusicInfo(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid);
				if (musicid == -1) 
				{
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MusicPlayActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在",Toast.LENGTH_LONG).show();
					return;
				}
			
				if (MusicUpdatePlay.status == Constant.STATUS_PLAY) 
				{
					intent.putExtra("cmd", Constant.COMMAND_PAUSE);
					MusicPlayActivity.this.sendBroadcast(intent);
				} 
				else if (MusicUpdatePlay.status == Constant.STATUS_PAUSE)
				{
					intent.putExtra("cmd", Constant.COMMAND_PLAY);
					MusicPlayActivity.this.sendBroadcast(intent);
				} 
				else 
				{
					intent.putExtra("cmd", Constant.COMMAND_PLAY);
					intent.putExtra("path",DatabaseUtil.getMusicPath(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid));
					MusicPlayActivity.this.sendBroadcast(intent);
				}
				
				
				//tv_play_gequ.setText(musicinfo.get(1));
				//tv_play_geshou.setText(musicinfo.get(2));
				break;
			// 按下停止按钮
			case R.id.player_iv_next:				
				//music_position++;
				//获取下一首歌曲ID
				playMode = getShared("playmode");
				musicid=getShared(Constant.SHARED_ID);
				System.out.println("next->getShared"+musicid);
				System.out.println("playmode"+playMode);
				//System.out.println("musicList.size()"+songlist.size());
				if(playMode==Constant.PLAYMODE_RANDOM)
				{
					musicid = DatabaseUtil.getRandomMusic(songlist, musicid);
				}else
				{
					musicid = DatabaseUtil.getNextMusic(songlist, musicid);
				}
				//记录下一首歌曲ID
				setShared(Constant.SHARED_ID,musicid);
				System.out.println("player_iv_next.musicid="+musicid);
				if (musicid == -1) 
				{
					//Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MusicPlayActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "没有歌曲",Toast.LENGTH_SHORT).show();
					return;
				}
				//更新歌曲歌手信息
				musicinfo = DatabaseUtil.getMusicInfo(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid);
				//System.out.println("next->file"+musicinfo.get(0));
				path = DatabaseUtil.getMusicPath(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid);
				tv_play_gequ.setText(musicinfo.get(1));
				tv_play_geshou.setText(musicinfo.get(2));
				System.out.println("getNextMusicPath="+path);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", path);
				break;
			case R.id.player_iv_pre:
				//music_position--;
				//获取上一首歌曲ID
				playMode = getShared("playmode");
				musicid=getShared(Constant.SHARED_ID);
				System.out.println("pre->getShared"+musicid);
				//ArrayList<Integer> songlist = DatabaseUtil.getMusicList(MusicPlayActivity.this,getShared(Constant.SHARED_LIST));
				if(playMode==Constant.PLAYMODE_RANDOM)
				{
					musicid = DatabaseUtil.getRandomMusic(songlist, musicid);
				}
				else
				{
					musicid = DatabaseUtil.getPreviousMusic(songlist, musicid);
				}
				
				//记录上一首歌曲ID
				setShared(Constant.SHARED_ID,musicid);
				
				if (musicid == -1) 
				{
					//Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MusicPlayActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "没有歌曲",Toast.LENGTH_SHORT).show();
					return;
				}
				//更新歌曲歌手信息
				musicinfo = DatabaseUtil.getMusicInfo(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid);
				path = DatabaseUtil.getMusicPath(MusicPlayActivity.this,getPlaylistShared("playlist"),musicid);
				tv_play_gequ.setText(musicinfo.get(1));
				tv_play_geshou.setText(musicinfo.get(2));
				
				intent.putExtra("cmd", Constant.COMMAND_PLAY); 
				intent.putExtra("path",path);
		}
		// 发送广播，将被Service组件中的BroadcastReceiver接收到
		MusicPlayActivity.this.sendBroadcast(intent);
	}

	

	@Override
	public void onStart() 
	{
		super.onStart();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
	//	filter.addAction(Constant.UPDATE_SEEKBAR);
		this.registerReceiver(pur, filter);
		
		IntentFilter filter2 = new IntentFilter();
		filter2.addAction(Constant.UPDATE_VISUALIZER);
		this.registerReceiver(muv, filter2);
		
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();
		
		ArrayList<String> musicinfo = DatabaseUtil.getMusicInfo(MusicPlayActivity.this,getPlaylistShared("playlist"),getShared(Constant.SHARED_ID));
		TextView tv_play_gequ = (TextView) findViewById(R.id.player_textView_gequ_w);
		TextView tv_play_geshou = (TextView) findViewById(R.id.player_textView_geshou_w);
		tv_play_gequ.setText(musicinfo.get(1));
		tv_play_geshou.setText(musicinfo.get(2));
		if (MusicUpdatePlay.status == Constant.STATUS_PLAY) 
		{
			iv_play.setImageResource(R.drawable.player_pause_w);
		}
		else 
		{
			iv_play.setImageResource(R.drawable.player_play_w);
		} 
	}
	
	@Override
	public void onStop() 
	{
		super.onStop();
		// 注销接收播放、暂停、停止状态更新Intent的UIUpdateReceiver
		this.unregisterReceiver(pur);
		this.unregisterReceiver(muv);
	}
	
	public void showDialog() 
	{		
		ListView listview = new ListView(MusicPlayActivity.this);
		setPlaylistShared("playlist",Constant.Localmusicid); 
		if (getPlaylistShared("playlist").equals(Constant.Localmusicid)) {
			System.out.println("Localmusicid->");
			musiclist = dbUtil.getAllSongs(MusicPlayActivity.this,Constant.Localmusicid);
		}else if (getPlaylistShared("playlist").equals(Constant.Netmusicid) ) {
			System.out.println("Netmusicid->");
			musiclist = dbUtil.getAllSongs(MusicPlayActivity.this,Constant.Netmusicid);
		}
				
		adapter=new MusiclistAdapter(musiclist, MusicPlayActivity.this);
		final AlertDialog dialog = new AlertDialog.Builder(
				MusicPlayActivity.this).create();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = 150;
		params.height = 250;
		dialog.setTitle("播放列表(" + musiclist.size() + ")");
		dialog.setIcon(R.drawable.player_current_playlist_w);
		dialog.setView(listview);
		dialog.getWindow().setAttributes(params);
		dialog.show();
						
		listview.setAdapter(adapter);

		// 响应listview中的item的点击事件
		listview.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) 
			{
				dialog.dismiss();
				music_position = position+1;
				System.out.println("position="+music_position);
				setShared(Constant.SHARED_ID,music_position);
				System.out.println("musiclist.get(position)->"+musiclist.get(position).getTitle());
				
				System.out.println("su->getShared->"+getShared(Constant.SHARED_ID));
				
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", musiclist.get(position).getFileUrl());
				
				MusicPlayActivity.this.sendBroadcast(intent);
			}
		});
	}
	
	public void setShared(String key, int value)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
		System.out.println("setShared="+value);
	}
	public void setPlaylistShared(String key, String value)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putString(key, value);
		spEditor.commit();
		System.out.println("setPlaylistShared="+value);
	}
	public int getShared(String key)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
	//设置播放模式
	public void initPlayMode()
	{
		ImageView iv_shunxu = (ImageView) findViewById(R.id.player_iv_shunxu);
		SharedPreferences sp = getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int bofang=sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);
		switch (bofang) {
		case Constant.PLAYMODE_SEQUENCE:
			iv_shunxu.setImageResource(R.drawable.player_shunxu_w);
			break;
		case Constant.PLAYMODE_REPEATALL:;
			iv_shunxu.setImageResource(R.drawable.player_liebiao_w);
			break;
		case Constant.PLAYMODE_RANDOM:
			iv_shunxu.setImageResource(R.drawable.player_suiji_w);
			break;
		case Constant.PLAYMODE_REPEATSINGLE:
			iv_shunxu.setImageResource(R.drawable.player_danqu_w);
			break;
		}
	}


}
