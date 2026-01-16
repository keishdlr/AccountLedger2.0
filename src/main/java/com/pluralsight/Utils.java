package com.pluralsight;

public class Utils {

    //Color formatting will go here

    //1. find the color codes and set them as final strings
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";


    //2. create a method for the color amount and use an if else statement to set the rule
    public static String colorAmount(double amount) {
        if (amount >= 0) {
            return GREEN + String.format("%.2f", amount) + RESET;
        } else {
            return RED + String.format("%.2f", amount) + RESET;
        }
    }

    //3. Print results
    public static void printHeader(String title) {
        System.out.println("\n" + BLUE + "==============================");
        System.out.println("   " + title.toUpperCase());
        System.out.println("==============================" + RESET);
    }
}