package chatserver;

import chat.Chatter;

// �û���Ϣ��
public class UserInfo {
    // �û���
    private String name;
    // Զ�̿ͻ��˶���
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