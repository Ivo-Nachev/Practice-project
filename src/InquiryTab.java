import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InquiryTab extends JPanel{
    Connection conn = DBConnection.getConnection();
    PreparedStatement state = null;
    ResultSet result = null;

    JPanel upIPanel = new JPanel();
    JPanel midIPanel = new JPanel();
    JPanel downIPanel = new JPanel();

    JLabel brandL = new JLabel("Марка:");
    JLabel firstnameL = new JLabel("Име:");
    JLabel familyNameL = new JLabel("Фамилия:");
    JTextField brandTFITab = new JTextField();
    JTextField firstnameTF = new JTextField();
    JTextField familyNameTF = new JTextField();

    JButton searchBTN = new JButton("Търсене");
    JButton sumTableBTN = new JButton("Обнови");

    JTable table = new JTable();
    JScrollPane inquiry_scrollPane = new JScrollPane(table);

    public InquiryTab(){
        this.setLayout(new GridLayout(3, 1));

        upIPanel.setLayout(new GridLayout(3,2));
        upIPanel.add(firstnameL);
        upIPanel.add(firstnameTF);
        upIPanel.add(familyNameL);
        upIPanel.add(familyNameTF);
        upIPanel.add(brandL);
        upIPanel.add(brandTFITab);
        this.add(upIPanel);

        midIPanel.setLayout(new GridLayout(1,1));
        downIPanel.add(searchBTN);
        downIPanel.add(sumTableBTN);
        this.add(downIPanel);

        searchBTN.addActionListener(new SearchActionBrandAndPeople());
        sumTableBTN.addActionListener(new RefreshActionInquiryTable());

        inquiry_scrollPane.setPreferredSize(new Dimension(300, 150));
        midIPanel.setLayout(new GridLayout(1,1));
        midIPanel.add(inquiry_scrollPane);
        this.add(midIPanel);

        refreshInquiryTable();
        this.setVisible(true);
    }

//    public void linkOwnersWithCars() {
//
//
//
//        try {
//            String insertSql = "SELECT \n" +
//                    "    person_name AS собственик,\n" +
//                    "    car_brand_model AS кола\n" +
//                    "FROM reports r\n" +
//                    "JOIN owners o ON r.person_id = o.person_id\n" +
//                    "JOIN cars c ON r.car_id = c.car_id;";
//            state = conn.prepareStatement(insertSql);
//            state.execute();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }

    public void refreshInquiryTable() {
            try {
                String sql = "SELECT \n" +
                        "    person_name AS собственик,\n" +
                        "    car_brand_model AS кола\n" +
                        "FROM reports";

                if (table.getRowCount() == 0) {
                    System.out.println("Няма намерени данни в таблица reports!");
                } else {
                    System.out.println("Заредени " + table.getRowCount() + " записа");
                }

                state = conn.prepareStatement(sql);
                result = state.executeQuery();
                table.setModel(new MyModel(result));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public void clearForm() {
        firstnameTF.setText("");
        familyNameTF.setText("");
        brandTFITab.setText("");
    }


    class SearchActionBrandAndPeople implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = "SELECT person_name, car_brand_model from reports where person_name=? or car_brand_model=?";

            try {
                state = conn.prepareStatement(sql);

                state.setString(1, firstnameTF.getText() + familyNameTF.getText());
                state.setString(2, brandTFITab.getText());

                result = state.executeQuery();
                table.setModel(new MyModel(result));

                refreshInquiryTable();
                clearForm();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshActionInquiryTable implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            refreshInquiryTable();
            clearForm();

        }

    }
    }


