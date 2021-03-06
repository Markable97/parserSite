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
    private static String url = "jdbc:mysql://localhost:3306/football_main_realese";
    
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
    private static String sqlInsertMatchSchedule = " "
            + "insert into matches "
            + "set id_season = 4,"
            + " id_tour = ?, "
            + " id_division = (select id_division from divisions where name_division = ?), "
            + " team_home = (select id_team from teams where team_name = change_team_name(?) ), "
            + " team_guest = (select id_team from teams where team_name = change_team_name(?) ), "
            + " m_date = ?, "
            + " id_stadium = (select id_stadium from stadiums where name_stadium like ?), "
            + " transfer = ?;";
    /*private static String SqlUpdateMatch = ""
            + " insert into matches "
            + " set "
            + " goal_home = ?, "
            + " goal_guest = ?, "
            + " id_referee = (select id_staff from staff where staff_name like ?), "
            + " transfer = ?"
            + " where team_home = (select id_team from teams where team_name = change_team_name(?) )"
            + " and team_guest = (select id_team from teams where team_name = change_team_name(?) )"
            + " and id_tour = ?;";*/
        private static String SqlUpdateMatch = ""
            + " insert into matches "
            + " set id_season = 4, "
            + " goal_home = ?, "
            + " goal_guest = ?, "
            + " id_referee = (select id_staff from staff where staff_name like ?), "
            + " transfer = ?, "
            + " team_home = (select id_team from teams where team_name = change_team_name(?) ), "
            + " team_guest = (select id_team from teams where team_name = change_team_name(?) ), "
            + " id_tour = ?,"
            + " id_division = (select id_division from divisions where name_division = ?), "
            + " played = ?;";
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
    private static String sqlPocPlayerInMatche = "CALL insPlayerInMatche2(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    
    private static PreparedStatement insertPlayer;
    
    //private static PreparedStatement updatePlayerStatistic;
    private static PreparedStatement insertTeam;
    private static PreparedStatement insertMatch;
    //private static PreparedStatement insertPlayerInMatch;
    //private static PreparedStatement insertStaff;
    
    private static CallableStatement  procStaff;
    private static CallableStatement  procPlayerMatch;
    
    private static Connection connect;

    private static ArrayList<Match> matches;
    static ArrayList<Player> listPlayer;
    ArrayList<Club> listClub;
    
    /*public DataBaseQuery(ArrayList<Player> list) {
        this.listPlayer = list;
        connection(listPlayer);
    }*/
    
    public DataBaseQuery(ArrayList<Match> matches){
        this.matches = matches;
        //connection_two(matches);
        //addResult(matches);
        addSchedule(matches);
    }

    public DataBaseQuery(ArrayList<Player> player,ArrayList<Match> matches){
            this.matches = matches;
            this.listPlayer = player;
            connection(listPlayer, null);
            //connection(this.matches);
            }
    private static void addSchedule(ArrayList<Match> matches){
        try{
           connect = DriverManager.getConnection(url, user, password);
           insertMatch = connect.prepareStatement(sqlInsertMatchSchedule);
           for(Match m : matches){
               System.out.println("Матч для добавения = " + m.getTeamHome() + " " + m.getTeamGuest());
                insertMatch.setInt(1, m.getTour());
                insertMatch.setString(2, "Третий дивизион B"/*m.getDivision()*/);
                insertMatch.setString(3, m.getTeamHome());
                insertMatch.setString(4, m.getTeamGuest());
                if(m.getDateMatch().equals("- -")){
                    insertMatch.setString(5, null);
                }else{
                    insertMatch.setString(5, m.getDateMatch());
                }
                insertMatch.setString(6, m.getStadium());
                insertMatch.setString(7, m.getMatchTransfer());
                try{
                    insertMatch.executeUpdate();
                    System.out.println("\ncomplete = " + m.getTour() + " " + m.getTeamHome() + " " +  m.getTeamGuest());
                }catch(SQLException ex){
                    System.out.println("EROOOOOOOOOR!!! МАТЧ\n" + ex);
                    //continue;
                }
           }
        }catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void addResult(ArrayList<Match> matches){
        try {
            connect = DriverManager.getConnection(url, user, password);
            insertMatch = connect.prepareStatement(SqlUpdateMatch);
            procStaff = connect.prepareCall(sqlProcInsertStaff);
            procPlayerMatch = connect.prepareCall(sqlPocPlayerInMatche);
            for(Match m : matches){
                procStaff.setString(1, m.getReferee());
                try{
                    procStaff.execute();
                }catch(SQLException ex){
                    System.out.println("EROOOOOOOOOR!!! СУДЬЯ \n" + ex);
                }
                System.out.println("Судья complete = " + m.getReferee());
                System.out.println("Матч для добавения = " + m.getTeamHome() + " " + m.getTeamGuest());
                insertMatch.setInt(1, m.getGoalHome());
                insertMatch.setInt(2, m.getGoalGuest());
                insertMatch.setString(3, m.getReferee());
                insertMatch.setString(4, m.getMatchTransfer());
                insertMatch.setString(5, m.getTeamHome());
                insertMatch.setString(6, m.getTeamGuest());
                insertMatch.setInt(7, m.getTour());
                insertMatch.setString(8, "Третий дивизион B"/*m.getDivision()*/);
                if(m.getReferee() != null){
                    insertMatch.setInt(9, 1);
                }else{
                    insertMatch.setInt(9, 0);
                }
                try{
                    insertMatch.executeUpdate();
                    System.out.println("\ncomplete = " + m.getTour() + " " + m.getTeamHome() + " " +  m.getTeamGuest());
                }catch(SQLException ex){
                    System.out.println("EROOOOOOOOOR!!! МАТЧ\n" + ex);
                    //continue;
                }
                if(m.getPlayers() != null){
                    for(Player p : m.getPlayers()){
                        System.out.println("\t\tИгрк для добавления = " + p.getName());
                        procPlayerMatch.setString(1, m.getTeamHome());
                        procPlayerMatch.setString(2, m.getTeamGuest());
                        procPlayerMatch.setInt(3, m.getTour());
                        procPlayerMatch.setString(4,p.getName()+'%');                     
                        procPlayerMatch.setString(5,p.getTeam());
                        procPlayerMatch.setInt(6,p.getGoal());
                        procPlayerMatch.setInt(7,p.getAssist());
                        procPlayerMatch.setInt(8,p.getYellow());
                        procPlayerMatch.setInt(9,p.getRed());
                        procPlayerMatch.setInt(10, p.getPenalty());
                        procPlayerMatch.setInt(11, p.getPenaltyOut());
                        procPlayerMatch.setInt(12, p.getOwnGoal());
                        procPlayerMatch.setString(13, p.getUrlPlayer());
                        procPlayerMatch.setString(14, p.getTeam());
                        try{
                            procPlayerMatch.execute();
                             System.out.println("Игрок добавлен = " + p.getName() + " " +  p.getUrlPlayer()+ " " + p.getTeam());
                        }catch(SQLException ex){
                            System.out.println("Ошибка на игроке = " + p.getName() + " " +  p.getUrlPlayer()+ " " + p.getTeam());
                            System.out.println("EROOOOOOOOOR!!! ИГРОК\n" + ex);
                        }
                       
                    }                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void connection_two(ArrayList<Match> matches){
        try {
            
            System.out.println("\n\n\n" + matches);
            
            //connect = DriverManager.getConnection(url, user, password);
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
                if(m.getTeamHome().equals("Инвиктус") ||m.getTeamGuest().equals("Инвиктус") || 
                        m.getTeamHome().equals("ММЗ Вперёд")||m.getTeamGuest().equals("ММЗ Вперёд")||
                        m.getTeamHome().equals("Спарта")||m.getTeamGuest().equals("Спарта")||
                        m.getTeamGuest().equals("Зеро")|| m.getTeamHome().equals("Стандарт")||
                        m.getTeamGuest().equals("Капстрой")||m.getTeamGuest().equals("ВИМ-АВИА")||
                        m.getTeamHome().equals("Селтик-М")||m.getTeamGuest().equals("Селтик-М")){
                    System.out.println("Нет таких команд");
                    continue;
                }
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
                try{
                    insertMatch.executeUpdate();
                    System.out.println("complete = " + m.getTour() + " " + m.getTeamHome() + " " +  m.getTeamGuest());
                }catch(SQLException ex){
                    System.out.println("EROOOOOOOOOR!!!\n" + ex);
                }
                System.out.println("complete = " + m.getTour() + " " + m.getTeamHome() + " " +  m.getTeamGuest());
                if(m.getPlayers() != null){
                    for(Player p : m.getPlayers()){
                        System.out.println("\t\tИгрк для добавления = " + p.getUrlPlayer());
                        procPlayerMatch.setString(1, m.getTeamHome());
                        procPlayerMatch.setString(2, m.getTeamGuest());
                        procPlayerMatch.setInt(3, m.getTour());
                        for(Player p_name : listPlayer){
                            
                            if(p_name.getUrlPlayer().equals(p.getUrlPlayer())){
                                //Берется имя из списка всех игроков а не с главного массива
                                procPlayerMatch.setString(4,p_name.getName());
                                /*if(p.getName()!= null){
                                    //System.out.println("БИБО = " + p_name.getName());
                                    procPlayerMatch.setString(4,p_name.getName());
                                }else{
                                    System.out.println("БОБА = " + p_name.getUrlPlayer());
                                    procPlayerMatch.setString(4,p_name.getUrlPlayer());
                                }*/
                                    
                            }
                        }
                        procPlayerMatch.setString(5,p.getTeam());
                        procPlayerMatch.setInt(6,p.getGoal());
                        procPlayerMatch.setInt(7,p.getAssist());
                        procPlayerMatch.setInt(8,p.getYellow());
                        procPlayerMatch.setInt(9,p.getRed());
                        procPlayerMatch.setInt(10, p.getPenalty());
                        procPlayerMatch.setInt(11, p.getPenaltyOut());
                        procPlayerMatch.setInt(12, p.getOwnGoal());
                        try{
                            procPlayerMatch.execute();
                             System.out.println("Игрок добавлен = " + p.getName() + " " +  p.getUrlPlayer()+ " " + p.getTeam());
                        }catch(SQLException ex){
                            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex1) {
                                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                        }
                        
                       
                    }                    
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
            /*insertTeam = connect.prepareStatement(sqlInserClub);
            //updatePlayerStatistic = connect.prepareStatement(sqlUpdate);
            for(Club c : listClub){
                insertTeam.setInt(1, c.getDivision());
                insertTeam.setString(2, c.getTeam_name());
                insertTeam.setString(3, c.getLogo());
                insertTeam.executeUpdate();
                System.out.println("Insert Team complete" + c.getTeam_name());
            }*/
            //int k = 0;
            for(Player p : listPlayer){
                /*if(k==0){
                    k++;
                    continue;
                }*/
                insertPlayer.setString(1, p.getTeam());
                insertPlayer.setString(2, p.getName());
                insertPlayer.setString(3, p.getAmplua());
                insertPlayer.setString(4, p.getBirthday());
                insertPlayer.setInt(5, p.getNumber());
                insertPlayer.setString(6, p.getName()+".png");
                try{
                    insertPlayer.executeUpdate();
                    System.out.println("Insert complete  " + p.getName() + " " + p.getUrlPlayer());
                }catch(SQLException ex){
                    Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                 
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
          
            connection_two(DataBaseQuery.matches);   
        } catch (SQLException ex) {
            System.out.println(ex);
            Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            /*try {
                connect.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            try {
                insertPlayer.close();
//                insertTeam.close();
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    
}
