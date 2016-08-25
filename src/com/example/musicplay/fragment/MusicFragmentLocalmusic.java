package com.example.musicplay.fragment;

import java.util.ArrayList;

import com.example.musicplay.MusicActivityScan;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.receiver.MusicUpdateMain;
import com.example.musicplay.utils.Constant;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MusicFragmentLocalmusic extends Fragment
{
	public static BaseAdapter ba;
	private PopupWindow popupWindow;
	MusicUpdateMain mu;
	ArrayList<Integer> musiclist;
	ArrayList<String[]> playlist;
	ListView lv;
	
	int selectTemp;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		//��ȡ�����ؼ�����
		View v = inflater.inflate(R.layout.fragment_localmusic_listview, container, false);
		LinearLayout ll_title_bar = (LinearLayout) v.findViewById(R.id.localmusic_linearlayout_titlebar);
		LinearLayout ll_edit_bar = (LinearLayout) v.findViewById(R.id.localmusic_linearlayout_editbar);
		LinearLayout ll_title = (LinearLayout) inflater.inflate(R.layout.fragment_localmusic_title, null);
		LinearLayout ll_edit1 = (LinearLayout) inflater.inflate(R.layout.fragment_localmusic_edit_l1, null);
		//���˰�ť
		ImageButton ib_back = (ImageButton) ll_title.findViewById(R.id.title_imagebutton_back_l);
		ib_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});
		
		//�������
		ll_edit1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				SharedPreferences sp = getActivity().getSharedPreferences("music",
						Context.MODE_MULTI_PROCESS);
				int musicid = sp.getInt(Constant.SHARED_ID, -1);
				if (musicid == -1) 
				{
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					getActivity().sendBroadcast(intent);
					Toast.makeText(getActivity(), "����������",Toast.LENGTH_LONG).show();
					return;
				}
				SharedPreferences.Editor spEditor = sp.edit();
				musicid = DatabaseUtil.getRandomMusic(musiclist, musicid);
				spEditor.putInt(Constant.SHARED_ID, musicid);
				spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
				spEditor.commit();
				sendintent(Constant.STATUS_STOP);
				for(int i=0;i<musiclist.size();i++)
				{
					if (musiclist.get(i)==musicid)
					{
						lv.setSelection(i);
					}
				}
			}
		});
		
		//�����������
		ImageView iv_search = (ImageView) ll_title.findViewById(R.id.title_imagebutton_search_l);
		iv_search.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				//��������
				FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
				MusicFragmentSearch fragment = new MusicFragmentSearch();
			//	MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
			}
		});

		//�����˵�
		ImageView iv_menu = (ImageView) ll_title.findViewById(R.id.title_imagebutton_menu_l);
		iv_menu.setOnClickListener(new View.OnClickListener() 
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) 
			{
				LayoutInflater inflater = LayoutInflater.from(getActivity());
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
				
				Animation popAnim=AnimationUtils.loadAnimation(getActivity(), R.anim.pop_menu);
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
						Intent intent = new Intent(getActivity(),MusicActivityScan.class);
						getActivity().startActivity(intent);
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

		//Ƕ��view
		ll_title_bar.addView(ll_title);
		ll_edit_bar.addView(ll_edit1);

		return v;
	}

	@Override
	public void onResume() 
	{
		super.onStart();
		musiclist = DatabaseUtil.getMusicList(getActivity(),Constant.Localmusicid);
		setListView();
	}

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
			String playpath = DatabaseUtil.getMusicPath(getActivity(),getPlaylistShared("playlist"),musicid);
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
	}

	private void setListView() 
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

				String musicName = DatabaseUtil.getMusicInfo(getActivity(),Constant.Localmusicid,musiclist.get(position)).get(2);
				musicName+="-"+DatabaseUtil.getMusicInfo(getActivity(),Constant.Localmusicid,musiclist.get(position)).get(1);

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

		lv = (ListView) getActivity().findViewById(R.id.localmusci_listview_musiclist);

		lv.setAdapter(ba);

		lv.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				setPlaylistShared("playlist",Constant.Localmusicid);
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

				ArrayList<String> musicinfo = DatabaseUtil.getMusicInfo(getActivity(),getPlaylistShared("playlist"),musicid);
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
		/*lv.setOnItemLongClickListener(new OnItemLongClickListener()
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
							DatabaseUtil.deleteMusic(musicTemp);
							SharedPreferences sp = getActivity().getSharedPreferences
									("music",Context.MODE_MULTI_PROCESS);
							int musicid = sp.getInt(Constant.SHARED_ID, -1);
							musiclist = DatabaseUtil.getMusicList(getActivity(),Constant.LIST_ALLMUSIC);
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
								intent_start.putExtra("path", DatabaseUtil.getMusicPath(getActivity(),musicid));
								getActivity().sendBroadcast(intent_start);
								Intent intent_pause = new Intent(Constant.MUSIC_CONTROL);
								intent_pause.putExtra("cmd", Constant.COMMAND_PAUSE);
								getActivity().sendBroadcast(intent_pause);
							}
							ba.notifyDataSetChanged();
							break;
						case 1:
							playlist = DatabaseUtil.getPlayList();
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
									DatabaseUtil.setMusicInPlaylist(musiclist.get(selectTemp), listid);
								}
							});
						}
						dialog.dismiss();
					}
				}).create().show();
				return false;
			}
		});*/
		lv.setOnCreateContextMenuListener(this);
	}
	
	public void setShared(String key, String value)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putString(key, value);
		spEditor.commit();
	}
	public void setPlaylistShared(String key, String value)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putString(key, value);
		spEditor.commit();
	}
	public String getShared(String key)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
}

class MyOnClickListener implements OnClickListener
{
	LinearLayout box, edit;

	public MyOnClickListener(LinearLayout box, LinearLayout edit)
	{
		this.box = box;
		this.edit = edit;
	}

	@Override
	public void onClick(View v) 
	{
		box.removeAllViews();
		box.addView(edit);
	}
}


