package com.example.android.snake;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkConnection implements Runnable {
	@Override
	public void run() {

		String hostname = "157.184.138.189";
		int portNumber = 9090;
		System.out.println("doing the socket now");
		try  {
			Socket echoSocket = new Socket(hostname, portNumber);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			out.println("Hello there");
		} catch (UnknownHostException e) {
			System.out.println("don't know about host " + hostname);
			System.out.println(e);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("couldn't get i/o for the connection to " + hostname);
			System.out.println(e);
			e.printStackTrace();
		}

		System.out.println("did the socket");
	}
}
