package com.goertek.albumdemo.main;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.contants.MsgIdConstants;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;
import com.goertek.albumdemo.util.AlbumUtils;
import com.goertek.albumdemo.util.CollectionHelper;
import com.goertek.albumdemo.util.TimeTransferUtil;
import com.goertek.albumdemo.view.YesNoDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class MediaPreviewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener{

    private static final String TAG = "MediaPreviewActivity";
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;

    private RelativeLayout mRlMainContent;
    private ImageView mIvImageShow;
    private TextureView mSvVideo;
    private ImageView mIbtnPlayControl;
    private ImageButton mIbtnControlVideo;
    private LinearLayout mLlVideoProgressBar;
    private TextView mTvVideoLiveTime;
    private SeekBar mSbVideo;
    private TextView mTvVideoTotalTime;
    private LinearLayout mLlBackBar;
    private ImageButton mBtnBack;
    private LinearLayout mLlDeleteControl;
    private ImageButton mIbtnCollect;
    private ImageButton mIbtnAutoPlay;
    private ImageButton mIbtnDelete;
    private RelativeLayout mRlNoAlbumFiles;


    private HideControl mHideControl;
    private boolean isControlLayoutShow = false;

    private AlbumFolder mAlbumFolder;
    private int mCurrentPosition;
    private AlbumFile mAlbumFile;
    private Surface surface;
    private MediaPlayer mediaPlayer;
    private GestureDetector mGestureDetector;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME:
                    updateTime();
                    mHandler.sendEmptyMessageDelayed(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME, 500);
                    break;
                case MsgIdConstants.MediaPreviewMsgId.BASE_MSG_START_PLAY:
                    loadMedia(mAlbumFile.getPath());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);               //全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_preview);

        mAlbumFolder = getIntent().getParcelableExtra(Album.KEY_SINGLE_ALBUM_FOLDER);
        mCurrentPosition = getIntent().getIntExtra(Album.KEY_GALLERY_CURRENT_POSITION,0);
        mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
        initView();
        initListener();
        setImageShow(mAlbumFile);
        setIsCollectedShow(mAlbumFile);
        mHideControl = new HideControl();
    }

    private void initView(){
        mRlMainContent = (RelativeLayout)findViewById(R.id.rl_main_content);
        mIvImageShow = (ImageView) findViewById(R.id.iv_image_show);
        mIbtnPlayControl = (ImageView) findViewById(R.id.ibtn_play_control);
        mSvVideo = (TextureView) findViewById(R.id.sv_video_show);
        mSvVideo.setSurfaceTextureListener(this);

        mLlVideoProgressBar = (LinearLayout) findViewById(R.id.ll_video_progress_bar);
        mIbtnControlVideo = (ImageButton) findViewById(R.id.ibtn_video_play_control);
        mTvVideoLiveTime = (TextView) findViewById(R.id.tv_video_live_time);
        mSbVideo = (SeekBar) findViewById(R.id.sb_control_video);
        mTvVideoTotalTime = (TextView) findViewById(R.id.tv_video_total_time);

        mLlBackBar = (LinearLayout)findViewById(R.id.ll_back_bar);
        mBtnBack = (ImageButton)findViewById(R.id.ibtn_back);
        mLlDeleteControl = (LinearLayout)findViewById(R.id.ll_delete_control);

        mIbtnCollect = (ImageButton)findViewById(R.id.ibtn_collect);
        mIbtnAutoPlay = (ImageButton)findViewById(R.id.ibtn_auto_play);
        mIbtnDelete = (ImageButton)findViewById(R.id.ibtn_delete_albumfile);

        mRlNoAlbumFiles = (RelativeLayout)findViewById(R.id.rl_no_album_file);
    }

    private void initListener(){
        //刚进入时控制播放的按钮
        mIbtnPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    mSvVideo.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_START_PLAY, 100);
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_pause);
                    mIbtnPlayControl.setVisibility(View.GONE);
                  //  mLlVideoProgressBar.setVisibility(View.GONE);
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_play);
                    mIbtnPlayControl.setVisibility(View.VISIBLE);
                    mLlVideoProgressBar.setVisibility(View.VISIBLE);
                }else{
                    mSvVideo.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_pause);
                    mIbtnPlayControl.setVisibility(View.GONE);
                 //   mLlVideoProgressBar.setVisibility(View.GONE);
                }
            }
        });

        //进度条上的控制播放按钮
        mIbtnControlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer == null) {
                    mSvVideo.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessageDelayed(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_START_PLAY, 100);
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_pause);
                    mIbtnPlayControl.setVisibility(View.GONE);
                 //   mLlVideoProgressBar.setVisibility(View.GONE);
                } else if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_play);
                    mIbtnPlayControl.setVisibility(View.GONE);
                    mLlVideoProgressBar.setVisibility(View.VISIBLE);
                }else{
                    mSvVideo.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                    mIbtnControlVideo.setImageResource(R.mipmap.icon_pause);
                    mIbtnPlayControl.setVisibility(View.GONE);
                  //  mLlVideoProgressBar.setVisibility(View.GONE);
                }
            }
        });

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIbtnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAlbumFile.isCollected()){
                    CollectionHelper.cancelCollected(mAlbumFile);
                    mAlbumFile.setCollected(false);
                }else{
                    CollectionHelper.collect(mAlbumFile);
                    mAlbumFile.setCollected(true);
                }
                setIsCollectedShow(mAlbumFile);
                mAlbumFolder.changeAlbumFiles(mAlbumFile,mCurrentPosition);
                EventBus.getDefault().post(mAlbumFolder);
            }
        });

        mIbtnAutoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAlbumFile.getMediaType()== AlbumFile.TYPE_IMAGE) {
                    //打开photos幻灯片播放
                    Intent intent = new Intent(MediaPreviewActivity.this,GalleryPhotosActivity.class);
                    intent.putExtra(Album.KEY_SINGLE_ALBUM_FOLDER,mAlbumFolder);
                    intent.putExtra(Album.KEY_GALLERY_CURRENT_POSITION,mCurrentPosition);
                    startActivity(intent);
                } else {
                    //打开Videos幻灯片播放
                    Intent intent = new Intent(MediaPreviewActivity.this,GalleryVideosActivity.class);
                    intent.putExtra(Album.KEY_SINGLE_ALBUM_FOLDER,mAlbumFolder);
                    intent.putExtra(Album.KEY_GALLERY_CURRENT_POSITION,mCurrentPosition);
                    startActivity(intent);
                }
            }
        });

        mIbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDeleteConfirmDialog();
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX()-e2.getX() > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    // Fling left
                  //  Toast.makeText(MediaPreviewActivity.this, "向左手势", Toast.LENGTH_SHORT).show();
                    if(mediaPlayer !=null){
                        releaseMediaPlayer();
                        mHandler.removeMessages(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME);
                        mSvVideo.setVisibility(View.GONE);
                        mLlVideoProgressBar.setVisibility(View.GONE);
                    }
                    //下一个图片/视频
                    if(mCurrentPosition != mAlbumFolder.getAlbumFiles().size()-1){
                        mCurrentPosition++;
                    }else{
                        mCurrentPosition = 0;
                    }
                    mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
                    setImageShow(mAlbumFile);
                    setIsCollectedShow(mAlbumFile);
                } else if (e2.getX()-e1.getX() > FLING_MIN_DISTANCE
                        && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                    // Fling right
                   // Toast.makeText(MediaPreviewActivity.this, "向右手势", Toast.LENGTH_SHORT).show();
                    if(mediaPlayer !=null){
                        releaseMediaPlayer();
                        mHandler.removeMessages(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME);
                        mSvVideo.setVisibility(View.GONE);
                        mLlVideoProgressBar.setVisibility(View.GONE);
                    }
                    //前一个图片/视频
                    if(mCurrentPosition != 0){
                        mCurrentPosition--;
                    }else{
                        mCurrentPosition = mAlbumFolder.getAlbumFiles().size()-1;
                    }
                    mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
                    setImageShow(mAlbumFile);
                    setIsCollectedShow(mAlbumFile);
                }
                return false;
            }
        });

        mSbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {                 //点击界面所有控制控件显示,3s后无操作隐藏
        mHideControl.startHideTimer();
        controlLayoutShow();
        return mGestureDetector.onTouchEvent(event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        mHideControl.endHideTimer();
    }

    /**
     * 设置图片显示
     *
     */
    private void setImageShow(AlbumFile albumFile) {
        if (albumFile.getMediaType()== AlbumFile.TYPE_IMAGE) {
            Glide.with(this).load(albumFile.getPath()).into(mIvImageShow);
            if (mIbtnPlayControl.getVisibility() == View.VISIBLE) {
                mIbtnPlayControl.setVisibility(View.GONE);
            }
        } else {
            Glide.with(this).load(albumFile.getPath()).into(mIvImageShow);
            if (mIbtnPlayControl.getVisibility() == View.GONE) {
                mIbtnPlayControl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setIsCollectedShow(AlbumFile albumFile){
        if(albumFile.isCollected()){
            mIbtnCollect.setImageResource(R.mipmap.preview_collected);
        }else{
           mIbtnCollect.setImageResource(R.mipmap.preview_uncollect);
        };
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

    private void updateTime() {
        if (mediaPlayer != null) {
            mTvVideoLiveTime.setText(TimeTransferUtil.getTimeStrByMin(mediaPlayer.getCurrentPosition()));
            mSbVideo.setProgress(mediaPlayer.getCurrentPosition());
        }
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

    /*-------------------------------mediaPlayer监听------------------------------------------------*/

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mHandler.removeMessages(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME);
        mSvVideo.setVisibility(View.GONE);
        mLlVideoProgressBar.setVisibility(View.GONE);
        releaseMediaPlayer();
        // mRlImageShow.setVisibility(View.VISIBLE);
        setImageShow(mAlbumFile);
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
        mTvVideoLiveTime.setText(TimeTransferUtil.getTimeStrByMin(mediaPlayer
                .getCurrentPosition()));
        mTvVideoTotalTime.setText(TimeTransferUtil.getTimeStrByMin(mediaPlayer
                .getDuration()));
        mSbVideo.setMax(mediaPlayer.getDuration());
        mSbVideo.setProgress(mediaPlayer.getCurrentPosition());
        mediaPlayer.start();
        mHandler.sendEmptyMessageDelayed(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_UPDATE_TIME, 500);
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setSurface(null);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    /*-------------------控制控件自动隐藏---------------------------------------------*/

    public class HideControl {


        private HideHandler mHideHandler;

        public HideControl() {
            mHideHandler = new HideHandler();
        }

        public class HideHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MsgIdConstants.MediaPreviewMsgId.BASE_MSG_HIDE_CONTROL_LAYOUT:
                        controlLayoutHide();  //控制相关布局隐藏
                        break;
                }

            }
        }

        private Runnable hideRunable = new Runnable() {

            @Override
            public void run() {
                mHideHandler.obtainMessage(MsgIdConstants.MediaPreviewMsgId.BASE_MSG_HIDE_CONTROL_LAYOUT).sendToTarget();
            }
        };

        public void startHideTimer() {//开始计时,三秒后执行runable
            mHideHandler.removeCallbacks(hideRunable);
            mHideHandler.postDelayed(hideRunable, 3000);
        }

        public void endHideTimer() {//移除runable,将不再计时
            mHideHandler.removeCallbacks(hideRunable);
        }

//        public void resetHideTimer() {//重置计时
//            mHideHandler.removeCallbacks(hideRunable);
//            mHideHandler.postDelayed(hideRunable, 3000);
//        }

    }

    private void controlLayoutHide(){
        mLlBackBar.setVisibility(View.GONE);
        mLlDeleteControl.setVisibility(View.GONE);
        mLlVideoProgressBar.setVisibility(View.GONE);
        isControlLayoutShow = false;
    }

    private void controlLayoutShow(){
        mLlBackBar.setVisibility(View.VISIBLE);
        mLlDeleteControl.setVisibility(View.VISIBLE);
        if (mAlbumFile.getMediaType()== AlbumFile.TYPE_IMAGE){
            mLlVideoProgressBar.setVisibility(View.GONE);
        }else{
            if(mediaPlayer!=null){
                mLlVideoProgressBar.setVisibility(View.VISIBLE);
            }else{
                mLlVideoProgressBar.setVisibility(View.GONE);
            }
        }
        isControlLayoutShow = true;
    }

    /**
     * 显示删除确认对话框
     */
    private void showDeleteConfirmDialog(){
        final YesNoDialog deleteConfirmDialog = new YesNoDialog(MediaPreviewActivity.this,YesNoDialog.DIALOG_DELETE_ITEM);
        deleteConfirmDialog.setOnYesNoDialogBtnClickListener(new YesNoDialog.OnYesNoDialogBtnClickListener() {
            @Override
            public void onDialogBtnCancel(View view) {
                deleteConfirmDialog.dismiss();
            }

            @Override
            public void onDialogBtnConfirm(View view) {
                deleteConfirmDialog.dismiss();
                AlbumUtils.deleteMedia(MediaPreviewActivity.this,mAlbumFile);
                if(mAlbumFile.isCollected()){
                    CollectionHelper.cancelCollected(mAlbumFile);
                }
                ArrayList<AlbumFile> currentAlbumFiles =  new ArrayList<>();
                currentAlbumFiles.addAll(mAlbumFolder.getAlbumFiles());
                currentAlbumFiles.remove(mAlbumFile);
                mAlbumFolder.setAlbumFiles(currentAlbumFiles);
                if(mCurrentPosition == currentAlbumFiles.size()){
                    mCurrentPosition = 0;
                }
                if(mAlbumFolder.getAlbumFiles().size()>0){
                    mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
                    setImageShow(mAlbumFile);
                }else{
                    mRlMainContent.setVisibility(View.GONE);
                    mRlNoAlbumFiles.setVisibility(View.VISIBLE);
                }

                EventBus.getDefault().post(mAlbumFolder);

            }
        });
        deleteConfirmDialog.show();

    }

}
