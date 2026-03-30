import javax.swing.*;
import java.awt.*;
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

    JLabel yearL = new JLabel("Година:");
    JLabel nameL = new JLabel("Име:");
    JTextField yearTF = new JTextField();
    JTextField nameTF = new JTextField();

    JButton searchBTN = new JButton("Търсене по име");
    JButton cleanTableBTN =new JButton("Изчисти таблица");

    JTable table = new JTable();
    JScrollPane inquiry_scrollPane = new JScrollPane(table);

    public InquiryTab(){
        this.setLayout(new GridLayout(3, 1));

        upIPanel.setLayout(new GridLayout(4,2));
        upIPanel.add(yearL);
        upIPanel.add(yearTF);
        upIPanel.add(nameL);
        upIPanel.add(nameTF);
        this.add(upIPanel);

        midIPanel.add(searchBTN);
        midIPanel.add(cleanTableBTN);
        this.add(midIPanel);

        inquiry_scrollPane.setPreferredSize(new Dimension(300, 150));
        downIPanel.add(inquiry_scrollPane);
        this.add(downIPanel);
        this.setVisible(true);
    }

    public void refreshInquiryTable() {

        try {
            state = conn.prepareStatement("select year, second_name from owners");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
