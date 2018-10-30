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

    public Player(String name, String team, String amplua, String birthday, int number) {
        this.name = name;
        this.team = team;
        this.amplua = amplua;
        this.birthday = birthday;
        this.number = number;
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
        return "Player{" + "name=" + name + ", team=" + team + ", amplua=" + amplua + ", birthday=" + birthday + ", number=" + number + "}\n";
    }
    
    
    
}
