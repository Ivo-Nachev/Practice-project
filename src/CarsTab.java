import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarsTab extends JPanel {
    Connection conn = DBConnection.getConnection();
    PreparedStatement state = null;
    ResultSet result = null;
    int car_id = -1;
    int owner_car_id = -1;

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
    JTextField ecoCategoryTF = new JTextField();

    String[] typesOfTransmissions = {"Ръчна", "Автоматична"};
    JComboBox<String> transmissionCombo = new JComboBox<>(typesOfTransmissions);
    JComboBox<String> carCombo = new JComboBox<>();

    JButton addBTN = new JButton("Добавяне");
    JButton deleteBTN = new JButton("Изтриване");
    JButton editBTN = new JButton("Редактиране");
    JButton searchByYear = new JButton("Търсене по година");
    JTextField year_search = new JTextField();

    JButton carRefresh=new JButton("Обнови");

    JTable table = new JTable();
    JScrollPane scrollPane = new JScrollPane(table);

    public CarsTab () {
        this.setSize(700, 500);
        this.setLayout(new GridLayout(3, 1));

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

        midPanel.setLayout(new GridLayout(4,2));
        midPanel.add(addBTN);
        midPanel.add(deleteBTN);
        midPanel.add(editBTN);
        midPanel.add(searchByYear);
        midPanel.add(carRefresh);
        midPanel.add(year_search);
        this.add(midPanel);

        addBTN.addActionListener(new AddAction());
        table.addMouseListener(new MouseAction());
        deleteBTN.addActionListener(new DeleteAction());
        editBTN.addActionListener(new EditAction());
        searchByYear.addActionListener(new SearchActionCar());
        carRefresh.addActionListener(new RefreshActionCar());

        scrollPane.setPreferredSize(new Dimension(600, 300));
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
        String item;
        try{
            state = conn.prepareStatement(sql);
            result = state.executeQuery();

            while (result.next()) {
                item = result.getObject(1).toString() + ". " + result.getObject(2).toString() + " " + result.getObject(3).toString();
                carCombo.addItem(item);
            }
           // String car_id = item.split("\\.")[0];
//
           // if (car_id.isEmpty()) {
           //     id = 0;
           // }
//
           // else id = Integer.parseInt(car_id);
//
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
        car_id = -1;
    }

    public int getCarId (String brand, String model, String country, int year,
                         int horsePower, float maxSpeed, float zeroToHundredSeconds,
                         String transmission, String ecoCategory) {

        try {
            String sql = "SELECT car_id FROM cars WHERE brand = ? AND model = ? " +
                    "AND country = ? AND YEAR_PRODUCTION = ? AND HORSE_POWER = ? " +
                    "AND MAX_SPEED = ? AND ZERO_TO_HUNDRED_SECONDS = ? " +
                    "AND TRANSMISSION = ? AND ECO_CATEGORY = ?";
            state = conn.prepareStatement(sql);

            state.setString(1, brand);
            state.setString(2, model);
            state.setString(3, country);
            state.setInt(4, year);
            state.setInt(5, horsePower);
            state.setFloat(6, maxSpeed);
            state.setFloat(7, zeroToHundredSeconds);
            state.setString(8, transmission);
            state.setString(9, ecoCategory);

            result = state.executeQuery();
            if (result.next()) {
                car_id = result.getInt("car_id");
                System.out.println("Намерено ID: " + car_id);
            } else {
                System.out.println("Няма кола с тези параметри");
            }


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return car_id;
    }

    class AddAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            try{
                String sql = "insert into cars (brand,model,country,year_production,horse_power,max_speed,zero_to_hundred_seconds,transmission,eco_category) values(?,?,?,?,?,?,?,?,?)";
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
            owner_car_id = getCarId(brandTF.getText(), modelTF.getText(), countryTF.getText(),
                    Integer.parseInt(yearTF.getText()), Integer.parseInt(horsePowerTF.getText()),
                    Float.parseFloat(maxSpeedTF.getText()), Float.parseFloat(zeroToHundredSecondsTF.getText()),
                    transmissionCombo.getSelectedItem().toString(), ecoCategoryTF.getText());

            try{
                String sqlInsertIntoReports = "insert into reports (person_name, car_brand_model, car_id, person_id) values(?,?,?,?)";

                String personName = NewFrame.personFirstName + " " + NewFrame.personFamilyName;
                String carName = brandTF.getText() + " " + modelTF.getText();

                state = conn.prepareStatement(sqlInsertIntoReports);
                state.setString(1, personName);
                state.setString(2, carName);
                state.setInt(3, owner_car_id);
                state.setInt(4, NewFrame.person_id_forAdd);
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
               state.setInt(1, car_id);
               state.execute();

               refreshTable();
               clearForm();
               refreshCarCombo();

           } catch (SQLException e1) {

               e1.printStackTrace();
           }
        }
    }

    class EditAction implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            try {
                String sql = "update cars set brand=?, model=?, country=?, YEAR_PRODUCTION=?, HORSE_POWER=?, MAX_SPEED=?, ZERO_TO_HUNDRED_SECONDS=?, TRANSMISSION=?, ECO_CATEGORY=? where car_id=?";
                state = conn.prepareStatement(sql);
                state.setInt(10, car_id);
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

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

//            try {
//                String updateReportsSQL = "update reports set person_name =?, car_brand_model=? where person_id=? and car_id = ?";
//                state = conn.prepareStatement(updateReportsSQL);
//                state.setString(1, NewFrame.personFirstName + NewFrame.personFamilyName);
//                state.setString(2, brandTF.getText() + modelTF.getText());
//
//                state.execute();
//            } catch (SQLException ex) {
//                throw new RuntimeException(ex);
//            }

            refreshTable();
            clearForm();
            refreshCarCombo();
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



            String brand = brandTF.getText();
            String model = modelTF.getText();
            String country = countryTF.getText();
            int year = Integer.parseInt(yearTF.getText());
            int horsePower = Integer.parseInt(horsePowerTF.getText());
            float maxSpeed = Float.parseFloat(maxSpeedTF.getText());
            float zeroToHundred = Float.parseFloat(zeroToHundredSecondsTF.getText());
            String transmission = (String) transmissionCombo.getSelectedItem();
            String ecoCategory = ecoCategoryTF.getText();

            car_id = getCarId(brand, model, country, year, horsePower,
                    maxSpeed, zeroToHundred, transmission, ecoCategory);

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
            String sql = "select brand,model,country,year_production,horse_power,max_speed,zero_to_hundred_seconds,transmission,eco_category from cars where year_production=?";

            try {
                state = conn.prepareStatement(sql);

                state.setInt(1, Integer.parseInt(year_search.getText()));
                result = state.executeQuery();
                table.setModel(new MyModel(result));

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshActionCar implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            refreshTable();
            clearForm();

        }

    }
}



