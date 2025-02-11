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
import com.cricket.model.Inning;
import com.cricket.model.MatchAllData;
import com.cricket.model.Player;
import com.cricket.model.Team;
import com.cricket.service.CricketService;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;

import net.sf.json.JSONArray;

public class DOAD_TRIO extends Scene{
	
	public char return_key = (char) 13;
	public char line_feed = (char) 10;
	
	public DOAD_TRIO() {
		super();
	}
	
	public Object ProcessGraphicOption(String whatToProcess, CricketService cricketService, MatchAllData match, PrintWriter print_writer, List<Scene> scenes, 
			String valueToProcess) throws IOException, JAXBException {
		System.out.println(whatToProcess.toUpperCase());
		switch(whatToProcess.toUpperCase()) {
		//Load Scene
		case "LOAD_GRAPHICS":
			DoadWriteToTrio(print_writer, "read_template 9020-Team_Single");
			break;
		//Save Scene	
		case "SAVE_GRAPHICS":
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess);
			break;
			
		case "POPULATE_GRAPHICS_ISPL_50_50":
			if(valueToProcess.split(",")[0].equalsIgnoreCase("drone")) {
				DoadWriteToTrio(print_writer, "read_template 50-50-OVERS-PlayerImage_New_drone");
			}else {
				DoadWriteToTrio(print_writer, "read_template 50-50-OVERS-PlayerImage_New");
			}
			popualteIspl50_50(print_writer, Integer.valueOf(valueToProcess.split(",")[1]), valueToProcess.split(",")[2],match, cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[3]);
			break;
		case "POPULATE_GRAPHICS_ISPL_TAPE":
			
			if(valueToProcess.split(",")[0].equalsIgnoreCase("drone")) {
				DoadWriteToTrio(print_writer, "read_template TAPE-BALL-OVERS_PLAYER_DRONE");
			}else {
				DoadWriteToTrio(print_writer, "read_template TAPE-BALL-OVERS_PLAYER");
			}
			popualteIsplTape(print_writer, Integer.valueOf(valueToProcess.split(",")[1]),match, cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[2]);
			break;
		case"POPULATE_GRAPHICS_COMPARISION":
			DoadWriteToTrio(print_writer, "read_template Comparison");
			populateComparison(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case"POPULATE_GRAPHICS_NEXT_TO_BAT":
			DoadWriteToTrio(print_writer, "read_template NextToBat");
			populateNextToBat(print_writer,match, cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
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
	 
	 public void popualteIspl50_50(PrintWriter print_writer,int player_id,String cr_value,MatchAllData match, CricketService cricketService) {
		 Player player = CricketFunctions.getPlayerFromMatchData(player_id ,match);
		 String photo = "";
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME " +(player.getTeamId()==match.getSetup().getHomeTeamId() ?
				 match.getSetup().getAwayTeam().getTeamName4() : match.getSetup().getHomeTeam().getTeamName4() )+ " ");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 005-STAT-VALUE " + cr_value);
		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 015-FIRST-NAME " +player.getFirstname());
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 016-LAST-NAME " +(player.getSurname()==null ? "" : player.getSurname()));
		 
		 if(IndexController.session_Configurations.getPrimaryIpAddress().equalsIgnoreCase("LOCALHOST")) {
				photo = "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+(player.getTeamId()==match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+player.getPhoto()+CricketUtil.PNG_EXTENSION;
		}else {
			photo = "\\\\\\\\"+IndexController.session_Configurations.getPrimaryIpAddress()+"\\\\\\\\c\\\\\\\\Images\\\\\\\\ISPL\\\\\\\\PHOTOS\\\\\\\\"+
					(player.getTeamId()==match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
						match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\\\\\"+player.getPhoto()+CricketUtil.PNG_EXTENSION;
		}
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 014-PLAYER-IMAGE " +photo);
	 }
	 private void popualteIsplTape(PrintWriter print_writer, Integer player_id, MatchAllData match,
				CricketService cricketService) {
		 Player player = CricketFunctions.getPlayerFromMatchData(player_id ,match);
		 String photo = "";
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME " +(player.getTeamId()==match.getSetup().getHomeTeamId() ?
				 match.getSetup().getAwayTeam().getTeamName4() : match.getSetup().getHomeTeam().getTeamName4() )+ " ");		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 005-FIRST-NAME " +player.getFirstname());
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 006-LAST-NAME " +(player.getSurname()==null ? "" : player.getSurname()));
		 
		 if(IndexController.session_Configurations.getPrimaryIpAddress().equalsIgnoreCase("LOCALHOST")) {
				photo = "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+(player.getTeamId()==match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+player.getPhoto()+CricketUtil.PNG_EXTENSION;
		}else {
			photo = "\\\\\\\\"+IndexController.session_Configurations.getPrimaryIpAddress()+"\\\\c\\\\Images\\\\ISPL\\\\PHOTOS\\\\"+
					(player.getTeamId()==match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
						match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+player.getPhoto()+CricketUtil.PNG_EXTENSION;
		}
		DoadWriteToTrio(print_writer,  "tabfield:set_value_no_update 004-PLAYER-IMAGE \"" + photo + "\"");
		}
	 	private void populateComparison(PrintWriter print_writer,MatchAllData match) {
	 		
	 		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME " + (match.getSetup().getHomeTeamId()==
	 				 match.getMatch().getInning().get(1).getBowlingTeamId() ? match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4() ));
			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME02 "+(match.getSetup().getHomeTeamId()==
	 				 match.getMatch().getInning().get(1).getBattingTeamId() ? match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4() ));			 
			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-HEADER "+"AFTER "+
			 CricketFunctions.getOvers(match.getMatch().getInning().get(1).getTotalOvers(), match.getMatch().getInning().get(1).getTotalBalls())+" OVERS");
			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 014-STAT-VALUE1 "+ match.getMatch().getInning().get(1).getTotalRuns()+"/"+
					 match.getMatch().getInning().get(1).getTotalWickets());
			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 013-STAT-VALUE1 "+ CricketFunctions.compareInningData(match, "/", 1, match.getEventFile().getEvents()));
		}

		private void populateNextToBat(PrintWriter print_writer, MatchAllData match, CricketService cricketService) {
			// TODO Auto-generated method stub
			
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