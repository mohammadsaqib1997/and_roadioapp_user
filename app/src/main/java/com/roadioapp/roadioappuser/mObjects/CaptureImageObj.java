package com.roadioapp.roadioappuser.mObjects;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.roadioapp.roadioappuser.MapActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptureImageObj {

    private Context context;
    private Activity activity;
    private ConstantAssign constantAssignObj;
    private PermissionCheckObj permissionCheckObj;

    private String mCurrentPhotoPath;
    public final int REQUEST_IMAGE_CAPTURE = 1;
    public final int REQUEST_IMAGE_PICK = 2;

    public CaptureImageObj(Context ctx, Activity act, ConstantAssign constantAssign){
        this.context = ctx;
        this.activity = act;
        this.constantAssignObj = constantAssign;
        this.permissionCheckObj = new PermissionCheckObj(act);
    }


    public void dispatchTakePictureIntent() {
        if (constantAssignObj.pickLocMarker != null && constantAssignObj.desLocMarker != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Take a picture of your parcel!");
            builder.setPositiveButton("Take Picture", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {

                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            Log.e("CaptureImageErr", ex.getMessage());
                        }

                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(context,
                                    "com.roadioapp.roadioappuser.fileprovider",
                                    photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                }
            });
            builder.setNegativeButton("Select Gallery Image", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(!permissionCheckObj.storagePermissionCheck()){
                        permissionCheckObj.setStoragePermission();
                    }else{
                        openGallery();
                    }
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            Toast.makeText(context, "Please select origin and destination location!", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_PICK);
    }

    public void imageToBytes(){
        if(mCurrentPhotoPath != null && !mCurrentPhotoPath.isEmpty()){
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            genBitmapToSetValues(imageBitmap);
            /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            constantAssignObj.selImgByteArray = stream.toByteArray();

            Bitmap thmbBM = Bitmap.createScaledBitmap(imageBitmap, (int)(imageBitmap.getWidth()*0.2), (int)(imageBitmap.getHeight()*0.2), false);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            thmbBM.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
            constantAssignObj.selImgThmbByteArray = stream1.toByteArray();*/
        }
    }

    public void selImageToBytes(Uri selFileURI){
        try {
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selFileURI);
            genBitmapToSetValues(imageBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void genBitmapToSetValues(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        constantAssignObj.selImgByteArray = stream.toByteArray();

        Bitmap thmbBM = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.2), (int)(bitmap.getHeight()*0.2), false);
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        thmbBM.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        constantAssignObj.selImgThmbByteArray = stream1.toByteArray();
    }
}
