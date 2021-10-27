package chatserver;

import java.util.EventListener;

// 服务器监听接口
public interface ChatServerListener extends EventListener {
    public void serverEvent(ChatServerEvent evt);
}