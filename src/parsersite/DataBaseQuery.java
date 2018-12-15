/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

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
    private static String url = "jdbc:mysql://localhost:3306/main_football";
    
    private static String sqlInser = "insert into players\n" +
                "set teams_id_team = (select id_team from teams where team_name like ?), \n" +
                "name = ?, amplua_id_amplua = (select id_amplua from amplua where name_amplua like ?),\n" +
                "birthdate = ?, number = ?, photo = ?;";
    private static String sqlUpdate = "update main_football.players_statistics\n" +
                "set games = ?, goal = ?, assist = ?, yellow_card = ?, red_card = ?\n" +
                "where players_id_player = (select id_player \n" +
                                            "from main_football.players\n" +
                                            "join teams on teams_id_team = id_team \n" +
                                            "where name like ? and team_name like ?);";
    
    private static PreparedStatement insertPlayer;
    private static PreparedStatement updatePlayerStatistic;
    
    private static Connection connect;

    ArrayList<Player> listPlayer;
    
    public DataBaseQuery(ArrayList<Player> list) {
        this.listPlayer = list;
        connection(listPlayer);
    }

    private static void connection(ArrayList<Player> listPlayer) {
        try {
            connect = DriverManager.getConnection(url, user, password);
            insertPlayer = connect.prepareStatement(sqlInser);
            updatePlayerStatistic = connect.prepareStatement(sqlUpdate);
            for(Player p : listPlayer){
                insertPlayer.setString(1, p.getTeam());
                insertPlayer.setString(2, p.getName());
                insertPlayer.setString(3, p.getAmplua());
                insertPlayer.setString(4, p.getBirthday());
                insertPlayer.setInt(5, p.getNumber());
                insertPlayer.setString(6, p.getName()+".png");
                insertPlayer.executeUpdate();
                System.out.println("Insert complete  " + p.getName());
                updatePlayerStatistic.setInt(1, p.getGames());
                updatePlayerStatistic.setInt(2, p.getGoal());
                updatePlayerStatistic.setInt(3, p.getAssist());
                updatePlayerStatistic.setInt(4, p.getYellow());
                updatePlayerStatistic.setInt(5, p.getRed());
                updatePlayerStatistic.setString(6, p.getName());
                updatePlayerStatistic.setString(7, p.getTeam());
                updatePlayerStatistic.executeUpdate();
                System.out.println("Update complete  " + p.getName());
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
            } catch (SQLException ex) {
                Logger.getLogger(DataBaseQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    
}
