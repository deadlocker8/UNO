package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import tools.PathUtils;

public class PathSettings implements Serializable
{	
	private static final long serialVersionUID = 1L;
	private String pathLogfile;
	private ArrayList<String> folders;

	public PathSettings()
	{
		pathLogfile = "";
		folders = new ArrayList<String>();
		PathUtils.checkFolder(new File(System.getenv("APPDATA") + "/Deadlocker/PlayCount/"));
	}

	public void save() throws Exception
	{
		FileOutputStream fileOut = new FileOutputStream(System.getenv("APPDATA") + "/Deadlocker/PlayCount/paths.config");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(this);
		out.close();
		fileOut.close();
	}
	
	public void load() throws Exception
	{
		 FileInputStream fileIn = new FileInputStream(System.getenv("APPDATA") + "/Deadlocker/PlayCount/paths.config");
         ObjectInputStream in = new ObjectInputStream(fileIn);
         PathSettings loaded = (PathSettings) in.readObject();
         in.close();
         fileIn.close();
         
         this.pathLogfile = loaded.getPathLogfile();
         this.folders = loaded.getFolders();
	}
	
	public String getPathLogfile()
	{
		return pathLogfile;
	}
	
	public ArrayList<String> getFolders()
	{
		return folders;
	}

	public void setPathLogfile(String pathLogfile)
	{
		this.pathLogfile = pathLogfile;
	}

	public void setFolders(ArrayList<String> folders)
	{
		this.folders = folders;
	}	
}