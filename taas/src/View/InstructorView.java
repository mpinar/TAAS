package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Helper.DatabaseHelper;
import Model.Assistant;
import Model.Course;
import Model.Department;
import Model.Instructor;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.Font;

public class InstructorView extends JFrame {

	private JPanel contentPane;
	private Instructor instructor;
	private Course selectedCourse;
	private DatabaseHelper dbh;
	private JTextField tfAdditionalRequest;
	private int messageCount = 0;
	//private JPanel panel;
	JLabel lblMaxAsst;
	JComboBox departmentBox;
	JComboBox assistantBox, cbTeaching;
	JButton btnRequest;
	private JLabel lblRequest;
	private ArrayList<Assistant> assistants;
	// private ArrayList<Assistant> string handle kolaysa buna gerek yok
	private int maxAsstForCourse=0;
	private Assistant selectedAssistant;
	private Department selectedDepartment;

	/**
	 * Create the frame.
	 */
	public InstructorView(Instructor inst) {

		dbh = new DatabaseHelper();
		instructor = inst;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 688, 298);
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


		cbTeaching = new JComboBox(arr);
		cbTeaching.setBounds(112, 49, 178, 27);
		cbTeaching.addItemListener(new ItemChangeListener());

		contentPane.add(cbTeaching);

		JLabel lblAdditionalRequest = new JLabel("Additional Request :");
		lblAdditionalRequest.setBounds(28, 200, 164, 16);
		contentPane.add(lblAdditionalRequest);

		tfAdditionalRequest = new JTextField();
		tfAdditionalRequest.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if(messageCount == 0){
					JOptionPane.showMessageDialog(getParent()
							, "The requests should be seperated with commas(,).\nExample: Java, INDR");
					messageCount++;
				}
			}
		});
		tfAdditionalRequest.setBounds(204, 194, 316, 28);
		contentPane.add(tfAdditionalRequest);
		tfAdditionalRequest.setColumns(10);

		//

		JButton btnSaveAdditionalRequest = new JButton("Save");
		btnSaveAdditionalRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Create course;
				String s = (String) cbTeaching.getSelectedItem();
				String[] sarr = s.split(" ");
				Course c = new Course(sarr[0], Integer.parseInt(sarr[1]));

				String additionalReq = tfAdditionalRequest.getText();
				boolean updated = instructor.setAdditionalRequestForCourse(c,additionalReq);

				if(updated){
					JOptionPane.showMessageDialog(getParent(), "Your request is saved","Success",JOptionPane.INFORMATION_MESSAGE);
				}else{
					JOptionPane.showMessageDialog(getParent(),
							"An error ocurred while saving your request. Try again.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnSaveAdditionalRequest.setBounds(562, 195, 117, 29);
		btnSaveAdditionalRequest.setToolTipText("Saves addtional request.");
		contentPane.add(btnSaveAdditionalRequest);


		lblMaxAsst = new JLabel();
		lblMaxAsst.setBounds(344, 53, 150, 17);
		contentPane.add(lblMaxAsst);


		departmentBox = new JComboBox();
		departmentBox.setBounds(11, 150, 175, 25);
		contentPane.add(departmentBox);
		departmentBox.setVisible(false);

		assistantBox = new JComboBox();
		assistantBox.setBounds(200, 150, 175, 25);
		contentPane.add(assistantBox);
		assistantBox.setVisible(false);

		lblRequest = new JLabel("REQUEST");
		lblRequest.setHorizontalAlignment(SwingConstants.CENTER);
		lblRequest.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		lblRequest.setBounds(39, 95, 123, 43);
		contentPane.add(lblRequest);

		btnRequest = new JButton("Request");
		btnRequest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				boolean req = instructor.makeAssistantRequest(selectedCourse,selectedAssistant);

				if(req){
					JOptionPane.showMessageDialog(getParent(), "Your request is saved","Success",JOptionPane.INFORMATION_MESSAGE);
					// a request has been made and update the max assistant count. this may be done with a trigger.
					//maxAsstForCourse--;
					selectedCourse.updateMaxAssistantCount();
					lblMaxAsst.setText("Max assistant : " + selectedCourse.maxAssistantNumber);
					contentPane.invalidate();
					contentPane.repaint();

				}else{
					JOptionPane.showMessageDialog(getParent(),
							"An error ocurred while saving your request. Try again.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnRequest.setBounds(409, 148, 117, 29);
		btnRequest.setVisible(false);
		contentPane.add(btnRequest);




	}



	class ItemChangeListener implements ItemListener{
		@Override
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				Object item = event.getItem();
				// do something with object
				String selected = item.toString();

				if(event.getSource() == cbTeaching ){

					if(!selected.isEmpty() && event.getStateChange() == ItemEvent.SELECTED){

						String[] arr = selected.split(" ");
						Course c = new Course(arr[0], Integer.parseInt(arr[1]));
						c.setID();
						selectedCourse = c;
						lblMaxAsst.setText("Max assistant : " + selectedCourse.maxAssistantNumber);

						if(selectedCourse.maxAssistantNumber != 0){
							ArrayList<Department> deps = dbh.getAllDepartments();
							String[] array = new String[deps.size()+1];
							for (int i=1; i<=deps.size(); i++) {
								Department d = deps.get(i-1);
								array[i] = d.code;
							}

							array[0] = "Select Department";
							DefaultComboBoxModel model = new DefaultComboBoxModel(array);
							departmentBox.setModel(model);
							departmentBox.addItemListener(new ItemChangeListener());
							departmentBox.setVisible(true);
							contentPane.invalidate();
							contentPane.repaint();
						}else{
							JLabel lblError = new JLabel("You have reached your maximum number of requests");
							lblError.setBounds(11, 150, 400, 25);
							contentPane.add(lblError);
							contentPane.invalidate();
							contentPane.repaint();
						}
					}
				}else if(event.getSource() == departmentBox ){

					if(!selected.isEmpty() && event.getStateChange() == ItemEvent.SELECTED){

						// TODO get assistants of selected department
						// display second list and 'request' button

						Department dept = new Department(selected);
						selectedDepartment = dept;
						assistants = dept.getAssistantsForThisDepartment();

						//(String[]) assistants.toArray(new String[assistants.size()])
						String[] array = new String[assistants.size()+1];
						array[0] = "Select Assistant";
						for (int i=1; i<=assistants.size(); i++) {
							Assistant a = assistants.get(i-1);
							array[i] = a.toString();
						}


						DefaultComboBoxModel model = new DefaultComboBoxModel(array);
						assistantBox.setModel(model);
						assistantBox.addItemListener(new ItemChangeListener());
						assistantBox.setVisible(true);
						btnRequest.setVisible(true);

						contentPane.invalidate();
						contentPane.repaint();

					}

				}else if(event.getSource() == assistantBox ){

					if(!selected.isEmpty() && event.getStateChange() == ItemEvent.SELECTED){

						String[] ns = selected.split(" ");
						String name = ns[0];
						String surname = ns[1];
						String mail = ns[2].substring(1, ns[2].length()-1);
						System.out.println(name + " "+ surname + " " + mail);
						Assistant a = new Assistant(name,surname,mail,selectedDepartment);
						selectedAssistant = a;
					}

				}
			}
		}       
	}
}
