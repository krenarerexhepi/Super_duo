package barqsoft.footballscores.WidgetsFootball;

/**
 * Created by Krenare Rexhepi on 12/16/2015.
 */
public class ListItems {
    public String homeTeamName,awayTeamName,homeScore, awayScore,matchDay;

    public ListItems(String homeTeamName,String awayTeamName, String homeScore,
                     String awayScore,String matchDay) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeScore = homeScore;
        this.awayScore=awayScore;
        this.matchDay=matchDay;
    }

}
