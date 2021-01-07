import java.net.*;


public class URLDepthPair {
    

    
    /**
     * Поля для представления текущего URL-адреса и текущей глубины.
     */
    private int currentDepth;
    private String currentURL;
    
    
    /**
     * Поля для представления текущего веб-хоста и пути к файлу.
     */
    private String currentHost;
    private String currentPath;
    
    /**
     * .Конструктор, который устанавливает для ввода текущий URL-адрес и глубину
     */
    public URLDepthPair(String URL, int depth) {
        currentDepth = depth;
        currentURL = URL;
        try {
            URL url = new URL(currentURL);
            currentHost = url.getHost();
            currentPath = url.getPath();
        }
        catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
        }
    }
    /**
     * метод возвращает текущий URL
     */
    public String getURL() {
        return currentURL;
    }
    /** 
     * метод возвращает текущyю глубину
     */
    public int getDepth() {
        return currentDepth;
    }
    /**
     * метод возвращает текущую глубину и URL в строковом формате
     */
    public String toString() {
        String stringDepth = Integer.toString(currentDepth);
        return stringDepth + '\t' + currentURL;
    }
    /** 
     * метод возвращает веб-хост текущего URL
     */
    public String getWebHost() {
        return currentHost;
    }
    /**
     * метод возвращает путь к файлу текущего URL
     */
    public String getDocPath() {
        return currentPath;
    }
    
    
}
