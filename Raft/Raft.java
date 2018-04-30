/**
 * Michael E Anilonis
 * Apr 20, 2018
 */
package com.groupe.roomgame.networking.Raft;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * @author manil
 *
 */
public class Raft {
	private int windowNum, seqNum;
	
	public Raft(int window) {
		windowNum = window;
		seqNum = 0;
	}
	
	/*
	 * OPCODES 
	 * vote package = 0 
	 * leader acknowledgement = 1 
	 * leader confirmation = 2
	 * data packet = 3 
	 * data acknowledgement = 4
	 */

	
	/*
	 * @param ip is the IPAdress for which the client is voting for as the leader
	 * @param term is the length of the leader's term
	 * @return the package used to vote for the leader
	 * 
	 */
	public static byte[] getVotePackage(InetAddress ip, int term) {
		byte opCode = 0;
		byte[] t = new byte[4];

		ByteBuffer bb = ByteBuffer.wrap(t);
		bb.putInt(term);
		bb.flip();
		t = bb.array();

		String name = ip.toString();
		String last4 = name.substring(name.length() - 4, name.length());
		if (last4.contains(".")) {
			int in = last4.indexOf('.');
			String temp = last4.substring(0, in);
			String temp1 = last4.substring(in + 1, last4.length());
			last4 = temp + temp1;
		}

		int four = Integer.parseInt(last4);
		ByteBuffer bb1 = ByteBuffer.allocate(4);
		bb1.putInt(four);
		bb1.flip();

		byte[] packet = new byte[9];
		packet[0] = opCode;

		byte[] b = bb1.array();
		for (int x = 0; x < b.length; x++) {
			packet[x + 1] = b[x];
		}

		int count = b.length;
		for (int y = 0; y < t.length; y++) {
			packet[count] = t[y];
			count++;
		}
		return packet;
	}

	/*
	 * @param numofVotes is the amount of votes the designated leader got
	 * @param term is the length of the leader term
	 * @param window is the size of the window used during data transfer between leader and other clients
	 * @return the leader acknowledgement packet
	 */
	public static byte[] getLeaderACKPacket(int numofVotes, int term, int window) {
		byte opCode = 1;
		ByteBuffer bb = ByteBuffer.allocate(13);
		bb.put(opCode);
		bb.putInt(numofVotes);
		bb.putInt(term);
		bb.putInt(window);
		bb.flip();

		return bb.array();
	}
	
	/*
	 * @return the Leader Confirmation Packet
	 */
	public static byte[] getLeaderConfirmationPacket() {
		return new byte[]{2};
	}
	
	/*
	 * @param data is the data wanting to be sent
	 * @return returns the complete data packet containing data
	 */
	public byte[] getDataPacket(byte[] data) {
		byte opcode = 3;
		
		byte[] seq = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(seq);
		bb.putInt(this.seqNum);
		bb.flip();
		seq = bb.array();
		
		byte[] r = new byte[5+data.length];
		r[0] = opcode;
		
		for(int x = 0; x<seq.length; x++) {
			r[x+1] = seq[x];
		}
		
		int k = seq.length + 1;
		for(int y = 0; y<data.length; y++) {
			r[k] = data[y];
			k++;
		}
		
		return r;
	}
	
	/*
	 * @return the acknowledgement packet for data
	 */
	public byte[] getDataAckPacket() {
		byte opcode = 4;
		
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(this.seqNum);
		bb.flip();
		byte[] seq = bb.array();
		
		byte[] r = new byte[seq.length+1];
		r[0] = opcode;
		
		for(int x = 0; x < seq.length; x++) {
			r[x+1] = seq[x];
		}
		
		return r;
	}

	/**
	 * @return the windowNum
	 */
	public int getWindowNum() {
		return windowNum;
	}

	/**
	 * @param windowNum the windowNum to set
	 */
	public void setWindowNum(int windowNum) {
		this.windowNum = windowNum;
	}

	/**
	 * @return the seqNum
	 */
	public int getSeqNum() {
		return seqNum;
	}

	/**
	 * @param seqNum the seqNum to set
	 */
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	
	public void increnmentSeqNum() {
		this.seqNum++;
	}

}
