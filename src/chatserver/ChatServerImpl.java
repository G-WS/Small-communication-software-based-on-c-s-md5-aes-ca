package chatserver;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DButil.Dbutil;
import bean.Userbean;
import chat.ChatServer;
import chat.Chatter;
import dao.UserDao;
import dao.impl.UserDaoImpl;

// 服务器接口
public class ChatServerImpl extends java.rmi.server.UnicastRemoteObject implements ChatServer {
    static ChatServerImpl server = null;
    private final static String BINDNAME = "ChatServer";
    private final static String[] STATEMSG = new String[]{"服务器启动", "服务器停止"};
    List chatters = new ArrayList();
    List listeners = new ArrayList();

    protected ChatServerImpl() throws RemoteException {

    }

    public static ChatServerImpl getInstance() {
        try {
            if (server == null) {
                server = new ChatServerImpl();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
        return server;
    }

    public void start() throws RemoteException, MalformedURLException {
        java.rmi.Naming.rebind(BINDNAME, server);
        notifyListener(STATEMSG[0]);
    }

    public void stop() throws RemoteException, NotBoundException, MalformedURLException {
        notifyListener(STATEMSG[1]);
        Iterator itr = chatters.iterator();
        while (itr.hasNext()) {
            UserInfo u = (UserInfo) itr.next();
            u.getChatter().serverStop();
        }
        java.rmi.Naming.unbind(BINDNAME);
    }

    public void addListener(ChatServerListener listener) {
        listeners.add(listener);
    }

    public void removeListense(ChatServerListener listener) {
        listeners.remove(listener);
    }

    void notifyListener(String msg) {
        Iterator itr = listeners.iterator();
        ChatServerEvent evt = new ChatServerEvent(this, msg);
        while (itr.hasNext()) {
            ((ChatServerListener) itr.next()).serverEvent(evt);
        }
    }

    public void login(String name, Chatter c) throws RemoteException {
        UserDao userDao = new UserDaoImpl();
        Userbean userbean = new Userbean();
        userbean = userDao.get(name);
        String daName = userbean.getName();
        String dIP = userbean.getIp();
        if (c != null && name != null && daName.equals(name)) {
            UserInfo u = new UserInfo(name, c);
            notifyListener(u.getName() + " 进入聊天室");
            Iterator itr = chatters.iterator();
            while (itr.hasNext()) {
                UserInfo u2 = (UserInfo) itr.next();
                u2.getChatter().receiveEnter(name, c, false);
                c.receiveEnter(u2.getName(), u2.getChatter(), true);
            }
            chatters.add(u);
        }
    }

    public void logout(String name) throws RemoteException {
        if (name == null) {
            System.out.println("null name on logout: cannot remove chatter");
            return;
        }
        UserInfo u_gone = null;
        Iterator itr = null;

        synchronized (chatters) {
            for (int i = 0; i < chatters.size(); i++) {
                UserInfo u = (UserInfo) chatters.get(i);
                if (u.getName().equals(name)) {
                    notifyListener(name + " 离开聊天室");
                    u_gone = u;
                    chatters.remove(i);
                    itr = chatters.iterator();
                    break;
                }
            }
        }

        if (u_gone == null || itr == null) {
            System.out.println("no user by name of " + name + " found: not removing chatter");
            return;
        }

        while (itr.hasNext()) {
            UserInfo u = (UserInfo) itr.next();
            u.getChatter().receiveExit(name);
        }
    }

    public void chat(String name, String message) throws RemoteException {
        Iterator itr = chatters.iterator();

        while (itr.hasNext()) {
            UserInfo u = (UserInfo) itr.next();
            if (!name.equals(u.getName()))
                u.getChatter().receiveChat(name, message);
        }
        notifyListener(name + ":" + message);
    }
}