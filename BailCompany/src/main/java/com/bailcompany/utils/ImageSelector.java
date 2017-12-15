package com.bailcompany.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import java.io.File;

public class ImageSelector {
    public static final String DIALOG_OPTIONS[] = {"Choose from Gallery",
            "Capture a Photo", "Remove", "Cancel"};
    public static final String DIALOG_OPTIONS1[] = {"Choose from Gallery",
            "Capture a Photo", "Cancel"};
    public static final int IMAGE_GALLARY = 1000;
    public static final int IMAGE_CAPTURE = 1888;
    public static final int CROP = 1002;
    private static final String IMAGE_PATTERN =
            "(.*/)*.+\\\\.(png|jpg|gif|bmp|jpeg|PNG|JPG|GIF|BMP)$";

    public static void openChooser(final Activity act, final File file,
                                   final RemoveListener listener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Select Picture");
        builder.setItems(listener == null ? DIALOG_OPTIONS1 : DIALOG_OPTIONS,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {

                        if (position == 0)
                            openGallary(act);
                        else if (position == 1)
                            openCamera(act, file);
                        else if (position == 2 && listener != null)
                            listener.onRemove();

                    }
                });
        builder.create().show();
    }

    public static boolean isImage(String image) {

        image = image.trim().toLowerCase();
        if (image.endsWith(".jpg") || image.endsWith(".png") || image.endsWith(".gif") || image.endsWith(".bmp") || image.endsWith(".jpeg")) {
            return true;
        }
        return false;

    }
    public static void openGallary(Activity act) {
//		String[] mimeTypes = {"image/*","application/*"};
        String[] mimeTypes = {"image/*", "application/*", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};

        // Intent gallery = new Intent(Intent.ACTION_PICK,
        // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // gallery.setType("image/*");
        //
        // act.startActivityForResult(
        // Intent.createChooser(gallery, "Select Picture"), IMAGE_GALLARY);
        Intent gallery = new Intent(Intent.ACTION_VIEW);
        gallery.setAction(Intent.ACTION_GET_CONTENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            gallery.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                gallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";

            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }

            gallery.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }


        act.startActivityForResult(
                Intent.createChooser(gallery, "Select Picture"), IMAGE_GALLARY);

    }

    public static void openCamera(Activity act, File file) {

        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        act.startActivityForResult(camera, IMAGE_CAPTURE);
    }

    public static String getImagePath(Activity act, Intent data) {

        try {
            Uri uri = data.getData();
            String[] projection = {MediaColumns.DATA};
            Cursor cursor = act.managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public interface RemoveListener {
        public void onRemove();
    }

}
