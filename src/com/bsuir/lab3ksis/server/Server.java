package com.bsuir.lab3ksis.server;



import com.bsuir.lab3ksis.reader.TextReader;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.file.*;

import java.util.*;

public class Server {
    private final static TextReader reader = TextReader.getInstance();
    private static String ip = "localhost";


    static {
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        boolean ready = false;
        int port=reader.readPort();
        while (!ready) {
            try {

                HttpServer server = HttpServer.create(new InetSocketAddress(port), 5);
                ready = true;
                System.out.println(ip + ":" + server.getAddress().getPort());
                server.createContext("/", new ServerHandler());
                server.setExecutor(null);
                server.start();
            } catch (InputMismatchException | IOException e) {
                e.printStackTrace();
                System.out.println("Некорректный ввод");
            }
        }
    }
    private static class ServerHandler implements HttpHandler {
        private final static String dirServer = "C:\\Users\\Иван\\Desktop\\Server\\";
        private static int status = 200;
        private static byte[] response;

        public void handle(HttpExchange exchange) throws IOException {
            response = new byte[0];
            String requestURI = exchange.getRequestURI().toString();
            System.out.println(exchange.getRequestMethod() + " " + exchange.getRequestURI() + " " + exchange.getProtocol());
            Headers headers = exchange.getRequestHeaders();
            for (String key : headers.keySet()) {
                System.out.println(key + " : " + headers.get(key));
            }
            String filename = requestURI.substring(requestURI.lastIndexOf('/') + 1);
            try {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        doGet(filename);
                        break;
                    case "POST":
                        doPost(exchange, filename);
                        break;
                    case "PUT":
                        doPut(exchange, filename);
                        break;
                    case "DELETE":
                        doDelete(filename);
                        break;
                    default:
                        status = 405;
                }
            } catch (FileNotFoundException | NoSuchFileException e) {
                e.printStackTrace();
                System.out.println("Файл не найден");
                status = 404;
            } catch (IOException e) {
                e.printStackTrace();
                status = 400;
            }
            exchange.sendResponseHeaders(status, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
            System.out.println(status);
            System.out.println("Размер: " + response.length);
        }

        private static void doGet(String filename) throws IOException {
            System.out.println(filename);
            response = Files.readAllBytes(Paths.get(dirServer + filename));
        }

        private static void doPost(HttpExchange exchange, String filename) throws IOException {
            StringBuilder bodystr = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String line;
            while ((line = in.readLine()) != null) {
                bodystr.append(URLDecoder.decode(line));
            }
            in.close();
            System.out.println("DOPOST" + bodystr.toString());

            String params[] = getParams(bodystr, new String[]{"type", "content"});
            System.out.println("DOPOST" + bodystr.toString());
            switch (params[0]) {
                case "copy": {
                    doCopy(exchange, filename, bodystr);
                    return;
                }
                case "move": {
                    doMove(exchange, filename, bodystr);
                    return;
                }
            }
            Path path = Paths.get(dirServer + filename);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            System.out.println(params[1]);
            Files.write(path, params[1].getBytes(), StandardOpenOption.APPEND);
        }

        private static void doPut(HttpExchange exchange, String filename) throws IOException {
            StringBuilder body = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String line;
            while ((line = in.readLine()) != null) {
                body.append(URLDecoder.decode(line));
            }
            in.close();
            Path path = Paths.get(dirServer + filename);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, getParams(body, new String[]{"content"})[0].getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }

        private static void doDelete(String filename) throws IOException {
            Files.delete(Paths.get(dirServer + filename));
        }

        private static void doMove(HttpExchange exchange, String filename, StringBuilder bodystr) throws IOException {
            String[] params = getParams(bodystr, new String[]{"type", "newPath"});
            Path path = Paths.get(params + "\\" + filename);
            if (Files.exists(path)) {
                exchange.getResponseHeaders().put("message", new ArrayList<>(Collections.singletonList("File with new name already exists ")));
                throw new IOException();
            }
            byte[] arr = Files.readAllBytes(Paths.get(dirServer + filename));
            File newFile = new File(dirServer + params[1] + "\\" + filename);
            if (!newFile.exists()) Files.createFile(Paths.get(dirServer + params[1] + "\\" + filename));
            newFile = new File(dirServer + params[1] + "\\" + filename);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            fileOutputStream.write(arr);
            fileOutputStream.close();
            Files.delete(Paths.get(dirServer + filename));
        }

        private static void doCopy(HttpExchange exchange, String filename, StringBuilder bodystr) throws IOException {
            String[] params = getParams(bodystr, new String[]{"type", "newPath", "newFilename"});
            Path path = Paths.get(dirServer + params[1] + "\\" + params[2]);
            if (Files.exists(path)) {
                exchange.getResponseHeaders().put("message", new ArrayList<>(Arrays.asList("File with new name is already exists ")));
                throw new IOException();
            }
            byte[] arr = Files.readAllBytes(Paths.get(dirServer + filename));
            File newFile = new File(dirServer + params[1] + "\\" + params[2]);
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            fileOutputStream.write(arr);
            fileOutputStream.close();
            System.out.println(filename + " " + params[1] + " " + params[2]);
        }

        private static String[] getParams(StringBuilder bodystr, String[] params) throws IOException {
            System.out.println(bodystr.toString());
            String[] result = new String[params.length];
            int i = 0;
            for (String param : params) {
                int start = bodystr.indexOf(param + ":") + param.length() + 1;
                if (start > bodystr.lastIndexOf("&")) {
                    result[i++] = bodystr.substring(start);
                } else {
                    int end = bodystr.substring(start).indexOf("&");
                    result[i++] = bodystr.substring(start, end + start);
                }
            }
            for (String str : result) {
                System.out.println(str);
            }
            return result;
        }

    }

}


