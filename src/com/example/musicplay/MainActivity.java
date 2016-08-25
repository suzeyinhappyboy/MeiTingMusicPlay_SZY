package com.example.musicplay;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.adapter.TabContentPagerAdapter;
import com.example.musicplay.receiver.MusicUpdateMain;
import com.example.musicplay.service.MusicPlayService;
import com.example.musicplay.utils.Constant;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Toast;
public class MainActivity extends FragmentActivity {

	public static int mainFragmentId;
	private PopupWindow popupWindow;
	MusicUpdateMain mu;
	SeekBar sb;
	String main_gequ;
	String main_geshou;
	public boolean Seekbar_touch = true;
	int progress_seekbar;
	private ViewPager mViewPager;
	private DrawerLayout slideMenu;
	private ActionBar actionBar;
	private ImageView btn_play;
	private ImageView btn_next;
	private LinearLayout music_play;
	private TabContentPagerAdapter mPagerAdapter;
	public String url ="http://192.168.253.1/meiting/getmusic.php";
	public static String BaseURL = "http://192.168.253.1/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//DBUtil.createTable();
		mu = new MusicUpdateMain(this);
		getNetMusic();
		actionBar = getActionBar();
		mViewPager = (ViewPager) findViewById(R.id.view_pager);
		slideMenu = (DrawerLayout) findViewById(R.id.slide_menu);
		btn_play = (ImageView) findViewById(R.id.btnplay);
		btn_next = (ImageView) findViewById(R.id.btnnext);
		music_play = (LinearLayout) findViewById(R.id.music_control);
		sb = (SeekBar) findViewById(R.id.seekBar1);
		slideMenu.setScrimColor(Color.argb(50, 0, 0, 0));

		mPagerAdapter = new TabContentPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

			}

			@Override
			public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

			}
		};

		TypedArray tabIconIds = getResources().obtainTypedArray(R.array.actionbar_icons);
		for (int i = 0; i < 3; i++) {
			View view = getLayoutInflater().inflate(R.layout.actionbar_tab, null);
			ImageView tabIcon = (ImageView) view.findViewById(R.id.actionbar_tab_icon);
			tabIcon.setImageResource(tabIconIds.getResourceId(i, -1));

			actionBar.addTab(actionBar.newTab().setCustomView(view).setTabListener(tabListener));
		}

		 enableEmbeddedTabs(actionBar);
		 
		ActivityManager mActivityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(100);
			if(!MusicServiceIsStart(mServiceList, Constant.SERVICE_CLASS))
			{
				this.startService(new Intent(this, MusicPlayService.class));
				
				/*// ��� ����������
				SharedPreferences sp = this.getSharedPreferences("music",Context.MODE_PRIVATE);
				int musicid = sp.getInt(Constant.SHARED_ID, -1);
				int seek = sp.getInt("current", 0);
				
				//setPlaylistShared("playlist", Constant.Localmusicid);
				Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
				intent_start.putExtra("cmd", Constant.COMMAND_START);
				intent_start.putExtra("path", DatabaseUtil.getMusicPath(MainActivity.this,getPlaylistShared("playlist"),musicid));
				intent_start.putExtra("current", seek);
				this.sendBroadcast(intent_start);*/
			}
						
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
		{
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) 
				{
					Seekbar_touch = true;
					int musicid=getShared(Constant.SHARED_ID);
					if (musicid == -1) 
					{
						sb.setProgress(0);
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_STOP);
						MainActivity.this.sendBroadcast(intent);
						Toast.makeText(getApplicationContext(), "没有歌曲",Toast.LENGTH_LONG).show();
						return;
					}
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
					intent.putExtra("current_progress", progress_seekbar);
					MainActivity.this.sendBroadcast(intent);
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) 
				{
					progress_seekbar = progress;
					if (fromUser) 
					{
						Seekbar_touch = false;
					}
				}
		});
		 btn_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int playMode = getShared("playmode");
				int musicid=getShared(Constant.SHARED_ID);
				
				ArrayList<Integer> musiclist = DatabaseUtil.getMusicList(MainActivity.this,getPlaylistShared("playlist"));
				if(playMode==Constant.PLAYMODE_RANDOM)
				{
					musicid = DatabaseUtil.getRandomMusic(musiclist, musicid);
				}else
				{
					musicid = DatabaseUtil.getNextMusic(musiclist, musicid);
				}
				setShared(Constant.SHARED_ID,musicid);
				
				if (musicid == -1) 
				{
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "没有歌曲",Toast.LENGTH_LONG).show();
					return;
				}
				Toast.makeText(getApplicationContext(), "下一曲",Toast.LENGTH_SHORT).show();
				//��ȡ��������
				String path = DatabaseUtil.getMusicPath(MainActivity.this,getPlaylistShared("playlist"),musicid);
				
				//���Ͳ�������
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", path);
				System.out.println("btn_next.path="+path);
				MainActivity.this.sendBroadcast(intent);
			}
		});
		btn_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int musicid=getShared(Constant.SHARED_ID);
				if (musicid == -1) 
				{
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "没有歌曲",Toast.LENGTH_LONG).show();
					return;
				}

				Toast.makeText(getApplicationContext(), "播放歌曲",Toast.LENGTH_SHORT).show();
				if (MusicUpdateMain.status == Constant.STATUS_PLAY) 
				{
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_PAUSE);
					MainActivity.this.sendBroadcast(intent);
				} 
				else if (MusicUpdateMain.status == Constant.STATUS_PAUSE) 
				{
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_PLAY);
					MainActivity.this.sendBroadcast(intent);
				}
				else
				{
					String path = DatabaseUtil.getMusicPath(MainActivity.this,getPlaylistShared("playlist"),musicid);
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_PLAY);
					intent.putExtra("path", path);
					System.out.println("btn_play.path="+path);
					MainActivity.this.sendBroadcast(intent);
				}

			}
		});
		 music_play.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,MusicPlayActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.click_enter, R.anim.click_exit);
			}
		});
		 
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
		this.registerReceiver(mu, filter);
		 
	}

	/**
	 * 在actionbar中内嵌Tab
	 *
	 * @param actionBar actionbar
	 */
	private void enableEmbeddedTabs(android.app.ActionBar actionBar) {
		try {
			Method setHasEmbeddedTabsMethod = actionBar.getClass().getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
			setHasEmbeddedTabsMethod.setAccessible(true);
			setHasEmbeddedTabsMethod.invoke(actionBar, true);
		} catch (Exception e) {
			Log.v("enableEmbeddedTabs", e.getMessage().toString());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id) {
		case R.id.action_search:
			Toast.makeText(this, "搜索", 0).show();
			break;
		case R.id.action_menu:
			if (slideMenu.isDrawerOpen(Gravity.RIGHT)) {
				slideMenu.closeDrawer(Gravity.RIGHT);
			} else {
				slideMenu.openDrawer(Gravity.RIGHT);
			}
		default:
			break;
		}		
		
		return true;
	}
	
	public static boolean MusicServiceIsStart(List<RunningServiceInfo> mServiceList,
			String serviceClass) //�����������Ȿ��������Ƿ�����
	{
		for (int i = 0; i < mServiceList.size(); i++)
		{
			if (serviceClass.equals(mServiceList.get(i).service.getClassName()))
			{
				return true;
			}
		}	
		return false;
	}
	
	public void onStart() 
	{
		super.onStart();
		
		System.out.println("onstart");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
		this.registerReceiver(mu, filter);

		//����֪ͨ������
		//Notification();
	}

/*	@SuppressWarnings("deprecation")
	public void Notification() {
		try {
			// ��ȡ֪ͨ������
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			// ����Intent
			Intent intent = new Intent(this, MainActivity.class);
			// ��Intent��װΪPendingIntent
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

			// ����Notification
			Notification myNotification = new Notification();
			// ����Notification��ͼ��
			myNotification.icon = R.drawable.ic_launcher;
			
			ArrayList<String> notification = DatabaseUtil.getMusicInfo(MainActivity.this,getShared(Constant.SHARED_ID));
			// ����Notification����Ϣ����������PendingIntent
			if (notification.get(1).equals("�й�������")) {
				
				myNotification.setLatestEventInfo(this, "û�в��ŵĸ���", "�͹�,����һ�װɣ�",
						pi);
			} else {

				myNotification.setLatestEventInfo(this, notification.get(0),
						"���ڲ�����...", pi);
			}

			// ����Notification
			nm.notify(0, myNotification);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}*/

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("onDestroy()");
		DatabaseUtil.deleteNetmusic(MainActivity.this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		System.out.println("onRestart");
		getNetMusic();
	}
	@Override
	public void onStop() {
		super.onStop();
		this.unregisterReceiver(mu);
		DatabaseUtil.deleteNetmusic(MainActivity.this);
		System.out.println("onStop()");
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		ImageView iv_play = (ImageView) findViewById(R.id.btnplay);
		if (MusicUpdateMain.status == Constant.STATUS_PLAY) 
		{
			iv_play.setImageResource(R.drawable.player_pause_w);
		}else
		{
			iv_play.setImageResource(R.drawable.playbar_btn_play);
		}
		
//		MusicApplication mApp=(MusicApplication)getApplication();
//		if(mApp.isExit())
//		{
//			// ��¼λ��;
//			SharedPreferences sp = this.getSharedPreferences("music",
//					Context.MODE_MULTI_PROCESS);
//			SharedPreferences.Editor spEditor = sp.edit();
//			spEditor.putInt("current", sb.getProgress());
//			spEditor.commit();
//			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//			notificationManager.cancel(0);
//			finish();
//			System.exit(0);
//		}
	}
	
	public void setShared(String key, int value)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}
	
	//��ȡsharedpreferences
	public int getShared(String key)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
	
	public void setPlaylistShared(String key, String value)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putString(key, value);
		spEditor.commit();
	}
	
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = this.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
	public void getNetMusic() {
		StringRequest request = new StringRequest(url, 
				new Listener<String>() {

				@Override
				public void onResponse(String arg0) {
					JSONArray jsonArray;					
					//Log.i("info", arg0);
						try {
							jsonArray = new JSONArray(arg0);
							for (int i = 0; i < jsonArray.length(); i++) {
	
								JSONObject object = jsonArray.getJSONObject(i);
								String title = object.getString("SongName");
								String singer= object.getString("SingerName");
								String path = object.getString("Path");
								String temp[]={null,title,singer,path,null,null};
								DatabaseUtil.setMusic(MainActivity.this,temp,Constant.Netmusicid);
								
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}				
																		
				}

			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {
					System.out.println("Response.ErrorListener=");

				}
			});
			Volley.newRequestQueue(MainActivity.this).add(request);
			System.out.println("creatmusic!!!");
	}

}
