import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    JButton sumTableBTN = new JButton("Обобщи данни");

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


        public void refreshInquiryTable() {
            try {
                String sql = "SELECT concat(owners.FIRST_NAME, ' ', owners.SECOND_NAME) as Собственик, concat(cars.BRAND, ' ', cars.model) as Кола\n" +
                        "FROM owners\n" +
                        "CROSS JOIN cars";
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
            String sql = "SELECT concat(owners.FIRST_NAME, ' ', owners.SECOND_NAME) as Собственик, concat(cars.BRAND, ' ', cars.model) as Кола\n from owners\n cross join cars where brand=? or first_name=? and second_name=? ";

            try {
                state = conn.prepareStatement(sql);

                state.setString(1, brandTFITab.getText());
                state.setString(2, firstnameTF.getText());
                state.setString(3, familyNameTF.getText());
                result = state.executeQuery();
                table.setModel(new MyModel(result));

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


