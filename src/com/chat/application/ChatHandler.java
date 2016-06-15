package com.chat.application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

public class ChatHandler extends Thread {
	protected Socket s;
	protected DataInputStream i;
	protected DataOutputStream o;
	protected static Vector<ChatHandler> handlers = new Vector<ChatHandler>();

	// making streams b/w public String name;
	public ChatHandler(Socket s, String name) throws IOException {
		this.s = s;
		this.setName(name);
		i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		o = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
	}
	
	public void run() {
		// String name = s.getInetAddress().toString (); 
		try {
		Enumeration<ChatHandler> e = handlers.elements();
		while (e.hasMoreElements()) {
			ChatHandler c = (ChatHandler) e.nextElement();
			String n = new String(c.getName() + " is also online.");
			o.writeUTF(n);
			o.flush();
		}
		handlers.addElement(this);
		o.flush();
		o.writeUTF("whom do you want to talk any single person or group chat");
		o.flush();
		o.writeUTF("type name of person for chat with one person or group for group chat");
		o.flush();
		String flag = new String();
		flag = i.readUTF();
		if (flag != "group") {
			unicast(flag);
		}
		broadcast(getName() + " has joined you.");
		/*
		 * Enumeration e = handlers.elements (); while (e.hasMoreElements ()) {
		 * ChatHandler c = (ChatHandler) e.nextElement (); String n=new
		 * String(c.name); o.writeUTF(n);}
		 */
		while (true) {
			String msg = i.readUTF();
			broadcast(getName() + " - " + msg);
		}
	}catch(IOException ex)
	{
		ex.printStackTrace();
	}finally
	{
		handlers.removeElement(this);
		broadcast(getName() + " has left.");
		try {
			s.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	}

	@SuppressWarnings("deprecation")
	protected static void broadcast(String message) {
		synchronized (handlers) {
			Enumeration<ChatHandler> e = handlers.elements();
			while (e.hasMoreElements()) {
				ChatHandler c = (ChatHandler) e.nextElement();
				try {
					synchronized (c.o) {
						c.o.writeUTF(message);
					}
					c.o.flush();
				} catch (IOException ex) {
					c.stop();
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	protected void unicast(String name) {
		synchronized (handlers) {
			Enumeration<ChatHandler> e = handlers.elements();
			while (e.hasMoreElements()) {
				ChatHandler c = (ChatHandler) e.nextElement();
				try {
					if (c.getName() == name)
						while (true) {
							String str = i.readUTF();
							o.writeUTF(str);
							o.flush();
						}
				} catch (IOException ex) {
					c.stop();
				}
			}
		}
	}
}