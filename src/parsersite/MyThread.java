/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Markable
 */
public class MyThread extends Thread {

    private ArrayList<Match> match;
    private ArrayList<String> listId;
    private ArrayList<Player> listAllPlayers;
    private int end;
    private int start;
    private String url = "http://lfl.ru";
    
    public MyThread(String name,ArrayList<Match> list, ArrayList<String> listId,  int start, int end, 
            ArrayList<Player> listAllPlayers) {
        super(name);
        this.match = list;
        this.listId = listId;
        this.start = start;
        this.end = end;
        this.listAllPlayers = listAllPlayers;
        start();
    }

    
    
    @Override
    public void run() {
       String nameAndDate = "", name = "", birthday = "";
       System.out.println("Поток:" + getName() + " запущен!!!!!!!!!!!!!!!!!!!!!!!!!");
       for(int i = start; i <= end; i++){
           try {
               //Document doc = Jsoup.connect(url + listId.get(i)).get();
               //Element titleName = doc.selectFirst("p.player_title_name");
               nameAndDate = ParserSite.getPlayerFullNameAndDAta(url + listId.get(i));
               name = ParserSite.returnStr(nameAndDate, 0); //Возвращает имя
               birthday = ParserSite.replaceDateFormat(ParserSite.returnStr(nameAndDate,1)); //Возврат даты и сразу ее изменнеие в формат MySql
               boolean f = true;
               for(Match m : match){
                   ArrayList<Player> players;// = new ArrayList<>();
                   //players = m.getPlayers();
                   if (m.getPlayers()!=null /*!players.isEmpty()*/){
                       players = m.getPlayers();
                       for(Player p : players){
                            if(p.getUrlPlayer().equals(listId.get(i))){
                                p.setName(name);                                
                                System.out.println("Поток:" + getName() + " игрок добавлен!!!!!!!!!!!!" + name);
                                for(Player e : listAllPlayers){
                                    if(e.getUrlPlayer().equals(listId.get(i))){
                                        e.setName(name);
                                        e.setTeam(p.getTeam());
                                        e.setBirthday(birthday);
                                    }
                                }
                                //listAllPlayers.add(new Player(p.getTeam(),titleName.text(), p.getUrlPlayer()));
                                f = false;
                                break;
                            }
                        }
                   }
                   if(f == false){
                       break;
                   }
               }
           } catch (IOException ex) {
               Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       System.out.println("**********************Потко: " + getName() + " закончен*************************");
    }
    
    
    
}
