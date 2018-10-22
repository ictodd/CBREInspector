package com.cbre.tsandford.cbreinspector.historic;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.cbre.tsandford.cbreinspector.AppState;
import com.cbre.tsandford.cbreinspector.InspectionSelectionActivity;
import com.cbre.tsandford.cbreinspector.R;

public class OldSplashScreenActivity extends AppCompatActivity implements View.OnClickListener{

    private static boolean TESTING_MODE = true;
    private static String ROOT_APP_PATH = "/cbre_inspector/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        LoadSpinner();
        GetPermissions();
        AppState.SetRootPath(ROOT_APP_PATH);

        if(TESTING_MODE){
            //Utils.RunTestingMethods();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Button loadBtn = findViewById(R.id.btn_load_app);
        loadBtn.setOnClickListener(this);
    }

    private void LoadSpinner(){
        Spinner spinner = findViewById(R.id.users_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.users_array,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        Spinner spinner = findViewById(R.id.users_spinner);
        AppState.CurrentUser = spinner.getSelectedItem().toString();

        Intent loadMainIntent = new Intent(this, InspectionSelectionActivity.class);
        Toast.makeText(this, "Welcome " + AppState.CurrentUser.substring(0,AppState.CurrentUser.indexOf(" ")) + ", happy inspecting!", Toast.LENGTH_LONG).show();

        startActivity(loadMainIntent);
    }


    public void GetPermissions(){
        final int REQUEST_PERMISSION=1;
        String[] permissions = new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };

        for(String permission: permissions){
            if(ContextCompat.checkSelfPermission(OldSplashScreenActivity.this, permission) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(OldSplashScreenActivity.this, new String[]{permission}, REQUEST_PERMISSION);
                Log.d("TODD","Granted : " + permission);

            } else {
                Log.d("TODD","Already granted : " + permission);
            }
        }
    }


}


