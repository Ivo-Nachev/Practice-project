import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyFrame extends JFrame {
    Connection conn = DBConnection.getConnection();
    PreparedStatement state = null;
    ResultSet result = null;
    int id = -1;

    JPanel upPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JPanel downPanel = new JPanel();

    JLabel brandL = new JLabel("Марка:");
    JLabel modelL = new JLabel("Модел:");
    JLabel modelProductionCountryL = new JLabel("Страна на производство на модела:");
    JLabel productionYearL = new JLabel("Година на производство:");
    JLabel horsePowerL = new JLabel("Конски сили:");
    JLabel topSpeedL = new JLabel("Макс. скорост:");
    JLabel zeroToHundredSecondsL = new JLabel("0-100 сек.:");
    JLabel transmissionL = new JLabel("Тип скоростна кутия:");
    JLabel ecoCategoryL = new JLabel("Екологична категория:");

    JTextField brandTF = new JTextField();
    JTextField modelTF = new JTextField();
    JTextField countryTF = new JTextField();
    JTextField yearTF = new JTextField();
    JTextField horsePowerTF = new JTextField();
    JTextField maxSpeedTF = new JTextField();
    JTextField zeroToHundredSecondsTF = new JTextField();
    JTextField ecoCategoryTF = new JTextField("Евро: ");

    String[] typesOfTransmissions = {"Ръчна", "Автоматична"};
    JComboBox<String> transmissionCombo = new JComboBox<String>(typesOfTransmissions);
    JComboBox<String> carCombo = new JComboBox<>();

    JButton addBTN = new JButton("Добавяне");
    JButton deleteBTN = new JButton("Изтриване");
    JButton editBTN = new JButton("Редактиране");
    JButton searchByYear = new JButton("Търсене по година");

    JTable table = new JTable();
    JScrollPane scrollPane = new JScrollPane(table);

    public MyFrame () {
        this.setSize(700, 500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 1));

        upPanel.addKeyListener(new MyKeyListener());
        upPanel.setLayout(new GridLayout(9, 2));
        upPanel.add(brandL);
        upPanel.add(brandTF);
        upPanel.add(modelL);
        upPanel.add(modelTF);
        upPanel.add(modelProductionCountryL);
        upPanel.add(countryTF);
        upPanel.add(productionYearL);
        upPanel.add(yearTF);
        upPanel.add(horsePowerL);
        upPanel.add(horsePowerTF);
        upPanel.add(topSpeedL);
        upPanel.add(maxSpeedTF);
        upPanel.add(zeroToHundredSecondsL);
        upPanel.add(zeroToHundredSecondsTF);
        upPanel.add(transmissionL);
        upPanel.add(transmissionCombo);
        upPanel.add(ecoCategoryL);
        upPanel.add(ecoCategoryTF);
        this.add(upPanel);



        midPanel.add(addBTN);
        midPanel.add(deleteBTN);
        midPanel.add(editBTN);
        midPanel.add(searchByYear);
        this.add(midPanel);

        addBTN.addActionListener(new AddAction());
        table.addMouseListener(new MouseAction());
        deleteBTN.addActionListener(new DeleteAction());
        searchByYear.addActionListener(new SearchActionCar());

        scrollPane.setPreferredSize(new Dimension(1100, 500));
        downPanel.add(scrollPane);
        this.add(downPanel);

        refreshTable();
        refreshCarCombo();
        this.setVisible(true);

    }

    public void refreshTable () {

        try {
            state = conn.prepareStatement("select brand,model,country,year_production,horse_power,max_speed,zero_to_hundred_seconds,transmission,eco_category from cars");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshCarCombo () {
        carCombo.removeAllItems();
        String sql = "select car_id, brand, model from cars";
        String item = "";
        try{
            state = conn.prepareStatement(sql);
            result = state.executeQuery();

            while (result.next()) {
                item = result.getObject(1).toString() + ". " + result.getObject(2).toString() + " " + result.getObject(3).toString();
                carCombo.addItem(item);
            }
            String car_id = item.split("\\.")[0];

            if (car_id.isEmpty()) {
                id = 0;
            }

            else id = Integer.parseInt(car_id);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearForm () {
        brandTF.setText("");
        modelTF.setText("");
        countryTF.setText("");
        yearTF.setText("");
        horsePowerTF.setText("");
        maxSpeedTF.setText("");
        zeroToHundredSecondsTF.setText("");
        ecoCategoryTF.setText("");
    }

    class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String sql = "insert into cars (brand,model,country,year_production,horse_power,max_speed,zero_to_hundred_seconds,transmission,eco_category) values(?,?,?,?,?,?,?,?,?)";

            try{
                state = conn.prepareStatement(sql);
                state.setString(1, brandTF.getText());
                state.setString(2, modelTF.getText());
                state.setString(3, countryTF.getText());
                state.setInt(4, Integer.parseInt(yearTF.getText()));
                state.setInt(5, Integer.parseInt(horsePowerTF.getText()));
                state.setFloat(6, Float.parseFloat(maxSpeedTF.getText()));
                state.setFloat(7, Float.parseFloat(zeroToHundredSecondsTF.getText()));
                state.setString(8, transmissionCombo.getSelectedItem().toString());
                state.setString(9, ecoCategoryTF.getText());

                state.execute();
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
            }

            refreshTable();
            clearForm();
            refreshCarCombo();
        }
    }

    class DeleteAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = "delete from cars where car_id=?";

            try {
               state=conn.prepareStatement(sql);
               state.setInt(1, id);
               state.execute();

               refreshTable();
               clearForm();
               refreshCarCombo();

           } catch (SQLException e1) {

               e1.printStackTrace();
           }
        }
    }

    class MouseAction implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            brandTF.setText(table.getValueAt(row, 0).toString());
            modelTF.setText(table.getValueAt(row, 1).toString());
            countryTF.setText(table.getValueAt(row, 2).toString());
            yearTF.setText(table.getValueAt(row, 3).toString());
            horsePowerTF.setText(table.getValueAt(row, 4).toString());
            maxSpeedTF.setText(table.getValueAt(row, 5).toString());
            zeroToHundredSecondsTF.setText(table.getValueAt(row, 6).toString());
            ecoCategoryTF.setText(table.getValueAt(row, 8).toString());

            if (table.getValueAt(row, 7).toString().equals("Ръчна")) transmissionCombo.setSelectedIndex(0);

            else transmissionCombo.setSelectedIndex(1);
            
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    class SearchActionCar implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = "select * from cars where year_production=?";

            try {
                state = conn.prepareStatement(sql);
                state.setInt(1, Integer.parseInt(yearTF.getText()));
                result = state.executeQuery();
                table.setModel(new MyModel(result));

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MyKeyListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {

        }

        @Override
        public void keyReleased(KeyEvent e) {

        }
    }

}