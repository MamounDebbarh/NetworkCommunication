import javax.sound.sampled.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;

public class Client {


    public static void main(String[] args) {

        // arguments handling
        int port;
        String name;
        String host;
        host = args[0];
        port = Integer.parseInt(args[1]);
        name = "files/" + args[2];

        // array of data
        byte[] rec = new byte[62800];
        byte[] b = "Connection successful, requesting data ...".getBytes();
        byte[] ok = "ok".getBytes();
        int i = 0;
        try {
            System.out.println("port : " + port);
            System.out.println("name : " + name);
            // datagram socket
            DatagramSocket ds=new DatagramSocket();
            // ip address
            InetAddress ip = InetAddress.getByName(host);
            // datagram packet to store stuff


            // request data from server

            // info cpy: , ip,3002
            DatagramPacket reqdp = new DatagramPacket(b,b.length, ip,port);
            ds.send(reqdp);


            FileOutputStream out = new FileOutputStream(name);


            while(true) {


                DatagramPacket recPacket = new DatagramPacket(rec, rec.length);
                ds.receive(recPacket);
                if (recPacket.getLength() < 62800){
                    System.out.println("\n Packet length: " + recPacket.getLength());
                    out.write(recPacket.getData(),0,recPacket.getLength());
                    System.out.println("\nPacket " + ++i + " written to file\n");
//                    Thread.sleep(5000);
                    DatagramPacket sanw = new DatagramPacket(ok,ok.length,ip,recPacket.getPort());
                    ds.send(sanw);
                    System.out.println("ending...");
                    break;
                }
                System.out.println("\n Packet length: " + recPacket.getLength());
                out.write(recPacket.getData(),0,recPacket.getLength());
                System.out.println("\nPacket " + ++i + " written to file\n");
                DatagramPacket sanw = new DatagramPacket(ok,ok.length,ip,recPacket.getPort());
                ds.send(sanw);



            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(name).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            while(clip.getMicrosecondLength() != clip.getMicrosecondPosition())
            {
            }
            clip.close();

            out.close();
            ds.close();


        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
