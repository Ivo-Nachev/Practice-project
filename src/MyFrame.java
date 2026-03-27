import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MyFrame extends JFrame {
    Connection conn = DBConnection.getConnection();
    PreparedStatement state = null;
    ResultSet result = null;
    int id = 0;

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
        this.add(midPanel);

        addBTN.addActionListener(new AddAction());
        deleteBTN.addActionListener(new DeleteAction());

        scrollPane.setPreferredSize(new Dimension(1100, 500));
        downPanel.add(scrollPane);
        this.add(downPanel);

        refreshTable();

        this.setVisible(true);

    }

    public void refreshTable () {

        try {
            state = conn.prepareStatement("select * from cars");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshCarCombo () {
        carCombo.removeAllItems();
        String sql = "select id, brand,model,country,year_production,horse_power,max_speed,zero_to_hundred_seconds,transmission,eco_category";
        String item;

        try{
            state = conn.prepareStatement(sql);
            result = state.executeQuery();
            while (result.next()) {
                item = result.getObject(1).toString() + ". " + result.getObject(2).toString() + " " + result.getObject(3).toString();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        }
    }

    class DeleteAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = "delete from cars where id=?";

            try {
               state=conn.prepareStatement(sql);
               state.setInt(1, id);
               state.execute();

               refreshTable();


           } catch (SQLException e1) {

               e1.printStackTrace();
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