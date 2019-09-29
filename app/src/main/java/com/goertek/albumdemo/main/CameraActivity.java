package com.goertek.albumdemo.main;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.BaseActivity;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.util.AlbumUtils;

import java.io.File;

public class CameraActivity extends BaseActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "CameraActivity";
    private static final int CODE_PERMISSION_IMAGE = 1;
    private static final int CODE_PERMISSION_VIDEO = 2;

    private static final int CODE_ACTIVITY_TAKE_IMAGE = 1;
    private static final int CODE_ACTIVITY_TAKE_VIDEO = 2;

    private ImageView mIvImageShow;
    private TextureView mSvVideo;
    private ImageView mIbtnPlayControl;
    private ImageButton mIbtnControlVideo;
    private LinearLayout mLlVideoProgressBar;
    private TextView mTvVideoLiveTime;
    private SeekBar mSbVideo;
    private TextView mTvVideoTotalTime;


    private int mFunction;
    private String mCameraFilePath;
    private int mQuality;
    private long mLimitDuration;
    private long mLimitBytes;

    private Surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initView();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mFunction = bundle.getInt(Album.KEY_INPUT_FUNCTION);
        mCameraFilePath = bundle.getString(Album.KEY_INPUT_FILE_PATH);
        mQuality = bundle.getInt(Album.KEY_INPUT_CAMERA_QUALITY);
        mLimitDuration = bundle.getLong(Album.KEY_INPUT_CAMERA_DURATION);
        mLimitBytes = bundle.getLong(Album.KEY_INPUT_CAMERA_BYTES);

        switch (mFunction) {
            case Album.FUNCTION_CAMERA_IMAGE: {
                if (TextUtils.isEmpty(mCameraFilePath)){
                    mCameraFilePath = AlbumUtils.randomJPGPath(this);
                }
                requestPermission(PERMISSION_TAKE_PICTURE, CODE_PERMISSION_IMAGE);
                break;
            }
            case Album.FUNCTION_CAMERA_VIDEO: {
                if (TextUtils.isEmpty(mCameraFilePath)){
                    mCameraFilePath = AlbumUtils.randomMP4Path(this);
                }

                requestPermission(PERMISSION_TAKE_VIDEO, CODE_PERMISSION_VIDEO);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    protected void onPermissionGranted(int code) {
        switch (code) {
            case CODE_PERMISSION_IMAGE: {
                AlbumUtils.takeImage(this, CODE_ACTIVITY_TAKE_IMAGE, new File(mCameraFilePath));
                break;
            }
            case CODE_PERMISSION_VIDEO: {
                AlbumUtils.takeVideo(this, CODE_ACTIVITY_TAKE_VIDEO, new File(mCameraFilePath), mQuality, mLimitDuration, mLimitBytes);
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    protected void onPermissionDenied(int code) {
        super.onPermissionDenied(code);
        Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_ACTIVITY_TAKE_IMAGE:
                if(resultCode== RESULT_OK){
                    Log.d(TAG, "onActivityResult: filePath: "+ mCameraFilePath);
                    Glide.with(this).load(mCameraFilePath).into(mIvImageShow);
                }
               break;
            case CODE_ACTIVITY_TAKE_VIDEO: {
                if(resultCode== RESULT_OK){
                    Log.d(TAG, "onActivityResult: filePath: "+ mCameraFilePath);
                    Glide.with(this).load(mCameraFilePath).into(mIvImageShow);
                }
                break;
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    private void initView(){
        mIvImageShow = (ImageView) findViewById(R.id.iv_image_show);
        mIbtnPlayControl = (ImageView) findViewById(R.id.ibtn_play_control);
        mSvVideo = (TextureView) findViewById(R.id.sv_video_show);
        mSvVideo.setSurfaceTextureListener(this);

        mLlVideoProgressBar = (LinearLayout) findViewById(R.id.ll_video_progress_bar);
        mIbtnControlVideo = (ImageButton) findViewById(R.id.ibtn_video_play_control);
        mTvVideoLiveTime = (TextView) findViewById(R.id.tv_video_live_time);
        mSbVideo = (SeekBar) findViewById(R.id.sb_control_video);
        mTvVideoTotalTime = (TextView) findViewById(R.id.tv_video_total_time);
    }

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
}
