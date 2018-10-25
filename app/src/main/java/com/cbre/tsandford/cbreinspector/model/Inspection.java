package com.cbre.tsandford.cbreinspector.model;

import android.util.Log;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Inspection {
    // region Directory Structure Example

    /*
            Each inspection will be located under \inspections\ in this apps root
            The structure will be as follows:

            inspections\
            |
            |-- abc_street\
                |-- main_info.json
                |-- notes.json
                |
                |-- pics\
                |   |-- photo1.jpg
                |   |-- photo2.jpg
                |
                |-- drawings\
                |   |-- drawing1.jpg
                |   |-- drawing2.jpg
                |
                |-- audio_clips\
                |   |-- clip1.wav
                |   |-- clip2.wav
                |
                |-- annotations\
                    |-- annotated1.pdf
                    |-- annotated2.pdf
     */

    // endregion

    public Date inspection_date;
    public String inspector;
    public String property_name_formatted; // i.e. 123 Abc Street, Auckland
    private String internal_name; // i.e. abc_street
    public String root_path;
    private String notes_file_path;
    private String info_file_path;
    private String notes_type;

    public PictureItem pictures;
    public Item drawings;
    public Item audio_clips;
    public Item annotations;

    public Map<String, String> notes;

    // region Public Consts

    public static String NAME_KEY = "inspector_name";
    public static String DATE_KEY = "inspection_date";
    public static String ADDRESS_KEY = "property_address";
    public static String NOTE_TYPE_KEY = "notes_type";
    public static String PATH_KEY = "inspect_path";

    public static String NOTES_JSON = "notes.json";
    public static String INFO_JSON = "info.json";

    // endregion

    public Inspection(String root_path){
        set_up_existing_inspection(root_path);
    }

    public Inspection(String address, Date new_inspection_date, String inspector, String notes_type){
        String new_root_path = set_up_new_inspection(address, new_inspection_date, inspector, notes_type);
        set_up_existing_inspection(new_root_path);
    }

    private String set_up_new_inspection(String address, Date new_inspection_date, String inspector, String notes_type){

        address = Utils.get_proper(address);

        this.internal_name = generate_internal_name(address);
        this.root_path = Utils.GetFolder(AppState.InspectionsPath + "/" + this.internal_name).getPath();

        this.notes_file_path = this.root_path + "/" + NOTES_JSON;
        this.info_file_path = this.root_path + "/" + INFO_JSON;

        Map<String,String> info = new HashMap<>();
        info.put(NAME_KEY, inspector);
        info.put(DATE_KEY, Utils.date_to_string(new_inspection_date));
        info.put(ADDRESS_KEY, address);
        info.put(PATH_KEY, this.root_path);
        info.put(NOTE_TYPE_KEY, notes_type);

        // make info json file and populate with setup info
        create_new_inspection_info(info);

        // make empty notes json file
        Utils.MakeFile(this.notes_file_path);

        return this.root_path;
    }

    private void set_up_existing_inspection(String root_path) {
        this.root_path = Utils.GetFolder(root_path).getPath();
        this.internal_name = this.root_path.substring(this.root_path.lastIndexOf("/") + 1);
        this.notes_file_path = this.root_path + "/" + NOTES_JSON;
        this.info_file_path = this.root_path + "/" + INFO_JSON;

        this.pictures = new PictureItem("pic", internal_name, this.root_path + "/pics", "jpg");
        this.drawings = new Item("drawing", internal_name, this.root_path + "/drawings", "jpg");
        this.audio_clips = new Item("audio_clips", internal_name, this.root_path + "/audio_clips", "3gp");
        this.annotations = new Item("annotations", internal_name, this.root_path + "/annotations", "jpg");

        if (Utils.FileHasContents(this.notes_file_path))
            this.restore_notes();


        if (Utils.FileHasContents(this.info_file_path))
            this.populate_inspection_info();

    }

    private String generate_internal_name(String address){
        // needs to take result in the following:
        // 123 Abc Street, Auckland = 123_abc_street_auckland

        String result;
        result = address.replace(" ", "_");
        result = result.replace(",","");
        result = result.replace("-","");
        result = result.replace("/","");
        result = result.replace("\\","");
        result = result.replace("&","");
        result = result.replace("#","");
        result = result.replace("(","");
        result = result.replace(")","");

        return result.toLowerCase();
    }

    // todo refactor notes into notes class
    private String get_pretty_json_notes(Map<String, String> data){
        return Utils.JsonTools.getPrettyJsonStrings(data);
    }

    public void save_notes(){
        String note_content = get_pretty_json_notes(this.notes);
        Utils.WriteToFile(note_content, this.notes_file_path);
    }

    public void restore_notes(){
        this.notes = populate_notes_from_file(this.notes_file_path);
    }

    private Map<String,String> populate_notes_from_file(String filePath){
        return Utils.JsonTools.getMapStrings(filePath);
    }

    public void update_info(Map<String, String> new_data){
        create_new_inspection_info(new_data);
        populate_inspection_info();
    }

    public Map<String, String> get_map_of_info(){
        return populate_notes_from_file(this.info_file_path);
    }

    private void create_new_inspection_info(Map<String, String> info){
        String info_as_json = get_pretty_json_notes(info);
        if(!Utils.FileHasContents(this.info_file_path))
            Utils.MakeFile(this.info_file_path);
        Utils.WriteToFile(info_as_json, this.info_file_path);
    }

    private void populate_inspection_info(){
        // region example of info.json:
        /*
                {
                    "inspector_name": "Todd Sandford",
                    "inspection_date": "10/02/1989",
                    "property_address": "123 ABC Street, Auckland",
                    "inspect_path": "\\inspection\\abc_street"
                    "notes_type": "Hotel"
                }
         */
        // endregion

        Map<String, String> info;
        info = populate_notes_from_file(this.info_file_path);
        if(info == null) return;
        if(!info.isEmpty()) {

            if (info.containsKey(NAME_KEY))
                this.inspector = info.get(NAME_KEY);

            if (info.containsKey(DATE_KEY))
                this.inspection_date = Utils.string_to_date(info.get(DATE_KEY));

            if (info.containsKey(ADDRESS_KEY))
                this.property_name_formatted = info.get(ADDRESS_KEY);

            if(info.containsKey(NOTE_TYPE_KEY)){
                this.notes_type = info.get(NOTE_TYPE_KEY);
            }

        }
    }

}
