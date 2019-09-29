package com.goertek.albumdemo.contants;

/**
 * Created by clara.tong on 2019/9/10
 */
public interface MsgIdConstants {
    interface CommonMsgId {

        int BASE_MSG_ID = 10001000;
    }

    interface MediaPreviewMsgId{
        int BASE_MSG_ID = 20001000;
        int BASE_MSG_UPDATE_TIME = BASE_MSG_ID + 1;
        int BASE_MSG_START_PLAY = BASE_MSG_ID + 2;
        int BASE_MSG_HIDE_CONTROL_LAYOUT = BASE_MSG_ID + 3;
    }

    interface GalleryMsgId{
        int BASE_MSG_ID = 30001000;
        int BASE_MSG_UPDATE_PHOTO = BASE_MSG_ID + 1;
        int BASE_MSG_PHOTO_CONTROL_LAYOUT_HIDE = BASE_MSG_ID +2;
        int BASE_MSG_UPDATE_VIDEO = BASE_MSG_ID + 3;
        int BASE_MSG_VIDEO_CONTROL_LAYOUT_HIDE = BASE_MSG_ID +4;
        int BASE_MSG_VIDEO_START_PLAY = BASE_MSG_ID + 5;

    }


}
