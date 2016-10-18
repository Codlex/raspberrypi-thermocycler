package com.codlex.thermocycler.tracker;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Tracker {
	final static int port = 2003;
	final static String address = "pi-dash";
	
	public static void track(String path, Number value) {
		Socket socket = null;
		OutputStreamWriter writer = null;
		try {
			socket = new Socket(address, port);
			writer = new OutputStreamWriter(socket.getOutputStream());
			writer.write(path + " " + value + " " + (System.currentTimeMillis() / 1000) + "\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
