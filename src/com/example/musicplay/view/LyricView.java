package com.example.musicplay.view;

import java.util.ArrayList;

import com.example.musicplay.R;
import com.example.musicplay.adapter.DatabaseUtil;
import com.example.musicplay.utils.Constant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LyricView extends View {
	private ArrayList<String[]> lyric;
	private String lyrictemp[];
	private int lineHeight;// ÿ��Ԥ���ռ�
	private int otherLyricSize = 24;// ��ͨ��ʴ�С
	private int nowLyricSize = 34;// ��ǰ��ʴ�С
	private int marginTop = 30;// �Ϸ�����
	private int lyricTime = 80;// ����������Ļ������ʱ��
	private int lyricSpeed;// ����1����Ҫ����������
	private int alphaSpan;// ͸���Ƚ��䲽��
	private int paddingTop;// ʣ��߶���������������
	private int paddingLeft = 70;// ʣ��߶���������������
	private int lineCount = 9;// ��������
	private int current;// ��ȡ����
	private int currentTemp;// ��ʱ����
	private int duration;// ��ȡ�����ܳ�
	private int viewWidth;// ��ͼ���
	private int viewHeight;// ��ͼ�߶�
	private int tempy;// ���䰴��ʱ��Y
	private boolean touchFlag = true;// ����ʱ�Ļ���
	private Paint p = new Paint();

	Context context;

	public LyricView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		setBackgroundResource(R.color.none);
	}

	// ��ʼ���ؼ�����
	public void initView() 
	{
		viewHeight = getHeight();//��ÿؼ��߶�
		viewWidth = getWidth();//��ÿؼ����
		lineHeight = viewHeight / (lineCount + 2);// �������������иߣ�3Ϊ��Ԥ�����µĿհ�����
		paddingTop = (viewHeight - lineCount * lineHeight) / 2;//����������Ԥ���Ŀհ�����
		lyricSpeed = viewHeight / lyricTime;
		otherLyricSize = (int) (lineHeight * 0.6);
		nowLyricSize = (int) (lineHeight * 0.85);
		paddingTop += lineHeight*1/4;
	}

	// ����ʱ�������λ��
	public int now() 
	{
		int now = 0;
		for (int i = 0; i < lyric.size(); i++) 
		{
			String lyric1[] = lyric.get(i);
			int time1 = (Integer.parseInt(lyric1[0]) * 60 + Integer.parseInt(lyric1[1])) * 1000;
			String lyric2[] = { "", "", "" };
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
			if (current < (Integer.parseInt(lyric.get(0)[0]) * 60 + Integer.parseInt(lyric.get(0)[1])) * 1000) 
			{
				break;
			}
			if (current > time1 && current < time2) {
				now = i;
				break;
			}
		}
		return now;
	}

	// ��ʼ�����
	public void initLayout() 
	{
		lyrictemp = new String[lineCount];//��������Ϊ������������������Ÿ��
		int iTemp = lyrictemp.length;
		if (lyric == null) //���û�и�ʡ��������ʾ���ɺ�����
		{
			for (int i = 0; i < iTemp; i++) 
			{
				if (i == iTemp / 2) 
				{
					lyrictemp[i] = "美听好音乐";
				} 
				else 
				{
					lyrictemp[i] = "";
				}
			}
			return;
		}

		int j = now();
		j = j - iTemp / 2;
		for (int i = 0; i < iTemp; i++, j++) 
		{
			try 
			{
				lyrictemp[i] = lyric.get(j)[2];
			} 
			catch (Exception e) //����Ҳ��������򷵻ؿ�ֵ
			{
				lyrictemp[i] = "";
			}
		}
	}

	public void setLyric(Context context,int musicid) //���ø��
	{
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		String value = sp.getString("playlist", null);

		lyric = DatabaseUtil.getLyric(DatabaseUtil.getLyricPath(context,musicid,value));//��ȡ�������
	}

	public void setMusictime(int current, int duration) //���õ�ǰ����ʱ����������ʱ��
	{
		this.current = current;
		this.duration = duration;
		if (touchFlag) 
		{
			invalidate();
		}
	}

	public int getCurrent()  //��ȡ��ǰ����ʱ��
	{
		return current;
	}

	public int getDuration()  //��ȡ������ʱ��
	{
		return duration;
	}

	public String fromMsToMinuteStr(int ms) //����תΪʱ���ַ���
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) //�������
	{
		int y = (int) event.getY();
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			tempy = y;
			touchFlag = false;
			currentTemp = current;
			break;
		case MotionEvent.ACTION_MOVE:
			current = currentTemp - 1000 * (y - tempy) / lyricSpeed;
			if (current > duration - 1000) 
			{
				current = duration - 1000;
			}
			if (current < 0) 
			{
				current = 0;
			}
			break;
		case MotionEvent.ACTION_UP:
			tempy = 0;
			touchFlag = true;
			if(duration==0)
			{
				break;
			}
			Intent intent = new Intent(Constant.MUSIC_CONTROL);
			intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
			intent.putExtra("current", current);
			context.sendBroadcast(intent);
			break;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	public void onDraw(Canvas canvas) {
		initView();
		initLayout();
		p.setAntiAlias(true);

		// ��������
		int x = viewWidth / 2;
		int y;
		int iTemp = lyrictemp.length;
		alphaSpan = (240 - 40) / (iTemp / 2);

		// ����ͼ��û���
		canvas.save();
		canvas.clipRect(paddingLeft, 0, viewWidth - paddingLeft, viewHeight);

		p.setTextAlign(Paint.Align.CENTER);
		p.setStyle(Style.STROKE);
		for (int i = 0; i < iTemp; i++) 
		{
			y = marginTop + paddingTop + i * lineHeight;
			if (i < iTemp / 2) 
			{
				p.setColor(Color.WHITE);
				p.setTextSize(otherLyricSize);
				p.setAlpha(alphaSpan * (i + 1) + 40);//����͸���Ƚ���Ч��
				canvas.drawText(lyrictemp[i], x, y, p);
			} 
			else if (i == iTemp / 2) 
			{
				p.setColor(Color.YELLOW);
				p.setTextSize(nowLyricSize);
				p.setAlpha(255);
				canvas.drawText(lyrictemp[i], x, y, p);
			} 
			else 
			{
				p.setColor(Color.WHITE);
				p.setTextSize(otherLyricSize);
				p.setAlpha(alphaSpan * (iTemp - i) + 40);
				canvas.drawText(lyrictemp[i], x, y, p);
			}
		}

		// �ָ�����
		canvas.restore();

		if (!touchFlag) 
		{
			// ����ʱ���
			p.setColor(Color.BLACK);
			p.setAlpha(200);
			p.setStyle(Style.FILL_AND_STROKE);
			canvas.drawRect(paddingLeft / 3, viewHeight / 2 - 40,
					paddingLeft / 3 + 60, viewHeight / 2, p);

			// ����ʱ����
			p.setStyle(Style.STROKE);
			p.setColor(Color.YELLOW);
			p.setStrokeWidth(3);
			p.setAlpha(255);
			canvas.drawLine(paddingLeft / 3, viewHeight / 2, viewWidth
					- paddingLeft / 3, viewHeight / 2, p);

			// ����ʱ��
			p.setTextAlign(Paint.Align.CENTER);
			p.setTextSize(20);
			p.setStrokeWidth(0);
			canvas.drawText(fromMsToMinuteStr(current), paddingLeft / 3 + 30,viewHeight / 2 - 10, p);
		}
	}
}