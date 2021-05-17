package com.bsuir.lab3ksis.validator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import com.bsuir.lab3ksis.validator.DataValidator;
public class DataValidator {
    private final static String IP_REGEXP = "[0-9]{1,3}([.][0-9]{1,3}){3}";


    public static boolean isIpValid(String ip) {
        return ip.matches(IP_REGEXP);
    }
    public static boolean isMenuNumberValid(String choice){
        return choice.equals("pu")||choice.equals("po")||choice.equals("g")||choice.equals("c")||choice.equals("m")
        ||choice.equals("d")||choice.equals("e");
    }


}
