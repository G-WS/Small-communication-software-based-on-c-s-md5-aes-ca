package DButil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Dbutil {
    /*
     * ���ڲ������ݿ��Ƿ����ӳɹ�
     */

    public static void main(String[] args) {
        Dbutil util = new Dbutil();
        Connection conn = util.getConnection();
        System.out.println(conn);
        //util.add();
    }//�˷������������ݿ��������Ϣ

    /*�������ڻ�����ݿ�����
     * ���Ҵ����ӷ���ֻ����������MySQL���ݿ�
     */

    public Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            //����������������ص���MySQL����������
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/"
                    + "CA?serverTimezone=UTC", "root", "Zhy06150412252814732");
            //����ʱ����ͬ������������������ݿ����ƺ����"?serverTimezone=UTC",����д�����������ݿ�Ķ˿ںź����ݿ����ƣ������û���������

        } catch (Exception e) {
            e.getStackTrace();

        }
        //�����������쳣����һ���Ľ��
        return null;

    }
    //�˷��������������Ӹ������ݿ�
	/*public Connection getConnection(String url,String driver,String username,String password) {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url,username,password);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;



	}*/
	/*public Connection openConnection() {
		properties prop=new properties();
	}*/
    //�������ڹر����ݿ�����

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}