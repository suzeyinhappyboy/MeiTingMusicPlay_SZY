package com.example.musicplay.fragment;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.musicplay.MusicActivityScan;
import com.example.musicplay.MusicPlayActivity;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.adapter.Music;
import com.example.musicplay.adapter.MusiclistAdapter;
import com.example.musicplay.adapter.Song;
import com.example.musicplay.utils.Constant;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by alex.lee on 2015-07-25.
 */
public class SongListFragment extends Fragment {

	private String url ="http://192.168.253.1/meiting/getmusic.php";
	private String BaseURL = "http://192.168.253.1/";
	MusiclistAdapter mAdapter;
	DatabaseUtil dbUtil;
	public static ArrayList<Song> musiclist;
	ListView listView;

	ArrayList<String> Music_gequ = new ArrayList<String>();
	ArrayList<String> Music_geshou = new ArrayList<String>();
	ArrayList<String> Music_path = new ArrayList<String>();
	int music_position;
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		dbUtil = new DatabaseUtil();
        musiclist = new ArrayList<Song>();
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_song_list, container, false);
		listView = (ListView) view.findViewById(R.id.network_musiclist);
		
		musiclist =dbUtil.getAllSongs(getActivity(),Constant.Netmusicid);
		System.out.println("musiclist.size()"+musiclist.size());
		mAdapter=new MusiclistAdapter(musiclist, getActivity());
		listView.setAdapter(mAdapter);		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				TextView tv_gequ = (TextView) getActivity().findViewById(R.id.main_textview_gequ);
				TextView tv_geshou = (TextView) getActivity().findViewById(R.id.main_textview_geshou);
				tv_gequ.setText(musiclist.get(position).getTitle());
				tv_geshou.setText(musiclist.get(position).getSinger());
				
				music_position= position+1;
				String playpath = BaseURL + musiclist.get(position).getFileUrl();
				setPlaylistShared("playlist",Constant.Netmusicid);
				setShared(Constant.SHARED_ID,music_position);
				Intent intentplay = new Intent(Constant.MUSIC_CONTROL);
				intentplay.putExtra("cmd", Constant.COMMAND_PLAY);
				intentplay.putExtra("path",playpath);
				getActivity().sendBroadcast(intentplay);
				
			}
		});
		return view;
	}
	
	public void setPlaylistShared(String key, String value)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putString(key, value);
		spEditor.commit();
	}
	public void setShared(String key, int value)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}
	public int getShared(String key)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
	
	/*
	private void sendintent(int code) 
	{
		switch (code) {
		case Constant.STATUS_PLAY:
			Intent intentplay = new Intent(Constant.MUSIC_CONTROL);
			intentplay.putExtra("cmd", Constant.COMMAND_PAUSE);
			this.getActivity().sendBroadcast(intentplay);
			break;
		case Constant.STATUS_STOP:
			SharedPreferences sp = getActivity().getSharedPreferences
					("music",Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			String playpath = DBUtil.getMusicPath(musicid);
			Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
			intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
			intentstop.putExtra("path", playpath);
			this.getActivity().sendBroadcast(intentstop);
			break;
		case Constant.STATUS_PAUSE:
			Intent intentpause = new Intent(Constant.MUSIC_CONTROL);
			intentpause.putExtra("cmd", Constant.COMMAND_PLAY);
			this.getActivity().sendBroadcast(intentpause);
			break;
		}
	}*/

	/*private void setListView() 
	{
		ba = new BaseAdapter()
		{
			LayoutInflater inflater = LayoutInflater.from(getActivity());

			@Override
			public int getCount() {
				return musiclist.size() + 1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int position, View arg1, ViewGroup arg2) 
			{
				if (position == musiclist.size())
				{
					LinearLayout lll = (LinearLayout) inflater.inflate(
							R.layout.listview_count, null).findViewById(
							R.id.linearlayout_null);
					TextView tv_sum = (TextView) lll.getChildAt(0);
					tv_sum.setText("总共有" + musiclist.size() + "首歌曲\n\n\n");
					return lll;
				}

				SharedPreferences sp = getActivity().getSharedPreferences
						("music",Context.MODE_MULTI_PROCESS);
				int musicid = sp.getInt(Constant.SHARED_ID, -1);

				String musicName = DBUtil.getMusicInfo(musiclist.get(position)).get(2);
				musicName+="-"+DBUtil.getMusicInfo(musiclist.get(position)).get(1);

				LinearLayout ll = (LinearLayout) inflater.inflate
						(R.layout.fragment_localmusic_listview_row,
						null).findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				tv.setText(musicName);

				if (musiclist.get(position)==musicid)
				{
					tv.setTextColor(getResources().getColor(R.color.blue));
				}
				return ll;
			}
		};

		listView = (ListView) getActivity().findViewById(R.id.network_musiclist);

		listView.setAdapter(ba);

		listView.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				// �������������һ��
				if (arg2 == arg0.getCount() - 1) 
				{
					return;
				}
				
				SharedPreferences sp = getActivity().getSharedPreferences
						("music",Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor=sp.edit();
				spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
				spEditor.commit();
				
				// ��ȡ���������ID;
				int musicid = musiclist.get(arg2);

				// ������ѡ����
				boolean flag;
				int oldmusicplay = sp.getInt(Constant.SHARED_LIST, -1);

				// �ж��Ƿ����������ڲ��ŵĸ���
				if (oldmusicplay == musicid) 
				{
					flag = false;
				} 
				else 
				{
					flag = true;
					spEditor.putInt(Constant.SHARED_ID, musicid);
					spEditor.commit();
				}

				ArrayList<String> musicinfo = DBUtil.getMusicInfo(musicid);
				TextView tv_gequ = (TextView) getActivity().findViewById(R.id.main_textview_gequ);
				TextView tv_geshou = (TextView) getActivity().findViewById(R.id.main_textview_geshou);
				tv_gequ.setText(musicinfo.get(1));
				tv_geshou.setText(musicinfo.get(2));

				if (flag) 
				{
					sendintent(Constant.STATUS_STOP);
				} 
				else 
				{
					if (MusicUpdateMain.status == Constant.STATUS_PLAY)
					{
						sendintent(Constant.STATUS_PLAY);
					} 
					else if (MusicUpdateMain.status == Constant.STATUS_STOP) 
					{
						sendintent(Constant.STATUS_STOP);
					}
					else 
					{
						sendintent(Constant.STATUS_PAUSE);
					}
				}
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				selectTemp = arg2;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("���๦��");
				builder.setItems(new String[] {"ɾ������","��ӵ��赥"}, new DialogInterface.OnClickListener()
				{
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						switch(which)
						{
						case 0:
							int musicTemp=musiclist.get(selectTemp);
							DBUtil.deleteMusic(musicTemp);
							SharedPreferences sp = getActivity().getSharedPreferences
									("music",Context.MODE_MULTI_PROCESS);
							int musicid = sp.getInt(Constant.SHARED_ID, -1);
							musiclist = DBUtil.getMusicList(Constant.LIST_ALLMUSIC);
							if(musicid==musicTemp)
							{
								if(musiclist.isEmpty())
								{
									musicid=-1;
								}
								else
								{
									musicid=musiclist.get(0);
								}
								SharedPreferences.Editor spEditor=sp.edit();
								spEditor.putInt(Constant.SHARED_ID, musicid);
								spEditor.commit();
								Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
								intent_start.putExtra("cmd", Constant.COMMAND_PLAY);
								intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
								getActivity().sendBroadcast(intent_start);
								Intent intent_pause = new Intent(Constant.MUSIC_CONTROL);
								intent_pause.putExtra("cmd", Constant.COMMAND_PAUSE);
								getActivity().sendBroadcast(intent_pause);
							}
							ba.notifyDataSetChanged();
							break;
						case 1:
							playlist = DBUtil.getPlayList();
							LinearLayout ll_list = new LinearLayout(getActivity());
							ll_list.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							ll_list.setLayoutDirection(0);
							ListView listview = new ListView(getActivity());
							listview.setLayoutParams(new LinearLayout.LayoutParams(
									LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							ll_list.addView(listview);
							final AlertDialog aDialog = new AlertDialog.Builder(getActivity()).create();
							aDialog.setTitle("�赥�б�(" + playlist.size() + ")");
							aDialog.setView(ll_list);
							aDialog.show();
							
							//����listview������
							BaseAdapter ba = new BaseAdapter() 
							{
								LayoutInflater inflater = LayoutInflater.from(getActivity());
								
								@Override
								public int getCount() 
								{
									return playlist.size();
								}

								@Override
								public Object getItem(int arg0) 
								{
									return null;
								}

								@Override
								public long getItemId(int arg0) 
								{
									return 0;
								}

								@Override
								public View getView(int arg0, View arg1, ViewGroup arg2) 
								{
									String musicName = playlist.get(arg0)[1];
									LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.list_w,null);
									TextView tvtemp=(TextView) ll.getChildAt(0);
									tvtemp.setText((arg0+1)+"");
									TextView tv = (TextView) ll.getChildAt(1);
									tv.setText(musicName);
									return ll;
								}
							};
							
							listview.setAdapter(ba);
							
							// ��Ӧlistview�е�item�ĵ���¼�
							listview.setOnItemClickListener(new OnItemClickListener() 
							{
								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
										long arg3) 
								{
									aDialog.cancel();
									int listid=Integer.parseInt(playlist.get(arg2)[0]);
									DBUtil.setMusicInPlaylist(musiclist.get(selectTemp), listid);
								}
							});
						}
						dialog.dismiss();
					}
				}).create().show();
				return false;
			}
		});
		listView.setOnCreateContextMenuListener(this);
	}*/
}
