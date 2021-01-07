import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Этот класс реализует основные функции нашего приложения-поискового робота. 
 * Он имеет метод getAllLinks для хранения всех ссылок на данной веб-странице 
 * в дополнение к основному методу, который отслеживает важные переменные.
 */
public class Crawler {
    

    
    public static void main(String[] args) {
        
        
        if (args.length != 2 ) {
            System.out.println("usage: java Crawler <URL> <depth>");
            System.exit(1);
        }
        int depth = 0;
        try {
        // Преобразовать строковый аргумент в целочисленное значение.
            depth = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e) {
            // если вышла ошибка, то вывести "глубина должно быть
            // целочисленным значением
            System.out.println("depth must be an integer");
            System.exit(1);
        }
        
        // Связанный список для представления ожидающих URL-адресов.
        LinkedList<URLDepthPair> pendingLinks = new LinkedList<URLDepthPair>();
        
        // Связанный список для представления обработанных URL-адресов.
        LinkedList<URLDepthPair> processedLinks = new LinkedList<URLDepthPair>();
        
        // Пары глубины и URL-адреса для представления веб-сайта, введенного пользователем.
        // с глубиной 0 
        URLDepthPair currentDepthPair = new URLDepthPair(args[0], 0);
        
        // Добавить текущий веб-сайт из пользовательского ввода в ожидающие URL-адреса.
        pendingLinks.add(currentDepthPair);
        
        // Список массивов для представления просмотренных URL. Добавить текущий
        // веб-сайт.
        ArrayList<String> seenLinks = new ArrayList<String>();
        seenLinks.add(currentDepthPair.getURL());
        
        // Пока pendingURL не пуст, выполнить итерацию, посетить каждый веб-сайт,
        // и получить все ссылки
        while (pendingLinks.size() != 0) {
            
            // Получить следующий URL-адрес из ожидающих URL-адресов, добавить к 
            // обработанным URL-адресам и хранить его глубину.
            URLDepthPair depthPair = pendingLinks.pop();
            processedLinks.add(depthPair);
            int myDepth = depthPair.getDepth();
            
            // Получите все ссылки с сайта и сохраните их в новом связанном списке.
            LinkedList<String> linksList = new LinkedList<String>();
            linksList = Crawler.getAllLinks(depthPair);
            
            // Если не достигли максимальной глубины, добавляем ссылки с сайта
            // которые ранее не просматривались в pendingURLs и visibleURLs.
            if (myDepth < depth) {
                // Перебрать все ссылки из сайта
                for (int i=0;i<linksList.size();i++) {
                    String newURL = linksList.get(i);
                    // Если ссылку мы уже просматривали, то пропустить
                    if (seenLinks.contains(newURL)) {
                        continue;
                    }
                    // Иначе создаем новый URLDepthPair
                    // с глубиной на единицу больше текущей глубины и добавляем
                    // в pendingLinks и seenLinks.
                    else {
                        URLDepthPair newDepthPair = new URLDepthPair(newURL, myDepth + 1);
                        pendingLinks.add(newDepthPair);
                        seenLinks.add(newURL);
                    }
                }
            }
        }
        // Распечатать все обработанные URL-адреса с глубиной.
        Iterator<URLDepthPair> iter = processedLinks.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
     // Метод, который принимает URLDepthPair и возвращает cписок ссылок на сайте
    private static LinkedList<String> getAllLinks(URLDepthPair myDepthPair) {
        
        // Инициализировать список
        LinkedList<String> URLs = new LinkedList<String>();
        
        // Инициализировать сокет.
        Socket sock;
        
        // Попытаться создать новый сокет с URL-адресом, переданным методу в
         // URLDepthPair и портом 80.
        try {
            sock = new Socket(myDepthPair.getWebHost(), 80);
        }
        // Вывести все возможные ошибки и вернуть cписок.
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException: " + e.getMessage());
            return URLs;
        }
        catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }
        
        // Попробовать установить сокет на тайм-аут через 3 секунды.
        try {
            sock.setSoTimeout(3000);
        }
        // Вывести все возможные ошибки и вернуть cписок.
        catch (SocketException e) {
            System.err.println("SocketException: " + e.getMessage());
            return URLs;
        }
        
        // поток вывода.
        OutputStream outStream;
        
        // Попытка получить поток вывода от сокета. 
        try {
            outStream = sock.getOutputStream();
        }
        // Вывести все возможные ошибки и вернуть cписок.
        catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            return URLs;
        }
        
        // Инициализирует PrintWriter. True означает, что PrintWriter будет
        // сброшен после каждого вывода.
        PrintWriter myWriter = new PrintWriter(outStream, true);
        
        // отправить запрос.  
        myWriter.println("GET " + myDepthPair.getDocPath() + " HTTP/1.1");
        myWriter.println("Host: " + myDepthPair.getWebHost());
        myWriter.println("Connection: close");
        myWriter.println();

        // поток ввода.
        InputStream inStream;
        
        // Попытка получить поток вывода от сокета.  
        try {
            inStream = sock.getInputStream();
        }
        // Вывести все возможные ошибки и вернуть cписок.
        catch (IOException excep){
            System.err.println("IOException: " + excep.getMessage());
            return URLs;
        }
        // создать буфферы для потоков ввода   
        InputStreamReader inStreamReader = new InputStreamReader(inStream);
        BufferedReader BuffReader = new BufferedReader(inStreamReader);
        
        // Попытаться прочитать строку из буффера ввода. 
        while (true) {
            String line;
            try {
                line = BuffReader.readLine();
            }
            // Вывести все возможные ошибки и вернуть cписок.
            catch (IOException except) {
                System.err.println("IOException: " + except.getMessage());
                return URLs;
            }
            // Прочитали документ
            if (line == null)
                break;
        
            
            // Переменные для представления индексов, в которых ссылки начинаются
            // и заканчиваются как и текущий индекс.
            int beginIndex = 0;
            int endIndex = 0;
            int index = 0;
            
            while (true) {
                // Ищем URL_INDICATOR в текущей строке.
                index = line.indexOf(URL_INDICATOR, index);
                if (index == -1) // -1 означает, что URL_INDICATOR не найден
                    break;
                
                // Переместить текущий индекс вперед и установить значение beginIndex.
                index += URL_INDICATOR.length();
                beginIndex = index;
                
                // Найти конец в текущей строке и установить значение endIndex.
                endIndex = line.indexOf(END_URL, index);
                index = endIndex;
                
                // Устанавливаем ссылку на подстроку между начальным индексом
                 // и конечным индексом. Добавить в наш список URL-адресов.
                String newLink = line.substring(beginIndex, endIndex);
                
                if (newLink.indexOf("://")==-1){
                    //если ссылка не имеет "://", 
                    //то это есть продолжение старой ссылки
                    newLink = myDepthPair.getURL() + newLink; // 
                }
                
                URLs.add(newLink);
            }
            
        }
        // Вернуть список
        return URLs;
    }
    
    /**
                 * Константа для строки, указывающей ссылку.
                 */
        public static final String URL_INDICATOR = "a href=\"";
                
                /**
                 * Константа для строки, указывающей конец веб-хостинга и
                 * начало docpath.
                 */
        public static final String END_URL = "\"";
    
}

