package com.example.musicplay.adapter;

import java.util.ArrayList;

import com.example.musicplay.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MusiclistAdapter extends BaseAdapter {

	ArrayList<Song> alist;
	Context mContext;
	private OnMoreClickListener mListener;
	
	public  MusiclistAdapter(ArrayList<Song> alist,Context mContext) {
		// TODO Auto-generated constructor stub
		this.alist=alist;
		this.mContext=mContext;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return alist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return alist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewhodler;
		if (convertView == null) {
			viewhodler = new ViewHolder();
			convertView = View.inflate(mContext, R.layout.item_song_list, null);
			viewhodler.tvTitle = (TextView) convertView.findViewById(R.id.songname);
			viewhodler.tvAuth = (TextView) convertView.findViewById(R.id.singer);
			viewhodler.ivMore = (ImageView) convertView.findViewById(R.id.iv_more);
			convertView.setTag(viewhodler);
		} else {
			viewhodler = (ViewHolder) convertView.getTag();
		}
		
		viewhodler.tvTitle.setText(""+alist.get(position).getTitle());
		viewhodler.tvAuth.setText(""+alist.get(position).getSinger());
		viewhodler.ivMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mListener.onMoreClick(position);
				
			}
		});
		//System.out.println("MusiclistAdapter tvTitle="+alist.get(position).getTitle());
		return convertView;
	}
	
	public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }
	
	public void refresh() {
	//	count += 10;
		notifyDataSetChanged();
	}

	static class ViewHolder {
		TextView tvAuth;
		TextView tvCount;
		TextView tvTitle;
		ImageView ivStar;
		ImageView ivMore;
	}
}
