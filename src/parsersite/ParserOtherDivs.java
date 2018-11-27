/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//класс для парсинга результатов матча и дейсвтий игроков всех дивизионов кроме первого
package parsersite;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author Markable
 * класс для парсинга результатов матча и дейсвтий игроков всех дивизионов кроме первого
 * http://lfl.ru/tournament4342/tourХ - сылка на тур для парсинга. Вместо Х - цифра тура. Для анализа
 * парсинга рекомендуется пройтись по турам посмотреть макеты. (Может что найдешь лучше, например, ajax, будет круто)
 */
public class ParserOtherDivs {
    //на вход классу на тур
    String url; //ссылка на тур
    String test;
    ArrayList<Match> matches; //ArrayList класса Match, для сохранения матчей (его будем возвращать в главный метод)
    ArrayList<Player> players; //ArrayList для игроков в матче и их действие (для передачи в matches)
    //твои переменные броха 
    public ParserOtherDivs() throws IOException {
        this.url = "http://lfl.ru/tournament4342/tour1";
        mainParserDivs();
    }
    
    private void mainParserDivs() throws IOException{
        Document doc = Jsoup.connect(url).get();
        test = doc.title();
    }
    
    private void parsResults(Document doc){
        //это твое бро. Нудно вытащить название команд, дивизион, тур, дата, стадион, судью
        //Если есть перенос вытащить и чей пернос также как и отображается на сайте
    }
    
    private void parseSquad(Document doc){
        
    }
    private void parseActiveSquad(Document doc){
        
    }
//метод для вывода в главный метод, можешь его изменять как хочешь в принципе
    @Override
    public String toString() {
        return "ParserOtherDivs{" + "matches=" + test + '}';
    }
    
    
}
