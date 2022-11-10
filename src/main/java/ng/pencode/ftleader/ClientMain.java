package ng.pencode.ftleader;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ClientMain {

    static private Registry reg = null;
    static private FTBillboard currentServer = null;
    static private String serverID = null;

    static public void main(String[] args) throws NotBoundException {

        if (args.length != 2) {
            System.out.println("USAGE: ServerMain master port");
            System.exit(0);
        }

        String address = args[0];
        int port = Integer.parseInt(args[1]);
        serverID = address + ":" + port;
        List<String> neigbors = null;

        try {
            reg = LocateRegistry.getRegistry(address, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            currentServer = (FTBillboard) reg.lookup(FTBillboard.LOOKUP_NAME);
            System.out.println(currentServer.getLeader());
            System.out.println(currentServer.getNeighbors());
            neigbors = currentServer.getNeighbors();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("Starting Stupid client");

        for (int i = 0; i < 1000; i++) {
            String message = "Hello guys " + i, received = "";

            System.out.println("Test with message " + message);

            try {

                currentServer.setMessage(message);
                Thread.sleep(2500);
                received = currentServer.getMessage();
            } catch (java.rmi.ConnectException c) {

                try {
                    address = neigbors.get(0).split(":")[0];
                    port = Integer.parseInt(neigbors.get(0).split(":")[1]);
                    reg = LocateRegistry.getRegistry(address, port);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

                try {
                    currentServer = (FTBillboard) reg.lookup(FTBillboard.LOOKUP_NAME);
                    System.out.println(currentServer.getLeader());
                    System.out.println(currentServer.getNeighbors());
                } catch (java.rmi.ConnectException cc) {
                    neigbors.remove(0);
                } catch (RemoteException re) {

                }

            } catch (RemoteException e) {

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (received.equals(message)) {
                System.out.println("no problem");
            } else {
                System.out.println("Problem: " + received + " instead of " + message);

            }
        }
    }

}
