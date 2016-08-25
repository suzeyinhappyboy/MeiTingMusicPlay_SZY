package com.example.musicplay.utils;

public class Constant {//��������Constant
	public static final String Localmusicid="1";
	public static final String Netmusicid="2";
	//��������
	public static final int COMMAND_PLAY = 0; // ��������
	public static final int COMMAND_PAUSE = 1; // ��ͣ����
	public static final int COMMAND_NEXT = 2; // ��ͣ����
	public static final int COMMAND_PREV = 10; // ��ͣ����
	public static final int COMMAND_PROGRESS = 3; // ���ò���λ��
	public static final int COMMAND_STOP = 15; // ֹͣ����
	public static final int COMMAND_SEEKBAR = 7;
	public static final int COMMAND_START = 8;
	public static final int COMMAND_PLAYMODE=9;
	//����״̬
	public static final int STATUS_PLAY = 4; // ����״̬
	public static final int STATUS_PAUSE = 5; // ��ͣ״̬
	public static final int STATUS_STOP = 6; // ֹͣ״̬
	//����ģʽ
	public static final int PLAYMODE_REPEATALL=10;
	public static final int PLAYMODE_REPEATSINGLE=11;
	public static final int PLAYMODE_SEQUENCE=-1;
	public static final int PLAYMODE_RANDOM=13;
	//handle����
	public static final int DATABASE_ERROR = 0;
	public static final int DATABASE_COMPLETE = 1;
	public static final int PROGRESS_UPDATE = 2;
	public static final int PATH_UPDATE = 3;
	public static final int LOAD_COMPLETE=4;
	public static final int LOAD_PREPARE=5;
	public static final int LOAD_ERROR=6;
	public static final int DOWNLOAD_UPDATE=14;
	//�����б���
	public static final int LIST_ALLMUSIC=-3;
	public static final int LIST_LocalMUSIC=-5;
	public static final int LIST_NetMUSIC=-6;
	public static final int LIST_ILIKE=-2;
	public static final int LIST_LASTPLAY=-1;
	public static final int LIST_DOWNLOAD=-4;	
	//sharedpreferences
	public static final String SHARED="music";
	public static final String SHARED_ID="id";
	public static final String SHARED_LIST="list";
	//fragment����
	public static final String FRAGMENT_MUSIC="ȫ������";
	public static final String FRAGMENT_SINGER="����";
	public static final String FRAGMENT_ALBUM="ר��";
	public static final String FRAGMENT_ILIKE="��ϲ��";
	public static final String FRAGMENT_MYLIST="�ҵĸ赥";
	public static final String FRAGMENT_DOWNLOAD="���ع���";
	public static final String FRAGMENT_LASTPLAY="�������";
	//IntentAction
	public static final String MUSIC_CONTROL = "com.example.musicplay.service.MusicPlayService.ACTION_CONTROL";
	public static final String UPDATE_STATUS = "com.example.musicplay.receiver.MusicUpdatePlay.ACTION_STATUS";
	public static final String UPDATE_SEEKBAR = "com.example.musicplay.receiver.MusicUpdatePlay.ACTION_SEEKBAR";
	public static final String UPDATE_VISUALIZER = "kugoumusic.ACTION_VISUALIZER";
	public static final String UPDATE_WIDGET = "kugoumusic.ACTION_WIDGET";
	public static final String WIDGET_STATUS = "kugoumusic.WIDGET_STATUS";
	public static final String WIDGET_SEEK = "kugoumusic.WIDGET_SEEK";
	//widget���ſ���
	public static final String WIDGET_PLAY="kugoumusic.WIDGET_PLAY";
	public static final String WIDGET_NEXT="kugoumusic.WIDGET_NEXT";
	public static final String WIDGET_PREVIOUS="kugoumusic.WIDGET_PREVIOUS";
	//service��
	public static final String SERVICE_CLASS = "com.example.musicplay.service.MusicService"; 
}