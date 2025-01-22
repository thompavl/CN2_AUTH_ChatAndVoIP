package com.sbaltsas.assignments.networksiichat;

public class AppStartup {
    public static void main(String[] args){
        if (args.length > 0){
            try{
                App.PORT = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e){
                System.out.println("Invalid port number! Using default port 6000!");
            }
        }
        App.main(args);
    }
}
