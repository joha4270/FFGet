package ffget.gui;

import java.awt.BorderLayout;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import java.awt.Component;

import ffget.backend.DownloadManager;
import ffget.backend.PageRequest;
import ffget.backend.PageRequestStatus;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class bulkDownloadWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7489405249477643109L;
	private final JPanel contentPanel = new JPanel();

	

	
	private Boolean quitWish = false;
	private JLabel lblStatus;
	private JTextPane textPane;
	private ArrayList<PageRequest> requests;
	private String Error = "";
	private Path folderTarget;
	/**
	 * @wbp.nonvisual location=41,409
	 */
	private final Timer timer = new Timer(0, (ActionListener) null);
	
	/**
	 * Create the dialog.
	 */
	public bulkDownloadWindow(final AdvDownloaderStatusTableModel tableModel,
			Path fileTarget) 
	{
		timer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				for(int i = requests.size() - 1; i >= 0; i--)
				{
					PageRequest p = requests.get(i);
					if(p.GetStatus() == PageRequestStatus.Ready)
					{
						PageRequest[] preq = new PageRequest[p.GetStoryPage().GetChapterCount()];
						System.out.println("Bulk started for Story " + p.GetStoryPage().GetStoryName()+ 
								" " + p.GetStoryPage().GetChapterCount() + " Chapters.");
						
						preq[0] = p;
						for(int j = 1; j != p.GetStoryPage().GetChapterCount(); j++)
						{
							
							
							preq[j] = DownloadManager.AddUrlToQue(
									GUIUtil.PageURL(p.GetURL(), "https://www.fanfiction.net/s/%s/%d", j + 1));
						}
						
						
						tableModel.AddGroup(
							preq, 
							FileSystems.getDefault().getPath
							(
								folderTarget.toString(),
								GUIUtil.genFileName(p.GetStoryPage().GetStoryName(),"epub")
							)
							
							
						);
						
						requests.remove(p);
						
					}
					else if(p.GetStatus() == PageRequestStatus.ERROR_GENERAL)
					{
						Error = Error + "\nGeneral Error for " + p.GetURL();
						requests.remove(p);
					}
					else if(p.GetStatus() == PageRequestStatus.ERROR_INVALID_ID)
					{
						Error = Error + "\nInvalid ID for " + p.GetURL();
						requests.remove(p);
					}
					else if(p.GetStatus() == PageRequestStatus.ERROR_NULL_STORY)
					{
						Error = Error + "\nNULL STORRY ERROR for "+ p.GetURL();
						requests.remove(p);
					}
				}
				String txt = "Fetching data needed to start download for " + requests.size() + " stories";
				if(!Error.isEmpty())
				{
					txt = txt + ", Encounted " + Error.split("\\n").length +  " Errors!";
					lblStatus.setToolTipText(Error);
				}
				
				lblStatus.setText(txt);
				
				if(requests.size() == 0 && quitWish)
				{
					Exit();
				}
			}
		});
		timer.setDelay(100);
		
		folderTarget = FileSystems.getDefault().getPath(System.getProperty("user.dir"));
		setMinimumSize(new Dimension(300, 250));
		setBounds(100, 100, 442, 370);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		textPane = new JTextPane();
		textPane.setBorder(null);
		textPane.setMargin(new Insets(0, 0, 0, 0));
		textPane.setToolTipText("Enter story  ID/URL here seperated by newline or ;");
		contentPanel.add(textPane, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.setLayout(new BorderLayout(0, 0));
			}
			{
				JPanel panel = new JPanel();
				buttonPane.add(panel, BorderLayout.EAST);
				JButton okButton = new JButton("Download");
				panel.add(okButton);
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						
						quitWish = true;
						
						String[] links = textPane.getText().split("\\n|;|\\r");
						DownloadManager.Slow += links.length;
						requests = new ArrayList<PageRequest>();
						lblStatus.setText("Fetching data needed to start download for " + links.length + " stories");
						
						System.out.println("Bulk Download stated. " + links.length + " valid or invalid links");
						for(String s : links)
						{
							//System.out.println("Trying String \"" + s + "\"");
							if(!s.trim().isEmpty())
							{
								//System.out.println(" Passed");
								requests.add(DownloadManager.AddUrlToQue(GUIUtil.PageURL(s,"https://www.fanfiction.net/s/%s/%d")));
								//TODO hang myself having to maintain this
								//this is a HORIBLE solution
							}
						}
						
						System.out.println("Timer Started");
						timer.start();
					}
				});
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
				{
					JButton cancelButton = new JButton("Cancel");
					panel.add(cancelButton);
					cancelButton.setActionCommand("Cancel");
				}
			}
			{
				JPanel panel = new JPanel();
				buttonPane.add(panel, BorderLayout.WEST);
				{
					lblStatus = new JLabel("Status");
					panel.add(lblStatus);
				}
			}
		}
	}

	void Exit()
	{
		this.dispose();
	}
	

}
