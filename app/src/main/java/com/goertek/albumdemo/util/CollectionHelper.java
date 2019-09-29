package com.goertek.albumdemo.util;


import com.goertek.albumdemo.model.AlbumFile;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 *
 * Created by clara.tong on 2019/9/5
 */
public class CollectionHelper {
    /**
     * 获取所有收藏的图片
     * @return
     */
    public static List<AlbumFile> getAllCollectedPhotos(){
        List<AlbumFile> albumFileList = DataSupport.where("mMediaType = ?","1").find(AlbumFile.class);
        return albumFileList;
    }

    /**
     * 获取所有收藏的相册
     * @return
     */
    public static List<AlbumFile> getAllCollectedVideos(){
        List<AlbumFile> albumFileList = DataSupport.where("mMediaType = ?","2").find(AlbumFile.class);
        return albumFileList;
    }

    /**
     * 收藏
     * @param albumFile
     */
    public static void collect(AlbumFile albumFile){
        albumFile.setCollected(true);
        albumFile.save();
    }

    /**
     * 取消收藏
     * @param albumFile
     */
    public static void cancelCollected(AlbumFile albumFile){
        albumFile.setCollected(false);
        String path = albumFile.getPath();
        DataSupport.deleteAll(AlbumFile.class,"mPath = ?",path);
    }
}
