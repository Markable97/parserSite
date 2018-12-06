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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static parsersite.ParserSite.replaceDateMatch;
import static parsersite.ParserSite.replaceName;
import static parsersite.ParserSite.replaceNameDivision;
import static parsersite.ParserSite.replaceNameTeam;

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
    private ArrayList<Match> matches; //ArrayList класса Match, для сохранения матчей (его будем возвращать в главный метод)
    private ArrayList<Player> players; //ArrayList для игроков в матче и их действие (для передачи в matches)
    //твои переменные броха 
    public ParserOtherDivs() throws IOException {
        this.url = "http://lfl.ru/tournament4342/tour5";
        players = new ArrayList<>();
        matches = new ArrayList<>();
        mainParserDivs();
    }
    
    private void mainParserDivs() throws IOException{
        Document doc = Jsoup.connect(url).get();
        Elements divMatches = doc.select("div.some_news");
        for(Element e : divMatches){
            parsResults(e);
        }
    }
    
    private void parsResults(Element some_news){
        //это твое бро. Нудно вытащить название команд, дивизион, тур, дата, стадион, судью
        //Если есть перенос вытащить и чей пернос также как и отображается на сайте
        String nameHome, nameGuest, dateMatch = "", referee = "", stadium = "", division = "", matchTransfer = "";
        int goalHome, goalGuest, tour = 0;
        Element head = some_news.select("div.match_head").first();
        Elements spans = head.select("span");
        if(!spans.get(2).text().equals("Дата и время: - -")){
            //если нет переноса и игра сыграна
            
            for(int i = 0; i < spans.size(); i++){
                Element e = spans.get(i);
                switch(i){
                    case 0: 
                        division = replaceNameDivision(e.text());
                        break;
                    case 1:
                        tour = Integer.parseInt(e.text());
                        break;
                    case 2:
                        dateMatch = replaceDateMatch(e.text());
                        break;
                    case 3:
                        stadium = e.text().replace("Стадион: ","");
                        break;
                    case 4:
                        referee = e.text().replace("Судья: ","");
                        break;
                    case 5:
                        //для вытаскивания ссылок на медиа
                        break;
                }
             }
            System.out.println("Инфо про матч\n" + division + " " + tour + " " + dateMatch + " " + stadium + " " + referee);
            Elements divLeft = some_news.select("div.match_left");
            nameHome = replaceNameTeam( divLeft.select("div.match_team.match_team_home > p.match_team_name").text() );
            nameGuest = replaceNameTeam( divLeft.select("div.match_team.match_team_away > p.match_team_name").text() );
            Elements score = divLeft.select("div.match_score");
            goalHome = Integer.parseInt(score.get(0).text());
            goalGuest = Integer.parseInt(score.get(2).text());
            System.out.println(nameHome + " " + goalHome+ ":" + goalGuest + " " + nameGuest );
            //matches.add(new Match(division, tour, dateMatch, nameHome, goalHome, goalGuest, nameGuest, stadium, referee));
            Element divRight = some_news.select("div.match_right").first();
            Element span = divRight.select("div.match_members").first();
            boolean f = ParserSite.findMatchWithTransfer(span.text());
            if(f==true){
                //перенос и игра сыграна
                matchTransfer = span.text();
                matches.add(new Match(division, tour, dateMatch, nameHome, goalHome, goalGuest, nameGuest, 
                        parseSquad(divRight, nameHome, nameGuest), stadium, referee, matchTransfer));
            }else{
                //нет переноса
                matches.add(new Match(division, tour, dateMatch, nameHome, goalHome, goalGuest, nameGuest,
                        parseSquad(divRight, nameHome, nameGuest), stadium, referee));
            }
            
            //parseSquad(divRight, nameHome, nameGuest);
        }else{
            //если есть перенос и матч не сыгран
            for(int i = 0; i < spans.size(); i++){
                    Element e = spans.get(i);
                    switch(i){
                        case 0: 
                            division = replaceNameDivision(e.text());
                            break;
                        case 1:
                            tour = Integer.parseInt(e.text());
                            break;
                    }
                }
                //инфо о камндах
            Elements divLeft = some_news.select("div.match_left");
            nameHome = replaceNameTeam( divLeft.select("div.match_team.match_team_home > p.match_team_name").text() );
            nameGuest = replaceNameTeam( divLeft.select("div.match_team.match_team_away > p.match_team_name").text() );
            matchTransfer = some_news.select("div.match_right > div.match_members").text();
            //System.out.println("\t\t" +division + " " + tour + " " + nameHome + " " + nameGuest + " " + matchTransfer);
            matches.add(new Match(division, tour, nameHome, nameGuest, matchTransfer));
        }
        
    }
    
    private ArrayList<Player> parseSquad(Element divRight , String nameHome, String nameGuest){
            //проход по сосставу на матч
        players.clear();//очистка перед каждым проходом по составам    
        Elements p = divRight.select("p.match_right_head");
        for(int i = 0; i < p.size(); i++){
            String str = p.get(i).text();
            if(str.equals("Составы:")){
                Element div = p.get(i).nextElementSibling();//div блок с составом
                String[] arraySquad = div.text().split(";"); //разбивка сплошного тексат через ;
                String team = nameHome; //для запсии команды в класс
                for(int j = 0; j <  arraySquad.length; j++){
                    if (arraySquad[j].contains(nameGuest)){
                        team = nameGuest; //если есть вхождение гостей то меняем эту команду
                    }
                    arraySquad[j] = arraySquad[j].replace(team + " ", "");
                    players.add(new Player(team, arraySquad[j]));
                }
            }
        }
        return players;
    }
    private void parseActiveSquad(Document doc){
        
    }
//метод для вывода в главный метод, можешь его изменять как хочешь в принципе

    @Override
    public String toString() {
        return "ParserOtherDivs{" + "matches=" + matches + '}';
    }

    
    
}
