package com.goertek.albumdemo.main;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.adapter.SingleAlbumAdapter;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;
import com.goertek.albumdemo.util.AlbumUtils;
import com.goertek.albumdemo.util.GridSpacingItemDecoration;
import com.goertek.albumdemo.util.OnCheckChangeListener;
import com.goertek.albumdemo.util.OnItemClickListener;
import com.goertek.albumdemo.view.YesNoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SingleAlbumActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRvFolderAlbum;
    private RelativeLayout mRlDeleteBar;
    private ImageButton mIbtnCancel;
    private TextView mTvDeleteSize;
    private ImageButton mIbtnDelete;
    private ImageButton mIbtnSelectAll;
    private RelativeLayout mRlNoAlbumFiles;

    private SingleAlbumAdapter mSingleAlumAdapter;
    private List<AlbumFile> mAlbumFiles = new ArrayList<>();
    private AlbumFolder mAlbumFolder;
    private boolean mIsSelectAll = false;
    private List<AlbumFile> mCheckedAlbumFiles = new ArrayList<>();

    @Subscribe
    public void onRefresh(AlbumFolder albumFolder) {
        mAlbumFolder = albumFolder;
        mAlbumFiles.clear();
        mAlbumFiles.addAll(mAlbumFolder.getAlbumFiles());

        if(mAlbumFiles.size() > 0){
            mRvFolderAlbum.setVisibility(View.VISIBLE);
            mRlNoAlbumFiles.setVisibility(View.GONE);
            mSingleAlumAdapter.notifyDataSetChanged();
        }else{
            mRvFolderAlbum.setVisibility(View.GONE);
            mRlNoAlbumFiles.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_album);
        initView();
        initListener();
        mAlbumFolder = getIntent().getParcelableExtra(Album.KEY_SINGLE_ALBUM_FOLDER);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mAlbumFolder.getName());
        }
        mAlbumFiles.addAll(mAlbumFolder.getAlbumFiles());
        if(mAlbumFiles.size() > 0){
            mRvFolderAlbum.setVisibility(View.VISIBLE);
            mRlNoAlbumFiles.setVisibility(View.GONE);
            mSingleAlumAdapter.notifyDataSetChanged();
        }else{
            mRvFolderAlbum.setVisibility(View.GONE);
            mRlNoAlbumFiles.setVisibility(View.VISIBLE);
        }

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
                default:
        }
        return true;
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            //解决Android5.0以上，状态栏设置颜色后变灰的问题
            Window window =getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        mRlDeleteBar = (RelativeLayout)findViewById(R.id.rl_delete_bar);
        mIbtnCancel = (ImageButton)findViewById(R.id.ibtn_cancel);
        mIbtnDelete = (ImageButton)findViewById(R.id.ibtn_delete);
        mIbtnSelectAll = (ImageButton)findViewById(R.id.ibtn_select_all);
        mTvDeleteSize = (TextView)findViewById(R.id.tv_selected_size);
        mRlNoAlbumFiles = (RelativeLayout)findViewById(R.id.rl_no_album_file);

        mRvFolderAlbum = (RecyclerView) findViewById(R.id.rv_folder_album);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        int spanCount = 4; // 4 columns
        int spacing = 15; // 15px
        boolean includeEdge = false;
        mRvFolderAlbum.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mRvFolderAlbum.setLayoutManager(gridLayoutManager);
        mSingleAlumAdapter = new SingleAlbumAdapter(SingleAlbumActivity.this,mAlbumFiles);

        mRvFolderAlbum.setAdapter(mSingleAlumAdapter);
    }

    private void initListener(){
        mSingleAlumAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlbumFile albumFile = new AlbumFile();
                albumFile = mAlbumFiles.get(position);
                Intent intent = new Intent(SingleAlbumActivity.this,MediaPreviewActivity.class);
                intent.putExtra(Album.KEY_SINGLE_ALBUM_FOLDER,mAlbumFolder);
              //  intent.putParcelableArrayListExtra(Album.KEY_AlBUM_FILES, (ArrayList<? extends Parcelable>) mAlbumFiles);
                intent.putExtra(Album.KEY_GALLERY_CURRENT_POSITION,position);
                startActivity(intent);
                /*Intent intent = new Intent(SingleAlbumActivity.this,GalleryActivity.class);
                intent.putExtra(Album.KEY_SINGLE_ALBUM_FOLDER,mAlbumFolder);
                intent.putExtra(Album.KEY_GALLERY_CURRENT_POSITION,position);
                startActivity(intent);*/
            }

            @Override
            public void onItemLongClick(View view, int position) {
                mSingleAlumAdapter.setMode(true);
                mSingleAlumAdapter.notifyItemRangeChanged(0,mAlbumFiles.size()-1);//通知 RecyclerView 所有的 item 状态发生变化
                mToolbar.setVisibility(View.GONE);
                mRlDeleteBar.setVisibility(View.VISIBLE);
                mTvDeleteSize.setText("select "+getSelectedFileNum(mAlbumFiles)+" item");
            }
        });

        mSingleAlumAdapter.setCheckChangeListener(new OnCheckChangeListener() {
            @Override
            public void onCheckChanged() {
                mTvDeleteSize.setText("select "+getSelectedFileNum(mAlbumFiles)+" item");
            }
        });

        mIbtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mToolbar.setVisibility(View.VISIBLE);
                mRlDeleteBar.setVisibility(View.GONE);
                mSingleAlumAdapter.setMode(false);
                mSingleAlumAdapter.notifyItemRangeChanged(0,mAlbumFiles.size()-1);
            }
        });

        mIbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(AlbumFile albumFile :mAlbumFiles){
                    if(albumFile.isChecked()){
                        mCheckedAlbumFiles.add(albumFile);
                    }
                }
                final YesNoDialog yesNoDialog;
                if(mCheckedAlbumFiles.size()>1){
                    yesNoDialog = new YesNoDialog(SingleAlbumActivity.this,YesNoDialog.DIALOG_DELETE_ITEMS);
                }else{
                    yesNoDialog = new YesNoDialog(SingleAlbumActivity.this,YesNoDialog.DIALOG_DELETE_ITEM);
                }
                yesNoDialog.setOnYesNoDialogBtnClickListener(new YesNoDialog.OnYesNoDialogBtnClickListener() {
                    @Override
                    public void onDialogBtnCancel(View view) {
                        yesNoDialog.dismiss();
                    }

                    @Override
                    public void onDialogBtnConfirm(View view) {
                        yesNoDialog.dismiss();
                        AlbumUtils.deleteMedias(SingleAlbumActivity.this,mCheckedAlbumFiles);
                        mAlbumFiles.removeAll(mCheckedAlbumFiles);
                        mSingleAlumAdapter.notifyDataSetChanged();
                        mAlbumFolder.setAlbumFiles((ArrayList<AlbumFile>) mAlbumFiles);
                        setDeleteBarGone();
                        EventBus.getDefault().post("DELECT_ALBUM");
                    }
                });
                yesNoDialog.show();
            }
        });

        mIbtnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsSelectAll){
                    mIsSelectAll = false;
                    for(AlbumFile albumFile :mAlbumFiles){
                        albumFile.setChecked(false);
                    }
                    mTvDeleteSize.setText("select "+getSelectedFileNum(mAlbumFiles)+" item");
                    mSingleAlumAdapter.notifyDataSetChanged();

                }else{
                    //所有条目全选
                    mIsSelectAll = true;
                    for(AlbumFile albumFile :mAlbumFiles){
                        albumFile.setChecked(true);
                    }
                    mTvDeleteSize.setText("select "+getSelectedFileNum(mAlbumFiles)+" item");
                    mSingleAlumAdapter.notifyDataSetChanged();

                }

            }
        });
    }

    /**
     * 计算选中的个数
     * @param albumFiles
     * @return
     */
    private int getSelectedFileNum(List<AlbumFile> albumFiles){
        int selectNum =0;
        if(albumFiles.size()== 0){
            selectNum = 0;
        }else{
            for(AlbumFile albumFile :albumFiles){
                if(albumFile.isChecked()){
                    selectNum++;
                }
            }
        }
        return selectNum;
    }

    private void setDeleteBarGone(){
        mToolbar.setVisibility(View.VISIBLE);
        mRlDeleteBar.setVisibility(View.GONE);
        mSingleAlumAdapter.setMode(false);
        mSingleAlumAdapter.notifyItemRangeChanged(0,mAlbumFiles.size()-1);
    }



}
