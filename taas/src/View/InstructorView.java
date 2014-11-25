package View;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Helper.DatabaseHelper;
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
	private JLabel lblRequest;

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


		cbTeaching = new JComboBox(arr);
		cbTeaching.setBounds(112, 49, 178, 27);
		cbTeaching.addItemListener(new ItemChangeListener());

		contentPane.add(cbTeaching);

		JLabel lblAdditionalRequest = new JLabel("Additional Request :");
		lblAdditionalRequest.setBounds(28, 377, 460, 16);
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
		tfAdditionalRequest.setBounds(172, 371, 316, 28);
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
		btnSaveAdditionalRequest.setBounds(533, 372, 117, 29);
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
						selectedCourse = c;

						lblMaxAsst.setText("Max assistant : " + c.maxAssistantNumber);
						//createRequestPanel(c.maxAssistantNumber); // selected dersin max asst sayisi
						ArrayList<Department> deps = dbh.getAllDepartments();
						String[] array = new String[deps.size()+1];
						for (int i=1; i<=deps.size(); i++) {
							Department d = deps.get(i-1);
							array[i] = d.code;
						}
						DefaultComboBoxModel model = new DefaultComboBoxModel(array);
						departmentBox.setModel(model);
						departmentBox.addItemListener(new ItemChangeListener());
						departmentBox.setVisible(true);
						contentPane.invalidate();
						contentPane.repaint();
					}
				}else if(event.getSource() == departmentBox ){

					if(!selected.isEmpty() && event.getStateChange() == ItemEvent.SELECTED){
						
						System.out.println("Selected dept is -->" + selected);

					}

				}

			}
		}       
	}


	/*
	private void createRequestPanel(int max){



		for (int i=0; i<max; i++){
			int y = 50 + 35*i;

			JComboBox 
			panel.add(departmentBox);

			JComboBox 
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

	 */
}
