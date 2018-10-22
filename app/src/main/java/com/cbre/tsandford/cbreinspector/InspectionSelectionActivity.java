package com.cbre.tsandford.cbreinspector;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cbre.tsandford.cbreinspector.fragments.FragmentNewInspection;
import com.cbre.tsandford.cbreinspector.misc.CustomListView;
import com.cbre.tsandford.cbreinspector.misc.PromptRunnable;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.Inspection;
import com.cbre.tsandford.cbreinspector.model.InspectionListAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class InspectionSelectionActivity extends AppCompatActivity {

    private static String ROOT_APP_PATH = "/cbre_inspector/";
    List<Inspection> saved_inspections = new ArrayList<>();
    ListView list;
    InspectionListAdapter custom_adapter;

    ImageView btnInspectionNew;
    ImageView btnInspectionLoad;
    ImageView btnInspectionDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.GetPermissions(this);

        AppState.SetRootPath(ROOT_APP_PATH);
        setContentView(R.layout.activity_inspection_selection);

        Toolbar toolbar = findViewById(R.id.toolbar_test);
        setSupportActionBar(toolbar);

        PopulateInspections();

        custom_adapter = new InspectionListAdapter(this, this.saved_inspections);
        list = findViewById(R.id.inspections_list);
        list.setAdapter(custom_adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppState.ActiveInspection = saved_inspections.get(position);
            }
        });

        this.btnInspectionDelete = findViewById(R.id.btn_delete_inspection);
        this.btnInspectionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteActiveInspection();
            }
        });

        this.btnInspectionLoad = findViewById(R.id.btn_load_inspection);
        this.btnInspectionLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppState.ActiveInspection != null) {
                    beginLoadingProcess();
                }
            }
        });

        this.btnInspectionNew = findViewById(R.id.btn_new_inspection);
        this.btnInspectionNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentNewInspection frag = new FragmentNewInspection();
                frag.InitProperties(
                        "Create New Inspection",
                        "Create",
                        true,
                        true);
                frag.setOnDialogCloseListener(new FragmentNewInspection.CustomDialogListener() {
                    @Override
                    public void onDialogClose(Map<String, String> new_inspection_data) {
                        makeNewInspection(new_inspection_data);
                    }
                });
                frag.show(fm, "fragment_new_inspection");
            }
        });
    }

    private void beginLoadingProcess() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentNewInspection fragEdit = new FragmentNewInspection();
        final Activity activity = this;
        final Map<String, String> info_to_pass = AppState.ActiveInspection.get_map_of_info();
        fragEdit.InitProperties(
                info_to_pass,
                "Load Inspection",
                "Load",
                false,
                true);
        fragEdit.setOnDialogCloseListener(new FragmentNewInspection.CustomDialogListener() {
            @Override
            public void onDialogClose(Map<String, String> new_inspection_data) {
                if(!info_to_pass.equals(new_inspection_data)){
                    AppState.ActiveInspection.update_info(new_inspection_data);
                    custom_adapter.notifyDataSetChanged();
                }
                Intent loadMainIntent = new Intent(activity, MainActivity.class);
                startActivity(loadMainIntent);
            }
        });

        fragEdit.show(fm,"fragment_load_slash_edit");
    }

    private void deleteActiveInspection(){
        final Activity activity = this;
        if(AppState.ActiveInspection != null){
            Utils.showYesNoDialog(
                    this,
                    "Confirm Delete",
                    "Are you sure you want to delete the '" + AppState.ActiveInspection.property_name_formatted + "' inspection? This action cannot be undone.",
                    new PromptRunnable(){
                        @Override
                        public void run() {
                            File inspectionRoot = new File(AppState.ActiveInspection.root_path);
                            if(Utils.deleteRecursive(inspectionRoot)){
                                custom_adapter.remove(AppState.ActiveInspection);
                                custom_adapter.notifyDataSetChanged();
                                list.clearChoices();
                                list.requestLayout();
                            } else {
                                Utils.showInfoDialog(
                                        activity,
                                        "Error Deleting",
                                        "Could not delete inspection."
                                );
                            }
                        }
                    },
                    new PromptRunnable(){
                        @Override
                        public void run() {
                            // dismiss on no
                        }
                    });
        }
    }

    private void makeNewInspection(Map<String, String> new_inspection_data){
        String name = null;
        String address = null;
        Date date = new Date();
        String type = null;

        if(new_inspection_data.containsKey(Inspection.NAME_KEY))
            name = new_inspection_data.get(Inspection.NAME_KEY);

        if(new_inspection_data.containsKey(Inspection.ADDRESS_KEY))
            address = new_inspection_data.get(Inspection.ADDRESS_KEY);

        if(new_inspection_data.containsKey(Inspection.DATE_KEY))
            date = Utils.string_to_date(new_inspection_data.get(Inspection.DATE_KEY));

        if(new_inspection_data.containsKey(Inspection.NOTE_TYPE_KEY))
            type = new_inspection_data.get(Inspection.NOTE_TYPE_KEY);

        Inspection newInspection = new Inspection(address, date, name, type);
        custom_adapter.add(newInspection);
        custom_adapter.notifyDataSetChanged();
    }

    private void PopulateInspections() {

        File rootInspectionsFolder = Utils.GetFolder(AppState.InspectionsPath);
        Inspection tempNewInspection;
        File[] files = rootInspectionsFolder.listFiles();

        if(files != null) {
            for (int i = 0; i < files.length; ++i) {

                File file = files[i];
                if(!file.getPath().contains(".")) {
                    tempNewInspection = PopulateInspection(file);
                    if (tempNewInspection != null) saved_inspections.add(tempNewInspection);
                }
            }
        }
    }

    private Inspection PopulateInspection(File file) {
        Inspection result = new Inspection(file.getPath());
        return result;
    }
}
