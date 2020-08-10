
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class Server {

    public static void main(String[] args) {
        // arguments handling
        int port;
        String name;

        port = Integer.parseInt(args[0]);
        name = "files/" + args[1];

        // array of data

        int size = 62800;
        byte[] rec = new byte[size];
        byte[] ack = new byte[2];
        byte[] ok = "ok".getBytes();
        int count;
        int totLength = 0;

        try {

            // file reading and processing
            System.out.println("port : " + port);
            System.out.println("name : " + name);
            File file = new File(name);
            FileInputStream fis = new FileInputStream(file);

            while((count = fis.read(rec)) != -1) {
                totLength += count;
            }
            System.out.println("Total Length :" + totLength);

            int noOfPackets = totLength/size;
            System.out.println("No of packets : " + noOfPackets);

            int off = noOfPackets * size;
            int lastPackLen = totLength - off;
            System.out.println("\nLast packet Length : " + lastPackLen);

            byte[] lastPack;
            fis.close();


            // datagram socket
            DatagramSocket ds=new DatagramSocket(port);
            // ip address
            // localhost
            // mmd4.host.cs.st-andrews.ac.uk
            // klovia.cs.st-andrews.ac.uk
            InetAddress ip = InetAddress.getByName("klovia.cs.st-andrews.ac.uk");
            // datagram packet to send stuff


            // wait for client

            DatagramPacket waitdp = new DatagramPacket(rec,rec.length);
            ds.receive(waitdp);

            // debugging and print
            String msg = new String(waitdp.getData(),0,waitdp.getLength());
            System.out.println("received :  "+ msg);


            // sends data to client

            FileInputStream fis1 = new FileInputStream(file);
            while(fis1.read(rec) != -1 )
            {
                if(noOfPackets<=0)
                    break;
                DatagramPacket sendPacket = new DatagramPacket(rec, rec.length, ip, waitdp.getPort());
                ds.send(sendPacket);
//                Thread.sleep(0,1);
                System.out.println("========");
                System.out.println("packets left : " + noOfPackets);
                noOfPackets--;
                DatagramPacket sanw = new DatagramPacket(ack,ack.length);
                ds.receive(sanw);
                ds.setSoTimeout(2000);

                while (!Arrays.equals(sanw.getData(), ok)){
                    try {
                        System.out.println("problem");
                        ds.send(sendPacket);
                        ds.receive(sanw);
                    } catch (SocketTimeoutException e) {
                        // timeout exception.
                        System.out.println("Timeout reached!!! " + e);
                        ds.close();
                        System.exit(0);
                    }
                }

            }


            lastPack = Arrays.copyOf(rec, lastPackLen);
            DatagramPacket sendPacket1 = new DatagramPacket(lastPack, lastPack.length, ip,waitdp.getPort());
            ds.send(sendPacket1);

            DatagramPacket sanw = new DatagramPacket(ack,ack.length);
            ds.receive(sanw);
            ds.setSoTimeout(3000);
            while (!Arrays.equals(sanw.getData(), ok)){
                try {
                    System.out.println("problem");
                    ds.send(sendPacket1);
                    ds.receive(sanw);
                } catch (SocketTimeoutException e) {
                    // timeout exception.
                    System.out.println("Timeout reached!!! " + e);
                    ds.close();
                    System.exit(0);
                }
            }

            ds.close();

        } catch (IOException e) {
            System.out.println("ERROR!");
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}