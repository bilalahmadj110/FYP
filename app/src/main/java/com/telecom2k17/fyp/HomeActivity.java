package com.telecom2k17.fyp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class HomeActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;
    private final int REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE = 2000;
    private final String PERMISSION_READING_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG);



    }

    public void browse(View view) {
        requestPermissionFor(new String[]{PERMISSION_READING_STORAGE}, REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE);
    }


    public boolean isPermissionGivenFor(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void requestPermissionFor(String[] permission, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("PERMISSION...", requestCode + " " + grantResults.length + " " + Arrays.toString(grantResults));
        if (requestCode == REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE) {
            Log.d("PERMISSION...", "Reach here");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showFileChooser();
                Log.d("PERMISSION...", "Reach here!!!");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    retryAlert(REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE);
                    Log.d("PERMISSION...", "Reach here........");
                } else
                    settingAlert();
            Log.d("PERMISSION...", "Reach here~~~");
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
        return super.shouldShowRequestPermissionRationale(permission);
    }

    public void settingAlert() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Permission Disabled")
                .setMessage(getResources().getString(R.string.setting_alert_storage))
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    public void retryAlert(final int requestCode) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Allow Permission")
                .setMessage(getResources().getString(R.string.alert_storage))
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (!isPermissionGivenFor(requestCode == REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE ?
                            Manifest.permission.READ_EXTERNAL_STORAGE : Manifest.permission.SEND_SMS))
                        requestPermissionFor(requestCode == REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE ?
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} :
                                new String[]{Manifest.permission.SEND_SMS}, requestCode);
                })
                .setNegativeButton("Deny", null)
                .create().show();
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType("*/*");
        } else {
            StringBuilder mimeTypesStr = new StringBuilder();
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select an Excel file"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            showToast("Please install a File Manager.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "CODE ;;;;" + requestCode);
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK && null != data) {
                Uri content_describer = data.getData();

                try {
                    PathUtil pathUtil = new PathUtil();
                    String path = pathUtil.getPath(getApplicationContext(), content_describer);
                    String extension = "";

                    int i = path.lastIndexOf('.');
                    if (i > 0) {
                        extension = path.substring(i + 1);
                    }
                    if (pathUtil.isImage(extension)) {

                        showOkAlert(path);

                    } else
                        showBrowseAlert("The only supported image files are:" + pathUtil.arrayToList() + "<br /><i>Please choose the correct file.</i>");
                } catch (Exception e) {
                    showBrowseAlert("Take screenshot and share to Bilal Ahmad:\n\n" + e.toString());
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showBrowseAlert(String msg) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setMessage(Html.fromHtml(msg))
                .setPositiveButton("Browse", (dialog, which) -> {
                    if (isPermissionGivenFor(PERMISSION_READING_STORAGE))
                        showFileChooser();
                    else
                        requestPermissionFor(new String[]{PERMISSION_READING_STORAGE}, REQUEST_CODE_FOR_READING_EXTERNAL_STORAGE);
                })
                .setNegativeButton("Cancel", null)
                .create().show();
    }

    public void showOkAlert(String msg_) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder
                .setMessage(msg_)
                .setPositiveButton("OK", (dialog, which) -> {


                })
                .create().show();
    }

    public void showToast(String text) {
        toast.setText(text);
        toast.show();
    }
}