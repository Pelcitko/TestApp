package cz.tul.lp.testapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import cz.tul.lp.testapp.fragment.AddNoteFragment;
import cz.tul.lp.testapp.fragment.NotesListFragment;
import cz.tul.lp.testapp.R;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        NotesListFragment.OnNoteClickedListener,
        AddNoteFragment.OnAddNoteListener {

    private static final int REQUEST_ADD_NOTE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed();
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            this.onFabPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onFabPressed() {
        startActivity(new Intent(this, DrawActivity.class));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId())
        {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.pdfinboard:
                startActivity(new Intent(this, FileBrowsingActivity.class));
                break;
            default:
                Toast.makeText(this, item.toString() + " touched", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onNoteClicked(long id) {
        showNote(id);
    }

    private void showNote(long id){
            Intent i = new Intent(this, DrawActivity.class);
        // TODO: 13.04.2017 Přidat  EXTRA_ID do DrawActivity
//            i.putExtra(DrawActivity.EXTRA_ID, id);
            startActivity(i);
    }


    public void onAddNoteClicked(View v){
            Intent i = new Intent(this, DrawActivity.class);
            startActivityForResult(i, REQUEST_ADD_NOTE);
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_ADD_NOTE){
            if(resultCode != RESULT_OK)
                return;
            // TODO: 13.04.2017 Přidat EXTRA_TITLE a EXTRA_TEXT do DrawActivity
//            String title = data.getStringExtra(DrawActivity.EXTRA_TITLE);
//            String text = data.getStringExtra(DrawActivity.EXTRA_TEXT);

//            onAddNote(title, text);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onAddNote(String title, String text) {
//        Notes notes = new Notes(this);
//        long id = notes.insertNote(title, text);

//        if(id >= 0){
//            ((NotesListFragment) getSupportFragmentManager().findFragmentById(
//                    R.id.notes_list)).updateList();
//        } else{
//            Toast.makeText(this, R.string.none_notebook, Toast.LENGTH_LONG).show();
//        }
    }
}
