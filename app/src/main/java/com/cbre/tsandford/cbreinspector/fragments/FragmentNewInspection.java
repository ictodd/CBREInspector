package com.cbre.tsandford.cbreinspector.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cbre.tsandford.cbreinspector.R;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.Inspection;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentNewInspection extends DialogFragment {

    // for passing data back to calling fragment
    public interface CustomDialogListener{
        void onDialogClose(Map<String, String> new_inspection_data);
    }

    private CustomDialogListener mDialogListener;
    private EditText addressField;
    private DatePicker inspectionDatePicker;
    private Spinner inspectionPersonSpinner;
    private Spinner inspectionTypeSpinner;

    // region Properties to Manipulate behaviour

    private String headingText;
    private String buttonText;
    private boolean inspectionTypeEnabled;
    private boolean inspectorEnabled;

    // endregion

    // will be included only if editing
    private Map<String, String> inspectionData;

    public void InitProperties(Map<String, String> existingData, String heading, String buttonText, boolean inspectionTypeEnabled, boolean inspectorEnabled){
        this.inspectionData = existingData;

        this.headingText = heading;
        this.buttonText = buttonText;
        this.inspectionTypeEnabled = inspectionTypeEnabled;
        this.inspectorEnabled = inspectorEnabled;
    }

    public void InitProperties(String heading, String buttonText, boolean inspectionTypeEnabled, boolean inspectorEnabled){
        this.headingText = heading;
        this.buttonText = buttonText;
        this.inspectionTypeEnabled = inspectionTypeEnabled;
        this.inspectorEnabled = inspectorEnabled;
    }

    public FragmentNewInspection() {} // Required empty public constructor

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_inspection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addressField = getView().findViewById(R.id.new_inspection_address);
        addressField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        inspectionDatePicker = getView().findViewById(R.id.new_inspection_date);
        inspectionPersonSpinner = getView().findViewById(R.id.new_inspection_person);
        inspectionTypeSpinner = getView().findViewById(R.id.new_inspection_type);

        loadSpinners();

        Button createBtn = getView().findViewById(R.id.btn_create_new);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allValidInput()) return;
                if(mDialogListener != null){
                    mDialogListener.onDialogClose(generate_result());
                }
                close();
            }
        });

        TextView heading = getView().findViewById(R.id.new_inspection_heading);
        heading.setText(headingText);
        createBtn.setText(buttonText);
        inspectionPersonSpinner.setEnabled(inspectorEnabled);
        inspectionTypeSpinner.setEnabled(inspectionTypeEnabled);
        if(inspectionData != null){
            loadData();
        }

        this.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void loadData() {
        String inspectionType = "";
        String inspector = "";
        Date date = Utils.string_to_date(Utils.get_current_date());
        String address = "";

        if(inspectionData.containsKey(Inspection.NOTE_TYPE_KEY))
            inspectionType = inspectionData.get(Inspection.NOTE_TYPE_KEY);


        if(inspectionData.containsKey(Inspection.NAME_KEY))
            inspector = inspectionData.get(Inspection.NAME_KEY);

        if(inspectionData.containsKey(Inspection.ADDRESS_KEY))
            address = inspectionData.get(Inspection.ADDRESS_KEY);

        if(inspectionData.containsKey(Inspection.DATE_KEY))
            date = Utils.string_to_date(inspectionData.get(Inspection.DATE_KEY));

        this.addressField.setText(address);

        // todo replace all Date with Calendar...
        Calendar cal = Utils.toCalendar(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH); // months need to be 0 indexed for datePicker.UpdateDate()
        int day = cal.get(Calendar.DAY_OF_MONTH);
        this.inspectionDatePicker.updateDate(year, month, day);

        // load some defaults
        if(inspectionType == "")
            inspectionType = "Hotel";
        if(inspector == "")
            inspector = "Todd Sandford";

        ArrayAdapter typeAdapter = getArrayAdapter(R.array.inspection_note_types);
        this.inspectionTypeSpinner.setSelection(typeAdapter.getPosition(inspectionType));

        ArrayAdapter userAdapter = getArrayAdapter(R.array.principal_inspectors);
        this.inspectionPersonSpinner.setSelection(userAdapter.getPosition(inspector));
    }

    private boolean allValidInput() {

        // might decide more info dialogs are necessary in future.
        String infoDialogTitle = "Invalid Input";

        if (addressField.getText() == null ||
                addressField.getText().toString().equals("")){
            showInfoDialog(infoDialogTitle, "Please fill out the property address.");
            return false;
        }

        return true;
    }

    private Map<String, String> generate_result(){
        Map<String, String> result = new HashMap<>();

        String inspector;
        String address;
        Date date;
        String strDate;
        String type;

        inspector = inspectionPersonSpinner.getSelectedItem().toString();
        address = addressField.getText().toString();
        type = inspectionTypeSpinner.getSelectedItem().toString();

        date = getDateFromPicker(inspectionDatePicker);
        strDate = Utils.date_to_string(date);

        result.put(Inspection.ADDRESS_KEY, address);
        result.put(Inspection.NAME_KEY, inspector);
        result.put(Inspection.NOTE_TYPE_KEY, type);
        result.put(Inspection.DATE_KEY, strDate);

        // if data came in with root path, keep it
        if(inspectionData != null && inspectionData.containsKey(Inspection.PATH_KEY))
            result.put(Inspection.PATH_KEY, inspectionData.get(Inspection.PATH_KEY));

        return result;
    }

    private Date getDateFromPicker(DatePicker datePicker){
        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();


        String dateString = day + "/" + month + "/" + year;

        return Utils.string_to_date(dateString);

    }

    private void showInfoDialog(String title, String message){
        Utils.showInfoDialog(getActivity(), title, message);
    }

    private void loadSpinners(){
        bindSpinner(inspectionTypeSpinner, R.array.inspection_note_types);
        bindSpinner(inspectionPersonSpinner, R.array.principal_inspectors);
    }

    private void bindSpinner(Spinner spinner, int arrayResource){
        ArrayAdapter<CharSequence> adapter = getArrayAdapter(arrayResource);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private ArrayAdapter<CharSequence> getArrayAdapter(int arrayResource){
        return ArrayAdapter.createFromResource(getContext(),
                arrayResource,
                android.R.layout.simple_spinner_item);
    }

    public void setOnDialogCloseListener(CustomDialogListener eventListener){
        this.mDialogListener = eventListener;
    }

    private void close(){
        this.dismiss();
    }

}
