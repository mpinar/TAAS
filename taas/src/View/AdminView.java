package View;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

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

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.SwingConstants;

public class AdminView extends JFrame {

	private JPanel contentPane;
	private JTextField tfYear;
	private DatabaseHelper dbh;
	String fileName = null;
	private ExcelHelper eh;
	private Cost cost;

	private ArrayList<Assistant> allAsst;
	private ArrayList<Course> allCourse;
	ArrayList<Course> coursesxmaxasst;
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
		setBounds(6, 6, 424, 209);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);


		JLabel lblYear = new JLabel("Year :");
		lblYear.setHorizontalAlignment(SwingConstants.CENTER);
		lblYear.setBounds(193, 52, 61, 16);
		contentPane.add(lblYear);

		JLabel lblSemester = new JLabel("Semester :");
		lblSemester.setHorizontalAlignment(SwingConstants.CENTER);
		lblSemester.setBounds(167, 24, 87, 16);
		contentPane.add(lblSemester);

		String[] semesters = {"FALL", "SPRING", "SUMMER"};
		final JComboBox cbSemester = new JComboBox(semesters);
		cbSemester.setToolTipText("Select the semester information for which the TA assignment will be done.");
		cbSemester.setBounds(264, 21, 136, 27);
		contentPane.add(cbSemester);

		tfYear = new JTextField();
		tfYear.setToolTipText("Enter the year for the term that TA assignment will be done.");
		tfYear.setBounds(266, 46, 134, 28);
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
		btnSaveButton.setBounds(288, 86, 110, 29);
		contentPane.add(btnSaveButton);

		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setBackground(new Color(0, 0, 153));
		btnSelectFile.setForeground(Color.BLACK);
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
						long starttime = System.currentTimeMillis();
						eh = new ExcelHelper(fileName);
						readExcel();
						long endtime = System.currentTimeMillis();
						long time  = endtime - starttime;
						System.out.println(time);
						JOptionPane.showMessageDialog(getParent(), "Operation Excel reading is complete.");
						
					}
				}
			}

		});
		btnSelectFile.setBounds(6, 19, 145, 29);
		contentPane.add(btnSelectFile);

		JButton btnCalculateCost = new JButton("Calculate Cost");
		btnCalculateCost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				long starttime = System.currentTimeMillis();
				getInformation();
				
				cost = new Cost(allAsst, allCourse);
				double[][] cm = cost.calculateCost();
				long endtime = System.currentTimeMillis();
				long time  = endtime - starttime;
				System.out.println(time);
			
			}
		});
		btnCalculateCost.setBounds(6, 61, 145, 29);
		contentPane.add(btnCalculateCost);

		JButton btnMunkres = new JButton("Assign Assistants");
		btnMunkres.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				long starttime = System.currentTimeMillis();
				getInformation();
				double[][] cm = new double[allAsst.size()][coursesxmaxasst.size()];
				HashMap<Course,Integer> hm ;
				for (int i=0; i<allAsst.size(); i++) {
					Assistant assistant = allAsst.get(i);
					hm = assistant.getCostsForCourses(assistant.id);
					int courseCount = 0;
					
					for (Course course : coursesxmaxasst) {
						int cost = hm.get(course);
						
						cm[i][courseCount]= cost;
						courseCount++;
					}
					

					
				}
	
				long starttime1 = System.currentTimeMillis();
				Hungarian hung = new Hungarian();
				int[][] assignment = hung.runMunkres(cm);
				long endtime1 = System.currentTimeMillis();
				long time1  = endtime1 - starttime1;
				System.out.println("Munkres = " + time1);
				
//				for(int j=0; j<assignment.length; j++){
//					System.out.print("Assignment # "+j+" = ");
//					for(int k =1; k<assignment[j].length; k++){
//						System.out.print(assignment[j][k-1]+" ------> "+assignment[j][k]+"\n");	
//					}
//				}
				eh = new ExcelHelper();
				eh.createOutputExcel(coursesxmaxasst, allAsst,assignment);
				long endtime = System.currentTimeMillis();
				long time  = endtime - starttime;
				System.out.println("Total Munkres : " +time);
			}
		});
		btnMunkres.setBounds(6, 143, 145, 29);
		contentPane.add(btnMunkres);
		
		JButton btnClearDB = new JButton("Clear Database");
		btnClearDB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				boolean b = dbh.clearDatabase();
				if(b){
					String cd = "Database has been cleared.";
					JOptionPane.showMessageDialog(getParent(), cd, "Success", JOptionPane.INFORMATION_MESSAGE);
				}else{
					String cd = "An error occured during the database clearing operation.";
					JOptionPane.showMessageDialog(getParent(), cd, "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		btnClearDB.setBounds(6, 102, 145, 29);
		contentPane.add(btnClearDB);


	}
	
	private void readExcel(){
		eh.getCoursesFromWorksheet();
		eh.getInstructorsFromWorksheet();
		eh.getAssistantsFromWorksheet();


	}

	private void getInformation() {
		allAsst = dbh.getAllAssistants();
		allCourse = dbh.getAllCourses();
		 coursesxmaxasst = new ArrayList<Course>();
		for (int i = 0; i<allCourse.size(); i++){
			Course c = allCourse.get(i);

			if(c.maxAssistantNumber >1){
				for(int k =0; k<c.maxAssistantNumber; k++){
					coursesxmaxasst.add(k,c);
				}
			}
		}
		
		Collections.sort(allCourse, new Comparator<Course>() {
	        @Override
	        public int compare(Course c1, Course  c2)
	        {
	        	
		        return c1.code.compareTo(c2.code);
	        }
	    });
		
		Collections.sort(allCourse, new Comparator<Course>() {
			
			public int compare(Course c1, Course c2){
				
				return c1.number - c2.number;
			}
		});
		
		
	}
}
