package version2.prototype.EastWebUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JList;

public class MainWindow {

    private JFrame frame;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);


        CreateButton();




    }

    private void CreateButton() {
        JButton btnNewButton = new JButton("Add New Plugin");
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainWindow window = new MainWindow();
                window.frame.setVisible(true);
                String name = JOptionPane.showInputDialog(frame,
                        "What is your name?", null);
                JOptionPane.showMessageDialog(frame,
                        "Eggs are not supposed to be green.");
            }
        });
        btnNewButton.setBounds(10, 11, 113, 23);
        frame.getContentPane().add(btnNewButton);

        JList list = new JList();
        list.setBounds(315, 33, 85, 94);
        frame.getContentPane().add(list);

        JComboBox<String> comboBox = new JComboBox<String>();

        comboBox.addItem("abc123");
        comboBox.addItem("abc1234");
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {

            }
        });
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {

            }
        });

        comboBox.setBounds(10, 77, 200, 50);
        frame.getContentPane().add(comboBox);


    }
}
