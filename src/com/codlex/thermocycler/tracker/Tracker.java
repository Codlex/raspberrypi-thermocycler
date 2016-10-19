package com.codlex.thermocycler.tracker;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Tracker {

	final static ExecutorService TRACK_EXECUTOR = Executors
			.newSingleThreadExecutor();

	final static int port = 2003;
	final static String address = "pi-dash";

	public static void track(String path, Number value) {
		TRACK_EXECUTOR.submit(() -> {
			Socket socket = null;
			OutputStreamWriter writer = null;
			try {
				socket = new Socket(address, port);
				writer = new OutputStreamWriter(socket.getOutputStream());
				writer.write(path + " " + value + " "
						+ (System.currentTimeMillis() / 1000) + "\n");
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
		});
	}

}
