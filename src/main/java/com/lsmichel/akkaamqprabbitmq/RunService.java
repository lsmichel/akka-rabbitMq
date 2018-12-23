/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lsmichel.akkaamqprabbitmq;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author lathsessakpamichel
 */
public class RunService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String  ip= null;
       try{
           DatagramSocket socket = new DatagramSocket();
           socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
           ip = socket.getLocalAddress().getHostAddress();
         }catch(Exception ex){
           ex.printStackTrace();
        }
       if(ip !=null)
        CardManagerRunner.run(ip);
       else {
           System.out.println("impossible de lancer le serveur");
       }
    }
    
}
