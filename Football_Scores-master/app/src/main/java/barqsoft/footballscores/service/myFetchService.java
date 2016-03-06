package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.DB.DatabaseContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class myFetchService extends IntentService {
    public static final String LOG_TAG = "myFetchService";
    public myFetchService() {
        super("myFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            getData("n2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            getData("p2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return;
    }

    private void getData(String timeFrame) throws JSONException {

        //Creating fetch URL
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        Log.v(LOG_TAG, R.string.url_find +fetch_build.toString()); //log spam
       // Uri fetch_build = Uri.parse(BASE_URL).buildUpon().build();
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();

        } catch (Exception e) {
            //if (e.getMessage().contains("Unable to resolve host")) {
            //  }
            Log.e(LOG_TAG, R.string.exception + e.getMessage());

            //Toast.makeText(getApplicationContext(), "No internet connection !!", Toast.LENGTH_SHORT).show();
           // return;

        } finally {
            if (m_connection != null) {
                m_connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,String.valueOf(R.string.error_closing_stream));
                }
            }
        }
        if (JSON_data != null) {
            //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
            JSONArray matches = new JSONObject(JSON_data).getJSONArray("fixtures");
            if (matches.length() == 0) {
                //if there is no data, call the function on dummy data
                //this is expected behavior during the off season.
                processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                return;
            }

            processJSONdata(JSON_data, getApplicationContext(), true);
        } else {
            //Could not Connect
            Log.d(LOG_TAG, String.valueOf(R.string.no_conn_server));
        }
    }


    private void processJSONdata(String JSONdata, Context mContext, boolean isReal) {
        //JSON data
        // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
        // be updated. Feel free to use the codes
        ContentValues[] insert_data =null;

        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";
         String imgHome="";
         String imgAway="";

        //Match data
        String League = null;
        String mDate = null;
        String mTime = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;

        String PREMIER_LEGAUE = "354";
        String BUNDESLIGA = "351";
        String BUNDESLIGA1 = "394";
        String BUNDESLIGA2 = "395";
        String LIGUE1 = "396";
        String LIGUE2 = "397";
        String PREMIER_LEAGUE = "398";
        String PRIMERA_DIVISION = "399";
        String SEGUNDA_DIVISION = "400";
        String SERIE_A = "401";
        String PRIMERA_LIGA = "402";
        String Bundesliga3 = "403";
        String EREDIVISIE = "404";
        String CHAMPIONS2015_2016 = "405";
        String DUMMYDATA = "357";

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);
            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector<ContentValues>(matches.length());
            for (int i = 0; i < matches.length(); i++) {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).
                        getString("href");
                League = League.replace(SEASON_LINK, "");


                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.

                if (League.equals(BUNDESLIGA) ||
                        League.equals(BUNDESLIGA1) ||
                        League.equals(BUNDESLIGA2) ||
                        League.equals(Bundesliga3) ||
                        League.equals(SERIE_A) ||
                        League.equals(LIGUE1) ||
                        League.equals(LIGUE2) ||
                        League.equals(CHAMPIONS2015_2016) ||
                        League.equals(SEGUNDA_DIVISION) ||
                        League.equals(PREMIER_LEAGUE) ||
                        League.equals(PRIMERA_LIGA) ||
                        League.equals(EREDIVISIE) ||
                        League.equals(PRIMERA_DIVISION) ||
                        League.equals(DUMMYDATA)) {
                    match_id = match_data.getJSONObject(LINKS).getJSONObject(SELF).
                            getString("href");

                    match_id = match_id.replace(MATCH_LINK, "");

                    JSONObject links = match_data.getJSONObject("_links").getJSONObject("homeTeam");
                    imgHome = links.getString("href");
                    JSONObject links_away = match_data.getJSONObject("_links").getJSONObject("awayTeam");
                    imgAway = links_away.getString("href");

                    try {
                        getDataForTeam(imgHome, "home");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        getDataForTeam(imgAway, "away");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        match_id = match_id + Integer.toString(i);
                    }
                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0, mDate.indexOf("T"));
                    SimpleDateFormat match_date = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    match_date.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date parseddate = match_date.parse(mDate + mTime);
                        SimpleDateFormat new_date = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        new_date.setTimeZone(TimeZone.getDefault());
                        mDate = new_date.format(parseddate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0, mDate.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentdate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate = mformat.format(fragmentdate);
                        }
                    } catch (Exception e) {
                        Log.d(LOG_TAG, "error here!");
                        Log.e(LOG_TAG, e.getMessage());
                    }
                    Home = match_data.getString(HOME_TEAM);
                    Away = match_data.getString(AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    Away_goals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    match_day = match_data.getString(MATCH_DAY);

                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.scores_table.MATCH_ID, match_id);
                    match_values.put(DatabaseContract.scores_table.DATE_COL, mDate);
                    match_values.put(DatabaseContract.scores_table.TIME_COL, mTime);
                    match_values.put(DatabaseContract.scores_table.HOME_COL, Home);
                    match_values.put(DatabaseContract.scores_table.AWAY_COL, Away);
                    match_values.put(DatabaseContract.scores_table.HOME_GOALS_COL, Home_goals);
                    match_values.put(DatabaseContract.scores_table.AWAY_GOALS_COL, Away_goals);
                    match_values.put(DatabaseContract.scores_table.LEAGUE_COL, League);
                    match_values.put(DatabaseContract.scores_table.MATCH_DAY, match_day);
                    match_values.put(DatabaseContract.scores_table.HOME_ICON_URL, footballDatasForTeam.get(0).getTeamLink());
                    match_values.put(DatabaseContract.scores_table.AWAY_ICON_URL, footballDatasForTeamAway.get(0).getTeamLink());

                    values.add(match_values);
                }
            }

            int inserted_data = 0;
            insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);

            inserted_data = mContext.getContentResolver().bulkInsert(
                    DatabaseContract.BASE_CONTENT_URI, insert_data);

            Log.v(LOG_TAG, String.valueOf(R.string.insert_data_ok) + String.valueOf(inserted_data));
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

    ArrayList<FootballData> footballDatasForTeam;
    ArrayList<FootballData> footballDatasForTeamAway;
    private void getDataForTeam(String link, String team) throws JSONException {
        Uri fetch_build;
        fetch_build = Uri.parse(link).buildUpon().build();
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token", getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            JSON_data = buffer.toString();
        } catch (Exception e) {
            Log.e("", R.string.exception + e.getMessage());
        } finally {
            if (m_connection != null) {
                m_connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("",String.valueOf(R.string.error_closing_stream));
                }
            }
        }
        if (JSON_data != null) {
            //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
            ParseTeams json = new ParseTeams(JSON_data);

            if (team.equals("home")) {
                footballDatasForTeam = json.parseData();

            } else {
                footballDatasForTeamAway = json.parseData();

            }
        } else {
            //Could not Connect
            Log.d("", String.valueOf(R.string.no_conn_server));
        }
    }


}

