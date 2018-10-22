package com.cbre.tsandford.cbreinspector;

// static class for storing information about the app
// easier than transferring an object between activities

import android.os.Environment;

import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.Inspection;

public class AppState {

    public AppState(){}

    public static String RootPath;
    public static String CurrentUser;
    public static String TempPath;
    public static String TempJsonFilePath;
    public static String InspectionsPath;

    public static Inspection ActiveInspection;

    public static void SetRootPath(String pathFromDocs){
        String extStorageRoot = Environment.getExternalStorageDirectory().getPath();

        RootPath = Utils.GetFolder(extStorageRoot + pathFromDocs).getPath();
        TempPath = Utils.GetFolder(RootPath + "/tmp").getPath();
        InspectionsPath = Utils.GetFolder(RootPath + "/inspections").getPath();

        TempJsonFilePath = TempPath + "/temp_notes_storage.json";
    }
}
