package com.goertek.albumdemo.main;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.contants.MsgIdConstants;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;

public class GalleryPhotosActivity extends AppCompatActivity {


    private ImageView mIvPhotoShow;
    private LinearLayout mLlBackBar;
    private ImageButton mIbtnBack;
    private ImageButton mIbtnPhotosPlayControl;

    private AlbumFolder mAlbumFolder;
    private int mCurrentPosition;
    private AlbumFile mAlbumFile;
    private int mPhotosSize;
    private boolean mIsPlaying = true;
    private boolean mIsControlLayoutShow =false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO:
                    if(mCurrentPosition == mPhotosSize-1){
                        mCurrentPosition = 0;
                    }else{
                        mCurrentPosition++;
                    }
                    mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
                    Glide.with(GalleryPhotosActivity.this).load(mAlbumFile.getPath()).into(mIvPhotoShow);
                    mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO,3000);
                    break;
                case MsgIdConstants.GalleryMsgId.BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE:
                    controlLayoutHide();
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
        setContentView(R.layout.activity_gallery_photos);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO);
        mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsControlLayoutShow){
            mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE);
            mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE,3000);
        }else{
            controlLayoutShow();
            mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE,3000);
        }
        return true;
    }

    private void initView(){
        mIvPhotoShow = (ImageView)findViewById(R.id.iv_photo_show);
        mLlBackBar = (LinearLayout)findViewById(R.id.ll_back_bar);
        mIbtnBack = (ImageButton)findViewById(R.id.ibtn_back);
        mIbtnPhotosPlayControl = (ImageButton)findViewById(R.id.ibtn_photos_play_control);
    }

    private void initListener(){
        mIbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIbtnPhotosPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(mIsPlaying){
                   //暂停
                   mIsPlaying = false;
                   mHandler.removeMessages(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO);
                   mIbtnPhotosPlayControl.setImageResource(R.mipmap.icon_play);
               }else{
                   //播放
                   mIsPlaying = true;
                   mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO,3000);
                   mIbtnPhotosPlayControl.setImageResource(R.mipmap.icon_pause);
               }
            }
        });
    }

    private void initData(){
        mAlbumFolder = getIntent().getParcelableExtra(Album.KEY_SINGLE_ALBUM_FOLDER);
        mCurrentPosition = getIntent().getIntExtra(Album.KEY_GALLERY_CURRENT_POSITION,0);
        mAlbumFile = mAlbumFolder.getAlbumFiles().get(mCurrentPosition);
        mPhotosSize = mAlbumFolder.getAlbumFiles().size();
        Glide.with(this).load(mAlbumFile.getPath()).into(mIvPhotoShow);
        mHandler.sendEmptyMessageDelayed(MsgIdConstants.GalleryMsgId.BASE_MSG_UPDATE_PHOTO,3000);
    }

    private void controlLayoutShow(){
        mIsControlLayoutShow = true;
        mLlBackBar.setVisibility(View.VISIBLE);
        mIbtnPhotosPlayControl.setVisibility(View.VISIBLE);
    }

    private void controlLayoutHide(){
        mIsControlLayoutShow =false;
        mLlBackBar.setVisibility(View.GONE);
        mIbtnPhotosPlayControl.setVisibility(View.GONE);
    }


}
