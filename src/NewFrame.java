import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewFrame extends JFrame {

    Connection conn = DBConnection.getConnection();
    PreparedStatement state = null;
    ResultSet result = null;
    int person_id_forEditAndDelete = -1;
    static int person_id_forAdd = -1;
    boolean actionCheck = true;

    JTabbedPane tab=new JTabbedPane();

    JPanel personPanel=new JPanel();
    JPanel person_upPanel = new JPanel();
    JPanel person_midPanel = new JPanel();
    JPanel person_downPanel = new JPanel();

    JLabel firstNameL = new JLabel("Име:");
    JLabel familyNameL = new JLabel("Фамилия:");
    JTextField firstNameTF = new JTextField();
    JTextField familyNameTF = new JTextField();

    static String personFirstName;
    static String personFamilyName;


    JComboBox<String> ownerCombo = new JComboBox<>();

    JButton person_addBTN = new JButton("Добавяне");
    JButton person_deleteBTN = new JButton("Изтриване");
    JButton person_editBTN = new JButton("Редактиране");
    JButton searchByName = new JButton("Търсене по име");
    JButton personRefresh=new JButton("Обнови");

    JTextField name_search = new JTextField();

    JTable table = new JTable();
    JScrollPane person_scrollPane = new JScrollPane(table);

    public NewFrame() {
        this.setSize(700, 400);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        personPanel.setLayout(new GridLayout(3, 1));

        person_upPanel.setLayout(new GridLayout(2,2));
        person_upPanel.add(firstNameL);
        person_upPanel.add(firstNameTF);
        person_upPanel.add(familyNameL);
        person_upPanel.add(familyNameTF);
        personPanel.add(person_upPanel);

        person_midPanel.setLayout(new GridLayout(4,2));
        person_midPanel.add(person_addBTN);
        person_midPanel.add(person_deleteBTN);
        person_midPanel.add(person_editBTN);
        person_midPanel.add(searchByName);
        person_midPanel.add(personRefresh);
        person_midPanel.add(name_search);

        personPanel.add(person_midPanel);

        person_addBTN.addActionListener(new AddActionPerson());
        table.addMouseListener(new MouseActionPerson());
        person_deleteBTN.addActionListener(new DeleteActionPerson());
        person_editBTN.addActionListener(new EditActionPerson());
        searchByName.addActionListener(new SearchActionPerson());
        personRefresh.addActionListener(new RefreshActionPerson());

        person_scrollPane.setPreferredSize(new Dimension(300, 150));
        person_downPanel.add(person_scrollPane);
        personPanel.add(person_downPanel);
        tab.add(personPanel,"Собственици");

        CarsTab carPanel = new CarsTab();
        InquiryTab inquiryPanel = new InquiryTab();
        tab.add(carPanel,"Коли");
        tab.add(inquiryPanel,"Справка по име и марка");

        refreshPersonTable();
        refreshOwnerCombo();
        this.add(tab);
        this.setVisible(true);
    }

    public void refreshPersonTable() {

        try {
            state = conn.prepareStatement("select first_name, second_name from owners");
            result = state.executeQuery();
            table.setModel(new MyModel(result));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshOwnerCombo() {
        ownerCombo.removeAllItems();
        String sql = "select person_id, first_name, second_name from owners";
        String item;
        try {
            state = conn.prepareStatement(sql);
            result = state.executeQuery();

            while (result.next()) {
                item = result.getObject(1).toString() + ". " + result.getObject(2).toString() + " " + result.getObject(3).toString();
                ownerCombo.addItem(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void clearForm() {
        firstNameTF.setText("");
        familyNameTF.setText("");
        person_id_forEditAndDelete = -1;
    }

    public int getPersonId (String first_name, String family_name) {

        try {
            String sql = "SELECT person_id FROM owners WHERE first_name = ? AND second_name = ?";
            state = conn.prepareStatement(sql);

            state.setString(1, first_name);
            state.setString(2, family_name);

            result = state.executeQuery();
            if (result.next()) {

                if (!actionCheck) {
                    person_id_forAdd = result.getInt("person_id");
                    System.out.println("Намерено ID: " + person_id_forAdd);
                } else {
                    person_id_forEditAndDelete = result.getInt("person_id");
                    System.out.println("Намерено ID: " + person_id_forEditAndDelete);
                }

            } else {
                System.out.println("Няма кола с тези параметри");
            }


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return person_id_forEditAndDelete;
    }

    class AddActionPerson implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            actionCheck = false;

            String sql = "insert into owners (first_name, second_name) values(?,?)\n";
            try{

                personFirstName = firstNameTF.getText();
                personFamilyName = familyNameTF.getText();

                state = conn.prepareStatement(sql);
                state.setString(1, personFirstName);
                state.setString(2, personFamilyName);

                state.execute();
            } catch (SQLException | NullPointerException ex) {
                ex.printStackTrace();
            }
            getPersonId(firstNameTF.getText(), familyNameTF.getText());
            refreshPersonTable();
            clearForm();
            refreshOwnerCombo();
        }
    }

    class DeleteActionPerson implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            actionCheck = true;
            String sql = "delete from owners where person_id=?";

            try {
                state=conn.prepareStatement(sql);
                state.setInt(1, person_id_forEditAndDelete);
                state.execute();

                refreshPersonTable();
                clearForm();
                refreshOwnerCombo();

            } catch (SQLException e1) {

                e1.printStackTrace();
            }
        }
    }

    class EditActionPerson implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            actionCheck = true;
            try {
                String sql = "update owners set first_name=?, second_name=? where person_id=?";
                state = conn.prepareStatement(sql);
                state.setString(1, firstNameTF.getText());
                state.setString(2, familyNameTF.getText());
                state.setInt(3, person_id_forEditAndDelete);

                state.execute();

                refreshPersonTable();
                clearForm();
                refreshOwnerCombo();


            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class MouseActionPerson implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            int row = table.getSelectedRow();
            firstNameTF.setText(table.getValueAt(row, 0).toString());
            familyNameTF.setText(table.getValueAt(row, 1).toString());

            String first_name = firstNameTF.getText();
            String second_name = familyNameTF.getText();

            person_id_forEditAndDelete = getPersonId(first_name, second_name);

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

    class SearchActionPerson implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String sql = "select first_name, second_name from owners where first_name=?";

            try {
                state = conn.prepareStatement(sql);

                state.setString(1, name_search.getText());
                result = state.executeQuery();
                table.setModel(new MyModel(result));

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    class RefreshActionPerson implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {

            refreshPersonTable();
            clearForm();

        }

    }
}