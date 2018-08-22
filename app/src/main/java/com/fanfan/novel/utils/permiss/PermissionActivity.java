package com.fanfan.novel.utils.permiss;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.fanfan.robot.R;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Administrator on 2018/3/28/028.
 */

public class PermissionActivity extends AppCompatActivity {

    public static int PERMISSION_TYPE_SINGLE = 1;

    private static PermissionCallback mCallback;

    private int mPermissionType;
    private List<String> mCheckPermissions;

    private static final int REQUEST_CODE_SINGLE = 1;
    private static final int REQUEST_CODE_MUTI = 2;
    public static final int REQUEST_CODE_MUTI_SINGLE = 3;

    private static final int REQUEST_SETTING = 110;

    private CharSequence mAppName;
    //重新申请权限数组的索引
    private int mRePermissionIndex;


    public static void setCallBack(PermissionCallback callBack) {
        PermissionActivity.mCallback = callBack;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mPermissionType = intent.getIntExtra(ConstantValue.DATA_PERMISSION_TYPE, PERMISSION_TYPE_SINGLE);
        mCheckPermissions = (List<String>) intent.getSerializableExtra(ConstantValue.DATA_PERMISSIONS);

        mAppName = getApplicationInfo().loadLabel(getPackageManager());

        if (mPermissionType == PERMISSION_TYPE_SINGLE) {
            //单个权限申请
            if (mCheckPermissions == null || mCheckPermissions.size() == 0)
                return;

            requestPermission(new String[]{mCheckPermissions.get(0)}, REQUEST_CODE_SINGLE);
        } else {
//            mAppName = getApplicationInfo().loadLabel(getPackageManager());
            //多个权限
//            showPermissionDialog();
            String[] strs = getPermissionStrArray();
            requestPermission(strs, REQUEST_CODE_MUTI);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCallback = null;
    }

    private void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(PermissionActivity.this, permissions, requestCode);
    }

    private String[] getPermissionStrArray() {
        String[] str = new String[mCheckPermissions.size()];
        for (int i = 0; i < mCheckPermissions.size(); i++) {
            str[i] = mCheckPermissions.get(i);
        }
        return str;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_SINGLE:
                String permission = getPermission(permissions[0]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onGuarantee(permission, 0);
                } else {
                    onDeny(permission, 0);
                }
                finish();
                break;
            case REQUEST_CODE_MUTI:
                for (int i = 0; i < grantResults.length; i++) {
                    //权限允许后，删除需要检查的权限
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        String item = getPermission(permissions[i]);
                        mCheckPermissions.remove(item);
                        onGuarantee(permissions[i], i);
                    } else {
                        //权限拒绝
                        onDeny(permissions[i], i);
                    }
                }
                if (mCheckPermissions.size() > 0) {
                    //用户拒绝了某个或多个权限，重新申请
                    reRequestPermission(mCheckPermissions.get(mRePermissionIndex));
                } else {
                    onFinish();
                }
                break;
            case REQUEST_CODE_MUTI_SINGLE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //重新申请后再次拒绝
                    //弹框警告! haha
                    try {
                        //permissions可能返回空数组，所以try-catch
                        String name = getPermissionName(permissions[0]);
                        String title = String.format(getString(R.string.permission_title), name);
                        String msg = String.format(getString(R.string.permission_denied_with_naac), mAppName, name, mAppName);
                        showAlertDialog(title, msg, getString(R.string.permission_reject), getString(R.string.permission_go_to_setting), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivityForResult(intent, REQUEST_SETTING);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    onClose();
                                }
                            }
                        });
                        onDeny(permissions[0], 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onClose();
                    }
                } else {
                    onGuarantee(permissions[0], 0);
                    if (mRePermissionIndex < mCheckPermissions.size() - 1) {
                        //继续申请下一个被拒绝的权限
                        reRequestPermission(mCheckPermissions.get(++mRePermissionIndex));
                    } else {
                        //全部允许了
                        onFinish();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) {
            checkPermission();
            if (mCheckPermissions.size() > 0) {
                mRePermissionIndex = 0;
                reRequestPermission(mCheckPermissions.get(mRePermissionIndex));
            } else {
                onFinish();
            }
        }

    }

    private void checkPermission() {

        ListIterator<String> iterator = mCheckPermissions.listIterator();
        while (iterator.hasNext()) {
            int checkPermission = ContextCompat.checkSelfPermission(getApplicationContext(), iterator.next());
            if (checkPermission == PackageManager.PERMISSION_GRANTED) {
                iterator.remove();
            }
        }
    }

    private void onFinish() {
        if (mCallback != null)
            mCallback.onFinish();
        finish();
    }

    private void onClose() {
        if (mCallback != null)
            mCallback.onClose();
        finish();
    }

    private void onDeny(String permission, int position) {
        if (mCallback != null)
            mCallback.onDeny(permission, position);
    }

    private void onGuarantee(String permission, int position) {
        if (mCallback != null)
            mCallback.onGuarantee(permission, position);
    }

    private String getPermission(String permission) {
        for (String checkPermission : mCheckPermissions) {
            if (checkPermission.equals(permission))
                return checkPermission;
        }
        return null;
    }

    private String getPermissionName(String permission) {
        if (Arrays.asList(Permission.CALENDAR).contains(permission)) {
            return "日历";
        } else if (Arrays.asList(Permission.CAMERA).contains(permission)) {
            return "相机";
        } else if (Arrays.asList(Permission.CONTACTS).contains(permission)) {
            return "通讯录";
        } else if (Arrays.asList(Permission.LOCATION).contains(permission)) {
            return "位置信息";
        } else if (Arrays.asList(Permission.MICROPHONE).contains(permission)) {
            return "麦克风";
        } else if (Arrays.asList(Permission.PHONE).contains(permission)) {
            return "电话";
        } else if (Arrays.asList(Permission.SENSORS).contains(permission)) {
            return "传感器";
        } else if (Arrays.asList(Permission.SMS).contains(permission)) {
            return "短信";
        } else if (Arrays.asList(Permission.STORAGE).contains(permission)) {
            return "存储";
        } else {
            return permission;
        }
    }

    private void reRequestPermission(final String permission) {
        String permissionName = getPermissionName(permission);
        String alertTitle = String.format(getString(R.string.permission_title), permissionName);
        String msg = String.format(getString(R.string.permission_denied), permissionName, mAppName);
        showAlertDialog(alertTitle, msg, getString(R.string.permission_cancel), getString(R.string.permission_ensure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPermission(new String[]{permission}, REQUEST_CODE_MUTI_SINGLE);
            }
        });
    }

    private void showAlertDialog(String title, String msg, String cancelTxt, String PosTxt, DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton(cancelTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        onClose();
                    }
                })
                .setPositiveButton(PosTxt, onClickListener).create();
        alertDialog.show();
    }

}
