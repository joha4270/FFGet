package ffget.gui;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JTable;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

import ffget.backend.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class GUIEvolved {

	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIEvolved window = new GUIEvolved();
					window.frmFanfictionnetDownloader.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	private JFrame frmFanfictionnetDownloader;
	private JTextField IdTextField;
	private JTextField FileTextField;
	private JTable table;
	private PageRequest workReq;
	private JButton btnDownload;
	private JLabel lblStoryName;
	private JComboBox<String> SiteSelectionBox;
	private JLabel lblStoryInfoExt;
	private Boolean userEditName = false;
	private JTextPane txtpnDesc;
	private AdvDownloaderStatusTableModel tableModel = new AdvDownloaderStatusTableModel();
	private Path fileTarget;
	/**
	 * @wbp.nonvisual location=421,39
	 */
	private final Timer FetchTimer = new Timer(0, (ActionListener) null);
	
	public GUIEvolved() {
		initialize();
		refreshTimer.start();
		frmFanfictionnetDownloader.setSize(400, 500);
	}
	
	protected String id;
	protected void UpdateTitle() {
		
		table.updateUI();
		if(workReq == null)
		{
			lblStoryName.setText("No story");
			lblStoryInfoExt.setText(" - ");
			btnDownload.setEnabled(false);
		}
		else if(workReq.GetStatus() == PageRequestStatus.Ready && id != workReq.GetStoryPage().GetStoryID())
		{
			id = workReq.GetStoryPage().GetStoryID();
			if(!userEditName && FileTextField.getText().trim().length() < 1 )
			{
				System.out.println("Empty Filename");
				FileTextField.setText(GUIUtil.genFileName(workReq.GetStoryPage().GetStoryName(),"epub")); 							
			}
			
			if(!userEditName && FileTextField.getText() != GUIUtil.genFileName(workReq.GetStoryPage().GetStoryName(),"epub"))
			{
				FileTextField.setText(GUIUtil.genFileName(workReq.GetStoryPage().GetStoryName(),"epub"));
			}
			
			lblStoryName.setText(workReq.GetStoryPage().GetStoryName());
			lblStoryInfoExt.setText(String.format("By: %s - %d Chapters - %d Words", workReq.GetStoryPage().GetAuthor(),workReq.GetStoryPage().GetChapterCount(),0));
			btnDownload.setEnabled(true);
			txtpnDesc.setText(workReq.GetStoryPage().GetStoryDescription());
			
		}
		else if(workReq.GetStatus() == PageRequestStatus.Ready)
		{}
		else
		{
			lblStoryName.setText(workReq.GetStatus().toString());
			lblStoryInfoExt.setText(" - ");
			btnDownload.setEnabled(false);
		}
		
	}
	
	private final String[] urlFormaters = 
	{
		"https://www.fanfiction.net/s/%s/%d"
	};
	/**
	 * @wbp.nonvisual location=421,99
	 */
	private final Timer refreshTimer = new Timer(0, (ActionListener) null);
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		refreshTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				UpdateTitle();
				tableModel.CheckFinished();
			}
		});
		refreshTimer.setDelay(500);
		FetchTimer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("FetchTimer Tick");
				
				String instring = IdTextField.getText();
				
				workReq = DownloadManager.AddUrlToFrontQueue(GUIUtil.PageURL(instring, urlFormaters[SiteSelectionBox.getSelectedIndex()]));
				
				
				UpdateTitle();
				
				
			}
		});
		FetchTimer.setRepeats(false);
		FetchTimer.setInitialDelay(10000);
		frmFanfictionnetDownloader = new JFrame();
		frmFanfictionnetDownloader.setTitle("Fanfiction.net Downloader");
		frmFanfictionnetDownloader.setBounds(100, 100, 242, 476);
		frmFanfictionnetDownloader.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmFanfictionnetDownloader.setJMenuBar(menuBar);
		
		JMenu mnMenu1 = new JMenu("Advanced");
		menuBar.add(mnMenu1);
		
		JMenu mnOptions = new JMenu("Options");
		mnMenu1.add(mnOptions);
		
		JMenu mnBulkDownload = new JMenu("Bulk Download");
		mnBulkDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				bulkDownloadWindow wind = new bulkDownloadWindow(tableModel,fileTarget);
				wind.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				wind.setVisible(true);
			}
		});
		mnMenu1.add(mnBulkDownload);
		
		lblStoryName = new JLabel("STORY NAME");
		
		lblStoryInfoExt = new JLabel("STORY INFO EXT");
		
		JLabel lblStory = new JLabel("Story:");
		
		SiteSelectionBox = new JComboBox<String>();
		SiteSelectionBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Fanfiction.net"}));
		SiteSelectionBox.setSelectedIndex(0);
		
		JButton button = new JButton("");
		
		JLabel lblFile = new JLabel("File:");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		
		IdTextField = new JTextField();
		IdTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				FetchTimer.start();
			}
		});
		IdTextField.setColumns(10);
		
		FileTextField = new JTextField();
		FileTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				userEditName = true;  //TODO this + filename part of UpdateTitle into something better
									 //mby a state machine, maybe voodoo
			}
		});
		FileTextField.setColumns(10);
		
		btnDownload = new JButton("Download");
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(workReq == null || workReq.GetStoryPage() == null)
					return;
					
				PageRequest[] p = new PageRequest[workReq.GetStoryPage().GetChapterCount()];
				p[0] = workReq;
				for(int i = 1; i != p.length; i++)
				{
					p[i] = DownloadManager.AddUrlToQue(String.format(urlFormaters[SiteSelectionBox.getSelectedIndex()],workReq.GetStoryPage().GetStoryID(),i + 1));
				}
				
				
				if(fileTarget == null)
				{
					String FilePathString = FileTextField.getText();
					if(FilePathString.contains(":/"))
					{
						fileTarget = FileSystems.getDefault().getPath(FilePathString);
					}
					else
					{
						fileTarget = FileSystems.getDefault().getPath(System.getProperty("user.dir"), FilePathString);
					}
				}
				tableModel.AddGroup(p,fileTarget);
				
				
				
				fileTarget = null;
				workReq = null;
				btnDownload.setEnabled(false);
				FileTextField.setText("");
				IdTextField.setText("");
				
				
			}
		});
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GroupLayout groupLayout = new GroupLayout(frmFanfictionnetDownloader.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblStoryName)
								.addComponent(lblStoryInfoExt)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblStory)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(IdTextField, GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(SiteSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblFile)
									.addGap(29)
									.addComponent(FileTextField, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(button))))
						.addGroup(groupLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
					.addContainerGap())
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(10,66,Short.MAX_VALUE)
					.addComponent(btnDownload, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
					.addGap(10,66,Short.MAX_VALUE))
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblStoryName)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblStoryInfoExt)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblStory)
						.addComponent(SiteSelectionBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(IdTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblFile)
							.addComponent(FileTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(button, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnDownload)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		
		txtpnDesc = new JTextPane();
		txtpnDesc.setBorder(null);
		txtpnDesc.setText(" - ");
		txtpnDesc.setEditable(false);
		txtpnDesc.setBackground(new Color(240,240,240));
		scrollPane.setViewportView(txtpnDesc);
		
		table = new JTable(tableModel);
		scrollPane_1.setViewportView(table);
		frmFanfictionnetDownloader.getContentPane().setLayout(groupLayout);
	}
}
