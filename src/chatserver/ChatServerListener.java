package chatserver;

import java.util.EventListener;

// �����������ӿ�
public interface ChatServerListener extends EventListener {
    public void serverEvent(ChatServerEvent evt);
}