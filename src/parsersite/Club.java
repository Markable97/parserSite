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
public class Club {
    
    String team_name;
    int division = 2; //Пока высший
    String logo;

    public Club(String team_name) {
        this.team_name = team_name;
        setLogo(team_name);
    }

    public void setLogo(String logо) {
        this.logo = (logо+".png").toLowerCase();
    }

    public String getTeam_name() {
        return team_name;
    }

    public int getDivision() {
        return division;
    }

    public String getLogo() {
        return logo;
    }
    
    
    
    
}
