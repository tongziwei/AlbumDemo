package com.goertek.albumdemo.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goertek.albumdemo.R;

/**
 * Created by clara.tong on 2019/9/17
 */
public class YesNoDialog extends Dialog {
    public static final int DIALOG_DELETE_ITEMS = 0;
    public static final int DIALOG_DELETE_ITEM = 1;

    private TextView mTvTitle;
    private TextView mTvSubTitle;
    private Button mBtnDialogCancel;
    private Button mBtnDialogConfirm;
    private LinearLayout mLlDialog;
    private LinearLayout mAttentionDialog;
    private TextView mTvComfirm;

    private int mDialogType;
    private Context mContext;
    private OnYesNoDialogBtnClickListener mOnYesNoDialogBtnClickListener;

    public interface OnYesNoDialogBtnClickListener {
        void onDialogBtnCancel(View view);

        void onDialogBtnConfirm(View view);
    }

    public YesNoDialog(Context context) {
        super(context);
    }

    public YesNoDialog(@NonNull Context context, int mDialogType) {
        super(context);
        this.mContext = context;
        this.mDialogType = mDialogType;
        initView();
        initListener();
    }

    public void setOnYesNoDialogBtnClickListener(OnYesNoDialogBtnClickListener listener) {
        this.mOnYesNoDialogBtnClickListener = listener;
    }

    private void initView(){
       View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_yes_no,null);
       this.setContentView(view);
        //获取当前Activity所在的窗体
        Window dialogWindow = this.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.common_transparent)));
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //将属性设置给窗体
        dialogWindow.setAttributes(lp);

        mLlDialog = (LinearLayout) view.findViewById(R.id.ll_dialog);
        mTvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        mTvSubTitle = (TextView) view.findViewById(R.id.tv_dialog_sub_title);
        mTvComfirm = (TextView) view.findViewById(R.id.tv_confirm_no_attention);
        mAttentionDialog = (LinearLayout) view.findViewById(R.id.ll_dialog_attention);
        mBtnDialogCancel = (Button) view.findViewById(R.id.btn_dialog_reset_cancel_btn);
        mBtnDialogConfirm = (Button) view.findViewById(R.id.btn_dialog_reset_confirm_btn);

        switch (mDialogType){
            case DIALOG_DELETE_ITEMS:
                mLlDialog.setVisibility(View.GONE);
                mAttentionDialog.setVisibility(View.VISIBLE);
                mTvComfirm.setText(R.string.dialog_delete_items_title);
                mBtnDialogCancel.setText(R.string.dialog_cancel);
                mBtnDialogCancel.setTextColor(mContext.getResources().getColor(R.color.dialog_cancel_text_color));
                mBtnDialogConfirm.setText(R.string.dialog_delete);
                mBtnDialogConfirm.setTextColor(mContext.getResources().getColor(R.color.dialog_delete_text_color));
                break;
            case DIALOG_DELETE_ITEM:
                mLlDialog.setVisibility(View.GONE);
                mAttentionDialog.setVisibility(View.VISIBLE);
                mTvComfirm.setText(R.string.dialog_delete_item_title);
                mBtnDialogCancel.setText(R.string.dialog_cancel);
                mBtnDialogCancel.setTextColor(mContext.getResources().getColor(R.color.dialog_cancel_text_color));
                mBtnDialogConfirm.setText(R.string.dialog_delete);
                mBtnDialogConfirm.setTextColor(mContext.getResources().getColor(R.color.dialog_delete_text_color));
                break;

            default:
                break;
        }

    }

    private void initListener() {
        mBtnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnYesNoDialogBtnClickListener.onDialogBtnCancel(view);
            }
        });

        mBtnDialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnYesNoDialogBtnClickListener.onDialogBtnConfirm(view);

            }
        });
    }

}
