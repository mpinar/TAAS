package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import Helper.DatabaseHelper;
import Model.Instructor;

public class SettingsView extends JFrame {

	private JPanel contentPane;
	private JTextField tfYear;
	private JTextField newPass;
	private DatabaseHelper dbh;
	private Instructor ins;


	/**
	 * Create the frame.
	 */
	
	public SettingsView(final int pID) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblYear = new JLabel("Year :");
		lblYear.setBounds(66, 49, 61, 16);
		contentPane.add(lblYear);
		
		JLabel lblSemester = new JLabel("Semester :");
		lblSemester.setBounds(66, 89, 61, 16);
		contentPane.add(lblSemester);
		String[] semesters = {"FALL","SPRING","SUMMER"};
		JComboBox comboBox = new JComboBox(semesters);
		comboBox.setBounds(171, 85, 128, 27);
		contentPane.add(comboBox);
		
		tfYear = new JTextField();
		tfYear.setBounds(171, 43, 128, 28);
		contentPane.add(tfYear);
		tfYear.setColumns(10);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(312, 132, 117, 29);
		contentPane.add(btnSave);
		
		JLabel lblSetNewPassword = new JLabel("Set New Password");
		lblSetNewPassword.setBounds(66, 162, 128, 16);
		contentPane.add(lblSetNewPassword);
		
		newPass = new JTextField();
		newPass.setBounds(76, 190, 185, 28);
		contentPane.add(newPass);
		newPass.setColumns(10);
		
		
		
		JButton btnProceed = new JButton("Proceed");
		btnProceed.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				String newPassword = newPass.getText();
				dbh.updatePassword(pID, newPassword);
			}
		});
		btnProceed.setBounds(312, 214, 117, 29);
		contentPane.add(btnProceed);
	}

}
