package chat;

import java.rmi.RemoteException;

// 聊天服务器接口
public interface ChatServer extends java.rmi.Remote {
    /**
     * 注册新的聊天用户
     */
    public void login(String name, Chatter chatter) throws RemoteException;

    /**
     * 用户退出
     */
    public void logout(String name) throws RemoteException;

    /**
     * 用户调用此函数将消息发给所有用户
     */
    public void chat(String name, String message) throws RemoteException;
}