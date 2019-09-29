package com.goertek.albumdemo.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.BaseActivity;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.adapter.FolderAdapter;
import com.goertek.albumdemo.data.MediaReadTask;
import com.goertek.albumdemo.data.MediaReader;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;
import com.goertek.albumdemo.util.AlbumUtils;
import com.goertek.albumdemo.util.OnItemClickListener;
import com.goertek.albumdemo.util.RecycleViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements MediaReadTask.Callback{

    private static final int CODE_PERMISSION_STORAGE = 1;
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
    private static final String BASE_PATH = ROOT_PATH + "AlbumDemo"+File.separator;

    private Toolbar toolbar;
    private RecyclerView mRvAlbum;
    private FolderAdapter mFolderAdapter;

    private MediaReadTask mMediaReadTask;
    private List<AlbumFolder> mAlbumFolders = new ArrayList<>();
    private int mFunction;
    private  ArrayList<AlbumFile> checkedList =new ArrayList<>();

    private int mQuality = 1;
    private long mLimitDuration = Integer.MAX_VALUE;
    private long mLimitBytes = Integer.MAX_VALUE;

    @Subscribe
    public void onRefresh(String info){
        if(info.equals("DELECT_ALBUM")){
            MediaReader mediaReader = new MediaReader(this);
            mMediaReadTask = new MediaReadTask(mFunction, checkedList, mediaReader, this);
            mMediaReadTask.execute();
        }
    }

    @Subscribe
    public void onRefresh(AlbumFolder albumFolder) {
        MediaReader mediaReader = new MediaReader(this);
        mMediaReadTask = new MediaReadTask(mFunction, checkedList, mediaReader, this);
        mMediaReadTask.execute();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mFunction = Album.FUNCTION_CHOICE_ALBUM;
        requestPermission(PERMISSION_STORAGE, CODE_PERMISSION_STORAGE);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.take_picture:
                File baseFile = new File(BASE_PATH);
                if(!baseFile.exists()){
                    baseFile.mkdir();
                }

                String filePath = BASE_PATH + AlbumUtils.getNowDateTime("yyyyMMddHHmmssSSS")+".jpg";
                Intent intent = new Intent(MainActivity.this,CameraActivity.class);
                intent.putExtra(Album.KEY_INPUT_FUNCTION,Album.FUNCTION_CAMERA_IMAGE);
                intent.putExtra(Album.KEY_INPUT_FILE_PATH,filePath);
                startActivity(intent);
                break;
            case R.id.record:
                File baseFile2 = new File(BASE_PATH);
                if(!baseFile2.exists()){
                    baseFile2.mkdir();
                }
                String videofilePath = BASE_PATH + AlbumUtils.getNowDateTime("yyyyMMddHHmmssSSS")+".mp4";
                Intent videoIntent = new Intent(MainActivity.this,CameraActivity.class);
                videoIntent.putExtra(Album.KEY_INPUT_FUNCTION,Album.FUNCTION_CAMERA_VIDEO);
                videoIntent.putExtra(Album.KEY_INPUT_FILE_PATH,videofilePath);
                videoIntent.putExtra(Album.KEY_INPUT_CAMERA_QUALITY,mQuality);
                videoIntent.putExtra(Album.KEY_INPUT_CAMERA_DURATION,mLimitDuration);
                videoIntent.putExtra(Album.KEY_INPUT_CAMERA_BYTES,mLimitBytes);
                startActivity(videoIntent);
                break;
            default:
        }
        return true;
    }

    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            //解决Android5.0以上，状态栏设置颜色后变灰的问题
            Window window =getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        mRvAlbum = (RecyclerView)findViewById(R.id.rv_album);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvAlbum.setLayoutManager(linearLayoutManager);
        mRvAlbum.addItemDecoration(new RecycleViewDivider(this,LinearLayoutManager.HORIZONTAL));
        mFolderAdapter = new FolderAdapter(mAlbumFolders,this);
        mRvAlbum.setAdapter(mFolderAdapter);
        mFolderAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlbumFolder albumFolder = mAlbumFolders.get(position);
                Intent singleAlbumIntent = new Intent(MainActivity.this,SingleAlbumActivity.class);
                singleAlbumIntent.putExtra(Album.KEY_SINGLE_ALBUM_FOLDER,albumFolder);
                startActivity(singleAlbumIntent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    @Override
    protected void onPermissionGranted(int code) {
        super.onPermissionGranted(code);
        MediaReader mediaReader = new MediaReader(this);
        mMediaReadTask = new MediaReadTask(mFunction, checkedList, mediaReader, this);
        mMediaReadTask.execute();
    }

    @Override
    protected void onPermissionDenied(int code) {
        super.onPermissionDenied(code);
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.album_title_permission_failed)
                .setMessage(R.string.album_permission_storage_failed_hint)
                .setPositiveButton(R.string.album_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();

    }

    @Override
    public void onScanCallback(ArrayList<AlbumFolder> albumFolders, ArrayList<AlbumFile> checkedFiles) {
         mAlbumFolders.clear();
         mAlbumFolders.addAll(albumFolders);
         mFolderAdapter.notifyDataSetChanged();
    }
}
