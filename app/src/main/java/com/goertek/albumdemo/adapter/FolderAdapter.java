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
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.goertek.albumdemo.R;
import com.goertek.albumdemo.model.AlbumFile;
import com.goertek.albumdemo.model.AlbumFolder;
import com.goertek.albumdemo.util.OnItemClickListener;

import java.util.List;

/**
 * <p>BottomSheet dialog adapter, show all folder.</p>
 * Created by Yan Zhenjie on 2016/10/18.
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<AlbumFolder> mAlbumFolders;
  //  private ColorStateList mSelector;
    private Context mContext;

    private OnItemClickListener mItemClickListener;

   /* public FolderAdapter(Context context, List<AlbumFolder> mAlbumFolders, ColorStateList buttonTint) {
        this.mInflater = LayoutInflater.from(context);
        this.mSelector = buttonTint;
        this.mAlbumFolders = mAlbumFolders;
    }*/

    public FolderAdapter(List<AlbumFolder> mAlbumFolders, Context mContext) {
        this.mAlbumFolders = mAlbumFolders;
        this.mContext = mContext;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_folder_item, parent, false);
        return new FolderViewHolder(view,
                new OnItemClickListener() {

                    private int oldPosition = 0;

                    @Override
                    public void onItemClick(View view, int position) {
                        if (mItemClickListener != null)
                            mItemClickListener.onItemClick(view, position);

                        AlbumFolder albumFolder = mAlbumFolders.get(position);
                        //进入预览界面
                       /* if (!albumFolder.isChecked()) {
                            albumFolder.setChecked(true);
                            mAlbumFolders.get(oldPosition).setChecked(false);
                            notifyItemChanged(oldPosition);
                            notifyItemChanged(position);
                            oldPosition = position;
                        }*/
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        final int newPosition = holder.getAdapterPosition();
      //  holder.setData(mAlbumFolders.get(newPosition));
        AlbumFolder albumFolder = mAlbumFolders.get(newPosition);
        List<AlbumFile> albumFiles = albumFolder.getAlbumFiles();
        holder.mTvTitle.setText("(" + albumFiles.size() + ") " + albumFolder.getName());
        Glide.with(mContext).load(albumFiles.get(0).getPath()).into(holder.mIvImage);
    }

    @Override
    public int getItemCount() {
        return mAlbumFolders == null ? 0 : mAlbumFolders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener mItemClickListener;

        private ImageView mIvImage;
        private TextView mTvTitle;
        private ImageButton mIbtnEnter;

        public FolderViewHolder(@NonNull View itemView,OnItemClickListener itemClickListener) {
            super(itemView);
            this.mItemClickListener = itemClickListener;

            mIvImage = itemView.findViewById(R.id.iv_gallery_preview_image);
            mTvTitle = itemView.findViewById(R.id.tv_gallery_preview_title);
            mIbtnEnter = itemView.findViewById(R.id.ibtn_gallery_preview_enter);

            itemView.setOnClickListener(this);

            mIbtnEnter.setOnClickListener(this);
        }

        private FolderViewHolder(View itemView, ColorStateList selector, OnItemClickListener itemClickListener) {
            super(itemView);

            this.mItemClickListener = itemClickListener;

            mIvImage = itemView.findViewById(R.id.iv_gallery_preview_image);
            mTvTitle = itemView.findViewById(R.id.tv_gallery_preview_title);
            mIbtnEnter = itemView.findViewById(R.id.ibtn_gallery_preview_enter);

            itemView.setOnClickListener(this);

            mIbtnEnter.setOnClickListener(this);
        }

        public void setData(AlbumFolder albumFolder) {
            List<AlbumFile> albumFiles = albumFolder.getAlbumFiles();
            mTvTitle.setText("(" + albumFiles.size() + ") " + albumFolder.getName());
           // mCheckBox.setChecked(albumFolder.isChecked());

          //  Album.getAlbumConfig().getAlbumLoader().load(mIvImage, albumFiles.get(0));
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}