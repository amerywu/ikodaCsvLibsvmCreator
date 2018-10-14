package ikoda.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

public class Spreadsheet
{
	private static Spreadsheet spreadsheet;
	private static final String UNDERSCORE = "_";

	public static Spreadsheet getInstance()
	{
		if (null == spreadsheet)
		{
			spreadsheet = new Spreadsheet();
		}
		return spreadsheet;
	}

	private Logger logger = null;

	private HashMap<String, Integer> fileNamesByBlockMap = new HashMap();

	private Map<String, AbstractSpreadsheetCreator> csvSpreadsheetsMap = new HashMap<String, AbstractSpreadsheetCreator>();

	private Spreadsheet()
	{
		logger = SSm.getAppLogger();
	}

	public synchronized Integer getBlockNumber(String fileName)
	{
		Integer block = fileNamesByBlockMap.get(fileName);
		if (null == block)
		{
			fileNamesByBlockMap.put(fileName, 0);
			return 0;
		}
		return block;
	}

	public synchronized CSVSpreadsheetCreator getCsvSpreadSheet(String name)
	{
		CSVSpreadsheetCreator csvSpreadsheet = (CSVSpreadsheetCreator) csvSpreadsheetsMap.get(getFileName(name));
		if (null == csvSpreadsheet)
		{
			logger.warn("Null. No csvspreadsheet for " + getFileName(name) + ". Available spreadsheets are "
					+ csvSpreadsheetsMap.keySet());
		}
		return csvSpreadsheet;
	}

	private String getFileName(String fileName)
	{

		Integer block = fileNamesByBlockMap.get(fileName);
		if (null == block)
		{
			fileNamesByBlockMap.put(fileName, 0);

			return fileName + UNDERSCORE + 0;
		}

		return fileName + UNDERSCORE + block;
	}

	public synchronized LibSvmProcessor getLibSvmProcessor(String name) throws ClassCastException, IKodaUtilsException
	{

		LibSvmProcessor libSvmProcessor = (LibSvmProcessor) csvSpreadsheetsMap.get(getFileName(name));
		if (null == libSvmProcessor)
		{
			logger.warn("Null. No csvspreadsheet for " + getFileName(name) + ". Available spreadsheets are "
					+ csvSpreadsheetsMap.keySet());
		}
		return libSvmProcessor;

	}

	public synchronized void initCsvSpreadsheet(String name, Logger logger, String dirPath)
	{
		initCsvSpreadsheet1(name, logger, dirPath);
	}

	public synchronized void initCsvSpreadsheet(String name, String dirPath)
	{

		initCsvSpreadsheet(name, logger, dirPath);
	}

	public synchronized void initCsvSpreadsheet(String name, String loggerName, String dirPath)
	{

		initCsvSpreadsheet(name, SSm.getLogger(loggerName), dirPath);

	}

	public synchronized void initCsvSpreadsheet1(String name, Logger logger, String dirPath)
	{
		String csvName = getFileName(name);
		if (null == csvSpreadsheetsMap.get(csvName))
		{

			csvSpreadsheetsMap.put(csvName, new CSVSpreadsheetCreator(name, logger, dirPath));
		}

	}

	public synchronized void initLibsvm2(String name, Logger logger, String targetColumnName, String dirPath)
	{
		String csvName = getFileName(name);
		if (null == csvSpreadsheetsMap.get(csvName))
		{
			logger.info("Created spreadsheet "+csvName);
			csvSpreadsheetsMap.put(csvName,
					new LibSvmProcessor(name, logger, targetColumnName, LibSvmProcessor.AUTO_ID, dirPath));
		}
		else
		{
			logger.warn("Spreadsheet already exists: " + csvName);
		}
	}

	public synchronized void initLibsvm2(String name, String targetColumnName, String dirPath)
	{
		initLibsvm2(name, logger.getName(), targetColumnName, dirPath);
	}

	public synchronized void initLibsvm2(String name, String loggerName, String targetColumnName, String dirPath)
	{

		initLibsvm2(name, SSm.getLogger(loggerName), targetColumnName, dirPath);
	}

	private Logger initLogger(String loggerName)
	{
		return SSm.getLogger(loggerName);
	}

	private void registerNewBlock(String fileName)
	{
		Integer block = fileNamesByBlockMap.get(fileName);
		if (null == block)
		{
			block = -1;
		}

		int newBlock = block + 1;
		fileNamesByBlockMap.put(fileName, Integer.valueOf(newBlock));
	}

	public void removeSpreadsheet(String name)
	{
		CSVSpreadsheetCreator success = (CSVSpreadsheetCreator) csvSpreadsheetsMap.remove(getFileName(name));
		if (null == success)
		{
			logger.warn("Could not remove " + name + ". The spreadsheet is not registered");
		}
	}

	public synchronized void resetSpreadsheet(final String fileName, String[] columnsToIgnore)
	{
		try
		{

			String currentFile = getFileName(fileName);
			logger.info("\n\nNEW SPREADSHEET replacing" + currentFile + "\n\n");

			getCsvSpreadSheet(fileName).finalizeAndJoinBlocks(currentFile + ".csv");

			saveExtantBlockAndClearData(fileName);

			String targetColumn = getCsvSpreadSheet(fileName).getTargetColumnName();
			String projectPrefix = getCsvSpreadSheet(fileName).getProjectPrefix();
			String path = getCsvSpreadSheet(fileName).getPathToDirectory();
			String keyspaceName = getCsvSpreadSheet(fileName).getKeyspaceName();
			String keyspaceUuid = getCsvSpreadSheet(fileName).getKeyspaceUUID();
			registerNewBlock(fileName);

			logger.info("Creating " + getFileName(fileName) + "\n");
			initCsvSpreadsheet(fileName, logger, path);
			getCsvSpreadSheet(fileName).setProjectPrefix(projectPrefix);
			getCsvSpreadSheet(fileName).setTargetColumnName(targetColumn);
			getCsvSpreadSheet(fileName).setKeyspaceName(keyspaceName);
			getCsvSpreadSheet(fileName).setKeyspaceUUID(keyspaceUuid);

			getCsvSpreadSheet(fileName).clearAll();

		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public synchronized void resetSpreadsheetLibsvm(final String fileName, String[] columnsToIgnore)
	{
		try
		{

			String currentFile = getFileName(fileName);
			logger.info("\n\nNEW SPREADSHEET replacing" + currentFile + "\n\n");

			getLibSvmProcessor(fileName).printLibSvmBlock(columnsToIgnore);

			String targetColumn = getLibSvmProcessor(fileName).getTargetColumnName();
			String projectPrefix = getLibSvmProcessor(fileName).getProjectPrefix();
			String path = getLibSvmProcessor(fileName).getPathToDirectory();
			String keyspaceName = getLibSvmProcessor(fileName).getKeyspaceName();
			String keyspaceUuid = getLibSvmProcessor(fileName).getKeyspaceUUID();
			String localurl =  getLibSvmProcessor(fileName).getLocalUrl();
			String localports =  getLibSvmProcessor(fileName).getLocalPorts();
			registerNewBlock(fileName);

			logger.info("Creating " + getFileName(fileName) + "\n");
			initLibsvm2(fileName, logger, targetColumn, path);
			getLibSvmProcessor(fileName).setProjectPrefix(projectPrefix);
			getLibSvmProcessor(fileName).setTargetColumnName(targetColumn);
			getLibSvmProcessor(fileName).setKeyspaceName(keyspaceName);
			getLibSvmProcessor(fileName).setKeyspaceUUID(keyspaceUuid);
			getLibSvmProcessor(fileName).setLocalUrl(localurl);
			getLibSvmProcessor(fileName).setLocalPorts(localports);
			getLibSvmProcessor(fileName).clearAll();

		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	

	private void saveExtantBlockAndClearData(String fileName) throws IKodaUtilsException
	{

		getCsvSpreadSheet(fileName).printCsvBlock("PART_" + fileName);
		getCsvSpreadSheet(fileName).clearData();
	}

	public void setSpreadsheetLogger(String s)
	{
		logger = initLogger(s);
	}
}
