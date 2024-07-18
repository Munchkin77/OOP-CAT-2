import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

// Model class for Student
class Student {
    private String name;
    private String mobile;
    private String gender;
    private String dob;
    private String address;

    public Student(String name, String mobile, String gender, String dob, String address) {
        this.name = name;
        this.mobile = mobile;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public String getAddress() {
        return address;
    }
}

// Main class for the Student Registration Form
public class StudentRegistrationForm extends JFrame {
    private JTextField nameField;
    private JTextField mobileField;
    private JTextField dobField;
    private JTextField addressField;
    private JComboBox<String> genderComboBox;
    private DefaultTableModel tableModel;
    private ArrayList<Student> students;

    private Connection connection;

    public StudentRegistrationForm() {
        students = new ArrayList<>();

        // Initialize database connection
        initializeDBConnection();

        setTitle("Student Registration Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        JLabel mobileLabel = new JLabel("Mobile:");
        mobileField = new JTextField();
        JLabel genderLabel = new JLabel("Gender:");
        genderComboBox = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        JLabel dobLabel = new JLabel("DOB (YYYY-MM-DD):");
        dobField = new JTextField();
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField();

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(mobileLabel);
        panel.add(mobileField);
        panel.add(genderLabel);
        panel.add(genderComboBox);
        panel.add(dobLabel);
        panel.add(dobField);
        panel.add(addressLabel);
        panel.add(addressField);
        panel.add(registerButton);

        add(panel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Mobile");
        tableModel.addColumn("Gender");
        tableModel.addColumn("DOB");
        tableModel.addColumn("Address");

        JTable studentTable = new JTable(tableModel);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);
    }

    private void initializeDBConnection() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish a connection to the database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/usersdb", "root", "password");
            // Create the table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "mobile VARCHAR(15), " +
                    "gender VARCHAR(10), " +
                    "dob DATE, " +
                    "address VARCHAR(255))";
            Statement stmt = connection.createStatement();
            stmt.execute(createTableSQL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String mobile = mobileField.getText();
            String gender = (String) genderComboBox.getSelectedItem();
            String dob = dobField.getText();
            String address = addressField.getText();

            if (!name.isEmpty() && !mobile.isEmpty() && !gender.isEmpty() && !dob.isEmpty() && !address.isEmpty()) {
                Student student = new Student(name, mobile, gender, dob, address);
                students.add(student);

                tableModel.addRow(new Object[] { name, mobile, gender, dob, address });

                // Insert student details into the database
                try {
                    String insertSQL = "INSERT INTO students (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = connection.prepareStatement(insertSQL);
                    pstmt.setString(1, name);
                    pstmt.setString(2, mobile);
                    pstmt.setString(3, gender);
                    pstmt.setDate(4, Date.valueOf(dob));
                    pstmt.setString(5, address);
                    pstmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                nameField.setText("");
                mobileField.setText("");
                genderComboBox.setSelectedIndex(0);
                dobField.setText("");
                addressField.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentRegistrationForm().setVisible(true);
        });
    }
}
