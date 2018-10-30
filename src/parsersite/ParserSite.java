/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersite;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Markable
 */
public class ParserSite {

    static String url = "http://lfl.ru/club2668/players_list"; //ссылка на список игркоов команды 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Начало парсинга");
        Document doc = Jsoup.connect(url).get();
        Element allListPlayerHtml = doc.select("div.cont.all_news").last();
        Elements infoPlayer = allListPlayerHtml.getElementsByClass("dark_blue_block");
        for(Element aboutPlayer : infoPlayer){
            
        }
        System.out.println("size listPlayet = " + infoPlayer.size());
        
    }
    
}
