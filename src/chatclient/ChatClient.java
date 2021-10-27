package chatclient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;

import chat.ChatServer;
import chat.Chatter;
import cryptOfThisSystem.AES;
import cryptOfThisSystem.MD5;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import static cryptOfThisSystem.AES.encrypt;
import static cryptOfThisSystem.AES.generateKey;

public class ChatClient extends JFrame {
    // �洢���������û���key���û�����value����Ӧ��Chatter����
    Hashtable hash = new Hashtable();
    // �Լ����û���
    String my_name = "chatter";
    // ��������ַ
    String serverAddr;
    // ����ͻ��˵�Զ�̶���
    Chatter chatter;
    // �������˵�Զ�̶���
    ChatServer server;
    JTextArea displayBox;
    JTextArea inputBox;
    JComboBox usersBox;
    JButton sendButton;
    JLabel statusLabel;
    ConnectionAction connectAction = new ConnectionAction();
    // ���û������û����ͷ�������ַ���Ի���
    ConnectDlg dlg = new ConnectDlg(this);
    static Charset charset = Charset.forName("UTF-8");
   /* SecretKey secretKey;

    {
        try {
            secretKey = secretKey = generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) {
        new ChatClient();
    }

    public ChatClient() {
        super("����-�ͻ���");
        layoutComponent();
        setupMenu();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        show();
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenuItem conn = new JMenuItem(connectAction);
        JMenuItem exit = new JMenuItem("�˳�");
        exit.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                exit();
            }
        });
        JMenu file = new JMenu("�ļ�");
        file.add(conn);
        menuBar.add(file);
        setJMenuBar(menuBar);
    }

    private void exit() {
        destroy();
        System.exit(0);
    }

    // �༭ҳ��ؼ�Ԫ��
    public void layoutComponent() {
        setSize(400, 400);
        JPanel contentPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gridbag);
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = 6;
        c.weightx = 100;
        c.weighty = 100;
        c.insets.top = 5;
        displayBox = new JTextArea();
        displayBox.setLineWrap(true);
        displayBox.setEditable(false);
        displayBox.setMargin(new Insets(5, 5, 5, 5));
        JScrollPane scrollPane = new JScrollPane(displayBox);
        contentPane.add(scrollPane, c);
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.insets.top = 10;
        JLabel msgLabel = new JLabel("��Ϣ:");
        contentPane.add(msgLabel, c);
        c.gridheight = 6;
        c.insets.top = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.weightx = 100;
        inputBox = new JTextArea();
        addKeymapBindings();
        inputBox.setLineWrap(true);
        inputBox.setWrapStyleWord(true);
        JScrollPane inputScrollPane = new JScrollPane(inputBox);
        inputScrollPane.setPreferredSize(new Dimension(250, 50));
        inputScrollPane.setMinimumSize(new Dimension(250, 50));
        contentPane.add(inputScrollPane, c);
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        sendButton = new JButton(new ImageIcon(getClass().getResource("../images/send.gif")));
        sendButton.setToolTipText("������Ϣ");
        sendButton.setPreferredSize(new Dimension(50, 50));
        sendButton.setMinimumSize(new Dimension(50, 50));
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendMessage();
            }
        });
        contentPane.add(sendButton, c);

        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        JLabel sendtoLabel = new JLabel("���͸�:");
        contentPane.add(sendtoLabel, c);
        usersBox = new JComboBox();
        usersBox.setBackground(Color.WHITE);
        usersBox.addItem("�����û�");
        contentPane.add(usersBox, c);
        JPanel statusPane = new JPanel(new GridLayout(1, 1));
        statusLabel = new JLabel("δ����");
        statusPane.add(statusLabel);
        contentPane.add(statusPane, c);
        setContentPane(contentPane);
        try {
            chatter = new ChatterImpl(this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void destroy() {
        try {
            disconnect();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public void connect() throws RemoteException, java.net.MalformedURLException, java.rmi.NotBoundException {
        server = (ChatServer) java.rmi.Naming.lookup("//" + serverAddr + "/ChatServer");
        server.login(my_name, chatter);
    }

    protected void disconnect() throws RemoteException {
        if (server != null)
            server.logout(my_name);
    }

    public void receiveEnter(String name, Chatter chatter, boolean hasEntered) {
        if (name != null && chatter != null) {
            hash.put(name, chatter);
            if (!name.equals(my_name)) {
                // ���¼���������û�����displayBox������ʾ
                if (!hasEntered)
                    display(name + " ����������");
                usersBox.addItem(name);
            }
        }
    }

    public void receiveExit(String name) {
        if (name != null && chatter != null)
            hash.remove(name);
        for (int i = 0; i < usersBox.getItemCount(); i++) {
            if (name.equals((String) usersBox.getItemAt(i))) {
                usersBox.remove(i);
                break;
            }
        }
        display(name + " �뿪������");
    }

    public void receiveChat(String name, String message) {
        int i = message.indexOf("!!");
        message = message.substring(0, i);
        display(name + ": " + message);
    }

    public void receiveWhisper(String name, byte[] message, SecretKey secretKey) {

        try {
            long timeStart = System.currentTimeMillis();

            long timeEnd = System.currentTimeMillis();
            MD5 md5 = new MD5();
            String decryptResult = AES.decrypt(message, secretKey);
            System.out.println("���ܺ�Ľ��Ϊ��" + decryptResult);
            System.out.println("������ʱ��" + (timeEnd - timeStart));
            int id = decryptResult.indexOf("!!");

            System.out.println(id);
            String message1 = decryptResult.substring(0, id);
            System.out.println(message1);
            String md = decryptResult.substring(id + 2, decryptResult.length());
            if (md5.start(message1).substring(0, md.length()).equals(md)) {
                System.out.println("true");
                display(name + " ˽��: " + message1);
            } else {
                System.out.println("false");
                display(name + " ˽��: (����ȫ�������Ѿ����۸�)" + decryptResult);
            }

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

    }

    // �󶨼����¼�
    protected void addKeymapBindings() {
        Keymap keymap = JTextComponent.addKeymap("MyBindings", inputBox.getKeymap());
        Action action = null;
        KeyStroke key = null;
        // �û�����Ϣ���а��س�������Ϣ
        action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                sendMessage();
            }
        };
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        keymap.addActionForKeyStroke(key, action);

        // Ctrl+Enterʵ�ֻ���
        action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                inputBox.append("\n");
            }
        };
        key = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK);
        keymap.addActionForKeyStroke(key, action);

        inputBox.setKeymap(keymap);
    }

    private void display(String s) {
        if (!s.endsWith("\n")) {
            displayBox.append(s + "\n");
        } else {
            displayBox.append(s);
        }

        int length = displayBox.getText().length() - 1;
        displayBox.select(length, length);
    }

    private void sendMessage() {
        String message = inputBox.getText();
        if (message != null && message.length() > 0) {
            inputBox.setText(null);
            inputBox.setCaretPosition(0);

            try {
                long timeStrat = System.currentTimeMillis();
                SecretKey secretKey;
                secretKey = generateKey();
                MD5 md5 = new MD5();
                message = message + "!!" + md5.start(message);
                byte[] encryptResult = encrypt(message, secretKey);
                long timeEnd = System.currentTimeMillis();

                display(my_name + ":" + new String(encryptResult, charset));
                if (server != null) {
                    if ("�����û�".equals(usersBox.getSelectedItem())) {// ���͸������û�
                        try {
                            server.chat(my_name, message);
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        }
                    } else {// ˽�ģ�������ѡ�û�
                        String destUserName = (String) usersBox.getSelectedItem();
                        Chatter destChatter = (Chatter) hash.get(destUserName);
                        try {
                            destChatter.receiveWhisper(my_name, encryptResult, secretKey);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
                e.printStackTrace();
            }


        }
        inputBox.requestFocus();
    }

    public void serverStop() {
        display("������ֹͣ");
        server = null;
        hash.clear();
        connectAction.setEnabled(true);
    }

    class ConnectionAction extends AbstractAction {
        public ConnectionAction() {
            super("����");
            putValue(Action.SHORT_DESCRIPTION, "���ӵ�������");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        }

        public void actionPerformed(ActionEvent evt) {
            dlg.pack();
            dlg.setLocationRelativeTo(ChatClient.this);
            dlg.setVisible(true);
            if (dlg.getValue() == JOptionPane.OK_OPTION) {
                try {
                    my_name = dlg.getUserName();
                    serverAddr = dlg.getServerAddr();
                    connect();
                    inputBox.setEditable(true);
                    displayBox.setText("");
                    statusLabel.setText(my_name + " ������");
                    this.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("�������ӵ�������");
                    return;
                }
            }
        }
    }
}