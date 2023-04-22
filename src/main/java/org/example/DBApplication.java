package org.example;
import javax.swing.*;
import java.sql.*;
import java.sql.Statement;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DBApplication extends JFrame{

    private String url = "jdbc:postgresql://localhost:5432/NS";
    private String user = "postgres";
    private String password = "0";

    public DBApplication(){

        setTitle("DataBase");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.CYAN);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem connectItem = new JMenuItem("Подключить базу данных");
        JMenuItem disconnectItem = new JMenuItem("Отключиться от базы данных");
        JMenuItem settingsItem = new JMenuItem("Настройки подключения");

        fileMenu.add(connectItem);
        fileMenu.add(disconnectItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        //Создаем панель с вкладками
        JTabbedPane tabbedPane = new JTabbedPane();

        //Добавляем вкладки
        JPanel panel1 = new JPanel();
        tabbedPane.addTab("Клиенты", panel1);
        addTableToPanel(panel1, "SELECT * FROM customer");

        JPanel panel2 = new JPanel();
        tabbedPane.addTab("Документы", panel2);
        addTableToPanel(panel2, "SELECT * FROM documents");

        JPanel panel3 = new JPanel();
        tabbedPane.addTab("Документ - Услуга", panel3);
        addTableToPanel(panel3, "SELECT * FROM allservicesindoc");

        JPanel panel4 = new JPanel();
        tabbedPane.addTab("Услуги", panel4);
        addTableToPanel(panel4, "SELECT * FROM service");

        JPanel panel5 = new JPanel();
        tabbedPane.addTab("Документ - Скидка", panel5);
        addTableToPanel(panel5, "SELECT * FROM allsaleindoc");

        JPanel panel6 = new JPanel();
        tabbedPane.addTab("Скидки", panel6);
        addTableToPanel(panel6, "SELECT * FROM sale");

        //Добавляем панель с вкладками на форму
        getContentPane().add(tabbedPane);

        setVisible(true);

        //Подключение
        connectItem.addActionListener(e -> {
            try {
                DBConnection dbConnection = new DBConnection(url, user, password);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка подключения к базе данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Настройка базы данных
        settingsItem.addActionListener(e -> {
            SettingDialog();
        });
    }

    private void SettingDialog(){
        JTextField urlField = new JTextField(url);
        JTextField userField = new JTextField(user);
        JTextField passwordField = new JTextField(password);

        JComponent[] inputs = new JComponent[] {
                new JLabel("URL:"),
                urlField,
                new JLabel("Пользователь:"),
                userField,
                new JLabel("Пароль:"),
                passwordField
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Данные подключения", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            url = urlField.getText();
            user = userField.getText();
            password = passwordField.getText();
        }

    }
    private void addTableToPanel(JPanel panel, String query){
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(query);

            //Создаем модель таблицы
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }
            Object[][] data = new Object[100][columnCount];
            int row = 0;
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    data[row][i - 1] = resultSet.getObject(i);
                }
                row++;
            }
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);

            //Добавляем таблицу на панель
            panel.add(scrollPane);
            revalidate();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к базе данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }


    //Базовые действия с базой
    private void insertData(String tableName, String[] values) throws SQLException{

        try{
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();

            String query = "Insert into " + tableName + "values (" + String.join(",", values ) + ")";
            Statement statement = connection.createStatement();
            int rowsInserted = statement.executeUpdate(query);

            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Успех");
            }

        }
        catch (SQLException ex)
        {
            JOptionPane.showConfirmDialog(this, "Ошибка выполнения: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

    }
}
