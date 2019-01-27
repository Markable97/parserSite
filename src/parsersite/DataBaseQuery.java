/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Markable
 * Загрузка матча с составами, в которых есть активность в бд с тригггерами для обновления других таблиц
 */
public class DataBaseQuery {
    
    private static String user = "root";
    private static String password = "7913194";
    private static String url = "jdbc:mysql://localhost:3306/football_main";
    
    private static String sqlInserClub = "insert into teams\n" +
                "set id_division = ?, team_name = ?, logo = ?;";
    
    private static String sqlInser = "insert into players\n" +
                "set id_team = (select id_team from teams where team_name like ?), \n" +
                "name = ?, id_amplua = (select id_amplua from amplua where name_amplua like ?),\n" +
                "birthdate = ?, number = ?, photo = ?;";
    /*private static String sqlUpdate = "update main_football.players_statistics\n" +
                "set games = ?, goal = ?, assist = ?, yellow_card = ?, red_card = ?\n" +
                "where players_id_player = (select id_player \n" +
                                            "from main_football.players\n" +
                                            "join teams on teams_id_team = id_team \n" +
                                            "where name like ? and team_name like ?);";*/
    private static String sqlInsertMatch = "insert into matches\n" +
"set id_season = (select id_season from sesons\n" +
"					where curdate() between year_start and year_end),\n" +
"	id_division = ?,\n" +
"    id_tour = ?,\n" +
"    team_home = (select id_team from teams \n" +
"		    where team_name like ?),\n" +
"	goal_home = ?, goal_guest = ?,\n" +
"    team_guest = (select id_team from teams \n" +
"		    where team_name like ?),\n" +
"	m_date = ?, id_stadium = (select id_stadium from stadiums where name_stadium like ?),\n" +
"    id_referee = (select id_staff from staff where staff_name like ?),\n" +
"    transfer = ?;";//sql для вставки в таблицу match
    
    private static String sqlInsertPlayerInMatch = "insert into players_in_match\n" +
"set id_match = (select id_match from v_matches\n" +
"				where team_home like ? and team_guest like ? and id_tour = ?),\n" +
"	id_player = (select id_player from players where name like ? and "
            + "id_team = (select id_team from teams where team_name in (?) ) ),\n" +
"    count_goals = ?, count_assist = ?, yellow = ?, red = ?, penalty = ?, penalty_out = ?, own_goal = ?;";
    
    private static String sqlInsertStaff = "insert into staff\n" +
                                            "set id_staff = ?,\n" +
                                            "staff_name = ?;";
    private static String sqlProcInsertStaff = "CALL insertStaff(?);";
    private static String sqlPocPlayerInMatche = "CALL insPlayerInMatche(?,?,?,?,?,?,?,?,?,?,?,?);";
    
    private static PreparedStatement insertPlayer;
    
    //private static PreparedStatement updatePlayerStatistic;
    private static PreparedStatement insertTeam;
    private static PreparedStatement insertMatch;
    //private static PreparedStatement insertPlayerInMatch;
    //private static PreparedStatement insertStaff;
    
    private static CallableStatement  procStaff;
    private static CallableStatement  procPlayerMatch;
    
    private static Connection connect;

    private ArrayList<Match> matches;
    ArrayList<Player> listPlayer;
    ArrayList<Club> listClub;
    
    /*public DataBaseQuery(ArrayList<Player> list) {
        this.listPlayer = list;
        connection(listPlayer);
    }*/
    
    public DataBaseQuery(ArrayList<Match> matches){
        this.matches = matches;
        connection(matches);
    }

    public DataBaseQuery(ArrayList<Player> player, ArrayList<Club> club){
        this.listClub = club;
        this.listPlayer = player;
        connection(listPlayer, listClub);
    }
    
    private static void connection(ArrayList<Match> matches){
        try {
            connect = DriverManager.getConnection(url, user, password);
            insertMatch = connect.prepareStatement(sqlInsertMatch);
            //insertPlayerInMatch = connect.prepareStatement(sqlInsertPlayerInMatch);
            procStaff = connect.prepareCall(sqlProcInsertStaff);
            procPlayerMatch = connect.prepareCall(sqlPocPlayerInMatche);
            //insertStaff = connect.prepareStatement(sqlInsertStaff);
            for(Match m : matches){
                System.out.println("Судья для добавения = " + m.getReferee());
                procStaff.setString(1, m.getReferee());
                procStaff.execute();
                System.out.println("Судья complete = " + m.getReferee());
                System.out.println("Матч для добавения = " + m.getTeamHome() + " " + m.getTeamGuest());
                switch(m.getDivision()){
                    case "Высший дивизион":
                        insertMatch.setInt(1, 1);
                        break;
                    case "Первый дивизион":
                        insertMatch.setInt(1, 2);
                        break;
                }
                insertMatch.setInt(2, m.getTour());
                insertMatch.setString(3, m.getTeamHome());
                insertMatch.setInt(4, m.getGoalHome());
                insertMatch.setInt(5, m.getGoalGuest());
                insertMatch.setString(6, m.getTeamGuest());
                insertMatch.setString(7, m.getDateMatch());
                insertMatch.setString(8, m.getStadium());
                insertMatch.setString(9, m.getReferee());
                insertMatch.setString(10, m.getMatchTransfer());
                insertMatch.executeUpdate();
                System.out.println("complete = " + m.getTour() + " " + m.getTeamHome() + " " +  m.getTeamGuest());
                for(Player p : m.getPlayers()){
                    System.out.println("\t\tИгрк для добавления = " + p.getName());
                    procPlayerMatch.setString(1, m.getTeamHome());
                    procPlayerMatch.setString(2, m.getTeamGuest());
                    procPlayerMatch.setInt(3, m.getTour());
                    procPlayerMatch.setString(4,p.getName());
                    procPlayerMatch.setString(5,p.getTeam());
                    procPlayerMatch.setInt(6,p.getGoal());
                    procPlayerMatch.setInt(7,p.getAssist());
                    procPlayerMatch.setInt(8,p.getYellow());
                    procPlayerMatch.setInt(9,p.getRed());
                    procPlayerMatch.setInt(10, p.getPenalty());
                    procPlayerMatch.setInt(11, p.getPenaltyOut());
                    procPlayerMatch.setInt(12, p.getOwnGoal());
                    procPlayerMatch.execute();
                    System.out.println("Игрок добавлен = " + p.getName() + " " + p.getTeam());
                }
                System.out.println("Конец матча \n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                connect.close();
                insertMatch.close();
                procStaff.close();
                procPlayerMatch.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void connection(ArrayList<Player> listPlayer, ArrayList<Club> listClub) {
        try {
            connect = DriverManager.getConnection(url, user, password);
            insertPlayer = connect.prepareStatement(sqlInser);
            insertTeam = connect.prepareStatement(sqlInserClub);
            //updatePlayerStatistic = connect.prepareStatement(sqlUpdate);
            for(Club c : listClub){
                insertTeam.setInt(1, c.getDivision());
                insertTeam.setString(2, c.getTeam_name());
                insertTeam.setString(3, c.getLogo());
                insertTeam.executeUpdate();
                System.out.println("Insert Team complete" + c.getTeam_name());
            }
        
            for(Player p : listPlayer){
                insertPlayer.setString(1, p.getTeam());
                insertPlayer.setString(2, p.getName());
                insertPlayer.setString(3, p.getAmplua());
                insertPlayer.setString(4, p.getBirthday());
                insertPlayer.setInt(5, p.getNumber());
                insertPlayer.setString(6, p.getName()+".png");
                insertPlayer.executeUpdate();
                System.out.println("Insert complete  " + p.getName());
                /*updatePlayerStatistic.setInt(1, p.getGames());
                updatePlayerStatistic.setInt(2, p.getGoal());
                updatePlayerStatistic.setInt(3, p.getAssist());
                updatePlayerStatistic.setInt(4, p.getYellow());
                updatePlayerStatistic.setInt(5, p.getRed());
                updatePlayerStatistic.setString(6, p.getName());
                updatePlayerStatistic.setString(7, p.getTeam());
                updatePlayerStatistic.executeUpdate();
                System.out.println("Update complete  " + p.getName());*/
            }
          
            
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                connect.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                insertPlayer.close();
                insertTeam.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    
}
