package barqsoft.footballscores.WidgetsFootball;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.service.FootballData;
import barqsoft.footballscores.service.ParseTeams;

/**
 * Created by Krenare Rexhepi on 12/22/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FootballWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FootballRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public class FootballRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        //     private static final int mCount = 10;
        private List<ListItems> mWidgetItems = new ArrayList<ListItems>();
        private Context mContext;
        private int mAppWidgetId;
        Utilies u = new Utilies();

        public FootballRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
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

        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            mWidgetItems.clear();
        }

        @Override
        public int getCount() {
            if (footballDatas != null) {
                return footballDatas.size();
            } else {
                return 0;
            }
        }

        // Bitmap img;
        String teamLogoUrl;
        String teamLogoUrlAway;
        RemoteViews rv;

        @Override
        public RemoteViews getViewAt(int position) {
            // position will always range from 0 to getCount() - 1.
            // We construct a remote views item based on our widget item xml file, and set the
            // text based on the position.

            rv = new RemoteViews(mContext.getPackageName(), R.layout.football_widget_items);

            String date = footballDatas.get(position).getGameDate();
            String a = date.substring(0, date.indexOf("T"));
            String awayTeamgoals = footballDatas.get(position).getGoalsAwayTeam();
            if (awayTeamgoals.equals("null")) {
                awayTeamgoals = "";
            }
            String homeTeamgoals = footballDatas.get(position).getGoalsAwayTeam();
            if (homeTeamgoals.equals("null")) {
                homeTeamgoals = "";
            }
            try {
                getDataForTeam(footballDatas.get(position).getHomeImgLink(), "home");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                getDataForTeam(footballDatas.get(position).getAwayTeamLink(), "away");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            rv.setTextViewText(R.id.home_name, footballDatas.get(position).getHomeTeamName());
            rv.setTextViewText(R.id.away_name, footballDatas.get(position).getAwayTeamName());
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
                u.LoadImageFromUrl(rv, R.id.home_img, teamLogoUrl, mContext);
            } else {
                rv.setImageViewResource(R.id.home_img, (Utilies.getTeamCrestByTeamName(
                        footballDatas.get(position).getHomeTeamName())));
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

                u.LoadImageFromUrl(rv, R.id.away_img, teamLogoUrlAway, mContext);
            } else {
                rv.setImageViewResource(R.id.away_img, (Utilies.getTeamCrestByTeamName(
                        footballDatas.get(position).getAwayTeamName())));
            }


            //   PendingIntent pendingIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0, p_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //  rv.setOnClickPendingIntent(R.id.football_widget_items, pendingIntent);

            try {
                System.out.println("Loading view " + position);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Return the remote views object.

            //   final Intent fillInIntent = new Intent();
            //   rv.setOnClickFillInIntent(R.id.football_widget_items, fillInIntent);
            //PendingIntent toastPendingIntent = PendingIntent.getBroadcast(mContext,0, p_intent,
            //        PendingIntent.FLAG_UPDATE_CURRENT);

            Intent p_intent = new Intent(mContext, MainActivity.class);
            rv.setOnClickFillInIntent(R.id.football_widget_items, p_intent);


            return rv;
        }


        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        ArrayList<FootballData> footballDatas;
        ArrayList<FootballData> footballDatasForTeam;
        ArrayList<FootballData> footballDatasForTeamAway;

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
                Log.d("",String.valueOf(R.string.no_conn_server));
            }
        }
    }
}
