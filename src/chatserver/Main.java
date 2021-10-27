package chatserver;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.KeyStore;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

// 服务器主函数
public class Main extends JFrame implements ChatServerListener {
    ChatServerImpl server = ChatServerImpl.getInstance();
    JTextArea textArea;
    JMenuBar menuBar;
    JToolBar toolBar;
    StarServerAction startAction = new StarServerAction();
    StopServerAction stopAction = new StopServerAction();

    public static void main(String[] args) {
        Main main = new Main();
        main.show();
    }

    public Main() {
        super("聊天-服务器");
        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        setSize(300, 500);
        layoutComponents();
        String s1 = this.getClass().getResource("").getPath();
        System.out.println(s1);
    }

    private void layoutComponents() {
        setupMenu();
        setupToolBar();
        textArea = new JTextArea();
        textArea.setSize(200, 300);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    private void setupMenu() {
        menuBar = new JMenuBar();
        JMenuItem startServer = new JMenuItem(startAction);
        JMenuItem stopServer = new JMenuItem(stopAction);
        JMenuItem exit = new JMenuItem("离开");
        exit.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                exit();
            }
        });
        JMenu server = new JMenu("服务器");
        server.add(startServer);
        server.add(stopServer);
        server.add(exit);
        menuBar.add(server);
        setJMenuBar(menuBar);
    }

    private void setupToolBar() {
        toolBar = new JToolBar();
        JButton button = null;
        addTool(toolBar, startAction);
        addTool(toolBar, stopAction);

        getContentPane().add(toolBar, BorderLayout.NORTH);
    }

    JButton addTool(JToolBar toolBar, AbstractAction action) {
        JButton b = new JButton();
        b.setAction(action);
        b.setText(null);
        toolBar.add(b);
        return b;
    }

    private void exit() {
        try {
            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void serverEvent(ChatServerEvent evt) {
        textArea.append(evt.getMessage() + "\n");
    }

    class StarServerAction extends AbstractAction {
        public StarServerAction() {
            super("启动");
            putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("../images/start.gif")));
            putValue(Action.SHORT_DESCRIPTION, "启动聊天服务器");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
        }

        public void actionPerformed(ActionEvent evt) {
            try {
                server.addListener(Main.this);
                textArea.setText("");
                server.start();
                stopAction.setEnabled(true);
                this.setEnabled(false);
            } catch (Exception ex) {
                textArea.append("服务器启动错误\n");
                server.removeListense(Main.this);
                ex.printStackTrace();
                return;
            }
        }
    }

    class StopServerAction extends AbstractAction {
        public StopServerAction() {
            super("停止");
            putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("../images/stop.gif")));
            putValue(Action.SHORT_DESCRIPTION, "停止聊天服务器");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control O"));
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent arg0) {
            try {
                server.stop();
                server.removeListense(Main.this);
                startAction.setEnabled(true);
                this.setEnabled(false);
            } catch (Exception e) {
                textArea.append("服务器停止错误\n");
                e.printStackTrace();
                return;
            }
        }
    }
}