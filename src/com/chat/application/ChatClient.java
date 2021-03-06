package com.chat.application;

import java.net.*;
import java.io.*;
import java.awt.*;

public class ChatClient extends Frame implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DataInputStream i;
	protected DataOutputStream o;
	protected TextArea output;
	protected TextField input;
	protected Thread listener;

	@SuppressWarnings("deprecation")
	public ChatClient(String title, InputStream i, OutputStream o) {
		super(title);
		this.i = new DataInputStream(new BufferedInputStream(i));
		this.o = new DataOutputStream(new BufferedOutputStream(o));
		setLayout(new BorderLayout());
		add("Center", output = new TextArea());
		output.setEditable(false);
		add("South", input = new TextField());
		pack();
		show();
		input.requestFocus();
		listener = new Thread(this);
		listener.start();
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			while (true) {
				String line = i.readUTF();
				output.appendText(line + "\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			listener = null;
			input.hide();
			validate();
			try {
				o.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public boolean handleEvent(Event e) {
		if ((e.target == input) && (e.id == Event.ACTION_EVENT)) {
			try {
				o.writeUTF((String) e.arg);
				o.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
				listener.stop();
			}
			input.setText("");
			return true;
		} else if ((e.target == this) && (e.id == Event.WINDOW_DESTROY)) {
			if (listener != null)
				listener.stop();
			hide();
			return true;
		}
		return super.handleEvent(e);
	}

	@SuppressWarnings({ "deprecation", "resource" })
	public static void main(String args[]) throws IOException {
		
		if (args.length != 2)
			throw new RuntimeException("Syntax: ChatClient <host> <port>");
		
		String msg, msg1;
		Socket s = new Socket(args[0], Integer.parseInt(args[1]));
		DataInputStream incoming = new DataInputStream(s.getInputStream());
		PrintStream outgoing = new PrintStream(s.getOutputStream());
		DataInputStream incoming1 = new DataInputStream(System.in);
		msg = incoming.readLine();
		System.out.println(msg);
		msg1 = incoming1.readLine();
		outgoing.println(msg1);
		new ChatClient(msg1, s.getInputStream(), s.getOutputStream());
	}
}