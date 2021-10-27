package chatclient;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.IllegalFormatCodePointException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// 连接对话
public class ConnectDlg extends JDialog implements PropertyChangeListener {
    JTextField serverAddrField;
    JTextField userNameField;
    JOptionPane optionPane;
    String serverAddr, userName;
    int value = -1;

    public ConnectDlg(Frame frame) {
        super(frame, "连接", true);
        serverAddrField = new JTextField(15);
        userNameField = new JTextField(20);
        Object[] array = {"服务器地址:", serverAddrField, "用户名:", userNameField};
        optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        setContentPane(optionPane);

        optionPane.addPropertyChangeListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        });
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (isVisible() && (e.getSource() == optionPane) && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            if (optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
                return;
            }
            value = ((Integer) optionPane.getValue()).intValue();
            optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
            if (value == JOptionPane.OK_OPTION) {
                if ("".equals(serverAddrField.getText()) || "".equals(userNameField.getText())) {
                    JOptionPane.showMessageDialog(this, "输入用户名或服务器地址");
                    if ("".equals(serverAddrField.getText()))
                        serverAddrField.requestFocus();
                    else if ("".equals(userNameField.getText()))
                        userNameField.requestFocus();
                } else {
                    setServerAddr(serverAddrField.getText());
                    setUserName(userNameField.getText());
                    setVisible(false);
                }
            } else {
                setVisible(false);
            }
        }
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getValue() {
        return value;
    }

}