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
    String team;
    String amplua;
    String birthday;
    int number;
    int games;
    int goal;
    int assist;
    int yellow;
    int red;

    public Player(String team, String name){
        this.team = team;
        this.name = name;
    }
    
    public Player(String name, String team, String amplua, String birthday, int number) {
        this.name = name;
        this.team = team;
        this.amplua = amplua;
        this.birthday = birthday;
        this.number = number;
    }

    public void setGames(int games) {
        this.games = games;
    }

    public void setGoal(int goal) {
        this.goal = goal;
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

    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", team=" + team + ", amplua=" + amplua + ", birthday=" + birthday + ", number=" + number + ", games=" + games + 
                ", goal=" + goal + ", assist=" + assist + ", yellow=" + yellow + ", red=" + red + '}'+'\n';
    }

    
   
}
