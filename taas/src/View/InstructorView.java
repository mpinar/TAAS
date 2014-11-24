package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Helper.DatabaseHelper;
import Model.Course;
import Model.Instructor;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InstructorView extends JFrame {

	private JPanel contentPane;
	private Instructor instructor;
	private DatabaseHelper dbh;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public InstructorView(Instructor inst) {
		
		dbh = new DatabaseHelper();
		instructor = inst;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 445);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIdentification = new JLabel("New label");
		lblIdentification.setBounds(344, 6, 248, 16);
		lblIdentification.setHorizontalAlignment(SwingConstants.CENTER);
		lblIdentification.setText(instructor.name + " " + instructor.surname );
		contentPane.add(lblIdentification);
		
		
		JLabel lblCourses = new JLabel("Courses");
		lblCourses.setBounds(28, 53, 72, 16);
		contentPane.add(lblCourses);
		
		// get teaching information of Instructor.
		// and set them to the combobox
		ArrayList<Course> teachingList = instructor.getTeachingInformationFromDB();
		int size = teachingList.size()+1;
		String[] arr = new String[size];
		for(int i=0; i<size; i++){
			String s ="";
			if(i!=0){
				Course c = teachingList.get(i-1);
				s = c.code + " " + c.number;
			}
				arr[i] = s;
		}
		
		
		JComboBox cbTeaching = new JComboBox(arr);
		cbTeaching.setBounds(112, 49, 178, 27);
		contentPane.add(cbTeaching);
		
		JLabel lblAdditionalRequest = new JLabel("Additional Request :");
		lblAdditionalRequest.setBounds(28, 377, 460, 16);
		contentPane.add(lblAdditionalRequest);
		
		textField = new JTextField();
		textField.setBounds(172, 371, 316, 28);
		contentPane.add(textField);
		textField.setColumns(10);
	
		//createRequestPanel(); // selected dersin max asst sayisi
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(533, 372, 117, 29);
		btnSave.setToolTipText("Saves addtional request.");
		contentPane.add(btnSave);
	}
	
	private void createRequestPanel(int max){
		
		JPanel panel = new JPanel();
		panel.setBounds(39, 105, 611, 235);
		contentPane.add(panel);
		panel.setLayout(null);
		
		
		for (int i=0; i<max; i++){
			int y = 50 + 35*i;

			JComboBox departmentBox = new JComboBox();
			departmentBox.setBounds(6, y, 188, 25);
			panel.add(departmentBox);
			
			JComboBox assistantBox = new JComboBox();
			assistantBox.setBounds(226, y, 238, 255);
			panel.add(assistantBox);
			
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnAdd.setBounds(476, y, 117, 30);
			panel.add(btnAdd);
		}
	}
}
