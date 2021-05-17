package com.bsuir.lab3ksis.client;


import com.bsuir.lab3ksis.reader.TextReader;
import com.bsuir.lab3ksis.validator.DataValidator;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Client {
    private final static TextReader textReader = TextReader.getInstance();
    private static String ip;
    private static int port;
    private static String url_s;
    private final static String relativePath = "C:\\Users\\Иван\\Desktop\\Server\\";

    public static void main(String[] args) {
        url_s = readIpAndPort();
        start();
    }

    private static void start() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            String choice = textReader.readInMenu();
            if (!DataValidator.isMenuNumberValid(choice)) {
                System.out.println("Your choice " + choice + "is not valid");
                continue;
            }
            switch (choice) {
                case "g": {
                    get();
                    break;
                }
                case "pu": {
                    put();
                    break;
                }
                case "po": {
                    post();
                    break;
                }
                case "d": {
                    delete();
                    break;
                }
                case "c": {
                    copy();
                    break;
                }
                case "m": {
                    move();
                    break;
                }
                case "e": {
                    return;
                }
            }
        }
    }

    private static String readIpAndPort() {
        StringBuilder stringBuilder = new StringBuilder();
        ip = textReader.readIp();
        port = textReader.readPort();
        stringBuilder.append("http://").append(ip).append(":").append(port).append("/");
        return stringBuilder.toString();
    }

    private static void get() {
        String filename = textReader.readIn("Введите имя файла");
        try {
            URL url = new URL(url_s + filename);
            System.out.println(url);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            File saveFile = new File(relativePath + filename);
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            fileOutputStream.write(response);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }
    }

    private static void put() {
        String filename = textReader.readIn("Введите имя файла");
        String data = textReader.readIn("Новое содержимое");
        System.out.println("im here ==================");
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .PUT(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("content:" + data)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }
    }

    private static void post() {
        String filename = textReader.readIn("Введите имя файла");
        String newData = textReader.readIn("Добавляемое содержимое");

        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.
                            BodyPublishers.ofString(URLEncoder.encode("type:post&content:" + newData)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("///"+URLEncoder.encode("content:"+newPath));
            System.out.println(response);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }

    }

    private static void delete() {
        String filename = textReader.readIn("Введите имя файла");
        URI uri;
        try {
            uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }
    }

    private static void copy() {
        String filename = textReader.readIn("Введите имя файла");
        String newPath = textReader.readIn("Новый путь");
        String newFilename = textReader.readIn("Новое имя файла");
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("type:copy&newPath:" + newPath + "&newFilename:" + newFilename)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println("///"+URLEncoder.encode("type:copy&newPath:"+newPath+"&newFilename:"+newFilename));
            System.out.println(response);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }

    }

    private static void move() {
        String filename = textReader.readIn("Введите имя файла");
        String newPath = textReader.readIn("Новый путь");
        try {
            URI uri = new URI(url_s + filename);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(URLEncoder.encode("type:move&newPath:" + newPath)))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Не получилось выполнить запрос");
        }

    }

}
