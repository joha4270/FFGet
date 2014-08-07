package ffget.gui;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

import ffget.backend.*;

public class AdvDownloaderStatusTableModel extends AbstractTableModel {
	
	private int lenCached;
	
	private class itmGroup {
		PageRequest[] reqPage;
		CompileRequest reqComp;
		Boolean colapsed;
		Path fileTarget;
		
		private int cachedFinishedPage = -1;
		StoryPage getFinishedPage()
		{
			if(cachedFinishedPage>=0)
			{
				return reqPage[cachedFinishedPage].GetStoryPage();
			}
			
			for(int i = 0; i != reqPage.length; i++)
			{
				if(reqPage[i].GetStatus() == PageRequestStatus.Ready && reqPage[i].GetStoryPage() != null)
				{
					cachedFinishedPage = i;
					return reqPage[i].GetStoryPage();
				}
			}
			
			return null;
		}
		
		itmGroup(PageRequest[] r, Path p)
		{
			reqPage = r;
			fileTarget = p;
			reqComp = null;
			colapsed = false; //true;
		}
		
		int Lenght()
		{
			if(colapsed)
				return infoRows;
			else
				return reqPage.length + infoRows;
		}

		public String getStatus() {
			if(reqComp != null)
			{
				if(reqComp.getStatus() == CompileRequestStatus.FINISHED) return "Finished Compilation";
				else if(reqComp.getStatus() == CompileRequestStatus.WORKING) return "Compiling";
				else if(reqComp.getStatus() == CompileRequestStatus.QUEUE) return "Waiting for Compilation";
				else return "Error in compiling";
			}
			else if(getFinishedPages() == reqPage.length)
			{
				return "Download Finished";
			}
			else if(getFinishedPage() != null)
			{
				
				
				if(DownloadManager.WorksAt(getFinishedPage().GetStoryID()))
					return "Downloading (" + getFinishedPages() + "/" + reqPage.length + ")";
				
				else
				{
					return "In Queue";
				}
			}
			else
			{
				return "In Queue";
			}
			
			
			// TODO Auto-generated method stub
			
		}

		private int getFinishedPages() {
			
			int fin = 0;
			for(int i = 0; i != reqPage.length; i++)
			{
				if(reqPage[i].GetStatus() == PageRequestStatus.Ready)
					fin++;
			}
			
			return fin;
		}
		
	}

	private static final long serialVersionUID = 2331628450274889627L;
	
	private List<itmGroup> groups = new ArrayList<itmGroup>();
	
	public void AddGroup(PageRequest[] Group, Path fileTarget)
	{
		groups.add(new itmGroup(Group, fileTarget));
		
		lenCached = lenCalc();
		System.out.println("Lenght set to " + lenCached);
	}
	
	private int lenCalc() {
		int len = 1;
		for(itmGroup g : groups)
		{
			len += g.Lenght();
		}
		
		return len;
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return lenCached;
	}

	
	public Boolean CheckFinished()
	{
		Boolean ret = false;
		
		for(itmGroup grp : groups)
		{
			if(grp.getFinishedPages() == grp.reqPage.length && grp.reqComp == null)
			{
				StoryPage[] chap = new StoryPage[grp.reqPage.length];
				for(int i = 0; i != chap.length; i++)
				{
					chap[i] = grp.reqPage[i].GetStoryPage();
				}
				
				grp.reqComp = FileManager.QueueCompiling(chap, grp.fileTarget,grp.reqPage[0].subjects);
				if(grp.reqPage[0].subjects != null) System.out.println("New compileRequest, subjects not null");
				grp.colapsed = true;
				lenCached = lenCalc();
				ret = true;
			}
		}
		
		
		return ret;
	}
	
	int infoRows = 1;
	@Override
	public Object getValueAt(int row, int column) {
		int trow = row;
		try
		{
			if(row == 0)
			{
				if(column == 0)
				{
					//finished/total epubs
					int finished = 0;
					for(itmGroup grp: groups)
					{
						if(grp.getStatus().equals("Finished Compilation"))
						{
							finished++;
						}
					}
					
					return "Finsihed ("+ finished + "/" + groups.size() + ")";
				}
				else
				{
					int total = 0;
					int finished = 0;
					
					for(itmGroup grp: groups)
					{
						total += grp.reqPage.length;
						finished += grp.getFinishedPages();
					}
					
					return "Downloading (" + finished + "/" + total + ")";
				}
			}
			else
			{
				row--;
			}
			
			//System.out.println("Row " + row + " called");
			int localRow = 0;
			int groupIndex = 0;
			if(groups.isEmpty())
				return null;
		
			while(groups.get(groupIndex).Lenght() <= row)
			{
				row -= groups.get(groupIndex).Lenght();
				groupIndex++;
				//if(groupIndex == groups.size())
					//groupIndex--;
			}
			
		
			localRow = row - infoRows;
			//System.out.println("gIndex=" + groupIndex + ", row=" + row + ", localRow=" + localRow);
			StoryPage dataSource = groups.get(groupIndex).getFinishedPage();
		
		
			if(column == 0)
			{
				if(localRow >= 0)
				{
					if(dataSource != null)
					{
						//System.out.println("localrow=" + localRow + ", chaplen=" + dataSource.getChapters().length);
						return "   "  + dataSource.getChapters()[localRow];
					
					}
					else
					{
						return "Chapter " + (localRow + 1);
					}
				}
				else if(localRow == - infoRows)
				{
					if(dataSource != null)
					{
						return dataSource.GetStoryName();
					}
					else
					{
						return "No story name available";
					}
				}
				else if(localRow == -(infoRows + 1))
				{
					JProgressBar b = new JProgressBar();
					b.setMaximum(groups.get(groupIndex).reqPage.length);
					b.setValue(groups.get(groupIndex).getFinishedPages());
				
					return b;
				
				
				}
			
			}
			else if(column == 1)
			{
			
				if(localRow >= 0)
				{
					return groups.get(groupIndex).reqPage[localRow].GetStatus().toString();
				}
				else if(localRow == - infoRows)
				{
				
					return groups.get(groupIndex).getStatus();
				
				}
				else if(localRow == -(infoRows + 1))
				{
					return groups.get(groupIndex).colapsed;
					//return new JButton("Hide");
				
				}
			
			}
		
			return "Error";
		}
		catch(Exception E)
		{
			System.out.println(trow + " --- " + column);
			throw E;
		}
	}
}
