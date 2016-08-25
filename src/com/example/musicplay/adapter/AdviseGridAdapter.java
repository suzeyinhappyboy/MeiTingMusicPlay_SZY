package com.example.musicplay.adapter;

import java.util.Random;

import com.example.musicplay.R;
import com.example.musicplay.utils.NumberUtil;
import com.example.musicplay.view.RoundImageView;
import com.example.musicplay.widget.AwesomeTextHandler;
import com.example.musicplay.widget.MentionSpanRenderer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by alex.lee on 2015-07-26.
 */
public class AdviseGridAdapter extends BaseAdapter {
	private Random random;
	private Context context;
	private int[] ids = new int[]{
			R.drawable.hot_advise_1, R.drawable.hot_advise_2, R.drawable.hot_advise_3,
			R.drawable.hot_advise_4, R.drawable.hot5, R.drawable.hot6
	};
	private String[] songs = new String[]{
			"春神曲", "夏恋曲", "秋歌曲",
			"冬伤曲", "流恋", "梦香雪情"
	};
	private static final String MENTION_PATTERN = "(@[\\p{L}0-9-_]+)";

	public AdviseGridAdapter(Context context) {
		this.context = context;
		random = new Random();
	}

	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_grid_advise, null);

			holder.rivCover = (RoundImageView) convertView.findViewById(R.id.riv_cover);
			holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
			holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.rivCover.setImageResource(ids[position]);
		holder.tvCount.setText(NumberUtil.getCount(random.nextInt(250000)));
		holder.tvDesc.setText(songs[position]);			
/*		if (position == 3 || position == 5) {
			AwesomeTextHandler awesomeEditTextHandler = new AwesomeTextHandler();
			awesomeEditTextHandler.addViewSpanRenderer(MENTION_PATTERN, new MentionSpanRenderer()).setView(holder.tvDesc);
		} */
		return convertView;
	}

	static class ViewHolder {
		RoundImageView rivCover;
		TextView tvCount;
		TextView tvDesc;
	}
}
