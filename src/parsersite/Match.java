/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

import java.util.ArrayList;

/**
 *
 * @author Markable
 */
public class Match {
    String division;
    int tour;
    String dateMatch;
    String teamHome;
    int goalHome;
    int goalGuest;
    String teamGuest;
    ArrayList<Player> players;
    String stadium;
    String referee;
    String matchTransfer;

    public Match(String division, int tour, String dateMatch, String teamHome, int goalHome, int goalGuest, String teamGuest, String stadium, String referee) {
        //Если матч сыгран для вышки
        this.division = division;
        this.tour = tour;
        this.dateMatch = dateMatch;
        this.teamHome = teamHome;
        this.goalHome = goalHome;
        this.goalGuest = goalGuest;
        this.teamGuest = teamGuest;
        this.stadium = stadium;
        this.referee = referee;
    }

    public Match(String division, int tour, String dateMatch, String teamHome, int goalHome, int goalGuest, String teamGuest, ArrayList<Player> players, String stadium, String referee, String matchTransfer) {
        //Если игра сыграна и был перенос для otherDivs
        this.division = division;
        this.tour = tour;
        this.dateMatch = dateMatch;
        this.teamHome = teamHome;
        this.goalHome = goalHome;
        this.goalGuest = goalGuest;
        this.teamGuest = teamGuest;
        setPlayers(players);
        //this.players = players;
        this.stadium = stadium;
        this.referee = referee;
        this.matchTransfer = matchTransfer;
    }

    
    
    public Match(String division, int tour, String dateMatch, String teamHome, int goalHome, int goalGuest, String teamGuest, ArrayList<Player> players, String stadium, String referee) {
        //Когда нет переноса и игра сыграна
        this.division = division;
        this.tour = tour;
        this.dateMatch = dateMatch;
        this.teamHome = teamHome;
        this.goalHome = goalHome;
        this.goalGuest = goalGuest;
        this.teamGuest = teamGuest;
        setPlayers(players);
        //this.players = players;
        this.stadium = stadium;
        this.referee = referee;
    }
    
    public Match(String division, int tour, String teamHome, String teamGuest, String matchTransfer) {
        //Если перенос то указываем команду переноса (Может быть пепенос лиги)
        this.division = division;
        this.tour = tour;
        this.teamHome = teamHome;
        this.teamGuest = teamGuest;
        this.matchTransfer = matchTransfer;
    }

    public void setPlayers(ArrayList<Player> players) {
        //Копируем пришедший массив в поле класса
        this.players = new ArrayList<Player>();
        for(Player e : players){
            this.players.add(e);
        }
        //this.players = players;
    }

    public void setMatchTransfer(String matchTransfer) {
        this.matchTransfer = matchTransfer;
    }

    public String getDivision() {
        return division;
    }

    public int getTour() {
        return tour;
    }

    public String getDateMatch() {
        return dateMatch;
    }

    public String getTeamHome() {
        return teamHome;
    }

    public int getGoalHome() {
        return goalHome;
    }

    public int getGoalGuest() {
        return goalGuest;
    }

    public String getTeamGuest() {
        return teamGuest;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public String getStadium() {
        return stadium;
    }

    public String getReferee() {
        return referee;
    }

    public String getMatchTransfer() {
        return matchTransfer;
    }
 
    
    
    @Override
    public String toString() {
        String pl = null;
        if (players != null){
            pl = players.toString();
        }
        
        return "Match{" + "division=" + division + ", tour=" + tour + ", dateMatch=" + dateMatch + 
                ", teamHome=" + teamHome + ", goalHome=" + goalHome + ", goalGuest=" + goalGuest + 
                ", teamGuest=" + teamGuest + ",\n players=" + pl + ", stadium=" + stadium + 
                ", referee=" + referee + ", matchTransfer=" + matchTransfer + "}\n\n";
    }
    
    
}