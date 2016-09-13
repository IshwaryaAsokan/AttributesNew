package com.kohler.importer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class FileDriver{
	private final static String columnOfAttributes[] = new String[]{"A", "B", "C", "D", "E", "F",
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
		"Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", 
		"AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB",
		"BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ",
		"BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", 
		"CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", 
		"CV", "CW", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", 
		"DK", "DL", "DM", "DN", "DO", "DP", "DQ", "DR", "DS", "DT", "DU", "DV", "DW", "DX", "DY", 
		"DZ", "EA", "EB", "EC", "ED", "EE", "EF", "EG", "EH", "EI", "EJ", "EK", "EL", "EM", "EN", 
		"EO", "EP", "EQ", "ER", "ES", "ET", "EU", "EV", "EW", "EX", "EY", "EZ", "FA", "FB", "FC", 
		"FD", "FE", "FF", "FG", "FH", "FI", "FJ", "FK", "FL", "FM", "FN", "FO", "FP", "FQ", "FR", 
		"FS", "FT", "FU", "FV", "FW", "FX", "FY", "FZ", "GA", "GB", "GC", "GD", "GE", "GF", "GG", 
		"GH", "GI", "GJ", "GK", "GL", "GM", "GN", "GO", "GP", "GQ", "GR", "GS", "GT", "GU", "GV", 
		"GW", "GX", "GY", "GZ", "HA", "HB", "HC", "HD", "HE", "HF", "HG", "HH", "HI", "HJ", "HK", 
		"HL", "HM", "HN", "HO", "HP", "HQ", "HR", "HS", "HT", "HU", "HV", "HW", "HX", "HY", "HZ", 
		"IA", "IB", "IC", "ID", "IE", "IF", "IG", "IH", "II", "IJ", "IK", "IL", "IM", "IN", "IO", 
		"IP", "IQ", "IR", "IS", "IT", "IU", "IV"};
	
	private final static String FIELD_POSITION_KEYWORD_PROPERTIES_FILE = "com/kohler/validator/properties/FieldPositionKeyword.properties";
	private final static String FIELD_POSITION_ATTRIBUTE_PROPERTIES_FILE = "com/kohler/validator/properties/FieldPositionAttribute.properties";
	private final static String FIELD_VALIDATION_PROPERTIES_FILE = "com/kohler/validator/properties/FieldValidation.properties";
	private final static String DELETE_TOKEN = "*";
	private static POIFSFileSystem pfs;
	
    private static Properties fieldPositionKeywordProp = new Properties();
    private static Properties fieldPositionAttributeProp = new Properties();
    private static Properties fieldValidationProp = new Properties();
    
	public FileDriver(String xlsInputFile) throws Exception{
		try{
			ClassLoader loader = Thread.currentThread().getContextClassLoader();           
			InputStream stream = loader.getResourceAsStream(FIELD_POSITION_KEYWORD_PROPERTIES_FILE);
			fieldPositionKeywordProp.load(stream);
			stream = loader.getResourceAsStream(FIELD_POSITION_ATTRIBUTE_PROPERTIES_FILE);
			fieldPositionAttributeProp.load(stream);
			stream = loader.getResourceAsStream(FIELD_VALIDATION_PROPERTIES_FILE);
			fieldValidationProp.load(stream);
    	   // Get the XLS file
           FileInputStream fis = new FileInputStream(xlsInputFile);
           pfs = new POIFSFileSystem(fis);

		}
		catch(Exception e){
			throw e;
		}
		
	}

	/*
	 * This method is to validate that the passed in spreadsheet has correct attributes 
	 * on the header row.  It will also populate a list of header names to be returned 
	 * to the caller.  This does not get the values from the values section of the
	 * spreadsheet.
	 */
    public HashMap validateAndPopulateHeader(String tabName, HashMap databaseHashMap) throws Exception
    {
        // Used to store exceptions from the excel document that was found
        StringBuffer exceptionStringBuffer = new StringBuffer();

    	Workbook workBook = null;
       
       Sheet sheet = null;
       Iterator rows = null;

       // Hashmap of the header row from the spreadsheet, used as the return object.
       HashMap headerRowHashMap = new HashMap();
       
       try
       {
           
           // Get the Workbook
           workBook = new HSSFWorkbook(pfs);
           try{
        	   sheet = workBook.getSheet(tabName);
        	   if(sheet == null){
        		   throw new Exception("ERROR, NO SHEET EXITS");
        	   }
           }
           catch(Exception e){
        	   exceptionStringBuffer.append("FD000 - The " + tabName + " Tab does not exist");
        	   
        	   throw new Exception(exceptionStringBuffer.toString());
           }
           // Get the row iterator
           rows = sheet.rowIterator();
           
           // Used in the First Row Cells Iterator
           Cell nextCell;
           
           // Counts the number of cells in the row
           int firstRowCounter = 0;
           
           // This is the first row check for header information
           if(rows.hasNext()){
        	   Row firstRow = (Row)rows.next();
        	   Iterator firstRowCells = firstRow.cellIterator();
        	   
        	   // This will check the second cell and beyond to make sure it contains values from the database
        	   while(firstRowCells.hasNext()){
        		   firstRowCounter++;
        		   nextCell = (Cell)firstRowCells.next();
        		   boolean noBlankCells = true;
        		   
        		   for(int counterRow = firstRowCounter; counterRow < (nextCell.getColumnIndex() + 1); counterRow++){
                 	   exceptionStringBuffer.append("FD001 - On tab " + tabName + " the first row in column " + columnOfAttributes[firstRowCounter - 1] 
		                				   + " has an empty cell value\n");
                 	   firstRowCounter++;
                 	   noBlankCells = false;
        		   }
        		   
        		   // This will make sure there were no blank cells, if so it will skip over this logic.
        		   if(noBlankCells){
        			   
        			   // This will parse the value from the cell as a string
	        		   String nextCellValue = parseValueFromCell(nextCell).toUpperCase();
	        		   
	        		   // This will validate that that if the field is to be in the spreadsheet it exists
	        		   int fieldValidatorInt = fieldPositionValidator(Integer.toString(firstRowCounter), nextCellValue, fieldPositionAttributeProp);
	        		   
	        		   // The field that is to be at a location in the spreadsheet exits, will add it to the hashmap
	                   if(fieldValidatorInt == 1){
	                	   headerRowHashMap.put(new Long(firstRowCounter - 1), nextCellValue);   
	                   }
	                   
	                   // The field does not need to be at the specific location in the spreadsheet, now checking agains database.
	                   else if (fieldValidatorInt == 0){
	                      
	                	   if(databaseHashMap.containsKey(nextCellValue)){
                        	   headerRowHashMap.put(new Long(firstRowCounter - 1), nextCellValue);
	                	   }
	                	   else{
	                       	   if(firstRowCounter > 255){
		                		   exceptionStringBuffer.append("FD002 - On tab " + tabName + " the first row in column number " + firstRowCounter + " containing '" + 
			                       			nextCellValue + "' is not found in the database\n");
	                       	   }
	                       	   else{
		                		   exceptionStringBuffer.append("FD002 - On tab " + tabName + " the first row in column " + columnOfAttributes[firstRowCounter - 1] + 
		                				   " containing '" + nextCellValue + "' is not found in the database\n");
	                       	   }
	                       }
	
	                   }
	                   
	                   // This will put out an error saying that the header row is missing a column validation
	                   else if(fieldValidatorInt == 2){
	                  	   exceptionStringBuffer.append("FD003 - On tab " + tabName + " the first row in column " + columnOfAttributes[firstRowCounter - 1] 
                				   + " containing '" + nextCellValue + "' does not pass validation due to invalid text'\n");
	                   }
	                   
	        	   }
        	   }        	 
           }
           if(exceptionStringBuffer.length() > 1){
        	   throw new Exception(exceptionStringBuffer.toString());
           }
       }
         
       catch(Exception e){
        	   //TODO: Add Exception Handling
    	   throw e;
       }
       
       return headerRowHashMap;
       
    }


    // TODO: WORK ON THIS FOR KEYWORDS
	/*
	 * This method is to validate that the passed in spreadsheet has correct attributes 
	 * on the header row.  It will also populate a list of header names to be returned 
	 * to the caller.  This does not get the values from the values section of the
	 * spreadsheet.
	 */
    public HashMap validateAndPopulateHeaderForKeywords(String tabName, HashMap databaseHashMap) throws Exception
    {
        // Used to store exceptions from the excel document that was found
        StringBuffer exceptionStringBuffer = new StringBuffer();
        Workbook workBook = null;
       
       Sheet sheet = null;
       Iterator rows = null;

       // Hashmap of the header row from the spreadsheet, used as the return object.
       HashMap headerRowHashMap = new HashMap();
       
       try
       {
           // Get the Workbook
           workBook = new HSSFWorkbook(pfs);
           try{
        	   sheet = workBook.getSheet(tabName);
        	   if(sheet == null){
        		   throw new Exception("ERROR, NO SHEET EXITS");
        	   }
           }
           catch(Exception e){
        	   exceptionStringBuffer.append("FD004 - The " + tabName + " Tab does not exist");
        	   
        	   throw new Exception(exceptionStringBuffer.toString());
           }
           // Get the row iterator
           rows = sheet.rowIterator();
           
           // Used in the First Row Cells Iterator
           Cell nextCell;
           
           // Counts the number of cells in the row
           int firstRowCounter = 0;
           
           // This is the first row check for header information
           if(rows.hasNext()){
        	   Row firstRow = (Row)rows.next();
        	   Iterator firstRowCells = firstRow.cellIterator();
        	   
        	   boolean newKeyword = true;
    		   String previousCellValue = "";
        	   
        	   // This will check the second cell and beyond to make sure it contains values from the database
        	   while(firstRowCells.hasNext()){
        		   firstRowCounter++;
        		   nextCell = (Cell)firstRowCells.next();

    			   // This will parse the value from the cell as a string
        		   String nextCellValue = parseValueFromCell(nextCell).toUpperCase();

        		   // This will validate that that if the field is to be in the spreadsheet it exists
        		   int fieldValidatorInt = fieldPositionValidator(Integer.toString(firstRowCounter), nextCellValue, fieldPositionKeywordProp);

        		   if(nextCellValue.equalsIgnoreCase("*")){
        			   newKeyword = true;
        		   }
        		   else if(!newKeyword){
        			   headerRowHashMap.put(new Long(firstRowCounter - 1), previousCellValue);
        		   }

        		   else if(fieldValidatorInt == 1){
                	   headerRowHashMap.put(new Long(firstRowCounter - 1), nextCellValue);
                	   if(newKeyword){
                		   headerRowHashMap.put(new Long(firstRowCounter - 2), "*_" + nextCellValue);
                		   previousCellValue = nextCellValue;
                	   }
        			   newKeyword = false;
        		   }
                   
                   // The field does not need to be at the specific location in the spreadsheet, now checking against database.
                   else if (fieldValidatorInt == 0){
                      
                	   if(databaseHashMap.containsKey(nextCellValue)){
                    	   headerRowHashMap.put(new Long(firstRowCounter - 1), nextCellValue);
	        			   previousCellValue = nextCellValue;
	        			   headerRowHashMap.put(new Long(firstRowCounter - 2), "*_" + nextCellValue);
                	   }
                	   else{
                       	   if(firstRowCounter > 255){
	                		   exceptionStringBuffer.append("FD006 - On tab " + tabName + " the first row in column number " + firstRowCounter + " containing '" + 
		                       			nextCellValue + "' is not found in the database\n");
                       	   }
                       	   else{
	                		   exceptionStringBuffer.append("FD006 - On tab " + tabName + " the first row in column " + columnOfAttributes[firstRowCounter - 1] + 
	                				   " containing '" + nextCellValue + "' is not found in the database\n");
                       	   }
                       }

                	   newKeyword = false;
                   }
                   
                   // This will put out an error saying that the header row is missing a column validation
                   else if(fieldValidatorInt == 2){
                  	   exceptionStringBuffer.append("FD008 - On tab " + tabName + " the first row in column " + columnOfAttributes[firstRowCounter - 1] 
	                				   + " containing '" + nextCellValue + "' does not pass validation due to invalid text\n");
        			   newKeyword = false;
                   }
        	   }
        	   for(int counter = 0; counter < 50; counter++){
            	   firstRowCounter++;
            	   headerRowHashMap.put(new Long(firstRowCounter - 1), previousCellValue);
        	   }
    	   }        	 
           if(exceptionStringBuffer.length() > 1){
        	   throw new Exception(exceptionStringBuffer.toString());
           }
       }
         
       catch(Exception e){
        	   //TODO: Add Exception Handling
    	   throw e;
       }
       
       return headerRowHashMap;
       
    }

    
    
    /**
     * This method will validate if each field 
     * 
     * The return status will be as follows:
     * 0 = The cell does need to check for existance in database because it is in the correct position
     * 1 = The cell has position validation on it and it does pass the database check.
     * 2 = The cell has position validation on it and it does not pass the database check.
     * 
     * Status 0 and 1 are ok, 2 is bad.
     * @return
     */
    private int fieldPositionValidator(String columnIndex, String fieldToValidate, Properties fieldPositionProp){
    	int returnStatus = 0;
    	String positionPropString = fieldPositionProp.getProperty(columnIndex);
    	
    	// Check if the field matches the position
    	if(positionPropString != null && !positionPropString.equalsIgnoreCase("")){
    		if(!positionPropString.equals(fieldToValidate)){
    			// There is position validation and this position does not contain the correct value
    			returnStatus = 2;
    		}
    		else{
       			// There is position validation and this position does contain the correct value
   				returnStatus = 1;
    		}
    	}
    	return returnStatus;
    }

    
    /**
     * This method will validate if each field 
     * 
     * The return status will be as follows:
     * 0 = The cell did pass regular express validation
     * 1 = The cell did NOT pass regular express validation
     * 
     * Status 0 is ok, 1 is bad.
     */
    private int fieldExpressionValidator(String headerToValidate, String fieldToValidate, Properties fieldValueProp, String businessUnit){
    	int returnStatus = 0;

    	if(headerToValidate != null && !headerToValidate.equalsIgnoreCase("")){
    		// if the header contains a '*_' then it is a delete record.
	    	if(headerToValidate.contains("*_")){
	    		headerToValidate = "*";
	    	}
	    	
	   	
	    	// Get the header to validate for "ALL"
	    	String valueAllPropString = fieldValueProp.getProperty("ALL_" + headerToValidate);
	    	String valueBusinessUnitPropString = fieldValueProp.getProperty(businessUnit + "_" + headerToValidate);
	
	    	// Check if the field matches the regular expression
			if(valueAllPropString != null && !valueAllPropString.equalsIgnoreCase("") && !fieldToValidate.matches(valueAllPropString)){
				returnStatus = 1;    				
			}
			else if(valueBusinessUnitPropString != null && !valueBusinessUnitPropString.equalsIgnoreCase("") && !fieldToValidate.matches(valueBusinessUnitPropString)) {
				// There is position validation and this position does contain the correct value
				returnStatus = 1;
			}
    	}
    	
    	return returnStatus;
    }

    
    /**
     * This method will parse the cell on the parameter and will return a string representation
     * of the value.
     * @param inCell
     * @return
     */
    private String parseValueFromCell(Cell inCell){
    	String returnString = "";
    	
    	inCell.setCellType(Cell.CELL_TYPE_STRING);
    	// If the cell is a string type.
        if(inCell.getCellType() == Cell.CELL_TYPE_STRING){
        	returnString = inCell.getStringCellValue().trim();
        }
    	return returnString;
    }
    
    /**
     * This method will validate and populate the values that are in the xls spreadsheet.  This
     * is only the values in the spreadsheet, does not include validating or retrieving the 
     * headers from the xls.  This will get all the information needed to do the insert.  This
     * includes getting the key for the item id.
     * 
     * @return
     */
    
    public ArrayList validateAndPopulateValues(String tabName, HashMap headerIn, HashMap attributesIn, HashMap itemIDIn, String businessUnit) throws Exception{
     
        Workbook workBook = null;
        Sheet sheet = null;
        Iterator rows = null;
    	ArrayList returnArrayList = new ArrayList();        
    	StringBuffer exceptionStringBuffer = new StringBuffer();
    	String columnBValue = "";
          
    	try{
            // Get the Workbook
            workBook = new HSSFWorkbook (pfs);
            sheet = workBook.getSheet(tabName);

            rows = sheet.rowIterator();
            if(rows.hasNext()){
	            rows.next(); // Skip first row since it is the header row
	            
		    	Row nextRow;
		        Iterator nextRowCells;
		        
		        int rowCounter = 1;
		        String cellValue = "";
		        
		        while(rows.hasNext()){
		        	rowCounter++;
		        	
		        	nextRow = (Row)rows.next();
			     	nextRowCells = nextRow.cellIterator();
			     	Cell nextCell;
			     	   
			     	String itemNo = "";
			     	ValueList newValueList = new ValueList();
			     	
			     	// Get Column A, which should be the Item Number
			     	nextCell = (Cell)nextRowCells.next();
	
			     		// If the first column is empty, then throw an error
		     		if(nextCell.getColumnIndex() != 0){
				     		exceptionStringBuffer.append("FD009 - Row " + rowCounter + " in tab "
				     				+ tabName + " does not have a value in the item column\n");
		     		}
		     		else{
		     			itemNo = parseValueFromCell(nextCell);
		     		}
		
		     		ArrayList valueArrayList = new ArrayList();
		     		boolean checkedItemNo = false;
			     	while(nextRowCells.hasNext()){
			     		nextCell = (Cell)nextRowCells.next();
			     		cellValue = parseValueFromCell(nextCell);
				     	// Get Column B, which should be the Item Type
				     	if(nextCell.getColumnIndex() == 1){
				     		if("I".equalsIgnoreCase(cellValue) || "G".equalsIgnoreCase(cellValue)){
				     			columnBValue = cellValue.toUpperCase();
				     			// Gets the first cell which should be ITEM_NO
					     	}
					     	else{
					     		exceptionStringBuffer.append("FD010 - Column B Row " + rowCounter + " in "
					     				+ " tab " + tabName + " Should be contain text with either a 'I' or a 'G' in it\n");
					     	}
				     	}
				     	else{
	
				     		if(!checkedItemNo){
				     			checkedItemNo = true;
					     		if(itemIDIn.containsKey(itemNo + "_" + columnBValue)){
					     			newValueList.setItem(itemNo); // Gets the first cell which should be ITEM_NO
					     			newValueList.setItemId(((Long)itemIDIn.get(itemNo + "_" + columnBValue)).intValue());
					     		}
					     		else{
						     		exceptionStringBuffer.append("FD011 - Row " + rowCounter + " in "
						     				+ "tab " + tabName + " contains a value that is not a current item. Row contains: " 
						     				+ itemNo + "\n");
					     		}
				     		}
				     		Values newValue = new Values();		     		
				     		String headerName = (String)headerIn.get(new Long(nextCell.getColumnIndex()));
				     		if(cellValue.length() == 0){
				     			newValue.setAction("EMPTY");
				     			// The cell was left empty and nothing needs to be updated
				     		}
				     		// Set the Cell Action based on the cell value from the cell and leave the value null
				     		else if(cellValue.equals(DELETE_TOKEN)){
				     			newValue.setAction("DELETE");
				     		}
				     		// Set the Cell Action and Value;
				     		else{
				     			if(fieldExpressionValidator(headerName, cellValue, fieldValidationProp, businessUnit) == 0){
					     			newValue.setAction("ADD");
					     			newValue.setValue(cellValue);
				     			}
				     			else{
						     		exceptionStringBuffer.append("FD012 - Row " + rowCounter + " in "
						     				+ " tab " + tabName + " in column '" + columnOfAttributes[nextCell.getColumnIndex()] 
						     				+ "' with the value of '" + cellValue + "' did not pass field validation\n");
				     			}
				     		}
				     		
	
				     		if(headerName != null && !headerName.equalsIgnoreCase("")){
				     			if(headerName.length() > 2 && headerName.substring(0, 2).equalsIgnoreCase("*_")){
				     				headerName = headerName.substring(2);
				     				
				     			}
				     			newValue.setItemAttributeTypeId(((Long)(attributesIn.get(headerName))).longValue());
				     			newValue.setAttribute(headerName);
				     		}
				     		else{
					     		exceptionStringBuffer.append("FD013 - Row " + rowCounter + " column "
					     				+ columnOfAttributes[nextCell.getColumnIndex()] + " in tab "
					     				+ tabName + " has a value that does not have a header\n");
				     		}
				     		valueArrayList.add(newValue);
				     		newValueList.setValues(valueArrayList);
				     	}
	
	
			     	}
			     	newValueList.setType(columnBValue);
			     	returnArrayList.add(newValueList);
	
		        }
		        
		        if(rowCounter == 0){
		        	exceptionStringBuffer.append("FD014 - The tab " + tabName + " does not "
		        			+ "have any rows to insert into the database.");
		        }
		        
		        if(exceptionStringBuffer.length() > 1){
		        	throw new Exception(exceptionStringBuffer.toString());
		        }
	    	}
    	}
    	
    	catch(Exception e){
    		throw e;
    	}
    	
    	return returnArrayList;
    }

}
