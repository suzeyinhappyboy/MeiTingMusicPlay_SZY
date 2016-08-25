package com.example.musicplay.service;

import com.example.musicplay.receiver.MusicUpdateMedia;
import com.example.musicplay.utils.Constant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;
import android.os.IBinder;


public class MusicPlayService extends Service
{
	MusicUpdateMedia mc;
	public Visualizer mVisualizer;//频谱
	public Equalizer mEqualizer;//均衡器
	private static final int SPEED_SHRESHOLD = 10;//定义加速度上限
	private static final int SPACE_TIME = 200;//定义加速度采样频率
	private static final int SPACE_MUSIC = 2000;//定义换歌频率
	private SensorManager mySensorManager;//传感器管理器
	private Sensor mySensor;//加速度传感器的引用
	private long currentTime,lastTime,lastTime2,duration,duration2;//判断参数
	private final static int RECT_COUNT=32;//频域频谱柱子数量
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	@Override
	public void onCreate()
	{
		super.onCreate();
		mc = new MusicUpdateMedia(this);
		mc.mp = new MediaPlayer();
		mc.status = Constant.STATUS_STOP;
		initSensor();
		//为Service注册广播接收器MusicUpdateMedia
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.MUSIC_CONTROL);
		registerReceiver(mc, filter);		
	}
	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		canalSensor();
		if(mEqualizer!=null)
		{
			mEqualizer.release();
		}
		if(mVisualizer!=null)
		{
			mVisualizer.release();
		}
		if(mc.mp!=null)
		{
			mc.mp.release();
		}
		this.unregisterReceiver(mc);		
	}

	@Override
	public void onStart(Intent intent, int id) 
	{
		mc.UpdateUI(this.getApplicationContext());
	}
	
	private SensorEventListener mySel=new SensorEventListener()
	{
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) 
		{
		}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			currentTime = System.currentTimeMillis();//返回从 UTC 1970 年 1 月 1 日午夜开始经过的毫秒数
			duration = currentTime - lastTime;
			duration2 = currentTime - lastTime2;
			if(duration2 < SPACE_TIME)//每隔0.2s响应
			{
				return;
			}
			if(duration < SPACE_MUSIC)//切歌后隔2s再次开始响应
			{
				return;
			}
			lastTime2 = currentTime;
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			double speed =Math.abs(Math.sqrt(x*x+y*y+z*z)-10);//计算加速度的平方,10为用于平衡重力的偏移量
			if(speed > SPEED_SHRESHOLD)
			{
				lastTime=currentTime;
				//mc.NumberRandom();
				mc.onComplete(mc.mp);//如果响应切歌，执行歌曲播放完成时执行的代码，代码重用，减少代码量
				mc.UpdateUI();//更新UI界面
			}
		}
	};
	
	public void initVisualizer(MediaPlayer mp)// 初始化频谱
	{
		if (mp != null) {
			mEqualizer = new Equalizer(0, mp.getAudioSessionId()); // 创建均衡器
			mVisualizer = new Visualizer(mp.getAudioSessionId()); // 创建频谱分析器
			mVisualizer.setCaptureSize(512);// 设置采样率
			mVisualizer.setDataCaptureListener(
				new Visualizer.OnDataCaptureListener() {
						// 时域频谱
					public void onWaveFormDataCapture(Visualizer visualizer,
							byte[] bytes,int samplingRate) {
						Intent intent = new Intent(Constant.UPDATE_VISUALIZER);
						intent.putExtra("visualizerwave", bytes);
						MusicPlayService.this.sendBroadcast(intent);
					}

						// 频域频谱
					public void onFftDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
						byte[] byt = new byte[RECT_COUNT];
						for (int i = 0; i < RECT_COUNT; i++) {
							byt[i] = (byte) Math.hypot(bytes[2 * (i + 1)],bytes[2 * (i + 1) + 1]);
						}
						Intent intent = new Intent(
								Constant.UPDATE_VISUALIZER);
						intent.putExtra("visualizerfft", byt);
						MusicPlayService.this.sendBroadcast(intent);
					}
				}, Visualizer.getMaxCaptureRate() / 2, true, true);// 更新频率、时域频谱是否启用、频域频谱是否启用
			startVisualizer();
			startSensor();
		}
	}
	
	public void startVisualizer()
	{
		mVisualizer.setEnabled(true);
		mEqualizer.setEnabled(true);
	}

	public void canalVisualizer()
	{
		if(mEqualizer!=null)
		{
			mEqualizer.release();
		}
		if(mVisualizer!=null)
		{
			mVisualizer.release();
		}
	}
	
	public void initSensor()
	{
		//获取加速度传感器引用。
		mySensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);  
		mySensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	}
	
	public void startSensor() 
	{
		//注册加速度传感器
		mySensorManager.registerListener(mySel, mySensor, SensorManager.SENSOR_DELAY_UI);
	}
	
	public void canalSensor()//暂停时调用
	{
		mySensorManager.unregisterListener(mySel);
		if(mEqualizer!=null)
		{
			mEqualizer.setEnabled(false);
		}
		if(mVisualizer!=null)
		{
			mVisualizer.setEnabled(false);
		}
	}
}

