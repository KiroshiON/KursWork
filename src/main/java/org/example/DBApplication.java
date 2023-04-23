package org.example;

import org.example.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;



public class DBApplication extends JFrame{

    private String url = "jdbc:postgresql://localhost:5432/NS";
    private String user = "postgres";
    private String password = "0";

    JTabbedPane tabbedPane = new JTabbedPane();


    public DBApplication(){

        setTitle("DataBase");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.CYAN);

        JMenuBar menuBar = new JMenuBar();

        //file menu
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem connectItem = new JMenuItem("Подключить базу данных");
        JMenuItem settingsItem = new JMenuItem("Настройки подключения");

        fileMenu.add(connectItem);
        fileMenu.addSeparator();
        fileMenu.add(settingsItem);

        menuBar.add(fileMenu);

        //operation menu

        JMenuItem operationItem = new JMenuItem("Операций");

        menuBar.add(operationItem);

        setJMenuBar(menuBar);

        //Создаем панель с вкладками


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

        operationItem.addActionListener(e -> {
            choiceTable();
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

    private void choiceTable(){
        JButton customerButton = new JButton("Клиенты");
        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableCustomer();
            }
        });


        JButton documentButton = new JButton("Документы");
        JButton docToServicesButton = new JButton("Документ-услуга");
        JButton serviceButton = new JButton("Услуга");
        JButton docToSaleButton = new JButton("Документ-скидка");
        JButton salesButton = new JButton("Скидки");

        JComponent[] inputs = new JComponent[] {
                new JLabel("Выберите таблицу"),
                customerButton,
                documentButton,
                docToServicesButton,
                serviceButton,
                docToSaleButton,
                salesButton


        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Выбор таблицы", JOptionPane.OK_CANCEL_OPTION);

    }

    private void tableCustomer(){
        String tableName = "customer";
        operations(tableName);
        refreshTable();


    }

    private void tableDocuments(){

    }
    private void tableDocToService(){

    }
    private void tableServices(){

    }
    private void tableDocToSales(){

    }
    private void tableSale(){

    }


    private void operations(String tableName){
        JButton insert = new JButton("Вставка");
        insert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertRecord(tableName);
            }
        });


        JButton update = new JButton("Обновить");
        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JTextField id = new JTextField();

                JComponent[] inputs = new JComponent[]{
                        new JLabel("Введите код:"),
                        id
                };

                int result = JOptionPane.showConfirmDialog(null, inputs, "Код", JOptionPane.OK_CANCEL_OPTION);

                int idInt = Integer.parseInt(id.getText());
                updateRecord(tableName, idInt);
            }
        });

        JButton delete = new JButton("Удалить");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecord(tableName);
            }
        });

        JComponent[] inputs = new JComponent[] {
                new JLabel("Выберите операцию"),
                insert,
                update,
                delete
        };

        int result = JOptionPane.showConfirmDialog(null, inputs, "Выбор операций", JOptionPane.OK_CANCEL_OPTION);
    }

    //Operation
    private void insertRecord(String tableName) {
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 0");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Object> values = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String inputValue = JOptionPane.showInputDialog("Введите значение для столбца " + columnName);
                values.add(inputValue);
            }

            String insertQuery = "INSERT INTO " + tableName + " VALUES (";
            for (int i = 0; i < columnCount; i++) {
                insertQuery += "?,";
            }
            insertQuery = insertQuery.substring(0, insertQuery.length() - 1);
            insertQuery += ")";

            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);
                if (value instanceof String && metaData.getColumnType(i + 1) == Types.BIGINT) {
                    preparedStatement.setLong(i + 1, Long.parseLong((String)value));
                } else {
                    preparedStatement.setObject(i + 1, value);
                }
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Запись успешно добавлена в таблицу " + tableName);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateRecord(String tableName, int recordId) {
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE id_customer = " + recordId);
            resultSet.next(); // Move the ResultSet to the first row

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Object> values = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                if (i == 1) { // First column
                    values.add(resultSet.getObject(columnName));
                } else {
                    String inputValue = JOptionPane.showInputDialog("Введите новое значение для столбца " + columnName);
                    values.add(inputValue);
                }
            }

            String updateQuery = "UPDATE " + tableName + " SET ";
            for (int i = 2; i <= columnCount; i++) { // Start from 2 to skip the first column
                String columnName = metaData.getColumnName(i);
                updateQuery += columnName + " = ?, ";
            }
            updateQuery = updateQuery.substring(0, updateQuery.length() - 2);
            updateQuery += " WHERE id_customer = " + recordId;

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            for (int i = 1; i < values.size(); i++) { // Start from 1 to skip the first column
                Object value = values.get(i);
                if (value instanceof String && metaData.getColumnType(i + 1) == Types.BIGINT) {
                    preparedStatement.setLong(i, Long.parseLong((String)value));
                } else {
                    preparedStatement.setObject(i, value);
                }
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Запись успешно обновлена в таблице " + tableName);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при обновлении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteRecord(String tableName) {
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 0");

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            String columnName = metaData.getColumnName(1); // Получаем название первого столбца

            String inputValue = JOptionPane.showInputDialog("Введите значение для столбца " + columnName);

            String deleteQuery = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setLong(1, Long.parseLong(inputValue));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Запись успешно удалена из таблицы " + tableName);
            } else {
                JOptionPane.showMessageDialog(this, "Не удалось найти запись для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при удалении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getQueryForTabIndex(int tabIndex) {
        String query = null;
        switch (tabIndex) {
            case 0:
                query = "SELECT * FROM customer";
                break;
            case 1:
                query = "SELECT * FROM documents";
                break;
            case 2:
                query = "SELECT * FROM allservicesindoc";
                break;
            case 3:
                query = "SELECT * FROM service";
                break;
            case 4:
                query = "SELECT * FROM allsaleindoc";
                break;
            case 5:
                query = "SELECT * FROM sale";
                break;
            default:
                throw new IllegalArgumentException("Invalid tab index: " + tabIndex);
        }
        return query;
    }
    private void refreshTable() {
        int tabIndex = tabbedPane.getSelectedIndex();
        JPanel panel = (JPanel) tabbedPane.getComponentAt(tabIndex);
        String query = getQueryForTabIndex(tabIndex);

        //Удаляем предыдущую таблицу из панели
        for (Component component : panel.getComponents()) {
            if (component instanceof JScrollPane) {
                panel.remove(component);
                break;
            }
        }

        //Добавляем обновленную таблицу
        addTableToPanel(panel, query);
    }
}
