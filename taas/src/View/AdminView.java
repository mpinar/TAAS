package View;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
import Model.Assistant;
import Model.Course;
import Munkres.Cost;
import Munkres.Hungarian;

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
	private Cost cost;

	private ArrayList<Assistant> allAsst;
	private ArrayList<Course> allCourse;
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

				int year =  Integer.parseInt(tfYear.getText());
				String semester = (String) cbSemester.getSelectedItem();

				String cd = "You have entered the following time information.\nYear as "+year+ "\nSemester as "+ semester +"\nDo you confirm that ?";
				int option = JOptionPane.showConfirmDialog(getParent(), cd);

				if(option == JOptionPane.YES_OPTION){
					TimeStorage.year = year;
					TimeStorage.semester = semester;
				}

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
					int option = JOptionPane.showConfirmDialog(getParent(),"You selected the file named : "+fileName);
					if(option == JOptionPane.YES_OPTION){
						eh = new ExcelHelper(fileName);
						readExcel();
						JOptionPane.showMessageDialog(getParent(), "Operation Excel reading is complete.");
					}
				}
			}

		});
		btnSelectFile.setBounds(250, 68, 117, 29);
		contentPane.add(btnSelectFile);

		JButton btnCalculateCost = new JButton("Calculate Cost");
		btnCalculateCost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				allAsst = dbh.getAllAssistants();
				allCourse = dbh.getAllCourses();
				ArrayList<Course> coursesxmaxasst = new ArrayList<Course>();
				for (int i = 0; i<allCourse.size(); i++){
					Course c = allCourse.get(i);

					if(c.maxAssistantNumber >1){
						for(int k =0; k<c.maxAssistantNumber; k++){
							coursesxmaxasst.add(k,c);
						}
					}
				}
				
				
				 // Sorts the new list in department order then the course number order.
				Collections.sort(coursesxmaxasst, new Comparator<Course>() {
			        @Override
			        public int compare(Course c1, Course  c2)
			        {
			        	
				        return c1.code.compareTo(c2.code);
			        }
			    });
				
				Collections.sort(coursesxmaxasst, new Comparator<Course>() {
					
					public int compare(Course c1, Course c2){
						
						return c1.number - c2.number;
					}
				});
				
				
				cost = new Cost(allAsst, coursesxmaxasst);
				double[][] cm = cost.calculateCost();
				Hungarian hung = new Hungarian(cm);
				eh = new ExcelHelper();
				eh.createOutputExcel(coursesxmaxasst, allAsst);
			}
		});
		btnCalculateCost.setBounds(401, 68, 117, 29);
		contentPane.add(btnCalculateCost);

		JButton btnMunkres = new JButton("MUNKRES");
		btnMunkres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnMunkres.setBounds(323, 106, 117, 29);
		contentPane.add(btnMunkres);


	}

	private void readExcel(){
		eh.getCoursesFromWorksheet();
		eh.getInstructorsFromWorksheet();
		eh.getAssistantsFromWorksheet();


	}
}
