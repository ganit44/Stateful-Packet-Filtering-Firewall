package firewall;

import static firewall.ClientSession.info;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Ganit
 */
public class Server {
    
        
        Server()
        {
            String info[] = new String[2];
            ServerSocket server = null;
            Socket client;
            InetAddress addr;
            
            try{
                addr=InetAddress.getLocalHost(); 
                server = new ServerSocket(7344);
                info[0]=addr.toString();
            }
            catch(IOException ie){
                System.out.println("Cannot Open Socket");
                System.exit(1);
            }
            new ServerWindowThread().start();
            
            
            System.out.println("Welcome !!!\nThis server is running on Local Port Number : " + server.getLocalPort() + "\nWaiting for connection\n");

            info[1]=Integer.toString(server.getLocalPort());
            ServerWindow.getWelcomeMessage(info);
            
            
            
            while(true) {
            try {
                  System.out.println("Yes I did it.");
                  client = server.accept();
                 new Thread(new ClientSession(client)).start();
            } catch (IOException ie) {}}
        }
}

class ClientSession implements Runnable {

        private final Socket clientsocket;
        static String info[] = new String[2];
        static String temp;
        List<String> list=new ArrayList<>();
        Scanner scanner=new Scanner("block.txt");
        ClientSession(Socket sock) {
            this.clientsocket = sock;
            temp = clientsocket.getRemoteSocketAddress().toString();
        }

        @Override
        
        public void run() {
            FileInputStream fstream;
            try 
            {
                fstream = new FileInputStream("block.txt");
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                while ((strLine = br.readLine()) != null)   
                {
                    list.add(strLine.trim()); 
                }
            }
            catch(Exception e){}
            //System.out.println(Arrays.toString(list.toArray()));
            try{
                OutputStream clientOut = clientsocket.getOutputStream();
                PrintWriter pw = new PrintWriter(clientOut, true);
                InputStream clientIn = clientsocket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(clientIn));
                //System.out.println("Connection Established\n" + clientsocket.getRemoteSocketAddress());
                if(list.contains(clientsocket.getRemoteSocketAddress().toString().substring(1, clientsocket.getRemoteSocketAddress().toString().indexOf(":"))))
                {
                    info[0]=clientsocket.getRemoteSocketAddress().toString() + "Blocked";
                    ServerWindow.getConfirmation(info);
                    pw.println("You are Blocked");
                    clientsocket.close();
                }
                else
                {
                    info[0]=clientsocket.getRemoteSocketAddress().toString();
                    //pw.println("You are Welcome.");
                    ServerWindow.getConfirmation(info);
                }
                
                //For Message Receiving
                
                Thread tq = new Thread();
                
                while(true)
                {
                    info[0]=br.readLine();
                    info[1]=clientsocket.getRemoteSocketAddress().toString();
                    
                    
                    FileInputStream fstreamMes;
                    java.util.List<String> list = new ArrayList<>();
                    try 
                    {
                        fstreamMes = new FileInputStream("stringblock.txt");
                        DataInputStream in = new DataInputStream(fstreamMes);
                        BufferedReader brMes = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        while ((strLine = brMes.readLine()) != null)   
                        {
                            list.add(strLine.trim()); 
                        }
                    }catch(Exception e){}
                    
                   
                    String packet[] = info[0].trim().split("\\s+");
                    //java.util.List<String> msglist = new ArrayList<>();
                    String msglist="";
                    int l = packet.length;
                    int j;
                    tq.resume();
                    for(j=0;j<l;j++)
                    {
                        if(list.contains(packet[j]))
                        {
                            info[0]=packet[j];
                            //ServerWindow.getClientMessage(info);
                            msglist = msglist + packet[j] + " Discarded#";
                        }
                        else
                        {
                            info[0]=packet[j];
                            ServerWindow.getClientMessage(info);
                            msglist = msglist + packet[j] + " Accepted#"; 
                        }
                        try 
                        {
                            tq.sleep(100);
                        }
                        catch(Exception e){}
                    }
                    tq.suspend();
                    pw.println(msglist);
                }
            }
            catch(IOException ie)
            {}
        }

    }

class ServerWindowThread extends Thread
{
    public void run()
    {
        ServerWindow sw = new ServerWindow();
    }
}
