package com.cbre.tsandford.cbreinspector.model.notes;

import android.provider.ContactsContract;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notes {
    // region example of json format
    /*
            {
          "Info":{
            "notesType": "Hotel"
          },
          "Sections":{
            "Public Areas":{
              "Car Parking":{
                "codename": "notes_public_areas_hotels_car_parking",
                "content" : "She folded her handkerchief neatly.\nShe only paints with bold colors; she does not like pastels.\nI am never at home on Sundays."
              },
              "Food and Beverage":{
                "codename": "notes_public_areas_hotels_car_parking",
                "content" :"She folded her handkerchief neatly.\nShe only paints with bold colors; she does not like pastels.\nI am never at home on Sundays."
              }
            }
          }
        }
     */
    // endregion

    private String mPath;
    private Notes.Type mType;
    private List<Section> mSections;

    public static String INFO_KEY = "Info";
    public static String SECTIONS_KEY = "Sections";
    public static String NOTES_TYPE_KEY = "NotesType";
    public static String PATH_KEY = "Path";

    public Notes(){
        mSections = new ArrayList<>();
    }

    // todo make this work
    public void populate(Map<String, Object> data){
//        //Map<String, String> infoData = new HashMap<String, String>(data.get(INFO_KEY));
//
//        mPath = infoData.get(PATH_KEY);
//        mType = (Type) data.get(NOTES_TYPE_KEY);
//
//        List<Section> sectionData = new ArrayList<Section>;
//        //sectionData.addAll(data.get(SECTIONS_KEY));
//
//        mSections = sectionData;
    }

    //public Map<String, String> getHashMap_string(LinkedTreeMap )


    public void addSection(Section section){
        mSections.add(section);
    }

    public Map<String, Object> generateDataMap(){
        if(mType == Type.None ||
                mSections.size() == 0 ||
                mType == null){
            return null;
        }

        Map<String, Object> resultMaster = new HashMap<>();

        // add header info
        Map<String, String> info = new HashMap<>();
        info.put(NOTES_TYPE_KEY, mType.name());
        info.put(PATH_KEY, mPath);
        resultMaster.put(INFO_KEY, info);

        // add sections
        Map<String, Object> sectionMap = new HashMap<>();
        for(Section section: mSections){
            sectionMap.put(section.mName, section.getDataMap());
        }
        resultMaster.put(SECTIONS_KEY, sectionMap);

        return resultMaster;

    }

    // region Getters and Setters

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        this.mType = type;
    }

    public List<Section> getSections() {
        return mSections;
    }

    public void setSections(List<Section> sections) {
        this.mSections = sections;
    }

    // endregion

    // region Inner Classes and Enums

    public enum Type{
        None,
        Hotel
    }

    public static class Section {

        private List<NoteContent> mContent;
        private String mName;

        public Section(String name){
            mName = name;
            mContent = new ArrayList<>();
        }

        public Map<String, Object> getDataMap(){
            Map<String, Object> result = new HashMap<>();
            for(NoteContent content: mContent){
                result.put(content.mName, content.getDataMap());
            }
            return result;
        }

        public void addContent(NoteContent newContent){
            mContent.add(newContent);
        }

        // region Getters and Setters

        public List<NoteContent> getContent() {
            return mContent;
        }

        public void setContent(List<NoteContent> content) {
            this.mContent = content;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }

        // endregion
    }

    public static class NoteContent {

        private String mName;
        private String mCodename;
        private String mContent;
        private boolean mContentIsArray;
        private String mControlType;

        public static String CODENAME_KEY = "Codename";
        public static String CONTENT_KEY = "Content";
        public static String CONTROL_TYPE_KEY = "ControlType";
        public static String IS_ARRAY_KEY = "IsArray";

        public NoteContent(String name, String codename, String content, String controlType){
            mName = name;
            mCodename = codename;
            mContent = content;
            mContentIsArray = false;
            mControlType = controlType;
        }

        public NoteContent(String name, String codename, String content, String controlType, boolean contentIsArray){
            mName = name;
            mCodename = codename;
            mContent = content;
            mContentIsArray = contentIsArray;
            mControlType = controlType;
        }

        public Map<String, String> getDataMap(){
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put(CODENAME_KEY, mCodename);
            contentMap.put(CONTENT_KEY, mContent);
            contentMap.put("IsArray", Boolean.toString(mContentIsArray));
            contentMap.put(CONTROL_TYPE_KEY, mControlType);
            return contentMap;
        }

        // region Getters and Setter

        public String getCodename() {
            return mCodename;
        }

        public void setCodename(String codename) {
            this.mCodename = codename;
        }

        public String getContent() {
            return mContent;
        }

        public void setContent(String content) {
            this.mContent = content;
        }

        public String getName() {
            return mName;
        }

        public void setName(String name) {
            this.mName = name;
        }

        // endregion
    }

    // endregion

}
