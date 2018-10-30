/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Markable
 */
public class ParserSite {

    static ArrayList<Player> listPlayer = new ArrayList<>();
    static String url = "http://lfl.ru/club2668/players_list"; //ссылка на список игркоов команды 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Начало парсинга");
        Document doc = Jsoup.connect(url).get();
        Element allListPlayerHtml = doc.select("div.cont.all_news").last();
        Elements infoPlayer = allListPlayerHtml.getElementsByClass("dark_blue_block");
        String name = null, team = null, amplua = null, birthdate = null; 
        int number = 0;
        int k = 0;
        for(Element aboutPlayer : infoPlayer){
            Elements playerTitle = aboutPlayer.select("div.player_title > p");
            System.out.println("Размер = " + playerTitle.outerHtml() + "коней!!!!!!!!!");
            for(Element p : playerTitle){
                switch(k){
                    case 0:
                        name = p.text();
                        break;                      
                    case 1:
                        team =replaceNameTeam( p.text().replace("Текущий клуб: ", "") );
                        break;                      
                    case 2:
                        amplua = p.text().replace("Амплуа: ", "");
                        break;                      
                    case 4:
                        birthdate = replaceDateFormat(p.text());
                        break;                       
                    case 5:
                        String text = p.text();
                        number = Integer.parseInt(text.replace("Номер на майке: ", ""));
                        break;                      
                }
                k++;
            }
            k=0;
            listPlayer.add(new Player(name, team, amplua, birthdate, number));
            
        }
        System.out.println("size listPlayet = " + listPlayer.size());
        System.out.println(listPlayer.toString());
        
    }
    
    static String replaceNameTeam(String str){
        int index = str.indexOf(" Восток");
        if(index!=-1){
            String mainStr = str.substring(0, index);
           // System.out.println(mainStr);
            return mainStr;
        }else{
            return str;
        }
        
    }
    
    static String replaceDateFormat(String str){
        String mainStr = "";
        String strNotText = str.replace("Дата рождения: ", "");
        System.out.println(strNotText);
        String[] numbers = strNotText.split("\\.");
        for(int i = numbers.length - 1; i >= 0; i--){
            if(i==0){
                mainStr+=numbers[i];
            }else
                mainStr+=numbers[i]+'-';
        }
        System.out.println(mainStr);
        return mainStr;
    }
}
