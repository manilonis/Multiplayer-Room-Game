/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.net.InetAddress;

/**
 *
 * @author maniloni
 */
public class FindOwnIP {

    /**
     * @param args the command line arguments
     */
    public static String getMyIP() { 
    int count = 0;
    String r = new String();
        try{
     Enumeration<NetworkInterface> nt = NetworkInterface.getNetworkInterfaces();
     while(nt.hasMoreElements()){
         NetworkInterface n = nt.nextElement();
        Enumeration<InetAddress> ias = n.getInetAddresses();
        while(ias.hasMoreElements()){
           InetAddress ia = ias.nextElement();
        if(count == 1){
            r = ia.getHostAddress();
        }
        count++;
        }
     }
        }catch(SocketException se){
            se.printStackTrace();
        }
        return r;
    }
    
    public static void main(String[] args){
        System.out.println(getMyIP());
    }
    
}
