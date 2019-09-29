package com.goertek.albumdemo.main;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.contants.MsgIdConstants;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;

public class GalleryVideosActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener {
    private static final String TAG = "GalleryVideosActivity";
    private TextureView mSvVideo;
    private LinearLayout mLlBackBar;
    private ImageButton mIbtnBack;
    private ImageButton mIbtnVideosAutoPlayControl;

    private AlbumFolder mAlbumFolder;
    private int mCurrentPosition;
    private AlbumFile mAlbumFile;
    private int mVideosNum;
    private boolean mIsAutoPlaying = true;
    private boolean mIsControlLayoutShow =false;

    private Surface surface;
    private MediaPlayer mediaPlayer;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_VIDEO:
                    if(mCurrentPosition == mVideosNum-1){
                        mCurrentPosition = 0;
                    }else{
                        mCurrentPosition++;
                    }
                    mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
                    loadMedia(mAlbumFile.getPath());
                    break;
                case MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE:
                    controlLayoutHide();
                    break;
                case MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_START_PLAY:
                    loadMedia(mAlbumFile.getPath());
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);               //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gallery_videos);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_VIDEO);
        mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE);
    }

    private void initView() {
        mSvVideo = (TextureView)findViewById(R.id.sv_video_show);
        mLlBackBar = (LinearLayout)findViewById(R.id.ll_back_bar);
        mIbtnBack = (ImageButton)findViewById(R.id.ibtn_back);
        mIbtnVideosAutoPlayControl = (ImageButton)findViewById(R.id.ibtn_videos_autoplay_control);

    }

    private void initListener(){
        mSvVideo.setSurfaceTextureListener(this);

        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIbtnVideosAutoPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsAutoPlaying){
                    //暂停
                    mIsAutoPlaying = false;
                    mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_VIDEO);
                    mIbtnVideosAutoPlayControl.setImageResource(R.mipmap.icon_play);
                }else{
                    //播放
                    mIsAutoPlaying = true;
                    mIbtnVideosAutoPlayControl.setImageResource(R.mipmap.icon_pause);
                    if(mediaPlayer == null){
                        loadMedia(mAlbumFile.getPath());
                    }
                }
            }
        });

    }

    private void initData(){
        mAlbumFolder = getIntent().getParcelableExtra(Album.KEY_SINGLE_ALBUM_FOLDER);
        mCurrentPosition = getIntent().getIntExtra(Album.KEY_GALLERY_CURRENT_POSITION,0);
        mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
        mVideosNum = mAlbumFolder.getAlbumFiles().size();
        mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_START_PLAY, 100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsControlLayoutShow){
            mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE);
            mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE,3000);
        }else{
            controlLayoutShow();
            mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE,3000);
        }
        return true;
    }

    /*-------------------------------mediaPlayer监听------------------------------------------------*/
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG, "onBufferingUpdate: ");

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        releaseMediaPlayer();
        if(mIsAutoPlaying){
            mHandler.sendEmptyMessage(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_VIDEO);
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared: ");
        mediaPlayer.start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    /*--------------------------------------SurfaceTextureListener---------------------------------*/

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 创建MediaPlayer,mediaPlayer监听
     * @param url
     */
    private void loadMedia(String url) {
        if (surface == null) {
            return;
        }

        try {
            createMediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setSurface(surface);
            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnInfoListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.w(TAG, "Media load failed");
        }
    }

    private void createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setSurface(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    /*-------------------------------------------------------------------------------------------*/
    private void controlLayoutShow(){
        mIsControlLayoutShow = true;
        mLlBackBar.setVisibility(View.VISIBLE);
        mIbtnVideosAutoPlayControl.setVisibility(View.VISIBLE);
    }

    private void controlLayoutHide(){
        mIsControlLayoutShow =false;
        mLlBackBar.setVisibility(View.GONE);
        mIbtnVideosAutoPlayControl.setVisibility(View.GONE);
    }


}
