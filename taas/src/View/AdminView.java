package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;

import Helper.DatabaseHelper;
import Helper.ExcelHelper;
import Helper.TimeStorage;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;

public class AdminView extends JFrame {

	private JPanel contentPane;
	private JTextField tfYear;
	private DatabaseHelper dbh;
	String fileName = null;
	private ExcelHelper eh;
	//private JTextField newPass;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AdminView frame = new AdminView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public AdminView() {
		dbh = new DatabaseHelper();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(6, 6, 631, 163);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		JLabel lblYear = new JLabel("Year :");
		lblYear.setBounds(39, 26, 61, 16);
		contentPane.add(lblYear);

		JLabel lblSemester = new JLabel("Semester :");
		lblSemester.setBounds(250, 25, 87, 16);
		contentPane.add(lblSemester);

		String[] semesters = {"FALL", "SPRING", "SUMMER"};
		final JComboBox cbSemester = new JComboBox(semesters);
		cbSemester.setToolTipText("Select the semester information for which the TA assignment will be done.");
		cbSemester.setBounds(323, 21, 134, 27);
		contentPane.add(cbSemester);

		tfYear = new JTextField();
		tfYear.setToolTipText("Enter the year for the term that TA assignment will be done.");
		tfYear.setBounds(86, 20, 134, 28);
		contentPane.add(tfYear);
		tfYear.setColumns(10);



		JButton btnSaveButton = new JButton("Save");
		btnSaveButton.setToolTipText("Configure the database to work on these terms");
		btnSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

//				int year = Integer.parseInt(tfYear.getText());
//				String semester = (String) cbSemester.getSelectedItem(); 
//				dbh.setYearAndSemester(year, semester);
				TimeStorage.year = Integer.parseInt(tfYear.getText());
				TimeStorage.semester =  (String) cbSemester.getSelectedItem();

			}
		});
		btnSaveButton.setBounds(487, 21, 110, 29);
		contentPane.add(btnSaveButton);

		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				int returnValue = fileChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					fileName = selectedFile.getName();
					System.out.println(fileName);
					JOptionPane.showMessageDialog(getParent(),"You selected the file named : "+fileName);
					eh = new ExcelHelper(fileName);
					readExcel();
				}
			}

		});
		btnSelectFile.setBounds(250, 68, 117, 29);
		contentPane.add(btnSelectFile);


	}
	
	private void readExcel(){
		eh.getInstructorsFromWorksheet();
		eh.getCoursesFromWorksheet();
		eh.getAssistantsFromWorksheet();
		
		
	}
}
