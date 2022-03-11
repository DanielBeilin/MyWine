package com.example.mywine.model;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

public class PicturePickDialog extends DialogFragment {

    public static String TAG = "PicturePickDialog";

//    private static final int CAMERA_REQUEST_CODE = 1;
//    private static final int STORAGE_REQUEST_CODE = 2;
    public static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 1;
    public static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 2;

//    String cameraPermissions[];
//    String storagePermissions[];

    Uri image_uri;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPickCompleted(int requestCode, int resultCode, @Nullable Intent data);
    }

    // Use this instance of the interface to deliver action events
    PicturePickDialog.NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (PicturePickDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // camera clicked
                    pickFromCamera();
                } else if (which == 1) {
                    // gallery clicked
                    pickFromGallery();
                }
            }
        });
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.listener.onDialogPickCompleted(requestCode, resultCode, data);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private void pickFromCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
