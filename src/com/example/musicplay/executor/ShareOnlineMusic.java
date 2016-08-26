package com.example.musicplay.executor;

import com.example.musicplay.R;

import android.content.Context;
import android.content.Intent;
import android.telecom.Call;

/**
 * 分享在线歌曲
 * Created by hzwangchenyan on 2016/1/13.
 */
public abstract class ShareOnlineMusic {
    private Context mContext;
    private String mTitle;
    private String mSongPath;

    public ShareOnlineMusic(Context context, String title, String playpath) {
        mContext = context;
        mTitle = title;
        mSongPath = playpath;
    }

    public void execute() {
        share();
    }

    private void share() {
        onPrepare();
        // 获取歌曲播放链接
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_music, mContext.getString(R.string.app_name),
        		mTitle,mSongPath));
        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)));
    }

    public abstract void onPrepare();

    public abstract void onSuccess();

    public abstract void onFail(Call call, Exception e);
}
