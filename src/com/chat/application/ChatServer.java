package com.chat.application;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
	public ChatServer(int port) throws IOException {

		ServerSocket server = new ServerSocket(port);
		while (true) {
			String name; 
			Socket client = server.accept();
			DataInputStream in = new DataInputStream(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream());
			out.println("enter your name : ");
			name = in.readLine();
			System.out.println("Accepted from " + client.getInetAddress());
			ChatHandler c = new ChatHandler(client, name);
			c.start();
			server.close();
		}
	}

	public static void main(String args[]) throws IOException {
		if (args.length != 1)
			throw new RuntimeException("Syntax: ChatServer <port>");
		new ChatServer(Integer.parseInt(args[0]));
	}
}