/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

/**
 *
 * @author Markable
 */
public class Player {
    
    String name;
    String urlPlayer;
    String team;
    String amplua;
    String birthday;
    int number;
    int games;
    int goal;
    int assist;
    int yellow;
    int red;
    String article = "";
    int penalty;
    int penaltyOut;
    int ownGoal;

    public Player(String name){
        this.name = name;
    }
    
    public Player(int i, String name, String url){
        this.name  = name;
        this.urlPlayer = url;
    }
    
    public Player(String team, String name){
        this.team = team;
        if(name.startsWith("/player")){
            this.urlPlayer = name;
        }else{
            this.name = name;
        }
    }
    
    public Player(String team, String name, String urlPlayer, String birthday){
        this.team = team;
        this.name = name;
        this.urlPlayer = urlPlayer;
        this.birthday = birthday;
    }
    
    public Player(String name, String team, String amplua, String birthday, int number) {
        this.name = name;
        this.team = team;
        this.amplua = amplua;
        this.birthday = birthday;
        this.number = number;
    }
    
    public Player(String urlPlayer, int non, String amplua){
        this.urlPlayer = urlPlayer;
        this.amplua = amplua;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setGames(int games) {
        this.games = games;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public void setAmplua(String amplua) {
        this.amplua = amplua;
    }

    public void setAssist(int assist) {
        this.assist = assist;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public String getUrlPlayer() {
        return urlPlayer;
    }
    
    public int getGames() {
        return games;
    }

    public int getGoal() {
        return goal;
    }

    public int getAssist() {
        return assist;
    }

    public int getYellow() {
        return yellow;
    }

    public int getRed() {
        return red;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public void setPenaltyOut(int penaltyOut) {
        this.penaltyOut = penaltyOut;
    }

    public void setOwnGoal(int ownGoal) {
        this.ownGoal = ownGoal;
    }

    public int getPenalty() {
        return penalty;
    }

    public int getPenaltyOut() {
        return penaltyOut;
    }

    public int getOwnGoal() {
        return ownGoal;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    
    
    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public String getAmplua() {
        return amplua;
    }

    public String getBirthday() {
        return birthday;
    }

    public int getNumber() {
        return number;
    }

    public void setTeam(String team) {
        this.team = team;
    }
 
    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", url=" + urlPlayer + ", team=" + team + ", amplua=" + amplua + 
                ", birthday=" + birthday + ", number=" + number + ", games=" + games + 
                ", goal=" + goal + ", assist=" + assist + ", yellow=" + yellow + ", red=" + red + 
                ", article=" + article + ", penalty=" + penalty + ", penaltyOut=" + penaltyOut + 
                ", ownGoal=" + ownGoal + "}\n";
    }

    

    
  
}
