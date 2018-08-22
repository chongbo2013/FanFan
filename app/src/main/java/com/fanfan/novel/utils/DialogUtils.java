package com.fanfan.novel.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.robot.R;
import com.seabreeze.log.Print;

/**
 * Created by zhangyuanyuan on 2017/11/14.
 */

public class DialogUtils {

    /**
     * basicNoTitle
     *
     * @param activity
     * @param content
     * @param left
     * @param right
     * @param listener
     */
    public static void showBasicNoTitleDialog(Activity activity, CharSequence content, CharSequence left, CharSequence right,
                                              final OnNiftyDialogListener listener) {
        new MaterialDialog.Builder(activity)
                .content(content)
                .negativeText(left).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.onClickLeft();
            }
        })
                .positiveText(right).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.onClickRight();
            }
        })
                .show();
    }

    /**
     * basic
     *
     * @param activity
     * @param title
     * @param content
     * @param left
     * @param right
     * @param listener
     */
    public static void showBasicDialog(Activity activity, CharSequence title, CharSequence content, CharSequence left, CharSequence right,
                                       final OnNiftyDialogListener listener) {
        new MaterialDialog.Builder(activity)
                .title(title)
                .content(content)
                .negativeText(left).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.onClickLeft();
            }
        })
                .positiveText(right).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.onClickRight();
            }
        })
                .show();
    }

    /**
     * @param activity
     * @param icon     R.mipmap.ic_logo
     * @param title
     * @param content
     * @param left
     * @param right
     * @param listener
     */
    public static MaterialDialog showBasicIconDialog(Activity activity, int icon, CharSequence title, CharSequence content,
                                                     CharSequence left, CharSequence right, final OnNiftyDialogListener listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .iconRes(icon)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(title)
                .content(content)
                .positiveText(left).onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onClickLeft();
                    }
                })
                .negativeText(right).onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        listener.onClickRight();
                    }
                });
        MaterialDialog dialog = builder.build();
        dialog.show();
        return dialog;
    }

    public static void showNeutralDialog(Context context, CharSequence title, CharSequence content,
                                         CharSequence neutralText, CharSequence negativeText, CharSequence positiveText,
                                         final OnNeutralDialogListener listener) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .neutralText(neutralText).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.neutralText();
            }
        })
                .negativeText(negativeText).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.negativeText();
            }
        })
                .positiveText(positiveText).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.positiveText();
            }
        })
                .show();
    }

    public static void showNeutralNotitleDialog(Context context, CharSequence content,
                                                CharSequence neutralText, CharSequence negativeText, CharSequence positiveText,
                                                final OnNeutralDialogListener listener) {
        new MaterialDialog.Builder(context)
                .content(content)
                .neutralText(neutralText).onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.neutralText();
            }
        })
                .negativeText(negativeText).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.negativeText();
            }
        })
                .positiveText(positiveText).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                listener.positiveText();
            }
        })
                .show();
    }

    public static void showLongListDialog(Context context, CharSequence title, int itemsRes, MaterialDialog.ListCallback callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .items(itemsRes)
                .itemsCallback(callback)
                .positiveText(android.R.string.cancel)
                .show();
    }

    public static void showLongListDialog(Context context, CharSequence title, int itemsRes,
                                          MaterialDialog.ListCallback callback, Integer... disabledIndices) {
        new MaterialDialog.Builder(context)
                .title(title)
                .items(itemsRes)
                .itemsCallback(callback)
                .itemsDisabledIndices(disabledIndices)
                .positiveText(android.R.string.cancel)
                .show();
    }

    public static void showMultiChoiceDisabledItems(Context context, CharSequence title, int itemsRes, MaterialDialog.ListCallbackSingleChoice callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .items(itemsRes)
                .itemsCallbackSingleChoice(2, callback)
                .positiveText("确定")
                .show();
    }

    public interface OnNiftyDialogListener {
        void onClickLeft();

        void onClickRight();
    }

    public interface OnNeutralDialogListener {
        void neutralText();

        void negativeText();

        void positiveText();
    }

}
