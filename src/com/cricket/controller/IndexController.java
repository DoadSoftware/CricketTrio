package com.cricket.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	CricketService cricketService;
	
	public static String expiry_date = "2025-12-31";
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
	public static Inning inning;
	
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
			
			getDataFromExcelFile();
			
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
		case "GRAPHIC_OPTIONS":
			Map<String,String> map =getDataFromExcelFile();
			return JSONArray.fromObject(map.keySet()).toString();
		case "ISPL_50_50_GRAPHIC_OPTIONS": case "ISPL_BALL_GRAPHIC_OPTIONS":
			inning = session_match.getMatch().getInning().stream().filter(inn -> inn.getIsCurrentInning().equalsIgnoreCase(CricketUtil.YES)).findAny().orElse(null);
			return JSONArray.fromObject(inning.getBowlingTeamId() == session_match.getSetup().getHomeTeamId() ? session_match.getSetup().getHomeSquad() 
					: session_match.getSetup().getAwaySquad()).toString();
		default:
			switch (session_selected_broadcaster) {
			case CricketUtil.DOAD_TRIO:
				this_DOAD_TRIO.ProcessGraphicOption(whatToProcess, cricketService, session_match, print_writer, session_selected_scenes, valueToProcess);
			}
			return JSONObject.fromObject(session_match).toString();
		}
	}
	
	 public static Map <String, String>  getDataFromExcelFile() {
	        File file = new File("C:\\Sports\\Cricket\\Trio\\StatisticsFullFrame.xls");
	        
	        Map<String, String> dataMap = new LinkedHashMap<>();
	        
	        
	        try (FileInputStream inputStream = new FileInputStream(file);
	             Workbook workbook = new XSSFWorkbook(inputStream)) {

	            Sheet sheet = workbook.getSheetAt(0);
	            int totalRows = sheet.getPhysicalNumberOfRows();
	            StringBuilder tableData = new StringBuilder();
	            String key = null;

	            for (int rowIndex = 0; rowIndex < totalRows; rowIndex++) {
	                Row row = sheet.getRow(rowIndex);
	                if (row != null && !isRowEmpty(row)) {
	                    String cellValue = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString();
	                    if (cellValue.matches("^\\d+\\.\\s.*")) {
	                        if (key != null && tableData.length() > 0) {
	                            dataMap.put(key, tableData.toString());
	                            tableData.setLength(0);
	                        }
	                        key = cellValue;
	                    }
	                    for (int j = 0; j < row.getLastCellNum(); j++) {
	                        tableData.append(row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).toString()).append("\t");
	                    }
	                    tableData.append("\n");
	                }
	            }

	            if (key != null && tableData.length() > 0) {
	                dataMap.put(key, tableData.toString());
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			return dataMap;
	    }
	
	    private static boolean isRowEmpty(Row row) {
	        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
	            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	            if (cell != null && cell.getCellType() != CellType.BLANK) {
	                return false;
	            }
	        }
	        return true;
	    }
}
