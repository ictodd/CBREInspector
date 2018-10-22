package com.cbre.tsandford.cbreinspector.misc;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageCompressor {

    public Uri compress_image(String input_jpg, int compression){
        Uri result = null;
        String compressed_file_path;

        try {

            File f = new File(input_jpg);
            String ex = Utils.GetFileExtension(f);

            compressed_file_path = f.getPath().replace("/" + f.getName(),"") + "/" + f.getName().replace(ex, "") + "_compressed" + ex;

            Utils.MakeFile(compressed_file_path);

            FileOutputStream compressedPhoto = new FileOutputStream(new File(compressed_file_path));

            Bitmap bitmap = BitmapFactory.decodeFile(input_jpg);
            bitmap.compress(Bitmap.CompressFormat.JPEG, compression,compressedPhoto);

            compressedPhoto.flush();
            compressedPhoto.close();

            result = Uri.fromFile(new File(compressed_file_path));

        }catch(FileNotFoundException ex){
            Log.d("TODD","File not found caught. Msg: " + ex.getMessage());
        }catch(IOException ex){
            Log.d("TODD","IO caught. Msg: " + ex.getMessage());
        }
        return result;
    }




}
