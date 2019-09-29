package com.goertek.albumdemo.main;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.goertek.albumdemo.Album;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;
import com.goertek.albumdemo.view.ShowImagesViewPager;

import java.util.ArrayList;
import java.util.List;



public class GalleryActivity extends AppCompatActivity {
    private ImageView mIvBack;
    private TextView mTvPage;
    private ShowImagesViewPager mVpGallery;

    private AlbumFolder mAlbumFolder;
    private int mCurrentPosition;
    private List<AlbumFile> mAlbumFiles;
    private List<PhotoView> mImageViewsList = new ArrayList<>();
    private ImagePageAdapter mImagePageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galllery);
        initView();
        initData();


    }

    private void initView(){
        mIvBack = (ImageView)findViewById(R.id.iv_photo_back);
        mTvPage = (TextView)findViewById(R.id.tv_photo_num);
        mVpGallery = (ShowImagesViewPager) findViewById(R.id.vp_gallery);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        mAlbumFolder = getIntent().getParcelableExtra(Album.KEY_SINGLE_ALBUM_FOLDER);
        mCurrentPosition = getIntent().getIntExtra(Album.KEY_GALLERY_CURRENT_POSITION,0);
        mAlbumFiles = mAlbumFolder.getAlbumFiles();
        mTvPage.setText(mCurrentPosition+ 1 + "/" + mAlbumFiles.size());

        for(int i= 0;i<mAlbumFiles.size();i++){
            final PhotoView photoView = new PhotoView(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(layoutParams);

            if(mAlbumFiles.get(i).getMediaType()== AlbumFile.TYPE_IMAGE){
                Glide.with(this).load(mAlbumFiles.get(i).getPath()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        photoView.setImageDrawable(resource);
                    }
                });//
            }else{
                Glide.with(this).load(mAlbumFiles.get(i).getPath()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        photoView.setImageDrawable(resource);
                    }
                });//
            }

            mImageViewsList.add(photoView);
        }

        mImagePageAdapter = new ImagePageAdapter();
        mVpGallery.setAdapter(mImagePageAdapter);
        mVpGallery.setCurrentItem(mCurrentPosition);
        mVpGallery.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTvPage.setText(i+ 1 + "/" + mAlbumFiles.size());
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    class ImagePageAdapter extends PagerAdapter{


        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mImageViewsList.get(position));
            return mImageViewsList.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                 container.removeView((View) object);  ;
        }

        @Override
        public int getCount() {
            return mImageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}
