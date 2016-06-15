package com.chat.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer {
	public ChatServer(int port) {
		try {
			ServerSocket server = new ServerSocket(port);

			while (true) {
				Socket client = server.accept();
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream());
				out.println("enter your name : ");
				String name = in.readLine();
				out.println("Accepted from " + client.getInetAddress());
				ChatHandler c = new ChatHandler(client, name);
				c.start();
				server.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public static void main(String args[]) {
		String port = null;
		if (args.length == 1)
			port = args[0];
		else {
			System.out.println("Enter port to run server");
			Scanner sn = new Scanner(System.in);
			port = sn.nextLine();
			sn.close();
		}
		new ChatServer(Integer.parseInt(port));
		System.out.println("Chat server started!!!");
	}
}