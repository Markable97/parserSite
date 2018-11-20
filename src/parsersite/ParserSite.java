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
    //static String url = "http://lfl.ru/club2668/players_list"; //ссылка на список игркоов команды 
    static String urlDiva = "http://lfl.ru/tournament4341"; //ссылка на список игркоов команды 
    static ArrayList<String> urlList = new ArrayList<>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Начало парсинга");
        parsingPlayerInMatch();
        /*Document docTournament = Jsoup.connect(urlDiva).get();
        Element teamTable = docTournament.getElementById("table_tab_slide_0");
        Elements teamUrls = teamTable.select("td.left_align_table > a");
        for(Element e : teamUrls){
            String url = e.attr("abs:href");
            urlList.add(url);
        }
        System.out.println("Кол-во ссылок = " + urlList.toString());
        parsingPlayerInfo(urlList);*/
        
    

    }
    
    static void parsingPlayerInMatch() throws IOException{
        String nameHome, nameGuest;
        int goalHome, goalGuest;
        Document doc = Jsoup.connect(urlDiva+"/tour1?").get();
        Elements divs = doc.select("div.some_news");
        ArrayList<Player> playerInMatch = new ArrayList();
        ArrayList <Player> plOneMatch = new ArrayList<>();
        for(Element div : divs){
            Elements head = div.select("div.match_head");
            Elements spans = head.select("span");
            //System.out.println(span.text());
            plOneMatch.clear();//очистка после каждого прохода
            if(!spans.get(2).text().equals("Дата и время: - -")){
                
                //playerInMatch.clear();//пока очистка для вывода
                Elements divLeft = div.select("div.match_left");
                nameHome = replaceNameTeam( divLeft.select("div.match_team.match_team_home > p.match_team_name").text() );
                nameGuest = replaceNameTeam( divLeft.select("div.match_team.match_team_away > p.match_team_name").text() );
                Elements score = divLeft.select("div.match_score");
                goalHome = Integer.parseInt(score.get(0).text());
                goalGuest = Integer.parseInt(score.get(2).text());
                System.out.println("\n" + nameHome + " " + goalHome+ ":" + goalGuest + " " + nameGuest );
                Elements divRight = div.select("div.match_right");
                Elements members = divRight.select("div.match_members");
                //Elements spans = members.select("span");
                Element squadTeam = members.get(1);
                Elements ps = squadTeam.select("p");//захват тега <p>
                if(ps.size() == 10 ){
                    System.out.println(nameHome);
                    Element p = ps.get(2);
                    Elements data = p.select("span");
                    System.out.println("Основные");
                    for(Element s : data){
                        //System.out.print(replaceName(s.text()) + " " );
                        plOneMatch.add(new Player(nameHome, replaceName(s.text())));
                    }
                    p = ps.get(4);
                    data = p.select("span");
                    System.out.println("Запасные");
                    for(Element s : data){
                        //System.out.print( replaceName(s.text()) + " ");
                        plOneMatch.add(new Player(nameHome, replaceName(s.text())));
                    }
                    System.out.println("\n" + nameGuest);
                    p = ps.get(7);
                    data = p.select("span");
                    System.out.println("Основные");
                    for(Element s : data){
                        //System.out.print( replaceName(s.text()) + " ");
                        plOneMatch.add(new Player(nameGuest, replaceName(s.text())));
                    }
                    p = ps.get(9);
                    data = p.select("span");
                    System.out.println("Запасные");
                    for(Element s : data){
                        //System.out.print( replaceName(s.text()) + " ");
                       plOneMatch.add(new Player(nameGuest, replaceName(s.text())));
                    }
                }else{
                    //если кол-во спан другое, то порядок записей другой
                }
                System.out.println(ps.size());
                Element goalAndAssist = members.get(0);
                ps = goalAndAssist.select("p");
                for(Element p : ps){
                    Elements urls = p.select("a");
                    System.out.println(urls.text());
                    String s = urls.text();
                    if (urls.size() == 1){//без ассистентов
                       for(int i = 0; i < plOneMatch.size(); i++){
                           if(urls.text().equals(plOneMatch.get(i).getName())){//совпадении имени
                               int goal = plOneMatch.get(i).getGoal();
                               //System.out.println(urls.text() + "Голов было = " + go);
                               plOneMatch.get(i).setGoal(++goal);
                           }
                       }
                    }else{//с ассистом
                        String[] ch = urls.text().split(" ");
                        String playerGoal = ch[0] + " " + ch[1];
                        String str = ch[2] + " " + ch[3];
                        String playerAssist = str.replace(")", "");
                        System.out.println("goal = " + playerGoal + " Assist = " + playerAssist);
                        for(int i = 0; i < plOneMatch.size(); i++){
                           if(playerGoal.equals(plOneMatch.get(i).getName())){//совпадении имени
                               int goal = plOneMatch.get(i).getGoal();
                               //System.out.println(urls.text() + "Голов было = " + go);
                               plOneMatch.get(i).setGoal(++goal);
                           }
                           if(playerAssist.equals(plOneMatch.get(i).getName())){//совпадении имени
                               int assist = plOneMatch.get(i).getAssist();
                               //System.out.println(urls.text() + "Голов было = " + go);
                               plOneMatch.get(i).setAssist(++assist);
                           }
                       }
                    }  
                }
                int sizeMember = members.size();
                if(sizeMember > 2 && sizeMember < 4){
                    //Если есть предупреждения или пенальти
                    Element action = members.get(2);
                    Elements spanAction = action.select("span");
                    for(Element span : spanAction){
                        Element link = span.select("a").first();
                        String playerName = replaceName(link.text());
                        String nameImage = link.select("img").attr("src");
                        if(nameImage.equals("/theme/img/popup_yc.png") || nameImage.equals("/theme/img/popup_2yc.png")){
                            //Если желтая карточка
                            for(int i = 0; i < plOneMatch.size(); i++){
                                if(playerName.equals(plOneMatch.get(i).getName())){
                                    int yellow = plOneMatch.get(i).getYellow();
                                    plOneMatch.get(i).setYellow(++yellow);
                                }
                            }
                        }else if(nameImage.equals("/theme/img/popup_rc.png")){
                            for(int i = 0; i < plOneMatch.size(); i++){
                                if(playerName.equals(plOneMatch.get(i).getName())){
                                    int red = plOneMatch.get(i).getRed();
                                    plOneMatch.get(i).setRed(++red);
                                }
                            }
                        }
                    }
                }
            }
            for(int i = 0; i < plOneMatch.size(); i++){
                playerInMatch.add(plOneMatch.get(i));
            }
        }
        System.out.println(playerInMatch.toString());
        System.out.println(divs.size());
    }
    
    static void parsingPlayerStatistic(String url, ArrayList<Player> players) throws IOException{
                Document doc = Jsoup.connect("http://lfl.ru/"
                        + "?ajax=1&method=tournament_squads_table&tournament_id="+
                        urlDiva.replace("http://lfl.ru/tournament", "") +"&club_id=" + 
                        url.replace("http://lfl.ru/club","")).get();
               Elements table = doc.getElementsByTag("table");
               Elements tbody = table.select("tbody");
               Elements trs = tbody.select("tr");
               for(Element tr : trs){   
                   Elements tds = tr.select("td");
                   if(tds.size()!=1){
                       String name = null;
                       int games = 0, goals = 0, assist = 0, yellow = 0, red = 0;
                       for(int i = 0; i < tds.size(); i++){
                           Element td = tds.get(i);
                           switch(i){
                               case 1:
                                   System.out.print("Name = " + replaceName(td.text()) );
                                   name = replaceName(td.text());
                                   break;
                               case 2:
                                   games = Integer.parseInt(td.text());
                                   System.out.print("Games = " + td.text() );
                                   break;
                               case 3:
                                   goals = Integer.parseInt(td.text());
                                   System.out.print("Goal = " + td.text());
                                   break;
                               case 4:
                                   assist = Integer.parseInt(td.text());
                                   System.out.print("Assist = " + td.text());
                                   break;
                               case 5:
                                   yellow = Integer.parseInt(td.text());
                                   System.out.print("Yellow = " + td.text());
                                   break;
                               case 7:
                                   red = Integer.parseInt(td.text());
                                   System.out.print("Red = " + td.text());
                                   break; 
                           }
                       }
                       for(Player p : players ){
                           if(p.getName().equals(name)){
                               p.setGames(games);
                               p.setGoal(goals);
                               p.setAssist(assist);
                               p.setYellow(yellow);
                               p.setRed(red);
                           }
                       }
                       System.out.println("\n");
                   }
                   
                }
    }
    
    static void parsingPlayerInfo(ArrayList<String> urlList) throws IOException{
        for(String url : urlList){
            Document doc = Jsoup.connect(url+"/players_list").get();
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
                            name = replaceName(p.text());
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
            parsingPlayerStatistic(url, listPlayer);
            System.out.println(listPlayer.toString());
            DataBaseQuery baseQuery = new DataBaseQuery(listPlayer);
            listPlayer.clear();
            }
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
    
    static String replaceName(String str){
        String main = "";
        String[] ch = str.split(" ");
        main = ch[0] + " " + ch[1];
        String[] ch1 = main.split("\\.");
        //System.out.println(ch1.length);
        if(ch1.length > 1){ //если есть в имени точка то возвращаем 1 член
            return main = ch1[1];
        }
        String[] ch2 = main.split("\\)");//если есть скобочка после имени то возвразаем 0 член
        if(ch2.length>1){
            return main = ch2[0];
        }
        
        return main;
        //return main;
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
