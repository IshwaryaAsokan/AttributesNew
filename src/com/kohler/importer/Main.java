package com.kohler.importer;

public class Main {

	
	/*
	 * 1) Validate the Header Row against DB
	 * 2) Populate the Header Row
	 * 3) Validate the contents of cells against rules
	 * 4) Populate the contents of the cells 
	 * 5) Run the updates against the database
	 */
	
	public static void main(String[] args) {
	     javax.swing.SwingUtilities.invokeLater(new Runnable() 
		     {
		        public void run() 
		        {
		            ProgressBarImpl2.createAndShowGUI();
		        }
		     }
	     );
   	}

}
