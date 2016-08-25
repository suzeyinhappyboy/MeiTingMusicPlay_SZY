package com.example.musicplay.receiver;

import com.example.musicplay.MusicPlayActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicUpdateVisualizer extends BroadcastReceiver
{
	MusicPlayActivity pa;
	public boolean visualzerFlag;
	public boolean visualzerMode;
	byte bytes[];
	
	public MusicUpdateVisualizer(MusicPlayActivity pa)
	{
		this.pa=pa;
		visualzerFlag=false;
		visualzerMode=false;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(visualzerFlag)
		{
			if(visualzerMode)
			{
				bytes=intent.getByteArrayExtra("visualizerwave");
				if(bytes!=null)
				{
					pa.mVisualizerView.updateVisualizer(bytes,visualzerMode);
				}
			}
			else
			{
				bytes=intent.getByteArrayExtra("visualizerfft");
				if(bytes!=null)
				{
					pa.mVisualizerView.updateVisualizer(bytes,visualzerMode);
				}
			}
		}
	}
}