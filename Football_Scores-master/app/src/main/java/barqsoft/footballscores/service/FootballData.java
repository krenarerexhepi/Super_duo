package barqsoft.footballscores.service;

/**
 * Created by Krenare Rexhepi on 12/17/2015.
 */
public class FootballData {

    private String imgLink;
    private String self;
    private String href;
    private String http;
    private String soccerseason;
    private String fixtures;
    private String homeTeam;
    private String teamName;
    private String teamLink;
    private String homeTeamName;
    private String awayTeamName;
    private String goalsAwayTeam;
    private String goalsHomeTeam;
    private String gameDate;
    private String awayTeamLink;

    public FootballData(String homeTeamName, String awayTeamName, String gameDate, String goalsHomeTeam,
                        String goalsAwayTeam, String teamLink, String awayTeamLink) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.gameDate = gameDate;
        this.goalsHomeTeam = goalsHomeTeam;
        this.goalsAwayTeam = goalsAwayTeam;
        this.teamLink = teamLink;
        this.awayTeamLink = awayTeamLink;
    }

    public FootballData(String teamName, String imageLink) {
        this.teamName = teamName;
        this.teamLink = imageLink;
    }

    public String getHomeImgLink() {
        return teamLink;
    }

    public String getAwayTeamLink() {
        return awayTeamLink;
    }

    public String getHttp() {
        return http;
    }


    public String getTeamLink() {
        return teamLink;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getGoalsAwayTeam() {
        return goalsAwayTeam;
    }


    public String getGameDate() {
        return gameDate;
    }
}
