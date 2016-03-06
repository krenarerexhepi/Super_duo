package barqsoft.footballscores;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.ExecutionException;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies {

    // KR: Added some other Liga
    public static final int PREMIER_LEGAUE = 354;
    public static final int BUNDESLIGA = 351;
    public static final int BUNDESLIGA1 = 394;
    public static final int BUNDESLIGA2 = 395;
    public static final int LIGUE1 = 396;
    public static final int LIGUE2 = 397;
    public static final int PREMIER_LEAGUE = 398;
    public static final int PRIMERA_DIVISION = 399;
    public static final int SEGUNDA_DIVISION = 400;
    public static final int SERIE_A = 401;
    public static final int PRIMERA_LIGA = 402;
    public static final int Bundesliga3 = 403;
    public static final int EREDIVISIE        = 404;
    public static final int CHAMPIONS2015_2016 = 405;

    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return context.getString(R.string.seriaA);
            case PREMIER_LEGAUE:
                return context.getString(R.string.premierleague);
            case CHAMPIONS2015_2016:
                return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.primeradivison);
            case BUNDESLIGA:
                return context.getString(R.string.bundesliga);
            case BUNDESLIGA1:
                return context.getString(R.string.bundesliga_1);
            case BUNDESLIGA2:
                return context.getString(R.string.bundesliga_2);
            case LIGUE1:
                return context.getString(R.string.ligue_1);
            case LIGUE2:
                return context.getString(R.string.ligue_2);
            case SEGUNDA_DIVISION:
                return context.getString(R.string.second_league);
            case PRIMERA_LIGA:
                return context.getString(R.string.primera_ligaa);
            case PREMIER_LEAGUE:
                return  context.getString(R.string.premierleague);
            case Bundesliga3:
                return context.getString(R.string.bundesliga_3);
             default:
                return context.getString(R.string.not_known);
        }
    }

    public static String getMatchDay(int match_day, int league_num,Context context) {
        if (league_num == CHAMPIONS2015_2016) {
            if (match_day <= 6) {
                return context.getString(R.string.group_stages);
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.Quarter);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.SemiFinal);
            } else {
                return context.getString(R.string.Final);
            }
        } else {
            return context.getString(R.string.matchay) + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    // Aded icons for the other teams
    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.sample_icon;
        }
         switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
             // I can not put names of team in String because when we use case they are in run state and they need to ne known from users
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            case "Chelsea FC":
                return R.drawable.chelsea;
            case "Arsenal FC":
                return R.drawable.arsenal;
            case "AS Roma":
                return R.drawable.as_roma;
            case "Olympiacos F.C.":
                return R.drawable.olympiacos_fc;
            case "Dynamo Kyiv":
                return R.drawable.dynamo_kyiv;
            case "Valencia CF":
                return R.drawable.valencia_cf;
            case "KAA Gent":
                return R.drawable.kaa_gent;
            case "Bayer Leverkusen":
                return R.drawable.bayer_leverkusen;
            case "FC Barcelona":
                return R.drawable.fc_barcelona;
            case "FC Porto":
                return R.drawable.pc_porto;
            case "FC Bayern Munchen":
                return R.drawable.fc_bayermunchen;
             case "Watford FC":
                 return R.drawable.watfordfc;
             case "AC Milan":
                 return R.drawable.ac_milan;
              case "Real Madrid":
                 return R.drawable.real_madrid_cf;
             case "Sevilla":
                 return R.drawable.sevilla;
             case "Ason villa":
             return  R.drawable.aston_villa;
             case "Bayer leverkusen":
                 return  R.drawable.bayer_leverkusen;
             case "Cordoba":
                 return R.drawable.cordoba;
             default:
                return R.drawable.sample_icon;
        }
    }

    public void LoadImageFromUrl(RemoteViews views, int viewId, String imageUrl , Context mContext) {
        Bitmap bitmap = null;
        // try to load the image into a bitmap from given url
        try {
            bitmap = Glide.with(mContext.getApplicationContext())
                    .load(imageUrl)
                    .asBitmap()
                    .error(R.drawable.sample_icon)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d("", R.string.error_retrieving_image + imageUrl, e);
        }

        // if bitmap loaded update the given imageview
        if (bitmap != null) {
            views.setImageViewBitmap(viewId, bitmap);
        }
    }

}
