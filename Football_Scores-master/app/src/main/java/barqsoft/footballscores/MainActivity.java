package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import barqsoft.footballscores.service.FootballData;
import barqsoft.footballscores.service.ParseTeams;

public class MainActivity extends ActionBarActivity {
    public static int selected_match_id;
    public static int current_fragment = 2;
    public static String LOG_TAG = "MainActivity";
    private final String save_tag = "Save Test";
    private PagerFragment my_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            my_main = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, my_main)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(String.valueOf(R.string.current_page), my_main.mPagerHandler.getCurrentItem());
        outState.putInt(String.valueOf(R.string.selected_match), selected_match_id);

        getSupportFragmentManager().putFragment(outState,String.valueOf(R.string.my_main), my_main);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        current_fragment = savedInstanceState.getInt(String.valueOf(R.string.current_page));
        selected_match_id = savedInstanceState.getInt(String.valueOf(R.string.selected_match));
        my_main = (PagerFragment) getSupportFragmentManager().getFragment(savedInstanceState,String.valueOf(R.string.my_main));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
