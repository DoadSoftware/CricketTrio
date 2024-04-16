package com.cricket.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cricket.containers.Scene;
import com.cricket.controller.IndexController;
import com.cricket.service.CricketService;

public class DOAD_TRIO extends Scene{
	
	public char return_key = (char) 13;
	public char line_feed = (char) 10;
	
	public DOAD_TRIO() {
		super();
	}
	
	public Object ProcessGraphicOption(String whatToProcess, CricketService cricketService, PrintWriter print_writer, List<Scene> scenes, String valueToProcess) throws IOException, JAXBException {
		
		switch(whatToProcess.toUpperCase()) {
		//Load Scene
		case "POINTS_TABLES":
			DoadWriteToTrio(print_writer, "read_template PointsTableTrio");
			break;
			
		//Save Scene	
		case "SAVE_POINTSTABLE":
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess);
			break;
		
		case "POPULATE_GRAPHICS":
			Map<String, String> mp =IndexController.getDataFromExcelFile();
			
			String [] str =mp.get(valueToProcess).split("\n");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-HEADER " + str[3] + "\n");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  001-Sub-HEader " +str[4] + "\n");
			
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-Team1_NAME "+ str[6].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-Team2_NAME  "+ str[7].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 004-Team3_NAME "+str[8].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 005-Team4_NAME "+str[9].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 006-Team5_NAME "+str[10].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 007-Team6_NAME "+ str[11].split("\t")[1]+" ");
			DoadWriteToTrio(print_writer, "saveas " + 100);
			
			break;
		
		}
		
		return null;
	}
	
	public void DoadWriteToTrio(PrintWriter print_writer,String sendCommand) {
		print_writer.println(sendCommand + return_key + line_feed);
	}
	
	
	
	 public static String[] splitString(String str) {
	        int index = str.indexOf("\t");
	        List<String> parts = new ArrayList<>();
	        
	        if (index != -1) {
	            parts.add(str.substring(0, index));
	            String[] rest = str.substring(index + 1).split("\t");
	            for (String part : rest) {
	                if (!part.isEmpty()) {
	                    parts.add(part.trim());
	                }
	            }
	        } else {
	            parts.add(str);
	        }
	        
	        return parts.toArray(new String[0]);
	    }
}