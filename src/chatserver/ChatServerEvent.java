package chatserver;

import java.util.EventObject;

// 服务器事件
public class ChatServerEvent extends EventObject {
    String message;

    public ChatServerEvent(Object src, String message) {
        super(src);
        setMessage(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}