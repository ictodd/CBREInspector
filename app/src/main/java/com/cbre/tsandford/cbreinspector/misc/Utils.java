package com.cbre.tsandford.cbreinspector.misc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cbre.tsandford.cbreinspector.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Utils {

    private Utils(){}

    public static void hideSystemUI(AppCompatActivity activity) {
        View decorView = activity.getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.hide();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void MakeFile(String filepath){
        try {
            File newFile = new File(filepath);
            FileOutputStream outputStream = new FileOutputStream(newFile);
            outputStream.write(new byte[]{});
            outputStream.flush();
            outputStream.close();
        }catch(Exception ex){

        }
    }

    public static File GetFolder(String location){
        File folder = new File(location);

        if(folder.exists()){
            Log.d("TODD", "Directory already exists: " + folder.getPath());
        } else if(folder.mkdirs()){
            Log.d("TODD", "Directory created: " + folder.getPath());
        } else {
            Log.d("TODD", "Directory NOT created: " + folder.getPath());
        }

        return folder;
    }

    public static String GetFileExtension(File file) {
        String extension = "";

        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }

        return extension;

    }

    // careful, this lags the shit out of the app
    private static void LoopDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if(files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        LoopDirectory(file);
                    } else {
                        Log.d("TODD", file.getPath());
                    }
                }
            }
        }
    }

    public static void WriteToFile(String content, String filePath) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception ex){
            Log.d("TODD", "Utils.WriteToFile() : Failed to writer to " + filePath);
        }
    }

    public static boolean FileHasContents(String filePath){
        File f = new File(filePath);
        return f.exists() && f.length() > 0;
    }

    public static void FillEditTextWithLorenIpsum(EditText editTextView){
        final String[] randomBits = new String[]
                {"Check back tomorrow; I will see if the book has arrived. \n" +
                        "Where do random thoughts come from? \n" +
                        "She borrowed the book from him many years ago and hasn't yet returned it.",

                        "She only paints with bold colors; she does not like pastels.\n" +
                                "He turned in the research paper on Friday; otherwise, he would have not passed the class.",

                        "Tom got a small piece of pie.\n" +
                                "Last Friday in three week’s time I saw a spotted striped blue worm shake hands with a legless lizard.\n" +
                                "The old apple revels in its authority.",

                        "He didn’t want to go to the dentist, yet he went anyway.\n" +
                                "She was too short to see over the fence.",

                        "The stranger officiates the meal.\n" +
                                "He told us a very exciting adventure story.\n" +
                                "I am never at home on Sundays.\n" +
                                "The old apple revels in its authority.\n" +
                                "I really want to go to work, but I am too sick to drive.",

                        "She only paints with bold colors; she does not like pastels.\n" +
                                "We have never been to Asia, nor have we visited Africa.\n" +
                                "Sixty-Four comes asking for bread.",

                        "She folded her handkerchief neatly.\n" +
                                "She only paints with bold colors; she does not like pastels.\n" +
                                "I am never at home on Sundays.",

                        "If the Easter Bunny and the Tooth Fairy had babies would they take your teeth and leave chocolate for you?\n" +
                                "I would have gotten the promotion, but my attendance wasn’t good enough.\n" +
                                "She only paints with bold colors; she does not like pastels.",

                        "Italy is my favorite country; in fact, I plan to spend two weeks there next year.\n" +
                                "Sixty-Four comes asking for bread.\n" +
                                "She borrowed the book from him many years ago and hasn't yet returned it.\n" +
                                "My Mum tries to be cool by saying that she likes all the same things that I do.\n" +
                                "He said he was not there yesterday; however, many people saw him there.",

                        "The waves were crashing on the shore; it was a lovely sight.\n" +
                                "He ran out of money, so he had to stop playing poker.\n" +
                                "Sometimes, all you need to do is completely make an ass of yourself and laugh it off to realise that life isn’t so bad after all.",

                        "Rock music approaches at high velocity.\n" +
                                "Two seats were vacant.\n" +
                                "This is a Japanese doll.",

                        "There were white out conditions in the town; subsequently, the roads were impassable.\n" +
                                "I was very proud of my nickname throughout high school but today- I couldn’t be any different to what my nickname was.\n" +
                                "Don't step on the broken glass.",

                        "Last Friday in three week’s time I saw a spotted striped blue worm shake hands with a legless lizard.\n" +
                                "He told us a very exciting adventure story.\n" +
                                "Christmas is coming."


                };

        Random rnd = new Random();
        int rand = rnd.nextInt(randomBits.length - 1);
        editTextView.setText(randomBits[rand]);
    }

    public static void showYesNoDialog(Activity activity, String title, String message,
                                       final PromptRunnable method_for_yes,
                                       final PromptRunnable method_for_no) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                method_for_yes.run();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                method_for_no.run();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showYesNoDialog(Activity activity, String title, String message,
                                       final PromptRunnable method_for_yes) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                method_for_yes.run();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInputDialog(Activity activity, String title, String message,
                                       final PromptRunnable method_for_ok,
                                       final PromptRunnable method_for_cancel) {


        View inputViewInflated = LayoutInflater.from(activity).inflate(R.layout.input_prompt, (ViewGroup) activity.getWindow().getDecorView(), false);

        final EditText input = inputViewInflated.findViewById(R.id.prompt_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(inputViewInflated);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method_for_ok.SetValue(input.getText().toString());
                dialog.dismiss();
                method_for_ok.run();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                method_for_cancel.run();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInputDialog(Activity activity, String title, String message,
                                       final PromptRunnable method_for_ok ) {


        View inputViewInflated = LayoutInflater.from(activity).inflate(R.layout.input_prompt, (ViewGroup) activity.getWindow().getDecorView(), false);

        final EditText input = inputViewInflated.findViewById(R.id.prompt_input);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setView(inputViewInflated);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                method_for_ok.SetValue(input.getText().toString());
                dialog.dismiss();
                method_for_ok.run();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void GetPermissions(Activity activity){
        final int REQUEST_PERMISSION=1;
        String[] permissions = new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
        };

        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(activity, new String[]{permission}, REQUEST_PERMISSION);
                Log.d("TODD","Granted : " + permission);

            } else {
                Log.d("TODD","Already granted : " + permission);
            }
        }
    }

    public static void showInfoDialog(Activity activity, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static Date string_to_date(String date_string){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            String no_slashes = date_string.replace("/","-");
            return df.parse(no_slashes);
        } catch (Exception ex) {
            Log.d("TODD", "Could not parse date '" + date_string + "'. Error: " + ex.getMessage());
            ex.printStackTrace();
        }
        return new Date();
    }

    public static String date_to_string(Date date){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        return  df.format(date);
    }

    public boolean is_date(String strDate){
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.UK);
        try {
            df.parse(strDate);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String get_proper(String str){
        String[] words = str.split(" ");
        StringBuilder sb = new StringBuilder();
        for(String word: words){
            sb.append(get_proper_word(word));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    private static String get_proper_word(String str){
        String first_letter = str.substring(0,1);
        String result = first_letter.toUpperCase() + str.substring(1);
        return result;
    }

    public static boolean deleteRecursive(File fileOrDirectory) {
        try {
            if (fileOrDirectory.isDirectory())
                for (File child : fileOrDirectory.listFiles())
                    deleteRecursive(child);

            fileOrDirectory.delete();
            return true;
        } catch (Exception ex){
            return false;
        }
    }

    public static String get_current_date(){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    // todo change all use of Date to Calendar in app
    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String getFileExtension(File file){
        String extension = "";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "";
        }
        return extension;

    }

    public static String getFileNameNoExtension(File file){
        String extn = getFileExtension(file);
        String dir = file.getParent();
        String result = file.getPath().replace(dir + "/", "").replace(extn,"");
        return result;
    }

    public static File getFilePathWithSuffix(File file, String suffix){
        String ex = Utils.GetFileExtension(file);
        String newFilePath = file.getPath().replace("/" + file.getName(), "") + "/" + file.getName().replace(ex, "") + "_" + suffix + ex;
        return new File(newFilePath);
    }

    public static class Math{
        public static int Clamp(int x, int min, int max) {
            if (x > max) {
                return max;
            }
            if (x < min) {
                return min;
            }
            return x;
        }
    }

    public static class Json {
        public Json(){}

        public static Map<String,Object> getMapObjects(String jsonFilePath){
            Gson gson = new Gson();
            String fileContents = null;
            try {
                fileContents = new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8");
            }catch(Exception ex){
                Log.d("TODD","Utils.Json.getMap(): Failed to read file " + jsonFilePath);
            }
            return gson.fromJson(fileContents, Map.class);
        }

        public static Map<String,String> getMapStrings(String jsonFilePath){
            Gson gson = new Gson();
            String fileContents = null;
            try {
                fileContents = new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8");
            }catch(Exception ex){
                Log.d("TODD","Utils.Json.getMap(): Failed to read file " + jsonFilePath);
            }
            return gson.fromJson(fileContents, Map.class);
        }

        public static String getPrettyJsonStrings(Map<String, String> data){
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting().serializeNulls();
            Gson gson = builder.create();
            return gson.toJson(data);
        }

        public static String getPrettyJsonObjects(Map<String, Object> data){
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting().serializeNulls();
            Gson gson = builder.create();
            return gson.toJson(data);
        }



    }

    public static class Image {

        public Image(){}

        public static File getCompressedImage(File image, int compression){

            File compressedFile = Utils.getFilePathWithSuffix(image, "compressed");
            Utils.MakeFile(compressedFile.getPath());

            try(FileOutputStream compressedPhoto = new FileOutputStream(compressedFile)) {

                Bitmap bitmap = BitmapFactory.decodeFile(image.getPath());
                bitmap.compress(Bitmap.CompressFormat.JPEG, compression, compressedPhoto);
                compressedPhoto.flush();
                compressedPhoto.close();
                return compressedFile;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public static File ScaleImage(File fileIn, int width, int height, int compression){

            File scaledFile = Utils.getFilePathWithSuffix(fileIn, "scaled");
            Utils.MakeFile(scaledFile.getPath());

            try(FileOutputStream fos = new FileOutputStream(scaledFile)){

                Bitmap original = BitmapFactory.decodeFile(fileIn.getPath());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compression, fos);
                return scaledFile;

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;

        }


        public static File ScaleImage(File fileIn, double scale, int compression){

            Bitmap original = BitmapFactory.decodeFile(fileIn.getPath());

            int originalHeight = original.getHeight();
            int originalWidth = original.getWidth();

            int newHeight = (int)(scale * originalHeight);
            int newWidth = (int)(scale * originalWidth);

            File scaledFile = Utils.getFilePathWithSuffix(fileIn, "scaled");
            Utils.MakeFile(scaledFile.getPath());

            try(FileOutputStream fos = new FileOutputStream(scaledFile)){

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, compression, fos);
                return scaledFile;

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;

        }

        public static File ScaleImage(File fileIn, File fileOut, int width, int height){

            final int FULL_QUALITY = 100;
            Utils.MakeFile(fileOut.getPath());

            try(FileOutputStream fos = new FileOutputStream(fileOut)){

                Bitmap original = BitmapFactory.decodeFile(fileIn.getPath());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, width, height, true);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, FULL_QUALITY, fos);
                return fileOut;

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;

        }

    }

}
