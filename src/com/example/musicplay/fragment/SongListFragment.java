package com.example.musicplay.fragment;


import java.io.File;
import java.util.ArrayList;

import com.example.musicplay.DownloadActivity;
import com.example.musicplay.MainActivity;
import com.example.musicplay.MusicPlayActivity;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.adapter.MusiclistAdapter;
import com.example.musicplay.adapter.OnMoreClickListener;
import com.example.musicplay.adapter.Song;
import com.example.musicplay.utils.Constant;
import com.example.musicplay.utils.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by alex.lee on 2015-07-25.
 */
public class SongListFragment extends Fragment implements OnMoreClickListener{

	private String url ="http://192.168.253.1/meiting/getmusic.php";
	private String BaseURL = "http://192.168.253.1/";
	MusiclistAdapter mAdapter;
	DatabaseUtil dbUtil;
	public static ArrayList<Song> musiclist;
	ListView listView;
	private ProgressDialog mProgressDialog;

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
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				final String playpath = BaseURL + musiclist.get(position).getFileUrl();
				String title = musiclist.get(position).getTitle();
		        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		        dialog.setTitle(title);
		        File file = new File(playpath);
		        int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
		        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                switch (which) {
		                    case 0:// 分享
		                    	System.out.println("playpath="+playpath);
		                    	//ShareOnlineMusic(getActivity(), title, playpath);
		                        break;
		                    case 1:// 
		                    	//DownloadOnlineMusic(playpath);
		                        break;
		                }
		            }
		        });
		        dialog.show();
				return false;
			}
		});
		mAdapter.setOnMoreClickListener(this);
		return view;
	}
	
	public void onMoreClick(int position) {
		final String playpath = BaseURL + musiclist.get(position).getFileUrl();
		final String title = musiclist.get(position).getTitle();
        File file = new File(playpath);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(title);
        
        int itemsId = file.exists() ? R.array.online_music_dialog_without_download : R.array.online_music_dialog;
        dialog.setItems(itemsId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 分享
                    	System.out.println("playpath="+playpath);
                    	Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.share_music, getActivity().getString(R.string.app_name),
                        		title,playpath));
                        getActivity().startActivity(Intent.createChooser(intent, getActivity().getString(R.string.share)));;
                        break;
                    case 1:// 
                    	System.out.println("title="+title);
                    	System.out.println("playpath="+playpath);
                    	Intent downloadintent = new Intent(getActivity(),DownloadActivity.class);
                    	downloadintent.putExtra("title", title);
                    	downloadintent.putExtra("path",playpath);
                    	getActivity().startActivity(downloadintent);
                        break;
                }
            }
        });
        dialog.show();
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
	
}
