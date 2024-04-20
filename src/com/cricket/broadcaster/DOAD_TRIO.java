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
		case "LOAD_GRAPHICS":
			DoadWriteToTrio(print_writer, "read_template 9020-Team_Single");
			break;
			
		//Save Scene	
		case "SAVE_GRAPHICS":
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess);
			break;
		
		case "POPULATE_GRAPHICS":
				Map<String, String> mp = IndexController.getDataFromExcelFile();
				String [] str =mp.get(valueToProcess).split("\n");
				
				if((str.length-5)<Integer.valueOf(str[1].split("X")[0])) {
					return " Something wrong with your excel at "+ valueToProcess;					
				}else {
					DoadWriteToTrio(print_writer, "read_template 9020-Team_Single");
					PopulateGraphics(print_writer,str);
				}
			
			break;
		
		}
		
		return null;
	}
	 public void PopulateGraphics(PrintWriter print_writer,String[] str) {
		//DoadWriteToTrio(print_writer, "page:read_internal /storage/shows/{0CAF4F6A-629C-4D6E-A3F8-DF6F7ED0A5DB}/elements/101");
		for (int i = 6; i < str.length; i++) {
		    String line[] = splitString(str[i]);
		    for (int j = 0; j < line.length; j++) {
		        String columnName;
		        switch (j) {
		            case 0:
		                columnName = "001-NAME";
		                break;
		            case 1:
		                columnName = "002-CAPTAIN";
		                break;
		            case 2:
		                columnName = "003-ROLE";
		                break;
		            case 3:
		                columnName = "004-STAT1";
		                break;
		            case 4:
		                columnName = "005-STAT2";
		                break;
		            default:
		                columnName = ""; 
		        }

		        DoadWriteToTrio(print_writer, "table:set_cell_value 002_DataALL " + columnName + " " + (i - 6) + " " + line[j]);
		    }
		}

		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-Header01 " + str[3] + " ");
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-Header02 " + " ");
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-Header03 " + " ");
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-SubHeader " + str[4] + " ");
		
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  001-Title1 " + " ");
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  002-Title2 " + " ");

		
		DoadWriteToTrio(print_writer, "saveas " + 1001);
		
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