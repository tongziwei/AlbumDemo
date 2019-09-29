/*
 * Copyright 2016 Yan Zhenjie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.goertek.albumdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.util.AlbumUtils;
import com.goertek.albumdemo.util.OnCheckChangeListener;
import com.goertek.albumdemo.util.OnItemClickListener;

import java.util.List;

/**
 * <p>Image adapter.</p>
 *
 */
public class SingleAlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mItemClickListener;
    private Context mContext;
    private boolean isShowEdit;
    private OnCheckChangeListener mCheckChangeListener;

    private List<AlbumFile> mAlbumFiles;

    public SingleAlbumAdapter(Context context, List<AlbumFile> albumFiles) {
        this.mContext = context;
        this.mAlbumFiles = albumFiles;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setMode(boolean isShowEdit){
        this.isShowEdit = isShowEdit;
        notifyDataSetChanged();
    }

    public void setCheckChangeListener(OnCheckChangeListener checkChangeListener){
        this.mCheckChangeListener = checkChangeListener;
    }


/*
    public void notifyDataSetChanged(List<AlbumFile> imagePathList) {
        this.mAlbumFiles = imagePathList;
        super.notifyDataSetChanged();
    }*/

    @Override
    public int getItemViewType(int position) {
        AlbumFile albumFile = mAlbumFiles.get(position);
        if (albumFile.getMediaType() == AlbumFile.TYPE_IMAGE) {
            return AlbumFile.TYPE_IMAGE;
        } else {
            return AlbumFile.TYPE_VIDEO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                View view =  LayoutInflater.from(mContext).inflate(R.layout.item_content_image, parent, false);
                return new ImageViewHolder(view, mItemClickListener);
            }
            case AlbumFile.TYPE_VIDEO: {
                View view =  LayoutInflater.from(mContext).inflate(R.layout.item_content_video, parent, false);
                return new VideoViewHolder(view, mItemClickListener);
            }
            default: {
                throw new AssertionError("This should not be the case.");
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case AlbumFile.TYPE_IMAGE: {
                final AlbumFile albumFile = mAlbumFiles.get(position);
                Glide.with(mContext).load(albumFile.getPath()).centerCrop().into(((ImageViewHolder) holder).mIvImage);
                //是否收藏
                if(albumFile.isCollected()){
                    ((ImageViewHolder) holder).mIvCollected.setVisibility(View.VISIBLE);
                }else{
                    ((ImageViewHolder) holder).mIvCollected.setVisibility(View.INVISIBLE);
                }

                //是否显示编辑框
                if(isShowEdit){
                    ((ImageViewHolder) holder).mCbImage.setVisibility(View.VISIBLE);
                }else{
                    ((ImageViewHolder) holder).mCbImage.setVisibility(View.GONE);
                }

                ((ImageViewHolder) holder).mCbImage.setOnCheckedChangeListener(null);
                if(albumFile.isChecked()){
                    ((ImageViewHolder) holder).mCbImage.setChecked(true);
                }else{
                    ((ImageViewHolder) holder).mCbImage.setChecked(false);
                }
                ((ImageViewHolder) holder).mCbImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            albumFile.setChecked(true);
                        }else{
                            albumFile.setChecked(false);
                        }
                        mCheckChangeListener.onCheckChanged();
                    }
                });

                break;
            }
            case AlbumFile.TYPE_VIDEO: {
                final AlbumFile albumFile = mAlbumFiles.get(position);
                Glide.with(mContext).load(albumFile.getPath()).centerCrop().into(((VideoViewHolder) holder).mIvImage);
                ((VideoViewHolder) holder).mTvDuration.setText(AlbumUtils.convertDuration(albumFile.getDuration()));

                if(albumFile.isCollected()){
                    ((VideoViewHolder) holder).mIvCollected.setVisibility(View.VISIBLE);
                }else{
                    ((VideoViewHolder) holder).mIvCollected.setVisibility(View.INVISIBLE);
                }

                if(isShowEdit){
                    ((VideoViewHolder) holder).mCbVideo.setVisibility(View.VISIBLE);
                }else{
                    ((VideoViewHolder) holder).mCbVideo.setVisibility(View.GONE);
                }
                ((VideoViewHolder) holder).mCbVideo.setOnCheckedChangeListener(null);
                if(albumFile.isChecked()){
                    ((VideoViewHolder) holder).mCbVideo.setChecked(true);
                }else{
                    ((VideoViewHolder) holder).mCbVideo.setChecked(false);
                }

                ((VideoViewHolder) holder).mCbVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            albumFile.setChecked(true);
                        }else{
                            albumFile.setChecked(false);
                        }
                        mCheckChangeListener.onCheckChanged();
                    }
                });

                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mAlbumFiles == null ? 0 : mAlbumFiles.size();
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        private final OnItemClickListener mItemClickListener;
        private ImageView mIvImage;
        private CheckBox mCbImage;
        private ImageView mIvCollected;

        ImageViewHolder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;
            this.mIvImage = itemView.findViewById(R.id.iv_album_content_image);
            this.mIvCollected = itemView.findViewById(R.id.iv_collected_show);
            this.mCbImage = itemView.findViewById(R.id.cb_image_select);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

    private static class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        private final OnItemClickListener mItemClickListener;

        private ImageView mIvImage;
        private TextView mTvDuration;
        private ImageView mIvCollected;
        private CheckBox mCbVideo;

        VideoViewHolder(View itemView, OnItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;
            this.mIvImage = itemView.findViewById(R.id.iv_album_content_image);
            this.mTvDuration = itemView.findViewById(R.id.tv_duration);
            this.mIvCollected = itemView.findViewById(R.id.iv_collected_show);
            this.mCbVideo = itemView.findViewById(R.id.cb_video_select);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

}
