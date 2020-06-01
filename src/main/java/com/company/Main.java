package com.company;
import com.company.ClientMassage;
public class Main {
    public static void main(String[] args) {
        Server server = new Server(4004);
        server.run();



    }
}
