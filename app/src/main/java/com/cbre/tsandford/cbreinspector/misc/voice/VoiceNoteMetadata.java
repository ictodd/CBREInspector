package com.cbre.tsandford.cbreinspector.misc.voice;

import android.support.annotation.Nullable;

import com.cbre.tsandford.cbreinspector.misc.Utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VoiceNoteMetadata {

    private String name;
    private String recordingPath;
    private String metadataPath;
    private List<String> tags;
    private String date;
    private String transcription;

    // wrap class members into hash map for easy json serialisation
    private Map<String, Object> data;
    public static String NAME_KEY = "name";
    public static String REC_PATH_KEY = "rec_path";
    public static String META_PATH_KEY = "meta_path";
    public static String TAGS_KEY = "tags";
    public static String DATE_KEY = "date";
    public static String TRANSCRIPTION_KEY = "transcription";

    // for loading from existing metadata
    public VoiceNoteMetadata(File recordingFile, File metadataFile){
        data = new HashMap<>();
        if(Files.exists(metadataFile.toPath())){
            this.data = Utils.JsonTools.getMapObjects(metadataFile.getPath());
            unpackData();
        }
        this.recordingPath = recordingFile.getPath();
    }

    // for setting up new metadata
    public VoiceNoteMetadata(File recordingFile){
        data = new HashMap<>();
        this.tags = new ArrayList<>();
        this.recordingPath = recordingFile.getPath();
        this.date = getFileDateFormatted(recordingFile.toPath());
        this.tags.add("None");
        this.name = Utils.getFileNameNoExtension(recordingFile);
    }

    private String getFileDateFormatted(Path filePath){
        BasicFileAttributes attrs;
        try{
            attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        }catch(Exception e){
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("d MMM yy - h:mm a ", Locale.UK);
        String result = "";
        if(attrs != null){
            result = df.format(attrs.creationTime().toMillis());
        }
        return result;
    }

    // region Getters and Setters

    public Map<String, Object> getData() {
        // take carbon copy of object's fields at time of getting
        // for the purpose of serialising json file
        data.clear();
        data.put(NAME_KEY, this.name);
        data.put(REC_PATH_KEY, this.recordingPath);
        data.put(META_PATH_KEY, this.metadataPath);
        data.put(TAGS_KEY, this.tags);
        data.put(DATE_KEY, this.date);
        data.put(TRANSCRIPTION_KEY, this.transcription);
        return data;
    }

    public void setData(Map<String, Object> data){
        this.data = data;
        unpackData();
    }

    public String getName(){
        return this.name;
    }

    public String getRecordingPath(){
        return this.recordingPath;
    }

    public String getDate(){
        return this.date;
    }

    public String getTranscription(){
        return this.transcription;
    }

    public List<String> getTags(){
        return this.tags;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setRecordingPath(String path){
        this.recordingPath = path;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getMetadataPath() {
        return metadataPath;
    }

    public void setMetadataPath(String metadataPath) {
        this.metadataPath = metadataPath;
    }

    // endregion

    public void saveData(){
        if(!Files.exists(new File(this.metadataPath).toPath()))
            Utils.MakeFile(this.metadataPath);

        String json = Utils.JsonTools.getPrettyJsonObjects(getData());
        Utils.WriteToFile(json, this.metadataPath);
    }

    public void restoreData(){
        if(!Files.exists(new File(this.metadataPath).toPath()))
            return;

        setData(Utils.JsonTools.getMapObjects(this.metadataPath));

    }

    private void unpackData(){
        if(data.containsKey(NAME_KEY))
            this.name = (String)data.get(NAME_KEY);

        if(data.containsKey(REC_PATH_KEY))
            this.recordingPath = (String)data.get(REC_PATH_KEY);

        if(data.containsKey(META_PATH_KEY))
            this.metadataPath = (String)data.get(META_PATH_KEY);

        if(data.containsKey(TAGS_KEY))
            this.tags = (List<String>)data.get(TAGS_KEY);

        if(data.containsKey(DATE_KEY))
            this.date = (String)data.get(DATE_KEY);

        if(data.containsKey(TRANSCRIPTION_KEY))
            this.transcription = (String)data.get(TRANSCRIPTION_KEY);

    }

}
