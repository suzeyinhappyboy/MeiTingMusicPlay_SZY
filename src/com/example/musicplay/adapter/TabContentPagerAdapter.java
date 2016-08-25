package com.example.musicplay.adapter;

import com.example.musicplay.fragment.BaseFragment;
import com.example.musicplay.fragment.DiscoverFragment;
import com.example.musicplay.fragment.MusicFragmentLocalmusic;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabContentPagerAdapter extends FragmentPagerAdapter {

	public TabContentPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment fragment = null;
		Bundle args = new Bundle();

		switch (i) {
			case 0:
				fragment = new DiscoverFragment();
				System.out.println("DiscoverFragment");
			//	ToastUtils.show(Context, "DiscoverFragment");
				break;
			case 1:
				fragment = new MusicFragmentLocalmusic();
				break;
			case 2:
				fragment = new BaseFragment();
				args.putInt(BaseFragment.ARG_COLOR, Color.WHITE);
				fragment.setArguments(args);
				break;
		}


		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Object -- " + (++position);
	}
}