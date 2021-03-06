/*
 * Copyright © Yan Zhenjie
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
package com.example.permission;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.SettingService;

import java.util.ArrayList;
import java.util.List;

/**
 * 弹出权限被拒绝的弹窗 提示用户去应用管理去开启
 */
public final class PermissionSetting {

    private final Context mContext;
    private AlertDialog dialog;
    private ArrayList<AlertDialog> dialogs = new ArrayList<>();

    public PermissionSetting(Context context) {
        this.mContext = context;
    }

    public void showSetting(final List<String> permissions) {
        List<String> permissionNames = PermissionsList.formatPermission(permissions);
        String message = mContext.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        final SettingService settingService = AndPermission.permissionSetting(mContext);
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        adb.setTitle(R.string.title_dialog);
        adb.setMessage(message);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingService.execute();
                dialog.dismiss();
            }
        });
        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingService.cancel();
                dialog.dismiss();
            }
        });
        dialog = adb.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialogs.add(dialog);
    }

    public void showSetting(final List<String> permissions, final OnResultCallBackListener listener) {
        List<String> permissionNames = /*Permission.transformText(mContext, permissions)*/PermissionsList.formatPermission(permissions);
        String message = mContext.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        final SettingService settingService = AndPermission.permissionSetting(mContext);
        AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
        adb.setTitle(R.string.title_dialog);
        adb.setMessage(message);
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingService.execute();
                dialog.dismiss();
                if (listener != null) {
                    listener.onResultCallBack(true);
                }
            }
        });
        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                settingService.cancel();
                dialog.dismiss();
                if (listener != null) {
                    listener.onResultCallBack(false);
                }
            }
        });
        dialog = adb.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialogs.add(dialog);
    }

    public interface OnResultCallBackListener {
        void onResultCallBack(boolean clickOk);
    }

    public void dismissDialog() {
        for (AlertDialog alertDialog : dialogs) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}