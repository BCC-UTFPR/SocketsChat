package Utils;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ListFiles {
	public ArrayList<String> listFilesForFolder() throws IOException {
		 ArrayList<String> myList = new ArrayList<String>();

		String home = System.getProperty("user.home");
		String path = home + "/Downloads/";
		File folder1 = new File(path);
		File[] listOfFiles = folder1.listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		    	myList.add(file.getName());
		    }
		}
		return myList;
	}
}
