package com.cbre.tsandford.cbreinspector.model;

import android.net.Uri;

import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Item {

    private String owner_name;
    private String item_root_path;
    private String item_name;
    public String extension;

    Item(String item_name, String owner_name, String item_root_path, String extension){
        this.item_name = item_name;
        this.owner_name = owner_name;
        this.item_root_path = Utils.GetFolder(item_root_path).getPath();
        this.extension = extension;
    }

    public List<Uri> get_all_items(int max_items) {
        List<Uri> result = new ArrayList<>();
        File[] pics = new File(item_root_path).listFiles();
        if(pics != null) {
            for (int i = 0; i < pics.length; i++) {
                if(i > max_items) break;
                result.add(Uri.fromFile(pics[i]));
            }
        }
        return result;
    }

    public Uri get_new_resource() {
        File pics_folder = Utils.GetFolder(item_root_path);
        int count = 0;

        File[] files = pics_folder.listFiles();
        if(files!=null){
            count = files.length;
        }
        String filename = pics_folder + "/" + owner_name + "_" + item_name + "_" + (count + 1) + "." + extension;
        return Uri.fromFile(new File(filename));
    }
}
