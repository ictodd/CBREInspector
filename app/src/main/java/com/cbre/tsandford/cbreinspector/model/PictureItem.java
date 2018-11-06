package com.cbre.tsandford.cbreinspector.model;

import android.net.Uri;

import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PictureItem extends Item {

    public enum Type{
        Main,
        ReducedQuality,
        ReducedSize
    }

    private final String REDUCED_QUALITY_EXTENSION = "compressed";
    private final String REDUCED_SIZE_EXTENSION = "scaled";

    private List<Pic> picItems;
    private final int MAX_ITEMS = 10000;

    PictureItem(String item_name, String owner_name, String item_root_path, String extension){
        super(item_name, owner_name, item_root_path, extension);
        picItems = new ArrayList<>();
        populatePicItems();
    }

    // Todo the max shouldnt count thumbnails
    private void populatePicItems(){
        List<File> allFiles = this.get_all_items_as_file_list(MAX_ITEMS);
        this.picItems.clear();
        for(File file : allFiles){
            if(isType(file, Type.Main)){
                int id = get_pic_id(file);
                File thumb = getSpecificPic(id, Type.ReducedSize);
                if(thumb != null){
                    this.picItems.add(new Pic(file, thumb));
                } else {
                    this.picItems.add(new Pic(file));
                }
            }
        }
    }

    public void RefreshItems(){
        populatePicItems();
    }

    public void deletePic(File file){
        // gets the pic object and deletes both instances of the image (main and thumb)
        Pic targetPic = getPic(file);
        if(targetPic == null)
            return;

        try{
            Files.delete(targetPic.getMain().toPath());
            if(targetPic.hasThumb)
                Files.delete(targetPic.getThumb().toPath());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    // returns pic object based on either a main or thumbnail
    public Pic getPic(File file){
        for(Pic pic : picItems)
            if(pic.getMain().getPath() == file.getPath() ||
                    pic.getThumb().getPath() == file.getPath())
                return pic;

        return null;
    }

    // region Getters and Setters

    public List<Pic> getPicItems() {
        return picItems;
    }

    public void setPicItems(List<Pic> picItems) {
        this.picItems = picItems;
    }

    // endregion

    public Uri get_new_resource(PictureItem.Type type) {

        File pics_folder = Utils.GetFolder(item_root_path);
        int count = pics_folder.listFiles() == null ? 0 : pics_folder.listFiles().length;

        String filename = null;

        switch(type){
            case Main:
                filename = pics_folder + "/" + owner_name + "_" + item_name + "_" + (count + 1) + "." + extension;
                break;
            case ReducedQuality:
                filename = pics_folder + "/" + owner_name + "_" + item_name + "_" + (count + 1) + "_" + REDUCED_QUALITY_EXTENSION + "." + extension;
                break;
            case ReducedSize:
                filename = pics_folder + "/" + owner_name + "_" + item_name + "_" + (count + 1) + "_" + REDUCED_SIZE_EXTENSION + "." + extension;
                break;
        }
        return Uri.fromFile(new File(filename));
    }

    private int get_pic_id(File f){
        // examples:
        // main:  abc_street_pic_19.jpg
        // thumb: abc_street_pic_19_compressed.jpg
        // or:    abc_street_pic_19_scaled.jpg

        String fName = f.getName();
        String strResult = null;

        if(isType(f, Type.Main)){
            strResult = fName
                    .replace(owner_name + "_" + item_name + "_", "")
                    .replace("." + this.extension,"");
        } else if(isType(f, Type.ReducedQuality)){
            strResult = fName
                    .replace(owner_name + "_" + item_name + "_", "")
                    .replace("_" + REDUCED_QUALITY_EXTENSION + "_." + this.extension,"");
        } else if(isType(f,Type.ReducedSize)){
            strResult = fName
                    .replace(owner_name + "_" + item_name + "_", "")
                    .replace("_" + REDUCED_SIZE_EXTENSION + "_." + this.extension,"");
        }
        return Integer.parseInt(strResult);
    }

    private File getSpecificPic(int item, Type picType){
        File[] files = new File(item_root_path).listFiles();

        if(files == null)
            return null;

        for(File file : files)
            if(isType(file, picType) && file.getName().contains(Integer.toString(item)))
                    return file;

        return null;
    }

    private boolean isValidImage(File f){
        return Utils.GetFileExtension(f).equals("." + this.extension);
    }

    private boolean isReducedQuality(File f){
        return f.getName().contains("_" + REDUCED_QUALITY_EXTENSION);
    }

    private boolean isReducedSize(File f){
        return f.getName().contains("_" + REDUCED_SIZE_EXTENSION);
    }

    private boolean isMain(File f){
        return !isReducedQuality(f) && !isReducedSize(f);
    }


    private boolean isType(File f, Type type){

        if(!isValidImage(f))
            return false;

        if(type.equals(Type.Main)){
            return isMain(f);
        } else if(type.equals(Type.ReducedQuality)) {
            return isReducedQuality(f);
        } else if(type.equals(Type.ReducedSize)){
            return isReducedSize(f);
        }
        return false;
    }

    public class Pic{

        private File main;
        private File thumb;
        private boolean hasThumb;

        Pic(){}

        Pic(String main, String thumb){
            this.main = new File(main);
            this.thumb = new File(thumb);
            this.hasThumb = true;
        }

        Pic(String main){
            this.main = new File(main);
            this.hasThumb = false;
        }

        Pic(File main){
            this.main = main;
            this.hasThumb = false;
        }

        Pic(File main, File thumb){
            this.main = main;
            this.thumb = thumb;
            this.hasThumb = true;
        }

        public boolean hasThumb() {
            return hasThumb;
        }

        public void setHasThumb(boolean hasThumb) {
            this.hasThumb = hasThumb;
        }

        public File getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = new File(main);
        }

        public File getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = new File(thumb);
        }
    }

}
