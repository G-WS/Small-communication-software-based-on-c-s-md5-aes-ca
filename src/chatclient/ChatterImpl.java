package chatclient;

import java.rmi.RemoteException;

import chat.*;

import javax.crypto.SecretKey;

public class ChatterImpl extends java.rmi.server.UnicastRemoteObject implements Chatter {
    ChatClient client;

    public ChatterImpl(ChatClient client) throws RemoteException {
        this.client = client;
    }

    public void receiveEnter(String name, Chatter chatter, boolean hasEntered) throws RemoteException {
        client.receiveEnter(name, chatter, hasEntered);
    }

    public void receiveExit(String name) throws RemoteException {
        client.receiveExit(name);
    }

    public void receiveChat(String name, String message) throws RemoteException {
        client.receiveChat(name, message);
    }

    public void receiveWhisper(String name, byte[] message, SecretKey secretKey) throws RemoteException {
        client.receiveWhisper(name, message, secretKey);
    }

    public void serverStop() throws RemoteException {
        client.serverStop();
    }
}