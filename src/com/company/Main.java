package com.company;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static String path;
    public static String file;
    public static ProductParser parser;

    public static void main(String[] args) throws Exception {
        path = args[0];
        file = "links.txt";
        Scanner scanner = new Scanner(new File(file));
        ArrayList<String> arrayList = new ArrayList<>();
        while (scanner.hasNext())
            arrayList.add(scanner.next());
        scanner.close();
        for (String string : arrayList)
        {
            parser = new ProductParser(path, string);
            parser.saveProductInfo();
        }
    }
}

