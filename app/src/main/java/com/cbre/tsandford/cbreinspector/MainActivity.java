package com.cbre.tsandford.cbreinspector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cbre.tsandford.cbreinspector.fragments.FragmentCamera;
import com.cbre.tsandford.cbreinspector.fragments.FragmentDictation;
import com.cbre.tsandford.cbreinspector.fragments.FragmentDraw;
import com.cbre.tsandford.cbreinspector.fragments.FragmentNewInspection;
import com.cbre.tsandford.cbreinspector.fragments.FragmentNotes;
import com.cbre.tsandford.cbreinspector.fragments.FragmentQuickNotes;
import com.cbre.tsandford.cbreinspector.misc.Utils;
import com.cbre.tsandford.cbreinspector.model.notes.NotesContentController;
import com.cbre.tsandford.cbreinspector.model.notes.NotesMainController;
import com.cbre.tsandford.cbreinspector.model.notes.NotesSectionController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get the intent that loaded this activity
        Intent intent = getIntent();
        String user = intent.getStringExtra("ActiveUser");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name) + " - " + AppState.ActiveInspection.property_name_formatted);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        this.fab = findViewById(R.id.fab);
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentQuickNotes frag = new FragmentQuickNotes();
                AppState.ActiveInspection.restore_notes();
                frag.setNotesTextContent(AppState.ActiveInspection.get_quick_note());
                frag.setOnCloseDialogListener(new FragmentQuickNotes.CustomDialogListener() {
                    @Override
                    public void OnQuickNotesClose(String quickNotes) {
                        AppState.ActiveInspection.update_quick_note(quickNotes);
                        AppState.ActiveInspection.save_notes();
                    }
                });
                frag.show(fm,"fragment_quick_notes");
            }
        });

        if(savedInstanceState == null){
            loadHomeFragment();
            //TEST_loadGenericNotesFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // todo implement options menu OR remove
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notes) {
            loadNewFragment(new FragmentNotes());
        } else if (id == R.id.nav_camera) {
            loadNewFragment(new FragmentCamera());
        } else if (id == R.id.nav_drawing) {
            loadNewFragment(new FragmentDraw());
        } else if (id == R.id.nav_dictate) {
            loadNewFragment(new FragmentDictation());
        } else if (id == R.id.nav_annotate){
            // todo make annotate app
        }

        Utils.hideSoftKeyboard(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadNewFragment(Fragment frag){
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FragPlaceholder, frag);
        ft.commit();
    }

    public void loadHomeFragment(){
        Fragment homeFragment = null;
        Class fragmentClass = FragmentNotes.class;
        try{
            homeFragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e){
            e.printStackTrace();
        }
        loadNewFragment(homeFragment);
    }

    private void TEST_loadGenericNotesFragment(){
        NotesMainController frag = new NotesMainController();

        // TEST
        List<NotesSectionController> testSectionControllers = new ArrayList<>();

        String sectionNames[] = new String[]{"Section 1", "Section 2", "Section 3"};
        NotesSectionController tmpSection;
        int count = 0;
        for(String name : sectionNames){
            count++;

            tmpSection = new NotesSectionController(name, getBaseContext());

            tmpSection.setContentControllers(TEST_getRandomContent(count, getBaseContext()));

            testSectionControllers.add(tmpSection);
        }

        frag.addSectionController(testSectionControllers);
        loadNewFragment(frag);

    }

    private List<NotesContentController> TEST_getRandomContent(int sec, Context context){
        String contentNames[] = null;
        switch (sec){
            case 1:
                contentNames = new String[]{
                        "Section 1 - Content 1",
                        "Section 1 - Content 2",
                        "Section 1 - Content 3"
                };
                break;
            case 2:
                contentNames = new String[]{
                        "Section 2 - Content 1",
                        "Section 2 - Content 2",
                        "Section 2 - Content 3"
                };
                break;
            case 3:
                contentNames = new String[]{
                        "Section 3 - Content 1",
                        "Section 3 - Content 2",
                        "Section 3 - Content 3"
                };
                break;
        }

        List<NotesContentController> result = new ArrayList<>();
        NotesContentController tmp;
        for(String n : contentNames){
            tmp = new NotesContentController(context);
            tmp.init(n, NotesContentController.Type.EditText);
            result.add(tmp);
        }
        return result;
    }

}
