package barqsoft.footballscores.WidgetsFootball;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.service.FootballData;
import barqsoft.footballscores.service.ParseTeams;
import barqsoft.footballscores.service.myFetchService;

/**
 * Created by Krenare Rexhepi on 12/22/2015.
 */
public class FootballWidgetProvider extends AppWidgetProvider {

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    String teamLogoUrl;
    String teamLogoUrlAway;

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);
    }

    Utilies u = new Utilies();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // context.startService(new Intent(context, myFetchService.class));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  getDataS("http://api.football-data.org/alpha/fixtures");
                    getDataS("http://api.football-data.org/v1/fixtures?timeFrame=n2");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        // We sleep for 3 seconds here to show how the empty view appears in the interim.
        // The empty view is set in the StackWidgetProvider and should be a sibling of the
        // collection view.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, FootballWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.football_widget_layout);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.list_view, intent);
            rv.setEmptyView(R.id.list_view, R.id.empty_view);

            if (footballDatas != null) {
                String date = footballDatas.get(i).getGameDate();
                String a = date.substring(0, date.indexOf("T"));
                String awayTeamgoals = footballDatas.get(i).getGoalsAwayTeam();
                if (awayTeamgoals.equals("null")) {
                    awayTeamgoals = "";
                }
                String homeTeamgoals = footballDatas.get(i).getGoalsAwayTeam();
                if (homeTeamgoals.equals("null")) {
                    homeTeamgoals = "";
                }
                try {
                    getDataForTeam(footballDatas.get(i).getHomeImgLink(), "home");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    getDataForTeam(footballDatas.get(i).getAwayTeamLink(), "away");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                rv.setTextViewText(R.id.home_name, footballDatas.get(i).getHomeTeamName());
                rv.setTextViewText(R.id.away_name, footballDatas.get(i).getAwayTeamName());
                rv.setTextViewText(R.id.score_view, homeTeamgoals + ":" + awayTeamgoals);
                rv.setTextViewText(R.id.match_date, a);
                if (footballDatasForTeam != null) {
                    teamLogoUrl = footballDatasForTeam.get(0).getTeamLink();
                    if (teamLogoUrl != null && teamLogoUrl.endsWith(".svg")) {
                        String svgLogoUrl = teamLogoUrl;
                        String filename = svgLogoUrl.substring(svgLogoUrl.lastIndexOf("/") + 1);
                        int wikipediaPathEndPos = svgLogoUrl.indexOf("/wikipedia/") + 11;
                        String afterWikipediaPath = svgLogoUrl.substring(wikipediaPathEndPos);
                        int thumbInsertPos = wikipediaPathEndPos + afterWikipediaPath.indexOf("/") + 1;
                        String afterLanguageCodePath = svgLogoUrl.substring(thumbInsertPos);
                        teamLogoUrl = svgLogoUrl.substring(0, thumbInsertPos);
                        teamLogoUrl += "thumb/" + afterLanguageCodePath;
                        teamLogoUrl += "/200px-" + filename + ".png";
                    }
                    u.LoadImageFromUrl(rv, R.id.home_img, teamLogoUrl, context);
                } else {
                    rv.setImageViewResource(R.id.home_img, (Utilies.getTeamCrestByTeamName(
                            footballDatas.get(i).getHomeTeamName())));
                }
                if (footballDatasForTeamAway != null) {
                    teamLogoUrlAway = footballDatasForTeamAway.get(0).getTeamLink();
                    if (teamLogoUrlAway != null && teamLogoUrlAway.endsWith(".svg")) {
                        String svgLogoUrl = teamLogoUrlAway;
                        String filename = svgLogoUrl.substring(svgLogoUrl.lastIndexOf("/") + 1);
                        int wikipediaPathEndPos = svgLogoUrl.indexOf("/wikipedia/") + 11;
                        String afterWikipediaPath = svgLogoUrl.substring(wikipediaPathEndPos);
                        int thumbInsertPos = wikipediaPathEndPos + afterWikipediaPath.indexOf("/") + 1;
                        String afterLanguageCodePath = svgLogoUrl.substring(thumbInsertPos);
                        teamLogoUrlAway = svgLogoUrl.substring(0, thumbInsertPos);
                        teamLogoUrlAway += "thumb/" + afterLanguageCodePath;
                        teamLogoUrlAway += "/200px-" + filename + ".png";
                    }

                    u.LoadImageFromUrl(rv, R.id.away_img, teamLogoUrlAway, context);
                } else {
                    rv.setImageViewResource(R.id.away_img, (Utilies.getTeamCrestByTeamName(
                            footballDatas.get(i).getAwayTeamName())));
                }
                try {
                    System.out.println(R.string.loading + i);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Intent p_intent = new Intent(context, MainActivity.class);
            //  PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, p_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //  rv.setOnClickPendingIntent(R.id.list_view, pendingIntent);

            PendingIntent toastPendingIntent = PendingIntent.getActivity(context, R.id.football_widget_items, p_intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.football_widget_layout, toastPendingIntent);

            //   rv.setPendingIntentTemplate(R.id.football_widget_layout, toastPendingIntent);
          /*   Intent toastIntent = new Intent(context, FootballWidgetProvider.class);
            toastIntent.setAction(FootballWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list_view, toastPendingIntent);*/
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

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
            m_connection.addRequestProperty("X-Auth-Token", String.valueOf(R.string.api_key));
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
            Log.e("",R.string.exception + e.getMessage());
        } finally {
            if (m_connection != null) {
                m_connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("", String.valueOf(R.string.error_closing_stream));
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
            Log.d("",String.valueOf(R.string.no_conn_server));
        }
    }
    private void getDataS(String timeFrame) throws JSONException {
        //Creating fetch URL
        Uri fetch_build;
        fetch_build = Uri.parse(timeFrame).buildUpon().build();
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token", String.valueOf(R.string.api_key));
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
                    Log.e("", String.valueOf(R.string.error_closing_stream));
                }
            }
        }
        if (JSON_data != null) {
            //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
            ParseTeams json = new ParseTeams(JSON_data);
            footballDatas = json.parseTeams();

        } else {
            //Could not Connect
            Log.d("",String.valueOf(R.string.no_conn_server));
        }
    }
    ArrayList<FootballData> footballDatas;
    ArrayList<FootballData> footballDatasForTeam;
    ArrayList<FootballData> footballDatasForTeamAway;

}
