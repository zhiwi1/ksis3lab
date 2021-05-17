package com.bsuir.lab3ksis.reader;

import com.bsuir.lab3ksis.validator.DataValidator;

import java.util.Scanner;

public class TextReader {
    private static TextReader instance;


    private TextReader() {
    }

    public static TextReader getInstance() {
        if (instance == null) {
            instance = new TextReader();
        }
        return instance;
    }

    public String readIn(String message) {
        System.out.println(message);
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        return line;
    }

    public String readIp() {
        String ip;
        System.out.print("Enter IP: ");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ip = scanner.nextLine();
            if (DataValidator.isIpValid(ip)) {
                break;
            }
            System.out.print("IP not valid ");
        }
        return ip;
    }

    public  int readPort() {
        System.out.print("Enter port: ");
        int port = 0;
        Scanner scanner = new Scanner(System.in);

        while (!scanner.hasNextInt()) {
            System.out.print("Port not valid ");
        }
        port = scanner.nextInt();
        return port;
    }
    public String readInMenu(){
        Scanner in=new Scanner(System.in);
        System.out.println("Menu:\ng:GET - чтение файла\npu:PUT - перезапись файла\npo:POST - добавление в конец файла \n" +
                "d:DELETE - удаление файла\nc:COPY - копирование файла\nm:MOVE - перемещение файла\ne-exit");
        return in.nextLine();

    }
}
