package barqsoft.footballscores.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Krenare Rexhepi on 12/15/2015.
 */
public class ParseTeams {
    String data;
    public static ArrayList<FootballData> urls = null;
    private static final String FIX = "fixtures";
    private static String result = "result";
    private static String homeTeam = "";
    private static String awayTeam = "";
    private static String homeTeamgoals = "";
    private static String awayTeamgoals = "";
    private static String homeTeamName = "homeTeamName";
    private static String awayTeamName = "awayTeamName";
    private static String goalsAwayTeam = "goalsAwayTeam";
    private static String goalsHomeTeam = "goalsHomeTeam";
    private static String game_date = "date";
    private static String date = "";
    private static String imgHome = "";
    private static String imgAway="";

    public ParseTeams(String data) {
        this.data = data;
    }

    public ArrayList<FootballData> parseTeams() {
        ArrayList<FootballData> allUrl = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray originalTitleArray = jsonObject.getJSONArray(FIX);

            for (int i = 0; i < originalTitleArray.length(); i++) {
                JSONObject movieresults = originalTitleArray.getJSONObject(i);
                homeTeam = movieresults.getString(homeTeamName);
                awayTeam = movieresults.getString(awayTeamName);
                date = movieresults.getString(game_date);
                JSONObject results = movieresults.getJSONObject(result);
                homeTeamgoals = results.getString(goalsHomeTeam);
                awayTeamgoals = results.getString(goalsAwayTeam);
                JSONObject links = movieresults.getJSONObject("_links").getJSONObject("homeTeam");
                imgHome = links.getString("href");
                JSONObject links_away = movieresults.getJSONObject("_links").getJSONObject("awayTeam");
                imgAway = links_away.getString("href");
                FootballData footballData = new FootballData(
                        homeTeam,
                        awayTeam,
                        date,
                        homeTeamgoals,
                        awayTeamgoals,
                        imgHome,
                        imgAway);
                allUrl.add(footballData);

            }


        } catch (JSONException e) {
            Log.e("", e.getMessage().toString());
        }

        urls = allUrl;
        return allUrl;
    }

    public ArrayList<FootballData> parseData() {
        ArrayList<FootballData> allUrl = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            String crestUrl = jsonObject.getString("crestUrl");
            String name = jsonObject.getString("name");

            FootballData footballData = new FootballData(name, crestUrl);
            allUrl.add(footballData);


        } catch (JSONException e) {
            Log.e("", e.getMessage().toString());
        }

        urls = allUrl;
        return allUrl;
    }
}
