package com.cricket.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
  
import java.io.FileInputStream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;  
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cricket.broadcaster.DOAD_TRIO;
import com.cricket.containers.Excel;
import com.cricket.containers.Scene;
import com.cricket.service.CricketService;
import com.cricket.model.*;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	CricketService cricketService;
	
	public static String expiry_date = "2024-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static DOAD_TRIO this_DOAD_TRIO;
	public static Excel this_Excel=new Excel();
	public static MatchAllData session_match = new MatchAllData();
	public static EventFile session_event = new EventFile();
	public static Configuration session_Configurations = new Configuration();
	
	List<Scene> session_selected_scenes = new ArrayList<Scene>();
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static PrintWriter print_writer;
	
	
	@RequestMapping(value = {"/","/initialise"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String initialisePage(ModelMap model) throws JAXBException, IOException 
	{
		if(current_date == null || current_date.isEmpty()) {
			current_date = CricketFunctions.getOnlineCurrentDate();
		}
		
		model.addAttribute("match_files", new File(CricketUtil.CRICKET_SERVER_DIRECTORY + CricketUtil.MATCHES_DIRECTORY).listFiles(new FileFilter() {
			@Override
		    public boolean accept(File pathname) {
		        String name = pathname.getName().toLowerCase();
		        return name.endsWith(".json") && pathname.isFile();
		    }
		}));
		
		if(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.CONFIGURATIONS_DIRECTORY + CricketUtil.TRIO_XML).exists()) {
			session_Configurations = (Configuration)JAXBContext.newInstance(Configuration.class).createUnmarshaller().unmarshal(
					new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.CONFIGURATIONS_DIRECTORY + CricketUtil.TRIO_XML));
		} else {
			session_Configurations = new Configuration();
			JAXBContext.newInstance(Configuration.class).createMarshaller().marshal(session_Configurations, 
					new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.CONFIGURATIONS_DIRECTORY + 
							CricketUtil.TRIO_XML));
		}
		
		model.addAttribute("session_Configurations",session_Configurations);
		
		return "initialise";
	
	}

	@RequestMapping(value = {"/output"}, method={RequestMethod.GET,RequestMethod.POST}) 
	public String outputPage(ModelMap model,
			@RequestParam(value = "select_broadcaster", required = false, defaultValue = "") String selectedBroadcaster,
			@RequestParam(value = "selectedMatch", required = false, defaultValue = "") String selectmatch,
			@RequestParam(value = "vizIPAddress", required = false, defaultValue = "") String vizIPAddress,
			@RequestParam(value = "vizPortNumber", required = false, defaultValue = "") String vizPortNumber) 
					throws UnknownHostException, IOException, JAXBException, IllegalAccessException, InvocationTargetException, ParseException, URISyntaxException, InvalidFormatException 
	{
		if(current_date == null || current_date.isEmpty()) {
			
			model.addAttribute("error_message","You must be connected to the internet online");
			return "error";
		
		} else if(new SimpleDateFormat("yyyy-MM-dd").parse(expiry_date).before(new SimpleDateFormat("yyyy-MM-dd").parse(current_date))) {
			
			model.addAttribute("error_message","This software has expired");
			return "error";
			
		}else {
			this_DOAD_TRIO = new DOAD_TRIO();
			
			session_Configurations.setBroadcaster(selectedBroadcaster);
			session_Configurations.setPrimaryIpAddress(vizIPAddress);
			
			if(!vizIPAddress.trim().isEmpty()) {
				session_Configurations.setPrimaryPortNumber(Integer.valueOf(vizPortNumber));
				session_socket = new Socket(vizIPAddress, Integer.valueOf(vizPortNumber));
				print_writer = new PrintWriter(session_socket.getOutputStream(), true);
			}
			
			session_selected_broadcaster = selectedBroadcaster;
			
			session_match = new MatchAllData();
			
			if(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.SETUP_DIRECTORY + selectmatch).exists()) {
				session_match.setSetup(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.SETUP_DIRECTORY + 
						selectmatch), Setup.class));
				session_match.setMatch(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.MATCHES_DIRECTORY + 
						selectmatch), Match.class));
			}
			if(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.EVENT_DIRECTORY + selectmatch).exists()) {
				session_match.setEventFile(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.EVENT_DIRECTORY + 
						selectmatch), EventFile.class));
			}
			
			session_match.getMatch().setMatchFileName(selectmatch);
//			session_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ, 
//					CricketUtil.SETUP + "," + CricketUtil.MATCH + "," + CricketUtil.EVENT, session_match));			
			session_match.getSetup().setMatchFileTimeStamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			
			JAXBContext.newInstance(Configuration.class).createMarshaller().marshal(session_Configurations, 
					new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.CONFIGURATIONS_DIRECTORY + CricketUtil.TRIO_XML));
			
			getDataExcelFile();
			
			model.addAttribute("session_match", session_match);
			model.addAttribute("session_selected_broadcaster", session_selected_broadcaster);
			model.addAttribute("session_port", vizPortNumber);
			model.addAttribute("session_selected_ip", vizIPAddress);
			
			return "output";
		}
	}

	@RequestMapping(value = {"/processCricketProcedures"}, method={RequestMethod.GET,RequestMethod.POST})    
	public @ResponseBody String processCricketProcedures(
			@RequestParam(value = "whatToProcess", required = false, defaultValue = "") String whatToProcess,
			@RequestParam(value = "valueToProcess", required = false, defaultValue = "") String valueToProcess)  
					throws IOException, IllegalAccessException, InvocationTargetException, JAXBException, InterruptedException, ParseException 
	{
		//System.out.println("session_selected_broadcaster = " + session_selected_broadcaster);
		switch (whatToProcess.toUpperCase()) {
		
		default:
			switch (session_selected_broadcaster) {
			case CricketUtil.DOAD_TRIO:
				this_DOAD_TRIO.ProcessGraphicOption(whatToProcess, cricketService, print_writer, session_selected_scenes, valueToProcess);
				break;
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
	
	public static void getDataExcelFile() throws IOException, InvalidFormatException {
        File file = new File("C:\\Sports\\Cricket\\Trio\\StatisticsFullFrame.xls");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        int tableStartIndex = 0;
        int totalRows = sheet.getPhysicalNumberOfRows();
        int totalCols = sheet.getRow(tableStartIndex).getPhysicalNumberOfCells();
        int emptyRowCount = 0;
        boolean dataExists = false;
        String[][] tableData = null;
        String key = "";
        Map<String, List<String[][]>> data = new HashMap<>();

        for (int i = tableStartIndex; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                boolean rowHasData = false;
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    if (cell != null && !cell.toString().isEmpty()) {
                        rowHasData = true;
                        if (!dataExists) {
                            if (emptyRowCount >= 2) {
                                key = cell.toString();
                                dataExists = true;
                                emptyRowCount = 0;
                            }
                        } else {
                            if (tableData == null) {
                                tableData = new String[totalRows - tableStartIndex][totalCols];
                            }
                            // Process the table
                            processTable(sheet, tableData, tableStartIndex, totalRows, totalCols);
                        }
                    }
                }
                if (!rowHasData) {
                    emptyRowCount++;
                }
            } else {
                emptyRowCount++;
            }

            if (dataExists && emptyRowCount >= 2) {
                // Add the table data to the map
                List<String[][]> tables = data.getOrDefault(key, new ArrayList<>());
                tables.add(tableData);
                data.put(key, tables);
                // Reset variables for the next table
                tableData = null;
                dataExists = false;
                emptyRowCount = 0;
            }
        }

        // Print the HashMap
        for (Map.Entry<String, List<String[][]>> entry : data.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            List<String[][]> tables = entry.getValue();
            for (int i = 0; i < tables.size(); i++) {
                System.out.println("Table " + (i + 1) + ":");
                printTable(tables.get(i));
            }
        }
    }

    private static void processTable(Sheet sheet, String[][] tableData, int tableStartIndex, int totalRows, int totalCols) {
        for (int i = tableStartIndex; i < totalRows; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    tableData[i - tableStartIndex][j] = (cell != null) ? cell.toString() : "";
                }
            }
        }
    }

    private static void printTable(String[][] tableData) {
        for (String[] row : tableData) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
	public static void printCellValue(Cell cell) {
	    CellType cellType = cell.getCellType().equals(CellType.FORMULA)
	      ? cell.getCachedFormulaResultType() : cell.getCellType();
	    if (cellType.equals(CellType.STRING)) {
	        System.out.print(cell.getStringCellValue() + " | ");
	    }
	    if (cellType.equals(CellType.NUMERIC)) {
	        if (DateUtil.isCellDateFormatted(cell)) {
	            System.out.print(cell.getDateCellValue() + " | ");
	        } else {
	            System.out.print(cell.getNumericCellValue() + " | ");
	        }
	    }
	    if (cellType.equals(CellType.BOOLEAN)) {
	        System.out.print(cell.getBooleanCellValue() + " | ");
	    }
	}
}
