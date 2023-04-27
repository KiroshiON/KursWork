package org.example;

import javax.swing.*;
import java.awt.*;
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

    String SPECIAL_SALE_TABLE = "allsaleindoc";
    String SPECIAL_SERVICE_TABLE = "allservicesindoc";
    public DBApplication(){

        setTitle("DataBase");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(Color.CYAN);

        JMenuBar menuBar = new JMenuBar();

        //operation menu

        JMenuItem operationItem = new JMenuItem("Операций");

        menuBar.add(operationItem);

        setJMenuBar(menuBar);

        //add panel to tables
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

        getContentPane().add(tabbedPane);

        setVisible(true);

        //connect


        operationItem.addActionListener(e -> choiceTable());
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
        customerButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableCustomer();
        });

        JButton documentButton = new JButton("Документы");
        documentButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableDocuments();
        });

        JButton docToServicesButton = new JButton("Документ-услуга");
        docToServicesButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableDocToService();
        });

        JButton serviceButton = new JButton("Услуга");
        serviceButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableServices();
        });

        JButton docToSaleButton = new JButton("Документ-скидка");
        docToSaleButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableDocToSales();
        });

        JButton salesButton = new JButton("Скидки");
        salesButton.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            tableSale();
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

        JOptionPane.showMessageDialog(null, inputs, "Выбор таблицы", JOptionPane.OK_CANCEL_OPTION);

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
        String tableName = "allservicesindoc";
        String primaryKeyColumnName = "id_doc";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }
    private void tableServices(){
        String tableName = "service";
        String primaryKeyColumnName = "id_service";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }
    private void tableDocToSales(){
        String tableName = "allsaleindoc";
        String primaryKeyColumnName = "id_doc";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }

    private void tableSale(){
        String tableName = "sale";
        String primaryKeyColumnName = "id_sale";
        operations(tableName, primaryKeyColumnName);
        refreshTable();
    }


    private void operations(String tableName, String primaryKeyColumnName){
        JButton insert = new JButton("Вставка");
        insert.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            insertRecord(tableName);
        });


        JButton update = new JButton("Обновить");
        update.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();

            if(tableName.equals(SPECIAL_SALE_TABLE) || tableName.equals(SPECIAL_SERVICE_TABLE)){
                specialUpdateRecord(tableName);
            }else{updateRecord(tableName, primaryKeyColumnName);}

        });

        JButton delete = new JButton("Удалить");
        delete.addActionListener(e -> {
            ((JDialog)((JButton)e.getSource()).getTopLevelAncestor()).dispose();
            deleteRecord(tableName);
        });

        JComponent[] inputs = new JComponent[] {
                new JLabel("Выберите операцию"),
                insert,
                update,
                delete
        };

        JOptionPane.showMessageDialog(null, inputs, "Выбор операций для " + tableName, JOptionPane.OK_CANCEL_OPTION);
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
                Object value = null;
                // Convert input value to the correct data type
                switch (metaData.getColumnType(i)) {
                    case Types.BIGINT -> value = Long.parseLong(inputValue);
                    case Types.DATE -> {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date parsedDate = dateFormat.parse(inputValue);
                        Date sqlDate = new Date(parsedDate.getTime());
                        value = sqlDate;
                    }
                    case Types.NUMERIC, Types.DOUBLE -> value = Double.parseDouble(inputValue);
                    case Types.VARCHAR -> value = inputValue;
                    default -> {
                        JOptionPane.showMessageDialog(this, "Неизвестный тип данных для столбца " + columnName, "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return; // Exit the method if an unknown data type is encountered
                    }
                }
                values.add(value);
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
                preparedStatement.setObject(i + 1, value);
            }

            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Запись успешно добавлена в таблицу " + tableName);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при добавлении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateRecord(String tableName, String primaryKeyColumnName) {
        try {
            JTextField id = new JTextField();

            JComponent[] inputs = new JComponent[]{
                    new JLabel("Введите код:"),
                    id
            };

            JOptionPane.showConfirmDialog(null, inputs, "Код", JOptionPane.OK_CANCEL_OPTION);

            int recordId = Integer.parseInt(id.getText());


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
                        case Types.BIGINT -> value = Long.parseLong(inputValue);
                        case Types.DATE -> value = Date.valueOf(inputValue);
                        case Types.NUMERIC, Types.DOUBLE -> value = Double.parseDouble(inputValue);
                        case Types.VARCHAR -> value = inputValue;
                        default -> {
                            JOptionPane.showMessageDialog(this, "Неизвестный тип данных для столбца " + columnName, "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return; // Exit the method if an unknown data type is encountered
                        }
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

    private void specialUpdateRecord(String tableName) {
        try {
            String primaryKey1ColumnName = "id_doc";
            String primaryKey2ColumnName = null;
            if (tableName.equals(SPECIAL_SALE_TABLE)){
                primaryKey2ColumnName = "id_sale";
            } else {
                primaryKey2ColumnName = "id_service";
            }

            String query = "SELECT * FROM " + tableName + " WHERE " + primaryKey1ColumnName + " = ? AND " + primaryKey2ColumnName + " = ?";
            JComponent[] inputs = new JComponent[] {
                    new JLabel("Введите id_doc:"),
                    new JTextField(),
                    new JLabel("Введите id_service (id_sale):"),
                    new JTextField()
            };
            int result = JOptionPane.showConfirmDialog(null, inputs, "Введите значения первичных ключей", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                long primaryKey1Value = Long.parseLong(((JTextField)inputs[1]).getText());
                long primaryKey2Value = Long.parseLong(((JTextField)inputs[3]).getText());
                JComponent[] updateInputs = new JComponent[] {
                        new JLabel("Введите новое значение id_doc:"),
                        new JTextField(),
                        new JLabel("Введите новое значение id_service (id_sale):"),
                        new JTextField()
                };
                int updateResult = JOptionPane.showConfirmDialog(null, updateInputs, "Новые значения", JOptionPane.OK_CANCEL_OPTION);
                if (updateResult == JOptionPane.OK_OPTION) {
                    long newPrimaryKey1Value = Long.parseLong(((JTextField)updateInputs[1]).getText());
                    long newPrimaryKey2Value = Long.parseLong(((JTextField)updateInputs[3]).getText());
                    DBConnection dbConnection = new DBConnection(url, user, password);
                    Connection connection = dbConnection.getConnection();
                    PreparedStatement selectStatement = connection.prepareStatement(query);
                    selectStatement.setLong(1, primaryKey1Value);
                    selectStatement.setLong(2, primaryKey2Value);
                    ResultSet resultSet = selectStatement.executeQuery();
                    if (resultSet.next()) {
                        String updateQuery = "UPDATE " + tableName + " SET " + primaryKey1ColumnName + " = ?, " + primaryKey2ColumnName + " = ? WHERE " + primaryKey1ColumnName + " = ? AND " + primaryKey2ColumnName + " = ?";
                        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                        updateStatement.setLong(1, newPrimaryKey1Value);
                        updateStatement.setLong(2, newPrimaryKey2Value);
                        updateStatement.setLong(3, primaryKey1Value);
                        updateStatement.setLong(4, primaryKey2Value);
                        updateStatement.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Данные успешно обновлены в таблице " + tableName);
                    } else {
                        JOptionPane.showMessageDialog(null, "Запись с такими значениями первичных ключей не найдена в таблице " + tableName);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка при обновлении данных: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void deleteRecord(String tableName) {
        try {
            DBConnection dbConnection = new DBConnection(url, user, password);
            Connection connection = dbConnection.getConnection();
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName + " LIMIT 0");

            ResultSetMetaData metaData = resultSet.getMetaData();
            metaData.getColumnCount();

            String inputValue = "";
            String deleteQuery = "";

            String columnName = metaData.getColumnName(1); // Получаем название первого столбца
            if (tableName.equals("allservicesindoc") || tableName.equals("allsaleindoc")) {
                String secondColumnName = metaData.getColumnName(2);

                int firstInputValue = Integer.parseInt(JOptionPane.showInputDialog("Введите значение для столбца " + columnName));
                int secondInputValue = Integer.parseInt(JOptionPane.showInputDialog("Введите значение для столбца " + secondColumnName));

                deleteQuery = "DELETE FROM " + tableName + " WHERE " + columnName + " = ? and " + secondColumnName + " = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, firstInputValue);
                preparedStatement.setInt(2, secondInputValue);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Запись успешно удалена из таблицы " + tableName);
                } else {
                    JOptionPane.showMessageDialog(this, "Не удалось найти запись для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                inputValue = JOptionPane.showInputDialog("Введите значение для столбца " + columnName);

                deleteQuery = "DELETE FROM " + tableName + " WHERE " + columnName + " = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
                preparedStatement.setLong(1, Long.parseLong(inputValue));

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Запись успешно удалена из таблицы " + tableName);
                } else {
                    JOptionPane.showMessageDialog(this, "Не удалось найти запись для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при удалении записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getQueryForTabIndex(int tabIndex) {
        String query = switch (tabIndex) {
            case 0 -> "SELECT * FROM customer";
            case 1 -> "SELECT * FROM documents";
            case 2 -> "SELECT * FROM allservicesindoc";
            case 3 -> "SELECT * FROM service";
            case 4 -> "SELECT * FROM allsaleindoc";
            case 5 -> "SELECT * FROM sale";
            default -> throw new IllegalArgumentException("Invalid tab index: " + tabIndex);
        };
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
