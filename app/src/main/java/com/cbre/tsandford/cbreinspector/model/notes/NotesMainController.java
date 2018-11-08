package com.cbre.tsandford.cbreinspector.model.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ViewFlipper;

import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.misc.voice.PlayButton;

import java.util.ArrayList;
import java.util.List;

public class NotesMainController extends Fragment{

    private List<NotesSectionController> mSectionControllers;

    private ViewFlipper mContentViewFlipper;
    private ScrollView mContentScrollView;
    private LinearLayout mSectionsContainer;

    private Context mContext;

    // region Main Event Overrides

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.generic_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentViewFlipper = getView().findViewById(R.id.generic_view_flipper);
        mContentScrollView = getView().findViewById(R.id.generic_content_scroll_view);
        mSectionsContainer = getView().findViewById(R.id.generic_sections_container);
        mContext = getView().getContext();
        addAllSectionButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // endregion

    public void addSectionController(List<NotesSectionController> controllers){
        for(NotesSectionController controller : controllers){
            addSectionController(controller);
        }
    }

    public void addSectionController(NotesSectionController controller){
        if(mSectionControllers == null)
            mSectionControllers = new ArrayList<>();

        controller.setCallback(new NotesSectionController.SectionButtonCallback() {
            @Override
            public void OnSectionButtonClicked(LinearLayout sectionLayout) {
                changeViewFlipperView(sectionLayout);
            }
        });

        mSectionControllers.add(controller);
    }

    private void addAllSectionButtons(){
        mSectionsContainer.removeAllViews();
        mContentViewFlipper.removeAllViews();

        for(NotesSectionController sectionController : mSectionControllers){
            mContentViewFlipper.addView(sectionController.getContentLayout());
            mSectionsContainer.addView(sectionController.getButton());
        }
    }

    public void changeViewFlipperView(LinearLayout newView){
        int id;
        if(newView != null){
            id = mContentViewFlipper.indexOfChild(newView);
            mContentViewFlipper.setDisplayedChild(id);
        }

        Utils.hideSoftKeyboard(getActivity());
        mContentViewFlipper.requestFocus();
        mContentScrollView.smoothScrollTo(0,0);
    }
}
