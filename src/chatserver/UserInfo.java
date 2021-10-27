package chatserver;

import chat.Chatter;

// 用户信息类
public class UserInfo {
    // 用户名
    private String name;
    // 远程客户端对象
    private Chatter chatter;

    public UserInfo(String name, Chatter chatter) {
        setName(name);
        setChatter(chatter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chatter getChatter() {
        return chatter;
    }

    public void setChatter(Chatter chatter) {
        this.chatter = chatter;
    }

}