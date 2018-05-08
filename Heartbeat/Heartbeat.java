/**
 * Michael E Anilonis
 * Apr 26, 2018
 */
package com.groupe.roomgame.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author manil
 *
 */
public class Heartbeat implements Runnable {
	private static final int port = 2700;
	private ArrayList<InetAddress> ips;
	private boolean isLeader;

	public Heartbeat(ArrayList<String> ip, boolean leader) {
		ips = Heartbeat.stringToInet(ip);
		isLeader = leader;
	}

	public Heartbeat(ArrayList<InetAddress> ip, boolean leader, boolean nothing) {
		ips = ip;
		isLeader = leader;
	}
	
	public Heartbeat(String ip, boolean leader) {
		ips = new ArrayList<InetAddress>();
		try {
			ips.add(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		isLeader = leader;
	}

	public void run() {
		if (!isLeader) {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
				DatagramPacket packet;
				socket.setSoTimeout(100);

				int timeouts = 0;
				while (!Thread.interrupted()) {
					packet = new DatagramPacket(makeHeartBeatPacket(), 1);
					try {
						socket.receive(packet);
						//System.out.println("Packet REceived");
						if (packet.getData()[0] != 100)
							throw new Error();
					} catch (SocketTimeoutException se) {
						timeouts++;
					}
					if (timeouts >= 3)
						break;
				}
				socket.close();
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		} else {
			DatagramSocket socket = null;
			try {
				socket = new DatagramSocket(port);
				DatagramPacket packet;

				while (!Thread.interrupted()) {
					for (InetAddress ia : ips) {
						packet = new DatagramPacket(makeHeartBeatPacket(), 1, ia, port);
						socket.send(packet);
						//System.out.println("Packet Sent");
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

	private byte[] makeHeartBeatPacket() {
		return new byte[] { 100 };
	}

	private static ArrayList<InetAddress> stringToInet(ArrayList<String> strs) {
		ArrayList<InetAddress> r = new ArrayList<InetAddress>();
		for (String str : strs) {
			try {
				r.add(InetAddress.getByName(str));
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		return r;
	}
}
