package com.team.lezomadetana.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by team on 31/08/2018.
 **/

public class ImageCaptureUtil {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final int MEDIA_TYPE_IMAGE = 1;

    // directory name to store captured images
    private static final String IMAGE_DIRECTORY_NAME = "lztCamera";

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass
    // ===========================================================

    // ===========================================================
    // Methods for Interfaces
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    // CapturedImage
    public static Bitmap getCapturedImage(Uri fileUri) {
        Bitmap bitmap = null;
        try {
            InputStream is = null;
            // bitmapFactory
            BitmapFactory.Options options = new BitmapFactory.Options();
            // downsizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // GalleryImage
    public static Bitmap getImageFromGallery(Context context, Intent data) {
        Bitmap bitmap = null;
        try {
            // bitmapFactory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // fix the outOfMemory
            options.inSampleSize = 8;
            InputStream stream = context.getContentResolver().openInputStream(data.getData());
            bitmap = BitmapFactory.decodeStream(stream, null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // Creating file uri to store image
    public static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    // returning image
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Failed", "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    // ===========================================================
    // Inner Classes/Interfaces
    // ===========================================================
}
