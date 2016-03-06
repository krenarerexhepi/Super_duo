package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter {
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_LEAGUE = 5;
    public static final int COL_MATCHDAY = 9;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;
    public double detail_match_id = 0;
    public static final int COL_HOME_URL = 10;
    public static final int COL_AWAY_URL = 11;


    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public scoresAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        if (!CheckConectivity()) {
            Toast.makeText(mContext.getApplicationContext(), R.string.no_internet_conn, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(COL_HOME));
        mHolder.away_name.setText(cursor.getString(COL_AWAY));
        mHolder.date.setText(cursor.getString(COL_MATCHTIME));
        mHolder.score.setText(Utilies.getScores(cursor.getInt(COL_HOME_GOALS), cursor.getInt(COL_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(COL_ID);

        String homeUrl = cursor.getString(COL_HOME_URL);
        String awayUrl = cursor.getString(COL_AWAY_URL);

        if (homeUrl != null && homeUrl.endsWith(".svg")) {
            String svgLogoUrl = homeUrl;
            String filename = svgLogoUrl.substring(svgLogoUrl.lastIndexOf("/") + 1);
            int wikipediaPathEndPos = svgLogoUrl.indexOf("/wikipedia/") + 11;
            String afterWikipediaPath = svgLogoUrl.substring(wikipediaPathEndPos);
            int thumbInsertPos = wikipediaPathEndPos + afterWikipediaPath.indexOf("/") + 1;
            String afterLanguageCodePath = svgLogoUrl.substring(thumbInsertPos);
            homeUrl = svgLogoUrl.substring(0, thumbInsertPos);
            homeUrl += "thumb/" + afterLanguageCodePath;
            homeUrl += "/200px-" + filename + ".png";

            Glide.with(mContext.getApplicationContext())
                    .load(homeUrl)
                    .error(R.drawable.sample_icon)
                    .into(mHolder.home_crest);
        } else if (homeUrl != null && homeUrl.endsWith(".png")) {
            Glide.with(mContext.getApplicationContext())
                    .load(homeUrl)
                    .error(R.drawable.sample_icon)
                    .into(mHolder.home_crest);
        } else {
            mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                    cursor.getString(COL_HOME)));
        }

        if (awayUrl != null && awayUrl.endsWith(".svg")) {
            String svgLogoUrl = awayUrl;
            String filename = svgLogoUrl.substring(svgLogoUrl.lastIndexOf("/") + 1);
            int wikipediaPathEndPos = svgLogoUrl.indexOf("/wikipedia/") + 11;
            String afterWikipediaPath = svgLogoUrl.substring(wikipediaPathEndPos);
            int thumbInsertPos = wikipediaPathEndPos + afterWikipediaPath.indexOf("/") + 1;
            String afterLanguageCodePath = svgLogoUrl.substring(thumbInsertPos);
            awayUrl = svgLogoUrl.substring(0, thumbInsertPos);
            awayUrl += "thumb/" + afterLanguageCodePath;
            awayUrl += "/200px-" + filename + ".png";

            Glide.with(mContext.getApplicationContext())
                    .load(awayUrl)
                    .error(R.drawable.sample_icon)
                    .into(mHolder.away_crest);

        }else if (awayUrl != null && awayUrl.endsWith(".png")) {
            Glide.with(mContext.getApplicationContext())
                    .load(awayUrl)
                    .error(R.drawable.sample_icon)
                    .into(mHolder.away_crest);
        }  else {
            mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                    cursor.getString(COL_AWAY)));
        }
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if (mHolder.match_id == detail_match_id)

        {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE),context));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(context,cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        } else

        {
            container.removeAllViews();
        }
        // Based in Developing Accessible Applications to get focusable
        // container.setFocusable(true);
        // cursor.close();

    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public boolean CheckConectivity() {
        // get the connectivity manager service
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        // get info about the network
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // return true if we have networkinfo and are connected
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
}
