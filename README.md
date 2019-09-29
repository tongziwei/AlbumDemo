# AlbumDemo
一个相册App,加载系统内的所有图片、视频，实现图片视频的预览，删除，自动轮播功能，调用系统的相机拍照、录像
1、系统内所有照片、视频的读取
  MeadiaRead 类
  通过ContentResolver类读取
     ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGES,
                null,
                null,
                null);
                
2、调用系统相机拍照录像 AlbumUtils，具体调用见CameraActivity
  /**
     * Take picture.
     *
     * @param activity    activity.
     * @param requestCode code, see {@link Activity#onActivityResult(int, int, Intent)}.
     * @param outPath     file path.
     */
    public static void takeImage(@NonNull Activity activity, int requestCode, File outPath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getUri(activity, outPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }
    
    /**
     * Take video.
     *
     * @param activity    activity.
     * @param requestCode code, see {@link Activity#onActivityResult(int, int, Intent)}.
     * @param outPath     file path.
     * @param quality     currently value 0 means low quality, suitable for MMS messages, and  value 1 means high quality.
     * @param duration    specify the maximum allowed recording duration in seconds.
     * @param limitBytes  specify the maximum allowed size.
     */
    public static void takeVideo(@NonNull Activity activity, int requestCode, File outPath,
                                 @IntRange(from = 0, to = 1) int quality,
                                 @IntRange(from = 1) long duration,
                                 @IntRange(from = 1) long limitBytes) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri uri = getUri(activity, outPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limitBytes);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, requestCode);
    }
    
  3、图片、视频的收藏，主要是将图片、视频文件添加的本地数据库当中，取消收藏即从数据库中删除，操作调用见CollectionHelper
    数据库使用 org.litepal.android:core:1.3.2 库，具体使用参考郭霖《第一行代码》6.5
