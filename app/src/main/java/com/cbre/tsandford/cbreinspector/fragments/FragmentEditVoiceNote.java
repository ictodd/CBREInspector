package com.cbre.tsandford.cbreinspector.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cbre.tsandford.cbreinspector.R;

public class FragmentEditVoiceNote extends DialogFragment {

    public interface CustomDialogListener{
        void onDialogClose(String name);
    }

    private CustomDialogListener dialogListener;
    private Button confirmButton;
    private EditText nameEditText;

    private String name;

    public FragmentEditVoiceNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_voice_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirmButton = getView().findViewById(R.id.btn_update_metadata);
        nameEditText = getView().findViewById(R.id.voice_note_name);
        this.nameEditText.setText(name);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.onDialogClose(nameEditText.getText().toString());
                close();
            }
        });

        //this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void init(String name){
        this.name = name;
    }

    public void setDialogCloseHandler(CustomDialogListener listener){
        this.dialogListener = listener;
    }

    private void close(){
        this.dismiss();
    }

}
