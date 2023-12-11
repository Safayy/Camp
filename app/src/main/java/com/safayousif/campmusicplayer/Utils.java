package com.safayousif.campmusicplayer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

public class Utils {
    public static boolean isStoragePermissionGranted(Context context){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        } else {
            int readStorage = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE);
            return readStorage == PackageManager.PERMISSION_GRANTED;
        }
    }
}
