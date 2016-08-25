package com.example.musicplay.fragment;

import java.util.List;

import com.example.musicplay.MainActivity;
import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.utils.Constant;
import com.example.musicplay.utils.DownloadMP3;
import com.example.musicplay.utils.NetInfoUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MusicFragmentSearch extends Fragment {

	ListView lv_search;
	ImageButton ib_back;
	ImageView iv_search;
	LinearLayout ll_search;
	LinearLayout ll_error;
	RelativeLayout rl_search;
	EditText et_search;
	BaseAdapter ba;
	List<String[]> musicList;
	int selectTemp;
	
	String searchStr;
	private Handler handle;
	private ProgressDialog loadDialog;

	public MusicFragmentSearch()
	{
		searchStr=null;
	}
	
	public MusicFragmentSearch(String searchStr)
	{
		this.searchStr=searchStr;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		View v = inflater.inflate(R.layout.fragment_search, container, false);
		rl_search = (RelativeLayout)inflater.inflate(R.layout.loading, null);
		ll_error = (LinearLayout)inflater.inflate(R.layout.web_error,null);
		lv_search = (ListView) v.findViewById(R.id.search_listview_resultlist);
		ib_back = (ImageButton) v.findViewById(R.id.search_imagebutton_back_l);
		iv_search = (ImageView) v.findViewById(R.id.search_imageview_search);
		ll_search = (LinearLayout) v.findViewById(R.id.search_linearlayou_resultlist);
		et_search=(EditText)v.findViewById(R.id.main_textview_search);
		
		loadDialog = new ProgressDialog(getActivity()); 
		
		handle = new Handler() 
		{
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);

				switch (msg.what) 
				{
				case Constant.LOAD_PREPARE:
					ll_search.removeAllViews();
					ll_search.addView(rl_search);
					break;
				case Constant.LOAD_COMPLETE:
					ll_search.removeAllViews();
					ll_search.addView(lv_search);
					ba.notifyDataSetChanged();
					break;
				case Constant.DOWNLOAD_UPDATE:
					Bundle b=msg.getData();
					int progressTemp = b.getInt("download");
					loadDialog.setProgress(progressTemp);
					break;
				case Constant.LOAD_ERROR:
					ll_search.removeAllViews();
					ll_error.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					ll_search.addView(ll_error);
					break;
				}
			}
		};
		
		et_search.setOnFocusChangeListener(new View.OnFocusChangeListener() 
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus) 
			{
				if(hasFocus)
				{
					et_search.setText("");
				}
			}
		});

		ib_back.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				if (getFragmentManager().getBackStackEntryCount() > 1) 
				{
					fragmentManager.popBackStack();
				}
			}
		});

		iv_search.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				new Thread() 
				{
					public void run() 
					{
						handle.sendEmptyMessage(Constant.LOAD_PREPARE);
						try{
							musicList=NetInfoUtil.searchSong(et_search.getText().toString().trim());
							if(musicList==null)
							{
								handle.sendEmptyMessage(Constant.LOAD_ERROR);
								return;
							}
							ba = new BaseAdapter() 
							{
								LayoutInflater inflater = LayoutInflater.from(getActivity());
								int count = 1;

								@Override
								public int getCount() 
								{
									count = musicList.size() + 1;
									return count;
								}

								@Override
								public Object getItem(int position) {
									return null;
								}

								@Override
								public long getItemId(int position) {
									return 0;
								}

								@Override
								public View getView(int arg0, View arg1, ViewGroup arg2) 
								{
									if(count==1)
									{
										LinearLayout lll = (LinearLayout) inflater.inflate
												(R.layout.listview_count, null)
												.findViewById(R.id.linearlayout_null);
										TextView tv_sum = (TextView) lll.getChildAt(0);
										tv_sum.setText("���ź�������տ���Ҳ"+"\n\n\n");
										return lll;
									}
									if (arg0 == musicList.size()) 
									{
										LinearLayout lll = (LinearLayout) inflater.inflate
												(R.layout.listview_count, null)
												.findViewById(R.id.linearlayout_null);
										TextView tv_sum = (TextView) lll.getChildAt(0);
										tv_sum.setText("����" + (count - 1) + "�����"+ "\n\n\n");
										return lll;
									}
									String musicName = musicList.get(arg0)[1]+"-"+musicList.get(arg0)[0];
									LinearLayout ll = (LinearLayout) inflater.inflate
											(R.layout.fragment_localmusic_listview_row,null)
											.findViewById(R.id.LinearLayout_row);
									TextView tv = (TextView) ll.getChildAt(1);
									tv.setText(musicName);
									return ll;
								}
							};

							lv_search.setAdapter(ba);

							lv_search.setOnItemClickListener(new OnItemClickListener() 
							{
								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) 
								{
									if (arg2 == arg0.getCount() - 1) 
									{
										return;
									}
									selectTemp = arg2;
									AlertDialog.Builder builder = new Builder(getActivity());
									builder.setMessage(musicList.get(selectTemp)[0]);
									builder.setTitle("����");
									builder.setPositiveButton("ȷ��",new OnClickListener() 
									{
										@Override
										public void onClick(DialogInterface dialog,int which) 
										{
											dialog.dismiss();
											loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//���ý�����Ϊ����
											loadDialog.setTitle("��������");//���ñ���
											loadDialog.setMessage(musicList.get(selectTemp)[4]);//��������
											loadDialog.setIcon(R.drawable.webmusic_download);//����ͼ��
											loadDialog.setIndeterminate(true);//���ý�����ֵ�Ӳ����ܸ���
											loadDialog.setMax(100);//�������ֵ
											loadDialog.setCancelable(false);//�����ܲ��ܱ�ȡ��
											loadDialog.show();//��ʾdialog
											new Thread() 
											{
												public void run() 
												{
													int musicid = -1 ;
													try 
													{
														 musicid = DownloadMP3.download(musicList.get(selectTemp)[3],
																musicList.get(selectTemp)[4],handle);
													} 
													catch (Exception e) 
													{
														e.printStackTrace();
													}
													finally
													{
														loadDialog.dismiss();
													}
													if(musicid!=-1)
													{
														SharedPreferences sp = getActivity().getSharedPreferences
																("music",Context.MODE_MULTI_PROCESS);
														SharedPreferences.Editor spEditor=sp.edit();
														spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
														spEditor.putInt(Constant.SHARED_ID, musicid);
														spEditor.commit();
														String playpath = DatabaseUtil.getMusicPath(getActivity(),getPlaylistShared("playlist"),musicid);
														Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
														intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
														intentstop.putExtra("path", playpath);
														getActivity().sendBroadcast(intentstop);
														try
														{
															FragmentManager fm = getFragmentManager();
															fm.popBackStack(MainActivity.mainFragmentId, 0);
														}
														catch(Exception e)
														{
															e.printStackTrace();
														}
													}
												}
											}.start();
										}
									});
									builder.setNegativeButton("ȡ��",new OnClickListener()
									{
										@Override
										public void onClick(DialogInterface dialog,int which)
										{
											dialog.dismiss();
										}
									});
									builder.create().show();
								}
							});
						}catch(Exception e)
						{
							e.printStackTrace();
						}
						handle.sendEmptyMessage(Constant.LOAD_COMPLETE);
					}
				}.start();
			}
		});
		if(searchStr!=null && !searchStr.isEmpty())
		{
			et_search.setText(searchStr);
			iv_search.callOnClick();
		}

		return v;
	}
	
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = getActivity().getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
}