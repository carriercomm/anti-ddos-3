import java.io.*;
import java.net.*;

class UDPClient{
        public static void main(String args[]) throws Exception{
                //BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
                DatagramSocket clientSocket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("10.10.13.2");
                byte[] sendData = new byte[1024];
                //String sentence = inFromUser.readLine();
                String sentence = "src:10.10.10.1;ctl:10.10.11.2;law:10.10.13.2";
                sendData = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);
                clientSocket.close();
        }
}
