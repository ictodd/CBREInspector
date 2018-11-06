package com.cbre.tsandford.cbreinspector.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.notes.Notes;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotes extends Fragment implements View.OnClickListener {

    ViewFlipper viewFlipper;
    ScrollView mainScrollView;

    public FragmentNotes() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = null;
        try {
            // Inflate the layout for this fragment
            returnView =  inflater.inflate(R.layout.fragment_notes, container, false);
        }catch(Exception ex){
            Log.d("TODD","Error on notes: " + ex.getMessage());
        }
        return returnView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFlipper = getView().findViewById(R.id.view_flipper);
        LinearLayout sectionsView = getView().findViewById(R.id.sections_linear_layout);
        SetUpButtonClickEvents(sectionsView);
        mainScrollView = getView().findViewById(R.id.notes_main_scroll_view);
    }

    public void onClick(View v){
        int id = v.getId();
        View includeView = null;
        int indexOfViewFlipper;

        switch(id){
            case R.id.btn_load_site_details_hotels:
                includeView = getActivity().findViewById(R.id.notes_site_details_hotels_include);
                break;
            case R.id.btn_load_construction_hotels:
                includeView = getActivity().findViewById(R.id.notes_construction_hotels_include);
                break;
            case R.id.btn_load_internal_finishes_hotels:
                includeView = getActivity().findViewById(R.id.notes_internal_finishes_hotels_include);
                break;
            case R.id.btn_load_public_areas_hotels:
                includeView = getActivity().findViewById(R.id.notes_public_areas_hotels_include);
                break;
            case R.id.btn_load_back_of_house_hotels:
                includeView = getActivity().findViewById(R.id.notes_back_of_house_hotels_include);
                break;
            case R.id.btn_load_rooms_hotels:
                includeView = getActivity().findViewById(R.id.notes_rooms_hotels_include);
                break;
            case R.id.btn_load_services_hotels:
                includeView = getActivity().findViewById(R.id.notes_services_hotels_include);
                break;
            case R.id.btn_load_capex_hotels:
                includeView = getActivity().findViewById(R.id.notes_capex_hotels_include);
                break;
            case R.id.btn_load_other_hotels:
                includeView = getActivity().findViewById(R.id.notes_other_hotels_include);
                break;
            case R.id.btn_load_operational_hotels:
                includeView = getActivity().findViewById(R.id.notes_operational_hotels_include);
                break;
        }

        if(includeView != null){
            indexOfViewFlipper = viewFlipper.indexOfChild(includeView);
            viewFlipper.setDisplayedChild(indexOfViewFlipper);
        }

        Utils.hideSoftKeyboard(getActivity());
        viewFlipper.requestFocus();
        mainScrollView.smoothScrollTo(0,0);
    }

    @Override
    public void onPause() {
        super.onPause();
        SyncNotes_AppToFile();
        //TEST_makeDataMap();
        //TEST_tryReadJsonFile();
    }

    @Override
    public void onResume() {
        super.onResume();
        SyncNotes_FileToApp();
        Utils.hideSoftKeyboard(getActivity());
        viewFlipper.requestFocus();
        mainScrollView.smoothScrollTo(0,0);

    }

    private void SyncNotes_AppToFile(){
        AppState.ActiveInspection.notes = GetHashMapOfNotes();
        AppState.ActiveInspection.save_notes();
    }

    private void SyncNotes_FileToApp(){
        AppState.ActiveInspection.restore_notes();
        PopulateEditTextFromHashMap(AppState.ActiveInspection.notes);
    }

    private void SetUpButtonClickEvents(LinearLayout view){
        Button btn;
        for (int i = 0; i < view.getChildCount(); i++) {
            View v = view.getChildAt(i);
            if (v instanceof android.widget.Button) {
                btn = (Button) v;
                btn.setOnClickListener(this);
            }
        }
    }

    private Map<String, String> GetHashMapOfNotes(){
        Map<String, String> result = new HashMap<>();
        ViewFlipper flipper = getActivity().findViewById(R.id.view_flipper);
        for(int i = 0; i < flipper.getChildCount(); i++){
            ViewGroup subLayout = (ViewGroup)viewFlipper.getChildAt(i);
            AddEditTextViewsToHashMap(subLayout, result);
        }

        return result;

    }

    private void TEST_makeDataMap(){
        Notes notes = new Notes();
        notes.setType(Notes.Type.Hotel);

        EditText noteField;
        String noteName;
        String noteContent;
        View tempView;

        ViewFlipper flipper = getActivity().findViewById(R.id.view_flipper);
        for(int i = 0; i < flipper.getChildCount(); i++){
            ViewGroup subLayout = (ViewGroup)viewFlipper.getChildAt(i);
            Notes.Section section = new Notes.Section(Integer.toString(subLayout.getId()));


            for(int j = 0; j < subLayout.getChildCount(); j ++){
                tempView = subLayout.getChildAt(j);
                if(tempView instanceof android.widget.EditText){
                    try{
                        noteField = (EditText)tempView;
                        if(noteField.getTag() != null){
                            noteName = noteField.getTag().toString();
                            noteContent = noteField.getText().toString();

                            Notes.NoteContent content = new Notes.NoteContent(noteName, noteName, noteContent, noteField.getClass().getName());
                            section.addContent(content);
                        }
                    } catch(Exception ex){
                        Log.d("TODD", "Failed to add EditText content. Error: " + ex.getMessage());
                    }
                }
            }

            notes.addSection(section);
        }

        String json = Utils.Json.getPrettyJsonObjects(notes.generateDataMap());
        Utils.WriteToFile(json, AppState.RootPath + "/test.json");
    }

    private void TEST_tryReadJsonFile(){
        String filepath = AppState.RootPath + "/test.json";
        Map<String,Object> dataMap = Utils.Json.getMapObjects(filepath);
        Notes notes = new Notes();
        notes.populate(dataMap);
    }


    private void AddEditTextViewsToHashMap(ViewGroup view, Map<String, String> resultToAddTo){
        EditText noteField;
        String noteName;
        String noteContent;
        View tempView;

        for(int i = 0; i < view.getChildCount(); i ++){
            tempView = view.getChildAt(i);
            if(tempView instanceof android.widget.EditText){
                try{
                    noteField = (EditText)tempView;
                    if(noteField.getTag() != null){
                        noteName = noteField.getTag().toString();
                        noteContent = noteField.getText().toString();
                        resultToAddTo.put(noteName, noteContent);
                    }
                } catch(Exception ex){
                    Log.d("TODD", "Failed to add EditText content. Error: " + ex.getMessage());
                }
            }
        }
    }

    private void PopulateEditTextFromHashMap(Map<String, String> notes){

        ViewFlipper flipper = getActivity().findViewById(R.id.view_flipper);

        for(int i = 0; i < flipper.getChildCount(); i++){
            ViewGroup subLayout = (ViewGroup)viewFlipper.getChildAt(i);

            EditText noteField;
            View tempView;

            for(int j = 0; j < subLayout.getChildCount(); j ++){
                tempView = subLayout.getChildAt(j);
                if(tempView instanceof android.widget.EditText){
                    try{
                        noteField = (EditText)tempView;
                        if(notes.containsKey(noteField.getTag().toString())){
                            noteField.setText(notes.get(noteField.getTag().toString()));
                        }
                    } catch(Exception ex){
                        Log.d("TODD", "Failed to get content from hash map. Error: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private void SetDummyText(){
        ViewFlipper flipper = getActivity().findViewById(R.id.view_flipper);
        for(int i = 0; i < flipper.getChildCount(); i++){
            ViewGroup subLayout = (ViewGroup)viewFlipper.getChildAt(i);

            EditText noteField;
            View tempView;

            for(int j = 0; j < subLayout.getChildCount(); j ++){
                tempView = subLayout.getChildAt(j);
                if(tempView instanceof android.widget.EditText){
                    try{
                        noteField = (EditText)tempView;
                        Utils.FillEditTextWithLorenIpsum(noteField);
                    } catch(Exception ex){
                        Log.d("TODD", "Failed to fill EditText content. Error: " + ex.getMessage());
                    }
                }
            }
        }
    }
}
