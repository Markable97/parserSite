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
    private int end;
    private int start;
    private String url = "http://lfl.ru";
    
    public MyThread(String name,ArrayList<Match> list, ArrayList<String> listId,  int start, int end) {
        super(name);
        this.match = list;
        this.listId = listId;
        this.start = start;
        this.end = end;
        start();
    }

    
    
    @Override
    public void run() {
       System.out.println("Поток:" + getName() + " запущен!!!!!!!!!!!!!!!!!!!!!!!!!");
       for(int i = start; i <= end; i++){
           try {
               Document doc = Jsoup.connect(url + listId.get(i)).get();
               Element titleName = doc.selectFirst("p.player_title_name");
               for(Match m : match){
                   ArrayList<Player> players = m.getPlayers();
                   if (!players.isEmpty()){
                       for(Player p : players){
                            if(p.getUrlPlayer().equals(listId.get(i))){
                                p.setName(titleName.text());                                
                                System.out.println("Поток:" + getName() + " игрок добавлен!!!!!!!!!!!!" + titleName.text());
                                break;
                            }
                        }
                   }
                   
               }
           } catch (IOException ex) {
               Logger.getLogger(MyThread.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       System.out.println("**********************Потко: " + getName() + " закончен*************************");
    }
    
    
    
}
