package com.kohler.importer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DatabaseDriver {

	private Logger logger;

	private static final String PUNI = "PUNI";

	private static final String PCEN = "PCEN";

	private static String SELECT_ALL_ITEM_ID = "";

	private static String SELECT_ITEM_ATTRIBUTE_TYPES = "";

	private static String SELECT_KEYWORD_TYPES = "";
	
	private static String SELECT_ITEM_ATTRIBUTE_VALUES = "";

	private static String SELECT_ITEM_ATTRIBUTE_VALUE_ID = "";

	private static String SELECT_ITEM_ATTRIBUTE_ID = "";

	private static String UPDATE_ITEM_ATTRIBUTE_VALUES = "";

	private static String INSERT_ITEM_ATTRIBUTE_VALUES = "";

	private static String INSERT_ITEM_ATTRIBUTES = "";

	private static String SELECT_ITEMS_TO_REMOVE = "";

	private static String REMOVE_ITEM_ATTRIBUTES = "";

	private static String REMOVE_ITEM_ATTRIBUTE_VALUES = "";
	
	private static String INSERT_KEYWORD_PHRASES = "";
	
	private static String SELECT_KEYWORD_PHRASES_TO_REMOVE = "";
	
	private static String REMOVE_KEYWORD_PHRASES = "";
	
	private static String SELECT_PHRASE_ID = "";
	
	private static String ATTRIBUTE_TYPE_UC = "";
	
	private static String ATTRIBUTE_TYPE_ID = "";
	
	private static String KEYWORD_TYPE_UC = "";
	
	private static String KEYWORD_TYPE_ID = "";
	
	private static Connection connection = null;
	
	private static String userName = "";
	
	private static String dbType = "";

	private static String dbShortName = "";
	
	private static String connectString = "";
	
	private static String user = "";
	
	private static String password = "";
	
	public DatabaseDriver(String connectStringIn, String userIn, String passwordIn,
			String instanceNameIn, String dbTypeIn) throws Exception {

		logger = Logger.getLogger("debug");
		connectString = connectStringIn;
		user = userIn;
		password = passwordIn;
		userName = System.getProperty("user.name").toUpperCase();
		String instanceNameForDB = "";
		dbShortName = "";
		dbType = dbTypeIn;
		instanceNameForDB = instanceNameIn + ".";
		
		if (instanceNameIn != null && instanceNameIn.length() > 7) {

			dbShortName = instanceNameIn.substring(4, 8);
		}

		// This setup the DB Statements for the PUNI Section
		if (dbType.equalsIgnoreCase(PUNI)) {
			SELECT_ALL_ITEM_ID = "SELECT ITEM_ID, ITEM, ITEM_KIND FROM "
					+ instanceNameForDB + "ITEMS";

			// Attributes:
			SELECT_ITEM_ATTRIBUTE_TYPES = "SELECT ITEM_ATTRIBUTE_TYPE_ID, ITEM_ATTRIBUTE_TYPE_UC FROM "
					+ instanceNameForDB + "ITEM_ATTRIBUTE_TYPES";
			SELECT_ITEM_ATTRIBUTE_VALUES = "SELECT IAV.ITEM_ATTRIBUTE_VALUE_ID, IAV.ITEM_ATTRIBUTE_ID, IAV.ITEM_ATTRIBUTE FROM "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES IAV, "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES IA, "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_TYPES IAT WHERE IAV.ITEM_ATTRIBUTE_ID = IA.ITEM_ATTRIBUTE_ID AND IA.ITEM_ATTRIBUTE_TYPE_ID = IAT.ITEM_ATTRIBUTE_TYPE_ID AND IA.ITEM_ID = ? AND IAT.ITEM_ATTRIBUTE_TYPE_UC = ?";
			SELECT_ITEM_ATTRIBUTE_VALUE_ID = "SELECT " + instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES_SEQ.NEXTVAL FROM DUAL";
			SELECT_ITEM_ATTRIBUTE_ID = "SELECT " + instanceNameForDB
					+ "ITEM_ATTRIBUTES_SEQ.NEXTVAL FROM DUAL";
			UPDATE_ITEM_ATTRIBUTE_VALUES = "UPDATE "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES SET ITEM_ATTRIBUTE = ?, TIMESTAMP = CURRENT_TIMESTAMP, UPDATED_BY = '" 
					+ userName + dbShortName + "' WHERE ITEM_ATTRIBUTE_VALUE_ID = ?";
			INSERT_ITEM_ATTRIBUTE_VALUES = "INSERT INTO "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES(Item_Attribute_Value_ID, Item_Attribute_ID, Item_Attribute, Item_Attribute_Version, Display_Order, Created_By, Created_Date, Updated_by, Timestamp) VALUES (?, ?, ?, -1, 0, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP)\n";
			INSERT_ITEM_ATTRIBUTES = "INSERT INTO "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES(ITEM_ATTRIBUTE_ID, ITEM_ATTRIBUTE_TYPE_ID, ITEM_ID, ITEM_ATTRIBUTE_VERSION, CREATED_BY, CREATED_DATE, UPDATED_BY, TIMESTAMP) VALUES (?, ?, ?, -1, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP)";
			SELECT_ITEMS_TO_REMOVE = "SELECT IA.ITEM_ATTRIBUTE_ID, IAV.ITEM_ATTRIBUTE FROM "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES IA, "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_TYPES IAT, "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES IAV WHERE IA.ITEM_ATTRIBUTE_TYPE_ID = IAT.ITEM_ATTRIBUTE_TYPE_ID AND IA.ITEM_ID = ? AND IAT.ITEM_ATTRIBUTE_TYPE_UC = ? AND IAV.ITEM_ATTRIBUTE_ID = IA.ITEM_ATTRIBUTE_ID";
			REMOVE_ITEM_ATTRIBUTES = "DELETE FROM " + instanceNameForDB
					+ "ITEM_ATTRIBUTES WHERE ITEM_ATTRIBUTE_ID = ?";
			REMOVE_ITEM_ATTRIBUTE_VALUES = "DELETE FROM " + instanceNameForDB
					+ "ITEM_ATTRIBUTE_VALUES WHERE ITEM_ATTRIBUTE_ID = ?";
			
			ATTRIBUTE_TYPE_UC = "Item_Attribute_Type_UC";
			ATTRIBUTE_TYPE_ID = "Item_Attribute_Type_ID";

			// Keywords:
			
			SELECT_KEYWORD_TYPES = "SELECT KEYWORD_TYPE_ID, KEYWORD_TYPE_UC FROM "
					+ instanceNameForDB + "KEYWORD_TYPES";
			SELECT_PHRASE_ID = "SELECT " + instanceNameForDB
					+ "KEYWORD_PHRASES_SEQ.NEXTVAL FROM DUAL";
			INSERT_KEYWORD_PHRASES = "INSERT INTO "
					+ instanceNameForDB
					+ "KEYWORD_PHRASES(KEYWORD_PHRASE_ID, KEYWORD_TYPE_ID, ITEM_ID, KEYWORD_PHRASE, CREATED_BY, CREATED_DATE, UPDATED_BY, TIMESTAMP) VALUES (?, ?, ?, ?, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP)";
			SELECT_KEYWORD_PHRASES_TO_REMOVE = "SELECT KP.KEYWORD_PHRASE_ID, KP.KEYWORD_PHRASE FROM "
					+ instanceNameForDB
					+ "KEYWORD_PHRASES KP, "
					+ instanceNameForDB
					+ "KEYWORD_TYPES KT WHERE KP.KEYWORD_TYPE_ID = KT.KEYWORD_TYPE_ID AND KP.ITEM_ID = ? AND KT.KEYWORD_TYPE_UC = ?";
			REMOVE_KEYWORD_PHRASES = "DELETE FROM " + instanceNameForDB
					+ "KEYWORD_PHRASES WHERE KEYWORD_PHRASE_ID = ?";
			
			KEYWORD_TYPE_UC = "Keyword_Type_UC";
			KEYWORD_TYPE_ID = "Keyword_Type_ID";
		}
		// This setup the DB Statements for the PCEN Section
		else {

			// Attributes
			
			SELECT_ITEM_ATTRIBUTE_VALUES = "SELECT IA.ITEM_ATTRIBUTE_ID, AT.ATTRIBUTE_TYPE_ID, IA.VALUE FROM "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES IA, "
					+ instanceNameForDB
					+ "ATTRIBUTE_TYPES AT WHERE IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID AND IA.ITEM_INFO_ID = ? AND AT.ATTRIBUTE_TYPE_UC = ?";
			UPDATE_ITEM_ATTRIBUTE_VALUES = "UPDATE "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES SET VALUE = ?, TIMESTAMP = CURRENT_TIMESTAMP, UPDATED_BY = '" 
					+ userName + dbShortName + "' WHERE ITEM_ATTRIBUTE_ID = ?";
			INSERT_ITEM_ATTRIBUTES = "INSERT INTO "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES(ITEM_ATTRIBUTE_ID, ATTRIBUTE_TYPE_ID, ITEM_INFO_ID, VALUE, CREATED_BY, CREATED_DATE, UPDATED_BY, TIMESTAMP) VALUES (?, ?, ?, ?, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP)";
			SELECT_ITEMS_TO_REMOVE = "SELECT IA.ITEM_ATTRIBUTE_ID, IA.VALUE FROM "
					+ instanceNameForDB
					+ "ITEM_ATTRIBUTES IA, "
					+ instanceNameForDB
					+ "ATTRIBUTE_TYPES AT WHERE IA.ATTRIBUTE_TYPE_ID = AT.ATTRIBUTE_TYPE_ID AND IA.ITEM_INFO_ID = ? AND AT.ATTRIBUTE_TYPE_UC = ?";
			REMOVE_ITEM_ATTRIBUTES = "DELETE FROM " + instanceNameForDB
					+ "ITEM_ATTRIBUTES WHERE ITEM_ATTRIBUTE_ID = ?";
			SELECT_ALL_ITEM_ID = "SELECT ITEM_INFO_ID, ITEM_NO, ITEM_TYPE FROM "
					+ instanceNameForDB + "ITEM_INFO";
			SELECT_ITEM_ATTRIBUTE_TYPES = "SELECT ATTRIBUTE_TYPE_ID, ATTRIBUTE_TYPE_UC FROM "
					+ instanceNameForDB + "ATTRIBUTE_TYPES";
			SELECT_ITEM_ATTRIBUTE_VALUE_ID = "SELECT " + instanceNameForDB
					+ "ITEM_ATTRIBUTES_SEQ.NEXTVAL FROM DUAL";
			SELECT_ITEM_ATTRIBUTE_ID = "SELECT " + instanceNameForDB
					+ "ITEM_ATTRIBUTES_SEQ.NEXTVAL FROM DUAL";
			
			// Keywords

			SELECT_KEYWORD_TYPES = "SELECT KEYWORD_TYPE_ID, KEYWORD_TYPE_UC FROM "
					+ instanceNameForDB + "KEYWORD_TYPES";
			SELECT_PHRASE_ID = "SELECT " + instanceNameForDB
					+ "KEYWORD_PHRASES_SEQ.NEXTVAL FROM DUAL";
			INSERT_KEYWORD_PHRASES = "INSERT INTO "
					+ instanceNameForDB
					+ "KEYWORD_PHRASES(PHRASE_ID, KEYWORD_TYPE_ID, ITEM_INFO_ID, PHRASE, CREATED_BY, CREATED_DATE, UPDATED_BY, TIMESTAMP) VALUES (?, ?, ?, ?, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP, '" 
					+ userName + dbShortName + "', CURRENT_TIMESTAMP)";
			SELECT_KEYWORD_PHRASES_TO_REMOVE = "SELECT KP.PHRASE_ID, KP.PHRASE FROM "
					+ instanceNameForDB
					+ "KEYWORD_PHRASES KP, "
					+ instanceNameForDB
					+ "KEYWORD_TYPES KT WHERE KP.KEYWORD_TYPE_ID = KT.KEYWORD_TYPE_ID AND KP.ITEM_INFO_ID = ? AND KT.KEYWORD_TYPE_UC = ?";
			REMOVE_KEYWORD_PHRASES = "DELETE FROM " + instanceNameForDB
					+ "KEYWORD_PHRASES WHERE PHRASE_ID = ?";
			
			ATTRIBUTE_TYPE_UC = "Attribute_Type_UC";
			ATTRIBUTE_TYPE_ID = "Attribute_Type_ID";
			
			KEYWORD_TYPE_UC = "Keyword_Type_UC";
			KEYWORD_TYPE_ID = "Keyword_Type_ID";
		}
		
		createConnection();

	}

	/**
	 * This method is to close any open connections
	 * 
	 * @throws Exception
	 */
	public void close() throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	/**
	 * This method will retrieve the Item Attribute Types from the
	 * Attributes table on PUNI and PCEN
	 * 
	 * @return
	 */
	public HashMap retrieveItemAttributeTypes()
			throws Exception {
		HashMap returnHashMap = new HashMap();

		ResultSet resultSet = null;

		Statement statement = connection.createStatement();

		resultSet = statement.executeQuery(SELECT_ITEM_ATTRIBUTE_TYPES);

		while (resultSet.next()) {
			returnHashMap.put(resultSet.getString(ATTRIBUTE_TYPE_UC),
					new Long(resultSet.getLong(ATTRIBUTE_TYPE_ID)));
		}
		statement.close();
		resultSet.close();
		return returnHashMap;
	}

	/**
	 * This method will get all the item numbers and items from the database.
	 * 
	 * @return
	 */
	public HashMap retrieveItemNumbers() throws Exception {

		HashMap returnHashMap = new HashMap();

		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(SELECT_ALL_ITEM_ID);
		while (resultSet.next()) {
			// Puts the field "ITEM" as key and "ITEM_ID" as value
			returnHashMap.put(resultSet.getString(2) + "_" + resultSet.getString(3), new Long(resultSet.getLong(1)));
		}
		statement.close();
		resultSet.close();
		return returnHashMap;
	}

	/**
	 * This method will retrieve the Keywords Types from the
	 * Keywords table on PUNI or PCEN
	 * 
	 * @return
	 */
	public HashMap retrieveKeywordTypes()
			throws Exception {
		HashMap returnHashMap = new HashMap();

		ResultSet resultSet = null;

		Statement statement = connection.createStatement();

		resultSet = statement.executeQuery(SELECT_KEYWORD_TYPES);

		while (resultSet.next()) {

			returnHashMap.put(resultSet.getString(KEYWORD_TYPE_UC),
					new Long(resultSet.getLong(KEYWORD_TYPE_ID)));
		}
		statement.close();
		resultSet.close();
		return returnHashMap;
	}
	
	/**
	 * This method will process the database for the Item Attributes Value table
	 * on PUNI or PCEN
	 * 
	 * @param attributesStatements
	 * @return
	 */
	public void processItemAttributeValuesStatements(
			ArrayList attributesStatements) throws Exception {

		try{
			Iterator attributeStatementIterator = attributesStatements
					.iterator();
			ValueList nextValueList;
			Values values;
			long itemNo;
	
			while (attributeStatementIterator.hasNext()) {
	
				nextValueList = (ValueList)attributeStatementIterator.next();
				itemNo = nextValueList.getItemId();
				ArrayList nextValuesArrayList = nextValueList.getValues();

				if (nextValuesArrayList != null && !nextValuesArrayList.isEmpty()) {
					for (int counter = 0; counter < nextValuesArrayList.size(); counter++) {
						values = (Values)nextValuesArrayList.get(counter);
	
						if(values.getAction().equals("EMPTY")){
							// DO NOTHING
						}
						else if (values.getAction().equals("ADD")) {
	
							PreparedStatement findTable = connection
									.prepareStatement(SELECT_ITEM_ATTRIBUTE_VALUES);
	
							findTable.setLong(1, itemNo);
							findTable.setString(2, values.getAttribute());
							ResultSet resultSet = findTable.executeQuery();
							
							if (resultSet.next()) {
	
								PreparedStatement update = connection
										.prepareStatement(UPDATE_ITEM_ATTRIBUTE_VALUES);
								update.setString(1, values.getValue());
								update.setLong(2, resultSet.getLong(1));
								update.executeUpdate();

								connection.commit();
								update.close();
								
								if(dbType.equalsIgnoreCase(PUNI)){
									logger.info("UPDATE for "
											+ dbShortName 
											+ " into ITEM_ATTRIBUTE_VALUES for Item '"
											+ nextValueList.getItem()
											+ "' with type of '"
											+ nextValueList.getType()											
											+ "' overwriting the attribute '"
											+ values.getAttribute()
											+ "' that had exsiting value '"
											+ resultSet.getString(3)
											+ "' with new value '"
											+ values.getValue()
											+ "'");
								}
								else{
									logger.info("UPDATE for "
											+ dbShortName 
											+ " into ITEM_ATTRIBUTES for Item '"
											+ nextValueList.getItem()
											+ "' with type of '"
											+ nextValueList.getType()											
											+ "' overwriting the attribute '"
											+ values.getAttribute()
											+ "' that had exsiting value '"
											+ resultSet.getString(3)
											+ "' with new value '"
											+ values.getValue()
											+ "'");
								}
							} 
							else {
								PreparedStatement selectID = connection
										.prepareStatement(SELECT_ITEM_ATTRIBUTE_ID);
								ResultSet rs = selectID.executeQuery();
								rs.next();
	
								long itemAttributeId = rs.getLong(1);
								rs.close();
								selectID.close();
								PreparedStatement insert = connection
										.prepareStatement(INSERT_ITEM_ATTRIBUTES);

								if(dbType.equalsIgnoreCase(PUNI)){
									insert.setLong(1, itemAttributeId);
									insert.setLong(2, values.getItemAttributeTypeId());
									insert.setLong(3, itemNo);
								}
								else{
									insert.setLong(1, itemAttributeId);
									insert.setLong(2, values.getItemAttributeTypeId());
									insert.setLong(3, itemNo);
									insert.setString(4, values.getValue());
								}
	
								insert.executeUpdate();
								insert.close();
								if(dbType.equalsIgnoreCase(PUNI)){
									PreparedStatement selectID2 = connection.prepareStatement(SELECT_ITEM_ATTRIBUTE_VALUE_ID);
									ResultSet rs2 = selectID2.executeQuery();
									rs2.next();
									PreparedStatement insert2 = connection.prepareStatement(INSERT_ITEM_ATTRIBUTE_VALUES);
									insert2.setLong(1, rs2.getLong(1));
									insert2.setLong(2, itemAttributeId);
									insert2.setString(3, values.getValue());
									insert2.executeUpdate();
									selectID2.close();
									insert2.close();
									rs2.close();
								}
								connection.commit();
								
								if(dbType.equalsIgnoreCase(PCEN)){
									logger.info("INSERT for "
											+ dbShortName 
											+ " into ITEM_ATTRIBUTES for Item '"
											+ nextValueList.getItem()
											+ "' with type of '"
											+ nextValueList.getType()
											+ "' inserting the attribute '"
											+ values.getAttribute()
											+ "' with value of '"
											+ values.getValue()
											+ "'");
								}
								else{
									logger.info("INSERT for "
											+ dbShortName 
											+ " into ITEM_ATTRIBUTE_VALUES for Item '" 
											+ nextValueList.getItem() 
											+ "' with type of '"
											+ nextValueList.getType()
											+ "' inserting the attribute '"
											+ values.getAttribute()
											+ "' with value of '"  
											+ values.getValue() 
											+ "'");

								}
								
							}
							findTable.close();
							resultSet.close();
						} else if (values.getAction().equals("DELETE")) {
	
							PreparedStatement select = connection
									.prepareStatement(SELECT_ITEMS_TO_REMOVE);
							select.setLong(1, itemNo);
							select.setString(2, values.getAttribute());
	
							ResultSet resultSet = select.executeQuery();
	
							if (resultSet.next()) {
	
								PreparedStatement ps = connection
										.prepareStatement(REMOVE_ITEM_ATTRIBUTES);
								ps.setLong(1, resultSet.getLong(1));
								ResultSet rs = ps.executeQuery();
								ps.close();
								if(dbType.equalsIgnoreCase(PUNI)){
									ps = connection
											.prepareStatement(REMOVE_ITEM_ATTRIBUTE_VALUES);
									ps.setLong(1, resultSet.getLong(1));
									ResultSet rs2 = ps.executeQuery();
									rs2.close();
									ps.close();
								}
								
								connection.commit();
								rs.close();
								
								if(dbType.equalsIgnoreCase(PUNI)){
									logger.info("DELETE for "
											+ dbShortName
											+ " from ITEM_ATTRIBUTE_VALUES for Item '"
											+ nextValueList.getItem()
											+ "' with type of '"
											+ nextValueList.getType()
											+ "' deleting the attribute '"
											+ values.getAttribute()
											+ "' with the value of '"
											+ resultSet.getString(2) + "'");
								}
								else
								{
									logger.info("DELETE for "
											+ dbShortName 
											+ " from ITEM_ATTRIBUTES for Item '"
											+ nextValueList.getItem()
											+ "' with type of '"
											+ nextValueList.getType()
											+ "' deleting the attribute '"
											+ values.getAttribute()
											+ "' with the value of '"
											+ resultSet.getString(2) + "'");
								}
							} 
							else {
							}
							select.close();
							resultSet.close();
						}
					}
				}
			}
		}
		catch(Exception e){
			logger.error(e.toString());
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This method will process the database for the Item Keywords table
	 * on PUNI or PCEN
	 * 
	 * @param attributesStatements
	 * @return
	 */
	public void processKeywordsStatements(
			ArrayList keywordStatements) throws Exception {

		try{
			Iterator keywordStatementIterator = keywordStatements
					.iterator();
			ValueList nextValueList;
			Values values;
			long itemNo;
	
			while (keywordStatementIterator.hasNext()) {
	
				nextValueList = (ValueList)keywordStatementIterator.next();
				itemNo = nextValueList.getItemId();
				ArrayList nextValuesArrayList = nextValueList.getValues();
				if (nextValuesArrayList != null && !nextValuesArrayList.isEmpty()) {
					for (int counter = 0; counter < nextValuesArrayList.size(); counter++) {
						values = (Values)nextValuesArrayList.get(counter);
	
						if(values.getAction().equals("EMPTY")){
							// DO NOTHING
						}
						else if (values.getAction().equals("ADD")) {
	
							PreparedStatement selectID = connection
									.prepareStatement(SELECT_PHRASE_ID);
							ResultSet rs = selectID.executeQuery();

							rs.next();
							
							long keywordPhraseId = rs.getLong(1);
							rs.close();
							PreparedStatement insert = connection
									.prepareStatement(INSERT_KEYWORD_PHRASES);

							insert.setLong(1, keywordPhraseId);
							insert.setLong(2, values.getItemAttributeTypeId());
							insert.setLong(3, itemNo);
							insert.setString(4, values.getValue());

							insert.executeUpdate();
							insert.close();

							selectID.close();
							logger.info("INSERT for "
									+ dbShortName 
									+ " into KEYWORD_PHRASES for Item '"
									+ nextValueList.getItem()
									+ "' with type of '"
									+ nextValueList.getType()
									+ "' inserting the PHRASE '"
									+ values.getAttribute()
									+ "' with value of '"
									+ values.getValue()
									+ "'");
						} else if (values.getAction().equals("DELETE")) {
	
							PreparedStatement select = connection
									.prepareStatement(SELECT_KEYWORD_PHRASES_TO_REMOVE);
							select.setLong(1, itemNo);
							select.setString(2, values.getAttribute());
	
							ResultSet resultSet = select.executeQuery();
							
							while (resultSet.next()) {
	
								PreparedStatement ps = connection
										.prepareStatement(REMOVE_KEYWORD_PHRASES);
								ps.setLong(1, resultSet.getLong(1));
								ResultSet rs = ps.executeQuery();
	
								ps.close();
								if(dbType.equalsIgnoreCase(PUNI)){
									PreparedStatement ps2 = connection
											.prepareStatement(REMOVE_KEYWORD_PHRASES);
									ps2.setLong(1, resultSet.getLong(1));
									ResultSet rs2 = ps2.executeQuery();
									rs2.close();
									ps2.close();
								}
								
								connection.commit();
								rs.close();
								
								logger.info("DELETE for "
										+ dbShortName
										+ " from KEYWORD_PHRASES for Item '"
										+ nextValueList.getItem()
										+ "' with type of '"
										+ nextValueList.getType()
										+ "' deleting the PHRASE '"
										+ values.getAttribute()
										+ "' with the value of '"
										+ resultSet.getString(2) + "'");
							} 
							select.close();
							resultSet.close();
						}
					}
				}
			}
		}
		catch(Exception e){
			logger.error(e.toString());
			logger.error(e.getMessage());
			logger.error(e.getStackTrace());
			e.printStackTrace();
			throw e;
		}
	}

	private void createConnection() throws Exception{
		if (connection == null) {
			// Load the database driver for Oracle
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// Get a connection to the database
			connection = DriverManager.getConnection(connectString, user,
					password);
		}

	}
}
