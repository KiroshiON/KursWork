package org.example;

import org.example.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        documentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableDocuments();
            }
        });

        JButton docToServicesButton = new JButton("Документ-услуга");
        docToServicesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableDocToService();
            }
        });

        JButton serviceButton = new JButton("Услуга");
        serviceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableServices();
            }
        });

        JButton docToSaleButton = new JButton("Документ-скидка");
        docToSaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableDocToSales();
            }
        });

        JButton salesButton = new JButton("Скидки");
        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableSale();
            }
        });


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
        String primaryKeyColumnName = "id_customer";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }

    private void tableDocuments(){
        String tableName = "documents";
        String primaryKeyColumnName = "id_doc";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }
    private void tableDocToService(){
        String tableName = "doctoservices";
        //operations(tableName);
        refreshTable();
    }
    private void tableServices(){

    }
    private void tableDocToSales(){

    }
    private void tableSale(){

    }


    private void operations(String tableName, String primaryKeyColumnName){
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
                updateRecord(tableName, primaryKeyColumnName ,idInt);
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
                } else if (value instanceof String && metaData.getColumnType(i + 1) == Types.DATE) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse((String)value);
                    java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                    preparedStatement.setDate(i + 1, sqlDate);
                } else if (value instanceof String && metaData.getColumnType(i + 1) == Types.NUMERIC) {
                    BigDecimal decimalValue = new BigDecimal((String) value);
                    preparedStatement.setBigDecimal(i + 1, decimalValue);
                } else {
                    preparedStatement.setObject(i + 1, value);
                }
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Запись успешно добавлена в таблицу " + tableName);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateRecord(String tableName, String primaryKeyColumnName, int recordId) {
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + primaryKeyColumnName + " = " + recordId);
            resultSet.next(); // Move the ResultSet to the first row

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<Object> values = new ArrayList<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                if (i == 1) { // Primary key column
                    values.add(resultSet.getObject(columnName));
                } else {
                    String inputValue = JOptionPane.showInputDialog("Введите новое значение для столбца " + columnName);
                    Object value = null;
                    // Convert input value to the correct data type
                    switch (metaData.getColumnType(i)) {
                        case Types.BIGINT:
                            value = Long.parseLong(inputValue);
                            break;
                        case Types.DATE:
                            value = Date.valueOf(inputValue);
                            break;
                        case Types.NUMERIC:
                            value = Double.parseDouble(inputValue);
                            break;
                        case Types.VARCHAR:
                            value = inputValue;
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "Неизвестный тип данных для столбца " + columnName, "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return; // Exit the method if an unknown data type is encountered
                    }
                    values.add(value);
                }
            }

            String updateQuery = "UPDATE " + tableName + " SET ";
            for (int i = 2; i <= columnCount; i++) { // Start from 2 to skip the primary key column
                String columnName = metaData.getColumnName(i);
                updateQuery += columnName + " = ?, ";
            }
            updateQuery = updateQuery.substring(0, updateQuery.length() - 2);
            updateQuery += " WHERE " + primaryKeyColumnName + " = " + recordId;

            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            for (int i = 2; i <= columnCount; i++) { // Start from 2 to skip the primary key column
                Object value = values.get(i - 1);
                preparedStatement.setObject(i - 1, value);
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
