package View;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import Helper.*;
import Model.*;

public class TAAS {

	private JFrame frame;
	private JTextField tfUsername;
	private JTextField tfPassword;
	private DatabaseHelper dbh;
	
	private Image bg;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TAAS window = new TAAS();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public TAAS() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		frame.setBounds(100, 100, 550, 350);

			BufferedImage image = ImageIO.read(new File("taas_bg.jpg"));
			frame.setContentPane(new JLabel(new ImageIcon(image)));
			frame.setSize(image.getWidth()	,image.getHeight());

		// Set background
	
			 bg = ImageIO.read(new File("taas_bg.jpg"));
	
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		
		BufferedImage passIcon = ImageIO.read(new File("lock.jpg"));
		JLabel lblPassword = new JLabel(new ImageIcon(passIcon));
		lblPassword.setBounds(85, 119, 20, 20);
		frame.getContentPane().add(lblPassword);

		tfUsername = new JTextField();

		tfUsername.setBounds(168, 72, 218, 28);
		tfUsername.setColumns(17);
		frame.getContentPane().add(tfUsername);

		JLabel lblUsername = new JLabel("Username : ");
		lblUsername.setBounds(82, 78, 74, 16);
		frame.getContentPane().add(lblUsername);

		tfPassword = new JPasswordField();

		tfPassword.setBounds(168, 113, 218, 28);
		tfPassword.setColumns(10);
		frame.getContentPane().add(tfPassword);

		JButton btnLogin = new JButton("Login");
		btnLogin.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String username = tfUsername.getText();
				String plainPassword = tfPassword.getText();
				//System.out.println("Username = "+tfUsername.getText()+"\nPassword = "+tfPassword.getText());
				dbh = new DatabaseHelper();

				// ISADMIN
				if(username.compareToIgnoreCase("admin") == 0  && plainPassword.compareTo("admintest") == 0){
					AdminView av = new AdminView();
					av.setVisible(true);
					frame.dispose();
				}else{

					if(dbh.authorizeUser(username, plainPassword)){

						Instructor ins = dbh.getAuthorizedInstructor(username);
						//System.out.println(ins);
						InstructorView iv = new InstructorView(ins);
						frame.dispose();
						
						try {
							Thread.sleep(1750);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						
						iv.setVisible(true);
					}

				}
			}
		});
		btnLogin.setBounds(218, 153, 117, 29);
		frame.getContentPane().add(btnLogin);



	}

	 public void paintComponent(Graphics g)
	    {
	        // Draws the img to the BackgroundPanel.
	        g.drawImage(bg, 0, 0, null);
	    }

}
