package com.cricket.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.cricket.containers.Scene;
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
		
		}
		
		return null;
	}
	
	public void DoadWriteToTrio(PrintWriter print_writer,String sendCommand) {
		print_writer.println(sendCommand + return_key + line_feed);
	}

}