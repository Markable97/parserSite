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
        //this.url = "http://lfl.ru/tournament4342/tour6";
        this.url = "http://lfl.ru/tournament5233/tour";
        players = new ArrayList<>();
        matches = new ArrayList<>();
        for(int i = 1; i < 2; i++){
            mainParserDivs(url + i);
        }
        System.out.println(matches.toString());
        DataBaseQuery baseQuery = new DataBaseQuery(matches);
        
    }
    
    private void mainParserDivs(String url) throws IOException{
        Document doc = Jsoup.connect(url).get();
        Elements divMatches = doc.select("div.some_news");
        for(Element e : divMatches){
            parsResults(e);
        }
    }
    
    private void parsResults(Element some_news) throws IOException{
        //это твое бро. Нудно вытащить название команд, дивизион, тур, дата, стадион, судью
        //Если есть перенос вытащить и чей пернос также как и отображается на сайте
        String nameHome, nameGuest, dateMatch = "", referee = "", stadium = "", division = "", matchTransfer = "Перенос";
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
            if(score.get(0).text().equals("-")){
                goalHome = -1; //если есть минус, то обоюдный перенос
            }else{
                goalHome = Integer.parseInt(score.get(0).text());
            }
            if(score.get(0).text().equals("-")){
                goalGuest = -1;
            }else{
                goalGuest = Integer.parseInt(score.get(2).text());
            }
            System.out.println(nameHome + " " + goalHome+ ":" + goalGuest + " " + nameGuest );
            //matches.add(new Match(division, tour, dateMatch, nameHome, goalHome, goalGuest, nameGuest, stadium, referee));
            Element divRight = some_news.select("div.match_right").first();
            Element span = divRight.select("div.match_members").first();
            int check = 0; //переменная отслеживание техничсекое поражение
            if(span == null){
                span = divRight.select("div.technical_defeat").first();
                check = 1;
            }
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
    
    private String returnUrlPerson(String url) throws IOException{
        Document doc = Jsoup.connect("http://lfl.ru"+url).get();
        Element p = doc.select("p.player_title_name").first();
        return p.getElementsByTag("a").attr("href");
    }
    
    private ArrayList<Player> parseSquad(Element divRight , String nameHome, String nameGuest) throws IOException{
            //проход по сосставу на матч
        players.clear();//очистка перед каждым проходом по составам 
        ArrayList<Player> plOnMatch = new ArrayList<>();
        ArrayList<Player> playerUrls = new ArrayList<>();
        plOnMatch = players;
        Elements p = divRight.select("p.match_right_head");
        for(int i = 0; i < p.size(); i++){
            String str = p.get(i).text();
            if(str.equals("Составы:")){
                Element div = p.get(i).nextElementSibling();//div блок с составом
                Elements spans = div.select("span");
                for(Element s : spans){
                    playerUrls.add(new Player(0,replaceName(s.text().replace(";", "").trim()),s.getElementsByTag("a").attr("href")));
                }
                String[] arraySquad = div.text().split(";"); //разбивка сплошного тексат через ;
                String team = nameHome; //для запсии команды в класс
                for(int j = 0; j <  arraySquad.length; j++){
                    if (arraySquad[j].contains(nameGuest)){
                        team = nameGuest; //если есть вхождение гостей то меняем эту команду
                    }
                    arraySquad[j] = arraySquad[j].replace(team + " ", "");
                    plOnMatch.add(new Player(team, replaceName(arraySquad[j])));
                }
            }
        }
        
        //System.out.println("\nПроверка ссылок:" + playerUrls.toString());
        for(Player player : plOnMatch){
            for(Player urls : playerUrls){
                if(player.getName().equals(urls.getName())){
                    player.urlPlayer = returnUrlPerson(urls.getUrlPlayer());
                    break;
                }
            }
        }
        parseActiveSquad(divRight, nameHome, nameGuest, players);
        return plOnMatch;
    }
    private void parseActiveSquad(Element divRight, String nameHome, String nameGuest, ArrayList<Player> players){
        Elements p = divRight.select("p.match_right_head");
        for(int i = 0; i < p.size(); i++){
            String str = p.get(i).text();
            if(str.equals("Голы:")){
                Element div = p.get(i).nextElementSibling();//div блок с голами
                String[] arraySquad = div.text().split(";");//разбивка через ;
                String team = nameHome; //для запсии команды в класс
                for(int j = 0; j < arraySquad.length; j++){
                    if(arraySquad[j].contains(nameGuest)){
                        team = nameGuest;//если встретилась команда, то меняем (действия игроков гостей)
                    }
                    arraySquad[j] = arraySquad[j].replace(team + " ","");//удаляем команду у первых вхождений
                    if(arraySquad[j].contains("(аг)")){
                        String s = arraySquad[j].replace(" (аг)", "");
                        arraySquad[j] = s;
                        Player pl = findPlayer(players, replaceName(arraySquad[j]));
                        int own_goal = activePlayet(arraySquad[j].trim());
                        pl.setOwnGoal(own_goal);
                        //int ownGoal = pl.getOwnGoal();
                        //pl.setOwnGoal(++ownGoal);
                    }else if(arraySquad[j].contains("(пен)")){
                        String s = arraySquad[j].replace(" (пен)", "");
                        arraySquad[j] = s;
                        Player pl = findPlayer(players, replaceName(arraySquad[j]));
                        int penalty = activePlayet(arraySquad[j].trim());
                        pl.setPenalty(penalty);
                    }
                    else{
                        //если просто голы
                        Player pl = findPlayer(players, replaceName(arraySquad[j]));
                        int goal = activePlayet(arraySquad[j].trim());
                        pl.setGoal(goal);
                    }
                }

            }else if(str.equals("Ассистенты:")){
                Element div = p.get(i).nextElementSibling();//div блок с голами
                String[] arraySquad = div.text().split(";");//разбивка через ;
                String team = nameHome; //для запсии команды в класс
                 for(int j = 0; j < arraySquad.length; j++){
                     if(arraySquad[j].contains(nameGuest)){
                        team = nameGuest;//если встретилась команда, то меняем (действия игроков гостей)
                    }
                    arraySquad[j] = arraySquad[j].replace(team + " ","");//удаляем команду у первых вхождений 
                    Player pl = findPlayer(players, replaceName(arraySquad[j]));
                    if(pl != null){
                        int assist = activePlayet(arraySquad[j].trim());
                        pl.setAssist(assist);
                    }
                 }
            }else if(str.equals("Предупреждения:")){
                Element div = p.get(i).nextElementSibling();//div блок с голами
                Elements spans = div.select("span");
                for(Element span : spans){
                    Element link = span.selectFirst("a");
                    String playerName = replaceName(link.text());
                    String nameImage = link.select("img").attr("src");
                    if(nameImage.equals("/theme/img/popup_yc.png") || nameImage.equals("/theme/img/popup_2yc.png")){
                        Player pl = findPlayer(players,playerName);
                        if(pl != null){//костыль из-за Зари (Колхоза)
                            int yellow = pl.getYellow();
                            pl.setYellow(++yellow);                            
                        }
                    }else if(nameImage.equals("/theme/img/popup_rc.png")){
                        Player pl = findPlayer(players,playerName);
                        if(pl != null){
                            int red = pl.getRed();
                            pl.setRed(++red);   
                        }
                    }
                }  
            }else if(str.equals("Нереализованные пенальти:")){
                Element div = p.get(i).nextElementSibling();//div блок с голами
                Elements spans = div.select("span");
                for(Element span : spans){
                    Element link = span.selectFirst("a");
                    String playerName = replaceName(link.text());
                    Player p1 = findPlayer(players, playerName);
                    int penaltyOut = p1.penaltyOut;
                    p1.setPenaltyOut(++penaltyOut);
                }
            }
        }
    }
//метод для вывода в главный метод, можешь его изменять как хочешь в принципе

    private Player findPlayer(ArrayList<Player> player, String name){
        for(int i = 0; i < player.size(); i++){
            if(player.get(i).getName().equals(name)){
                return player.get(i);
            }
        } 
        return null;
    }
    
    private int activePlayet(String str){
        str = str.replace("Восток","").trim();
        String[] s = str.split(" ");
        //System.out.println("Ghtttttt = " + s[2]);
        String ss = s[2].replaceAll("[(|)]+", "");
        return Integer.parseInt(ss);
         
    }
    
    private String replaceName(String str){
        str = str.replace("Восток","").trim();
        if(str.contains("(вр)")){
            str = str.replace("(вр)", "");
        }
        String[] s = str.split("\\.");
        if(s.length>1){
            //Есть точка и номер
            return s[1].trim();
        }else{
            //Только имя
            String[] ss = s[0].split("\\(");
            s[0] = ss[0];
            return s[0].trim();
        }
        
        //return
    }
    
    @Override
    public String toString() {
        return "ParserOtherDivs{" + "matches=" + matches + '}';
    }

    
    
}
