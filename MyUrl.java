import java.io.*;
import java.net.*;

public class MyUrl {
    public static void main(String[] args) throws Exception {
        URL myUrl = new URL("http://www.wikipedia.org:80/index.html?v=45@t=67y=67#eventos");

        System.out.println("Protocolo: " + myUrl.getProtocol());
        System.out.println("Host: " + myUrl.getHost());
        System.out.println("Puerto: " + myUrl.getPort());
        System.out.println("Path: " + myUrl.getPath());
        System.out.println("File: " + myUrl.getFile());
        System.out.println("Query: " + myUrl.getQuery());
        System.out.println("Ref: " + myUrl.getRef());
        System.out.println("Authority: " + myUrl.getAuthority());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(myUrl.openStream()))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
