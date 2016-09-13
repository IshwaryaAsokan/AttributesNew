package com.kohler.importer;

import java.io.File;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;


abstract class SwingWorker {
    private Object value;  // see getValue(), setValue()

    
    /** 
     * Class to maintain reference to current worker thread
     * under separate synchronization control.
     */
    private static class ThreadVar {
        private Thread thread;
        ThreadVar(Thread t) { thread = t; }
        synchronized Thread get() { return thread; }
        synchronized void clear() { thread = null; }
    }

    private ThreadVar threadVar;

    /** 
     * Get the value produced by the worker thread, or null if it 
     * hasn't been constructed yet.
     */
    protected synchronized Object getValue() { 
        return value; 
    }

    /** 
     * Set the value produced by worker thread 
     */
    private synchronized void setValue(Object x) { 
        value = x; 
    }

    /** 
     * Compute the value to be returned by the <code>get</code> method. 
     */
    public abstract Object construct();

    /**
     * Called on the event dispatching thread (not on the worker thread)
     * after the <code>construct</code> method has returned.
     */
    public void finished() {
    }

    /**
     * A new method that interrupts the worker thread.  Call this method
     * to force the worker to stop what it's doing.
     */
    public void interrupt() {
        Thread t = threadVar.get();
        if (t != null) {
            t.interrupt();
        }
        threadVar.clear();
    }

    /**
     * Return the value created by the <code>construct</code> method.  
     * Returns null if either the constructing thread or the current
     * thread was interrupted before a value was produced.
     * 
     * @return the value created by the <code>construct</code> method
     */
    public Object get() {
        while (true) {  
            Thread t = threadVar.get();
            if (t == null) {
                return getValue();
            }
            try {
                t.join();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // propagate
                return null;
            }
        }
    }


    /**
     * Start a thread that will call the <code>construct</code> method
     * and then exit.
     */
    public SwingWorker() {
        final Runnable doFinished = new Runnable() {
           public void run() { finished(); }
        };

        Runnable doConstruct = new Runnable() { 
            public void run() {
                try {
                    setValue(construct());
                }
                finally {
                    threadVar.clear();
                }

                SwingUtilities.invokeLater(doFinished);
            }
        };

        Thread t = new Thread(doConstruct);
        threadVar = new ThreadVar(t);
    }

    /**
     * Start the worker thread.
     */
    public void start() {
        Thread t = threadVar.get();
        if (t != null) {
            t.start();
        }
    }
    
    
}

class LongTask {
	private Logger logger = Logger.getLogger("debug");
	private final static String PUNI = "PUNI";
	private final static String PCEN = "PCEN";
	private final static String TCEN = "TCEN";
	private final static String SACK = "SACK";
	private final static String NBKR = "NBKR";
	private final static String STRLDISC = "STRLDISC";
	private final static String MCGR = "MCGR";
	private final static String KPNA = "KPNA";
	private final static String KALS = "KALS";
	private final static String RBRN = "RBRN";
	private final static String INDI = "INDI";
	private final static String DFON = "DFON";
	private final static String DFNS = "DFNS";
	private final static String KMEE = "KMEE";
	private final static String POWR = "POWR";
	private final static String STRL = "STRL";
	private final static String BAKR = "BAKR";
	private final static String ENGL = "ENGL";
	private final static String HYTC = "HYTC";
	private final static String GRMY = "GRMY";
	private final static String KMEX = "KMEX";
	private final static String KOUK = "KOUK";
	private final static String MODS = "MODS";
	private final static String SPRT = "SPRT";
	private final static String VIGN = "VIGN";
	private final static String RESI = "RESI";
	private final static String ENGN = "ENGN";
	private final static String PORT = "PORT";
	private final static String ENGC = "ENGC";
	private final static String ENGM = "ENGM";
	private final static String CNUC = "CNUC";
	private final static String CNTR = "CNTR";
	private final static String KREA = "KREA";
	private final static String KMAP = "KMAP";
	private final static String MIRA = "MIRA";
	private final static String NKUK = "NKUK";
	private final static String PWRC = "PWRC";
	private final static String RADA = "RADA";
	private final static String ANNS = "ANNS";
	private final static String NDST = "NDST";
	private final static String ASIA = "ASIA";
	private final static String T_KPNA = "T_KPNA";
	private final static String T_SPRT = "T_SPRT";
	private final static String KBRZ = "KBRZ";
	private final static String KALL = "KALL";
	private final static String INDA = "INDIA (2016)";
	private final static String STEX = "Store Experience";
	private final static String LAMX = "LAMX";
	private final static String HTEC = "HTEC";
	
	private final static String runningDirectory = "c:\\applications\\AttributesAndKeywordsImporter\\";
	private final static String logDirectory = "logs";
   	private final static String xlsInputFile = "AttributesAndKeywordsSpreadsheet.xls";
   	
	private final static String ATTRIBUTE_TAB_NAME = "Attributes";
	private final static String KEYWORD_TAB_NAME = "Keywords";
	
	private final static String TEST_PCEN = "TEST_PCEN";
	private final static String TEST_PUNI = "TEST_PUNI";
	
    private int lengthOfTask;
    private int current = 0;
    private boolean done = false;
    private String statMessage;


    private String businessUnit;
    
    
    public LongTask() {
        //Compute length of task...
        //In a real program, this would figure out
        //the number of bytes to read or whatever.
        lengthOfTask = 1000;
        
        
    }

    /**
     * Called from ProgressBarDemo to start the task.
     */
    public void go(String businessUniIn) 
    {
    	done = false;
    	current = 0;
    	businessUnit = businessUniIn.toUpperCase();
    	statMessage = "";
    	logger.info("Running the Keywords and Attributes Importer for: " + businessUnit);
    
        final SwingWorker worker = new SwingWorker() 
        {
        	
            public Object construct() 
            {

            	statMessage = "STARTING ATTRIBUTE/KEYWORD IMPORTER\n";
            	DatabaseDriver dd = null;

            	boolean errored = false;
            	try{
            		
            		String connectString = "";
            		String userID = "";
            		String password = "";
            		String instanceName = "";
            		String dbType = "";
             
            		
            		// PCEN
            		if (businessUnit.equalsIgnoreCase(STRLDISC)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02strldisc_user";
        				password = "strldiscjinx9393";
        				instanceName = "CB02STRLDISC";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(MCGR)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
           				userID = "cb02mcgr_user";
        				password = "mcgrin60days";
        				instanceName = "CB02MCGR";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(KPNA)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02kpna_user";
        				password = "kpnain60days";
        				instanceName = "CB02KPNA";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(KALS)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
                		userID = "cb02kals_user";
        				password = "kalsin60days";
        				instanceName = "CB02KALS";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(RBRN)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02rbrn_user";
        				password = "rbrnin60days";
        				instanceName = "CB02RBRN";
        				dbType = PCEN;
        			} else if (businessUnit.equalsIgnoreCase(INDI)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02indi_user";
        				password = "indiin60days";
        				instanceName = "CB02INDI";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(DFON)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02dfon_user";
        				password = "dfonjinx9393";
        				instanceName = "CB02DFON";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(DFNS)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02dfns_user";
        				password = "dfnsjinx9393";
        				instanceName = "CB02DFNS";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(KMEE)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02kmee_user";
        				password = "kmeejinx9393";
        				instanceName = "CB02KMEE";
        				dbType = PCEN;
            		} else if (businessUnit.equalsIgnoreCase(POWR)) {
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02powr_user";
        				password = "powrin60days";
        				instanceName = "CB02POWR";
        				dbType = PCEN;
            		} else if(businessUnit.equalsIgnoreCase(STRL)){
                		connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
        				userID = "cb02strl_user";
        				password = "strljinx9393";
        				instanceName = "CB02STRL";
            			dbType = PCEN;
            		} else if(businessUnit.equalsIgnoreCase(BAKR)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
            			userID = "CB02BAKR_USER";
            			password = "bakrjinx9393";
            			instanceName = "CB02BAKR";
            			dbType = PCEN;
        			} else if(businessUnit.equalsIgnoreCase(SACK)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02SACK_USER";
	        			password = "sackin60days";
	        			instanceName = "CB02SACK";
	        			dbType = PCEN;
        			} else if(businessUnit.equalsIgnoreCase(ENGL)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02ENGL_USER";
	        			password = "engljinx9393";
	        			instanceName = "CB02ENGL";
	        			dbType = PCEN;
        			} else if(businessUnit.equalsIgnoreCase(HYTC)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02HYTC_USER";
	        			password = "hytcjinx9393";
	        			instanceName = "CB02HYTC";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(GRMY)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02GRMY_USER";
	        			password = "grmyjinx9393";
	        			instanceName = "CB02GRMY";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(KMEX)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02KMEX_USER";
	        			password = "kmexjinx9393";
	        			instanceName = "CB02KMEX";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(KOUK)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02KOUK_USER";
	        			password = "koukjinx9393";
	        			instanceName = "CB02KOUK";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(MODS)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02MODS_USER";
	        			password = "modsjinx9393";
	        			instanceName = "CB02MODS";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(SPRT)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02SPRT_USER";
	        			password = "sprtjinx9393";
	        			instanceName = "CB02SPRT";
	        			dbType = PCEN;
	    			} else if(businessUnit.equalsIgnoreCase(VIGN)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1524:PCEN";
	        			userID = "CB02VIGN_USER";
	        			password = "vignin60days";
	        			instanceName = "CB02VIGN";
	        			dbType = PCEN;
	    			}
	        		else if(businessUnit.equalsIgnoreCase(T_KPNA)){
	        			connectString = "jdbc:oracle:thin:@irving.kohlerco.com:1539:TCEN";
	        			userID = "kocpdb";
	        			password = "tcenaccess";
	        			instanceName = "CB02KPNA";
	        			dbType = TCEN;
	        		}
	        		else if(businessUnit.equalsIgnoreCase(T_SPRT)){
	        			connectString = "jdbc:oracle:thin:@irving.kohlerco.com:1539:TCEN";
	        			userID = "kocpdb";
	        			password = "tcenaccess";
	        			instanceName = "CB02SPRT";
	        			dbType = TCEN;
	        		}
	        		// PUNI
                	else if(businessUnit.equalsIgnoreCase(RESI)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02RESI_USER";
            			password = "resiin60days";
            			instanceName = "CB02RESI";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(NDST)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02NDST_USER";
            			password = "ndstin60days";
            			instanceName = "CB02NDST";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(ENGN)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02ENGN_USER";
            			password = "engnin60days";
            			instanceName = "CB02ENGN";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(PORT)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02PORT_USER";
            			password = "portin60days";
            			instanceName = "CB02PORT";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(ENGC)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02ENGC_USER";
            			password = "engcjinx9393";
            			instanceName = "CB02ENGC";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(ENGM)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02ENGM_USER";
            			password = "engmjinx9393";
            			instanceName = "CB02ENGM";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(CNUC)){
                		connectString = "jdbc:oracle:thin:@uswix107.kohlerco.com:1526:QKOHL";
                		userID = "ko41251cnuc";
                		password = "eagle1";
            			instanceName = "CB02CNUC";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(CNTR)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02CNTR_USER";
            			password = "cntrjinx9393";
            			instanceName = "CB02CNTR";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(KREA)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02KREA_USER";
            			password = "kreain60days";
            			instanceName = "CB02KREA";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(KMAP)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02KMAP_USER";
            			password = "kmapin60days";
            			instanceName = "CB02KMAP";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(MIRA)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02MIRA_USER";
            			password = "mirain60days";
            			instanceName = "CB02MIRA";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(NKUK)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02NKUK_USER";
            			password = "nkukin60days";
            			instanceName = "CB02NKUK";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(PWRC)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02PWRC_USER";
            			password = "pwrcjinx9393";
            			instanceName = "CB02PWRC";
            			dbType = PUNI;
                	} else if(businessUnit.equalsIgnoreCase(RADA)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02RADA_USER";
            			password = "radajinx9393";
            			instanceName = "CB02RADA";
            			dbType = PUNI;
            		} else if(businessUnit.equalsIgnoreCase(ANNS)){
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02ANNS_USER";
            			password = "annsin60days";
            			instanceName = "CB02ANNS";
            			dbType = PUNI;
	        		} else if(businessUnit.equalsIgnoreCase(ASIA)){
	        			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
	        			userID = "CB02ASIA_USER";
	        			password = "asiain60days";
	        			instanceName = "CB02ASIA";
	        			dbType = PUNI;
	    			} else if(businessUnit.equalsIgnoreCase(NBKR)){
	        	    	// Call the database to get the Item Attributes from PUNI
	        			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
	        			userID = "CB02NBKR_USER";
	        			password = "nbkrin60days";
	        			instanceName = "CB02NBKR";
	        			dbType = PUNI;
	    			}
	    			else if(businessUnit.equalsIgnoreCase(KBRZ)){
	        			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
	        			userID = "cb02kbrz_user";
	        			password = "kbrzin60days";
	        			instanceName = "CB02KBRZ";
	        			dbType = PUNI;
	    			}
	    			else if(businessUnit.equalsIgnoreCase(KALL)){
	        			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
	        			userID = "cb02kall_user";
	        			password = "kallin60days";
	        			instanceName = "CB02KALL";
	        			dbType = PUNI;
	    			}
	    			else if(businessUnit.equalsIgnoreCase(INDA)){
	        			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
	        			userID = "cb02inda_user";
	        			password = "indain60days";
	        			instanceName = "CB02INDA";
	        			dbType = PUNI;
	    			}
                	else if(businessUnit.equalsIgnoreCase(STEX)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02STEX_USER";
            			password = "stexin60days";
            			instanceName = "CB02STEX";
            			dbType = PUNI;
                	}
                	else if(businessUnit.equalsIgnoreCase(LAMX)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02LAMX_USER";
            			password = "lamxin60days";
            			instanceName = "CB02LAMX";
            			dbType = PUNI;
                	}
                	else if(businessUnit.equalsIgnoreCase(HTEC)){
            	    	// Call the database to get the Item Attributes from PUNI
            			connectString = "jdbc:oracle:thin:@wilbur:1533:PUNI";
            			userID = "CB02HTEC_USER";
            			password = "htecin60days";
            			instanceName = "CB02HTEC";
            			dbType = PUNI;
                	}
            		
            		// TEST
                	else if(businessUnit.equalsIgnoreCase(TEST_PUNI)){
            			connectString = "jdbc:oracle:thin:@localhost:1521:xe";
            			userID = "system";
            			password = "admin";
            			instanceName = "system";
            			dbType = PUNI;
                	}
                	else if(businessUnit.equalsIgnoreCase(TEST_PCEN)){
            			connectString = "jdbc:oracle:thin:@localhost:1521:xe";
            			userID = "system";
            			password = "admin";
            			instanceName = "system";
            			dbType = PCEN;
                	}
                	else{
                		throw new Exception("NO BUSINESS UNIT");
                	}
            		
            		cleanupLogFiles(runningDirectory + logDirectory, 14);
           			dd = new DatabaseDriver(connectString, userID, password, instanceName, dbType);                	

           			// Get all the item numbers in the db
           			HashMap itemNumbers = dd.retrieveItemNumbers();
           			
           			// Get all the attributes that are in the database
           			HashMap itemAttributeTypesHashMap = dd.retrieveItemAttributeTypes();
           			
            		// Get all the keywords that are in the database	
           			HashMap keywordsTypeHashMap = dd.retrieveKeywordTypes();
       	
                   	FileDriver fd = new FileDriver(runningDirectory + xlsInputFile);
                    // This will validate and populate the header information for the attributes
                   	HashMap columnHeaderAttributesHashMap = fd.validateAndPopulateHeader(ATTRIBUTE_TAB_NAME, itemAttributeTypesHashMap);
                   	ArrayList attributesFromSpreadsheetArrayList = fd.validateAndPopulateValues(ATTRIBUTE_TAB_NAME, columnHeaderAttributesHashMap, itemAttributeTypesHashMap, itemNumbers, businessUnit);
 
                  	// This will validate and populate the header information
                   	HashMap columnHeaderKeywordsHashMap = fd.validateAndPopulateHeaderForKeywords(KEYWORD_TAB_NAME, keywordsTypeHashMap);
                	ArrayList keywordsFromSpreadsheetArrayList = fd.validateAndPopulateValues(KEYWORD_TAB_NAME, columnHeaderKeywordsHashMap, keywordsTypeHashMap, itemNumbers, businessUnit);
                   	statMessage = statMessage + "IMPORTED XLS FILE!\n";
                   	current += lengthOfTask/4;	

                   	dd.processItemAttributeValuesStatements(attributesFromSpreadsheetArrayList);

                   	current += lengthOfTask/4;	
                   	statMessage = statMessage + "ATTRIBUTE SUCESSFULLY FINISHED!\n";

 
                	current += lengthOfTask/4;	
                	dd.processKeywordsStatements(keywordsFromSpreadsheetArrayList);
   	            	statMessage = statMessage + "KEYWORDS SUCESSFULLY FINISHED!\n";
   	            	done = true;
   	            	current += lengthOfTask/4;	                    	
               	}
            	catch (Exception e){
            		if(e.getLocalizedMessage() == null || e.getLocalizedMessage().equals("")){
            			statMessage = statMessage + "Error! Please first try deleting columns not" +
            					"being used.  If this does not work, send Transaction Log and XLS " +
            					"to Support";
            		}
            		else{
            			statMessage = statMessage + e.getLocalizedMessage();	
            		}
            		
            		logger.error( "Stack Trace: ", e);
            		done = true;
            		errored = true;
            		return null;
            	}
        		finally{
        			try{
       	            	if(errored){
       	            		statMessage = statMessage + "\nERRORED! \n";
       	            	}else{
       	   	            	statMessage = statMessage + "FINISHED!";
       	            	}
        				dd.close();
        			}
        	    	catch(Exception e){
        	    		System.out.println(e.toString());
        	    		e.printStackTrace();
        	    	}
        			
        		}

            	return null;
            }
        };
        worker.start();
    }

    /**
     * Called from ProgressBarDemo to find out how much work needs
     * to be done.
     */
    public int getLengthOfTask() {
        return lengthOfTask;
    }

    /**
     * Called from ProgressBarDemo to find out how much has been done.
     */
    public int getCurrent() {
        return current;
    }

    public void stop() {
        statMessage = "";
    }

    /**
     * Called from ProgressBarDemo to find out if the task has completed.
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Returns the most recent status message, or null
     * if there is no current status message.
     */
    public String getMessage() {
        return statMessage;
    }
    
    private void cleanupLogFiles(String logDirectory, int daysBackToDelete){
        File directory = new File(logDirectory);  
        if(directory.exists()){  
  
            File[] listFiles = directory.listFiles();              
            // Delete files older than 14 days
            long purgeTime = System.currentTimeMillis() - (daysBackToDelete * 24 * 60 * 60 * 1000);  
            
            for(int counter = 0; counter < listFiles.length; counter++) {  
            	File tempFile = listFiles[counter];
                if(tempFile.lastModified() < purgeTime) {  
                    if(!tempFile.delete()) {  
                        System.err.println("Unable to delete file: " + tempFile.getName());  
                    }  
                }  
            }  
        }
    }
}
public class ProgressBarImpl2 extends JPanel
 implements ActionListener {

	//There because of extending the JPanel
	private final static long serialVersionUID = 1;
	
	private final static String SACK = "SACK";
	private final static String NBKR = "NBKR";
	private final static String STRLDISC = "STRLDISC";
	private final static String MCGR = "MCGR";
	private final static String KPNA = "KPNA";
	private final static String KALS = "KALS";
	private final static String RBRN = "RBRN";
	private final static String INDI = "INDI";
	private final static String DFON = "DFON";
	private final static String DFNS = "DFNS";
	private final static String KMEE = "KMEE";
	private final static String POWR = "POWR";
	private final static String STRL = "STRL";
	private final static String BAKR = "BAKR";
	private final static String ENGL = "ENGL";
	private final static String HYTC = "HYTC";
	private final static String GRMY = "GRMY";
	private final static String KMEX = "KMEX";
	private final static String KOUK = "KOUK";
	private final static String MODS = "MODS";
	private final static String SPRT = "SPRT";
	private final static String VIGN = "VIGN";
	private final static String RESI = "RESI";
	private final static String ENGN = "ENGN";
	private final static String PORT = "PORT";
	private final static String ENGC = "ENGC";
	private final static String ENGM = "ENGM";
	private final static String CNUC = "CNUC";
	private final static String CNTR = "CNTR";
	private final static String KREA = "KREA";
	private final static String KMAP = "KMAP";
	private final static String MIRA = "MIRA";
	private final static String NKUK = "NKUK";
	private final static String PWRC = "PWRC";
	private final static String RADA = "RADA";
	private final static String ANNS = "ANNS";
	private final static String NDST = "NDST";
	private final static String ASIA = "ASIA";
	private final static String T_KPNA = "T_KPNA";
	private final static String T_SPRT = "T_SPRT";
	private final static String KBRZ = "KBRZ";
	private final static String KALL = "KALL";
	private final static String INDA = "INDIA (2016)";
	private final static String STEX = "Store Experience";
	private final static String LAMX = "LAMX";
	private final static String HTEC = "HTEC";
	
	//String[] optionString = new String[] {"TEST_PUNI", "TEST_PCEN", "PORT", "RESI" };
	String[] optionString = new String[] {INDI, DFON, DFNS, KALS, KMEE, KPNA, MCGR, POWR, RBRN, STRLDISC, STRL, BAKR, SACK, NBKR, ENGL, HYTC, GRMY, KMEX, KOUK, MODS, SPRT, VIGN, RESI, ENGN, PORT, ENGC, ENGM, CNUC, CNTR, KREA, KMAP, MIRA, NKUK, PWRC, RADA, ANNS, NDST, ASIA, T_KPNA, T_SPRT, KBRZ, KALL, INDA, STEX, LAMX, HTEC};
	// public final static int ONE_SECOND = 1000;
	public final static int ONE_SECOND = 10;

	private JProgressBar progressBar;
	private javax.swing.Timer timer;
	private JButton startButton;
	private LongTask task;
	private JTextArea taskOutput;
	private String prevMessage = "";

	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JLabel jLabel1;
	// private javax.swing.JTextField jTextField1;

	public ProgressBarImpl2() {
		super(new BorderLayout());
		task = new LongTask();

		jLabel1 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();


		jLabel1.setText("Select Location:");
		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(optionString));

		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);

		progressBar = new JProgressBar(0, task.getLengthOfTask());
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		taskOutput = new JTextArea(5, 20);
		taskOutput.setMargin(new Insets(5, 5, 5, 5));
		taskOutput.setEditable(false);
		taskOutput.setCursor(null); // inherit the panel's cursor
		// see bug 4851758

		JPanel panel = new JPanel();

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				panel);
		panel.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.add(21, 21, 21)
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.TRAILING,
												false)
										.add(org.jdesktop.layout.GroupLayout.LEADING,
												jPanel1Layout
														.createSequentialGroup()
														.add(26, 26, 26))
										.add(org.jdesktop.layout.GroupLayout.LEADING,
												jPanel1Layout
														.createSequentialGroup()
														.add(jPanel1Layout
																.createParallelGroup(
																		org.jdesktop.layout.GroupLayout.TRAILING,
																		false)
																.add(jLabel1,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																		100,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(org.jdesktop.layout.GroupLayout.LEADING,
																		jPanel1Layout
																				.createSequentialGroup()
																				.add(10,
																						10,
																						10)
																				.add(jPanel1Layout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(startButton)
																					)
																		)
														)
														.addPreferredGap(
																org.jdesktop.layout.LayoutStyle.RELATED)
														.add(jPanel1Layout
																.createParallelGroup(
																		org.jdesktop.layout.GroupLayout.LEADING)
																.add(progressBar,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																		org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(jComboBox1,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
																		155,
																		org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
																.add(jPanel1Layout
																		.createSequentialGroup()
																		.add(jPanel1Layout
																				.createParallelGroup(
																						org.jdesktop.layout.GroupLayout.LEADING,
																						false)
																				.add(jPanel1Layout
																						.createSequentialGroup()
																						.addPreferredGap(
																								org.jdesktop.layout.LayoutStyle.RELATED,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE)))
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)))))
								.addContainerGap(39, Short.MAX_VALUE)));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.add(20, 20, 20)
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(jComboBox1,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
										.add(jLabel1))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.RELATED)
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE))
								.add(9, 9, 9)
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(startButton)
										.add(progressBar,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
								.add(20, 20, 20)
								.addContainerGap(23, Short.MAX_VALUE)));

		add(panel, BorderLayout.PAGE_START);
		add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// Create a timer.
		timer = new javax.swing.Timer(ONE_SECOND, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				progressBar.setValue(task.getCurrent());
				String s = task.getMessage();
				if (s != null && !s.equals(prevMessage)) {
					taskOutput.setText(s);
					taskOutput.setCaretPosition(taskOutput.getDocument()
							.getLength());
				}
				if (task.isDone()) {
					Toolkit.getDefaultToolkit().beep();
					timer.stop();
					startButton.setEnabled(true);
					setCursor(null); // turn off the wait cursor
					progressBar.setValue(progressBar.getMaximum());
				}
			}
		});
	}

	/**
	 * Called when the user presses the start button.
	 */
	public void actionPerformed(ActionEvent evt) {
		startButton.setEnabled(false);
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		
		String location = "";

		location = "" + jComboBox1.getSelectedItem();


		task.go(location);
		timer.start();
	}

	// This method returns the selected radio button in a button group
	public JRadioButton getSelection(ButtonGroup group) {
		for (Enumeration e = group.getElements(); e.hasMoreElements();) {
			JRadioButton b = (JRadioButton) e.nextElement();
			if (b.getModel() == group.getSelection()) {
				return b;
			}
		}
		return null;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	public static void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("Attribute And Keyword Importer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new ProgressBarImpl2();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

}

