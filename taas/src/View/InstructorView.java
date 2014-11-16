package View;


import java.awt.event.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import Model.*;
import Helper.*;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class InstructorView extends JFrame {

	private JPanel contentPane;
	private DatabaseHelper dbh;

	private Instructor instructor;
	private Course course;
	ArrayList<Assistant> assistantList;
	/**
	 * Create the frame.
	 */
	public InstructorView(Instructor ins) {

		dbh = new DatabaseHelper();
		assistantList  = dbh.getAllAssistants();
		
		instructor = ins;
		final int personID = dbh.getPIDFromInstructor(instructor.id);
		instructor.teaches = dbh.getTeachingInformationForInstructor(instructor.id);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblInfo = new JLabel(ins.name +" "+ ins.surname);
		lblInfo.setBounds(306, 6, 114, 16);
		contentPane.add(lblInfo);


		JLabel lblCourses = new JLabel("Courses");
		lblCourses.setBounds(30, 40, 61, 16);
		contentPane.add(lblCourses);

		ArrayList<Instructor> c =null;

		for(int i = 0; i < instructor.teaches.size(); i++){ // Bence burada dersi kadar alt alta Combo Box koyalim
			int course_id= instructor.teaches.get(i).id;
			c = dbh.getInstructorFromCourseID(course_id);
			course = dbh.getCourseInfoFromID(course_id);
		}

		// JLabel a cekiyorum.	
		int assistantCount = course.setMaxAssistantCount();
		String astCount = ""+assistantCount;
		//		AssistantCount = AssistantCount + 1;

		System.out.println(astCount);

		//		Integer []asstCountArray = new Integer[AssistantCount];
		//		int dummy = 0;
		//		for (int i =0; i<AssistantCount; i++){
		//
		//			asstCountArray[i]= dummy;
		//			dummy++;
		//
		//		}




		//System.out.println(c);

		JComboBox cbCourses = new JComboBox(instructor.teaches.toArray()); // TODO
		cbCourses.setBounds(103, 36, 107, 27);

		contentPane.add(cbCourses);

		JLabel cbAsstCount = new JLabel(astCount);
		cbAsstCount.setBounds(368, 34, 61, 27);

		contentPane.add(cbAsstCount);

		JLabel lblAsstCount = new JLabel("Assistant Count");
		lblAsstCount.setBounds(240, 40, 101, 16);
		contentPane.add(lblAsstCount);



		ArrayList<Department> dn = dbh.getAllDepartments();
		String[] names = new String[dn.size()];

		for(int i=0; i<dn.size(); i++){
			names[i] = dn.get(i).toString();
		}

		
//		String[] anames = new String[al.size()];
//
//		for(int i=0; i<anames.length; i++){
//			anames[i] = al.get(i).toString();
//		}

		// Request comboboxes
		for(int i=0; i<assistantCount; i++ ) {
			final int y = 30*i;

			JComboBox cbDept = new JComboBox(dn.toArray());
			cbDept.setBounds(30, 97+y, 134, 27);
			contentPane.add(cbDept);
			final JComboBox cbAsst = new JComboBox();

			ItemListener itemListener = new ItemListener() {
				public void itemStateChanged(ItemEvent itemEvent) {
					
					int state = itemEvent.getStateChange();
					
					if(state == itemEvent.SELECTED){
					
						Department selected = (Department) itemEvent.getItem();
						String ass_inSelected[] = filterAssistantsAccordingToDepartment(assistantList, selected);
						cbAsst.setModel(new DefaultComboBoxModel(ass_inSelected));
						cbAsst.setBounds(176, 97+y, 139, 27);
						contentPane.add(cbAsst);	
						cbAsst.setVisible(true);
					}

				}
			};


			cbDept.addItemListener(itemListener);


			JButton btnMakeReq = new JButton("Make Request");
			btnMakeReq.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			btnMakeReq.setBounds(327, 96+y, 117, 29);
			contentPane.add(btnMakeReq);


		}// request cb end
		if(ins.isAdmin){
			JButton btnSettings = new JButton("Settings");
			btnSettings.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {

					SettingsView sv = new SettingsView(personID);
					sv.setVisible(true);
				}
			});
			btnSettings.setBounds(327, 243, 117, 29);
			contentPane.add(btnSettings);
		}
	}
	
	
	
	private String[] filterAssistantsAccordingToDepartment(ArrayList<Assistant> assistants, Department d){
		
		ArrayList<String> filtered = new ArrayList<String>();
		
		for(int i =0; i<assistants.size(); i++){
			Assistant a = assistants.get(i);
			if(a.department.id == d.id){
				filtered.add(a.toString());
			}

			
		}
		
		String[] result = new String[filtered.size()];
		result = filtered.toArray(result);
		return result;
		
 	}
	
}
