package com.example.mywine.model;

import android.graphics.Bitmap;

public class ImageStorageFunctions {
    ModelFirebase modelFirebase = new ModelFirebase();

    public interface SaveImageListener {
        void onComplete(String url);
    }

    public void saveImage(Bitmap imageBitmap, String imageName, String imgType, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap, imageName, imgType, listener);
    }

}