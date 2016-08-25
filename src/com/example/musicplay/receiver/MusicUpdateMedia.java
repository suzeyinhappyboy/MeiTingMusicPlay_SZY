package com.example.musicplay.receiver;

import java.util.ArrayList;

import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.service.MusicPlayService;
import com.example.musicplay.utils.Constant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.widget.Toast;

public class MusicUpdateMedia extends BroadcastReceiver 
{
	public MediaPlayer mp;
	public int status = Constant.STATUS_STOP;
	public int playMode;
	public int threadNumber;
	private String BaseURL = "http://192.168.253.1/";
	Context context;

	MusicPlayService ms;
	int duration=0;
	Handler handler = new Handler();
	public MusicUpdateMedia(MusicPlayService ms) 
	{
		this.ms = ms;////获取Service的引用
	}
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context=context;
		int cmd = intent.getIntExtra("cmd", -1);
		switch (cmd)
		{
			case Constant.COMMAND_PLAYMODE://��������ģʽ
				playMode = intent.getIntExtra("playmode",Constant.PLAYMODE_SEQUENCE);
				break;
			case Constant.COMMAND_PLAY:					
				// 准备并播放音乐					
				String path = intent.getStringExtra("path");
				//System.out.println("onReceive---haha--->"+path);
				if (path != null) //如果路径为空则表示歌曲从暂停状态到播放状态
				{
					prepareAndPlay(path);
				} 
				else //否则表示播放一首新歌
				{
					mp.start();//播放歌曲
					ms.startSensor();//注册传感器监听
					try
					{
						ms.startVisualizer();//尝试开启频谱
					}
					catch(Exception e)
					{
						e.printStackTrace();
						status=Constant.STATUS_STOP;
						break;
					}
				}
				status = Constant.STATUS_PLAY;										
				break; 
			
			case Constant.COMMAND_PAUSE:
				// 暂停
				mp.pause();
				ms.canalSensor();
				// 改变为暂停状态
				status = Constant.STATUS_PAUSE;
				handler.removeCallbacks(update_thread);
				break;
			case Constant.COMMAND_STOP://停止
				status = Constant.STATUS_STOP;
				if(mp!=null)
				{
					mp.release();
				}
				ms.canalSensor();
				break;
			case Constant.COMMAND_PROGRESS:	
				int seekbar_progress = intent.getIntExtra("current_progress", 0);
				mp.seekTo(seekbar_progress); 
			    break;

		}		
		UpdateUI();
	}

	public void UpdateUI() 
	{
		Intent sendIntent = new Intent(Constant.UPDATE_STATUS);
		//sendIntent.putExtra("duration", duration);
		sendIntent.putExtra("status", status);
		// 发送广播，将被Activity组件中的BroadcastReceiver接收到
		context.sendBroadcast(sendIntent);
	}
	
	public void UpdateUI(Context context)//与上一方法功能一致
	{
		Intent sendIntent = new Intent(Constant.UPDATE_STATUS);
		//sendIntent.putExtra("duration", duration);
		sendIntent.putExtra("status", status);
		// 发送广播，将被Activity组件中的BroadcastReceiver接收到
		context.sendBroadcast(sendIntent);
	}
	
	private void prepareAndPlay(String path)
	{
		if (mp != null) 
		{
			mp.release();//释放播放器
		}
		ms.canalVisualizer();  //注销频谱监听
		mp = new MediaPlayer(); 
		mp.setOnCompletionListener(new OnCompletionListener()//������ɼ���
		{
			@Override
			public void onCompletion(MediaPlayer mp)
			{
				
				onComplete(mp);
				UpdateUI();
				
			}
		});
		try
		{
			//System.out.println("path---haha--->"+path);
			//mp.reset();
			// 使用MediaPlayer加载指定的声音文件。
			mp.setDataSource(path);
			// 准备声音
			mp.prepare();
			// 播放
			mp.start();
			ms.initVisualizer(mp);  //初始化频谱监听
			duration = mp.getDuration();
			//handler.postDelayed(mRunnable, 800);
			//将线程接口立刻送到线程队列中
            handler.post(update_thread);
            status = Constant.STATUS_PLAY;
			//new MusicPlayerThread(this).start();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			ms.canalVisualizer();
		}
	}
	
	public void onComplete(MediaPlayer mp) 
	{
		String playpath;
		SharedPreferences sp = ms.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);//��ȡSharedPreferences������
		int musicid = sp.getInt(Constant.SHARED_ID, -1);//������ڲ��ŵ�����id
		int playMode = sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);//��õ�ǰ�Ĳ���ģʽ
		//int list=sp.getInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);//��õ�ǰ�Ĳ����б�
		ArrayList<Integer> musicList = DatabaseUtil.getMusicList(context,getPlaylistShared("playlist"));//��ø��������б�
		System.out.println("onComplete.musicid="+musicid);
		System.out.println("onComplete.musicList.size()="+musicList.size());
		if(musicid==-1)//�����ǰ���Ÿ����������򷵻�
		{
			return;
		}
		if(musicList.size()==0)//��������б�Ϊ���򷵻�
		{
			return;
		}
		
		System.out.println("playMode="+playMode);
		
		System.out.println("onComplete.getPlaylistShared="+getPlaylistShared("playlist"));
		switch (playMode)
		{
		case Constant.PLAYMODE_REPEATSINGLE://����ѭ��ģʽ
			playpath = DatabaseUtil.getMusicPath(context,getPlaylistShared("playlist"),musicid);//��ø�����
			System.out.println("playpath haha="+playpath);
			prepareAndPlay(playpath);
			break;
		case Constant.PLAYMODE_REPEATALL://�б�ѭ��ģʽ
			musicid = DatabaseUtil.getNextMusic(musicList,musicid);//�����һ�׸���
			playpath = DatabaseUtil.getMusicPath(context,getPlaylistShared("playlist"),musicid);
			System.out.println("playpath haha="+playpath);
			prepareAndPlay(playpath);
			break;
		case Constant.PLAYMODE_SEQUENCE://�б���ģʽ
			if (musicList.get(musicList.size() - 1) == musicid) //�ж��Ƿ�Ϊ�����б�����һ��
			{
				ms.canalSensor();
				ms.canalVisualizer();
				mp.release();
				status = Constant.STATUS_STOP;
				Toast.makeText(context, "最后一首歌了", Toast.LENGTH_LONG).show();
			} 
			else 
			{
				musicid = DatabaseUtil.getNextMusic(musicList,musicid);
				playpath =DatabaseUtil.getMusicPath(context,getPlaylistShared("playlist"),musicid);
				System.out.println("playpath haha="+playpath);
				prepareAndPlay(playpath);
			}
			break;
		case Constant.PLAYMODE_RANDOM://�������ģʽ
			musicid = DatabaseUtil.getRandomMusic(musicList, musicid);//����������id
			playpath = DatabaseUtil.getMusicPath(context,getPlaylistShared("playlist"),musicid);
			System.out.println("playpath haha="+playpath);
			prepareAndPlay(playpath);
			break;
		}
		SharedPreferences.Editor spEditor = sp.edit();//��ñ༭SharedPreferences������
		spEditor.putInt(Constant.SHARED_ID, musicid);//��������id
		spEditor.commit();
		UpdateUI();
	}
	public String fromMsToMinuteStr(int ms) 
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}
	public String getPlaylistShared(String key)
	{
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString(key, null);
		return value;
	}
	Runnable update_thread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Intent intent = new Intent();
			intent.setAction(Constant.UPDATE_STATUS);
			intent.putExtra("status",Constant.COMMAND_SEEKBAR);
			intent.putExtra("status2",status);
			intent.putExtra("duration", duration);
			intent.putExtra("current_time", mp.getCurrentPosition());
			context.sendBroadcast(intent);
			handler.postDelayed(update_thread, 1000);
		}
	};
}