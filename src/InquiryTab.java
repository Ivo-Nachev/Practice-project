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

    //JPanel upIPanel = new JPanel();
    //JPanel midIPanel = new JPanel();
    //JPanel downIPanel = new JPanel();
//
    //JLabel brandL = new JLabel("Марка:");
    //JLabel nameL = new JLabel("Име:");
    //JTextField brandTF = new JTextField();
    //JTextField nameTF = new JTextField();
//
    //JButton searchBTN = new JButton("Търсене");
    //JButton cleanTableBTN =new JButton("Изчисти таблица");

    JTable table = new JTable();
    JScrollPane inquiry_scrollPane = new JScrollPane(table);

    public InquiryTab(){
        this.setLayout(new GridLayout(3, 1));

        //upIPanel.setLayout(new GridLayout(4,2));
        //upIPanel.add(brandL);
        //upIPanel.add(brandTF);
        //upIPanel.add(nameL);
        //upIPanel.add(nameTF);
        //this.add(upIPanel);

        //midIPanel.add(searchBTN);
        //this.add(midIPanel);

        //inquiry_scrollPane.setPreferredSize(new Dimension(300, 150));
        //downIPanel.add(inquiry_scrollPane);
        //this.add(downIPanel);

        inquiry_scrollPane.setPreferredSize(new Dimension(300, 150));
        this.add(inquiry_scrollPane);

        refreshInquiryTable();
        this.setVisible(true);
    }


        public void refreshInquiryTable() {
            try {
                String sql = "SELECT " +
                        "r.REPORT_ID, " +
                        "r.PERSON_ID, " +
                        "o.FIRST_NAME || ' ' || o.SECOND_NAME, " +
                        "r.CAR_ID, " +
                        "c.BRAND, " +
                        "r.REPORT_DATE " +
                        "FROM reports r " +
                        "JOIN owners o ON r.PERSON_ID = o.PERSON_ID " +
                        "JOIN cars c ON r.CAR_ID = c.CAR_ID " +
                        "ORDER BY r.REPORT_DATE DESC";
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
    }


