package com.cricket.broadcaster;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cricket.containers.Scene;
import com.cricket.controller.IndexController;
import com.cricket.model.BattingCard;
import com.cricket.model.Inning;
import com.cricket.model.MatchAllData;
import com.cricket.model.Player;
import com.cricket.model.Team;
import com.cricket.service.CricketService;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;

public class DOAD_TRIO extends Scene{
	
	public char return_key = (char) 13;
	public char line_feed = (char) 10;
	
	public Inning inning;
	
	public DOAD_TRIO() {
		super();
	}
	
	public Object ProcessGraphicOption(String whatToProcess, CricketService cricketService, MatchAllData match, PrintWriter print_writer, List<Scene> scenes, 
			String valueToProcess) throws IOException, JAXBException {
		System.out.println(whatToProcess.toUpperCase());
		System.out.println(valueToProcess);
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
		case "POPULATE_GRAPHICS_LINEUP":	
			DoadWriteToTrio(print_writer, "read_template LineUp_2Teams_WithChangeOns");
			populateLineUp(print_writer,match, cricketService,Integer.valueOf(valueToProcess.split(",")[0]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[1]);
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
	private void popualteIsplTape(PrintWriter print_writer, Integer player_id, MatchAllData match, CricketService cricketService) {
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
		
		inning = match.getMatch().getInning().stream().filter(inn -> inn.getIsCurrentInning().equalsIgnoreCase(CricketUtil.YES)).findAny().orElse(null);
		
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-HEADER " + "NEXT TO BAT");
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 00-REF-NAME " + (match.getSetup().getHomeTeamId() == inning.getBattingTeamId() ? 
				match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4()));
		
		int rowId = 0;
		int position = 0;
		for(BattingCard bc : inning.getBattingCard()) {
			position++;
			if(rowId>=3) break;
			switch (bc.getStatus()) {
			case CricketUtil.STILL_TO_BAT:
				if(bc.getHowOut() != null && !bc.getHowOut().equalsIgnoreCase(CricketUtil.RETIRED_HURT)) continue;
				rowId++;
				DoadWriteToTrio(print_writer, "table:set_cell_value 002-LIST 001-SCORE-POSITION-OMO " +(rowId-1) +" 0");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 01-PLAYER-IMAGE " +(rowId-1)+ " C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+
				(inning.getBattingTeamId()== match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+bc.getPlayer().getPhoto()+CricketUtil.PNG_EXTENSION + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 02-ICON " +(rowId-1)+ " IMAGE*/Default/Essentials/Icons/"+getPlayerIconName(bc.getPlayer())+ "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 03-FIRST-NAME " +(rowId-1)+" "+ bc.getPlayer().getFirstname() + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 04-LAST-NAME " + (rowId-1)+" "+(bc.getPlayer().getSurname() == null ? "" : bc.getPlayer().getSurname()) + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 05-IN-AT " +(rowId-1)+" IN AT " + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 06-HIDE-NUMBER " +(rowId-1)+" "+ "1" );
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 07-POSITION " +(rowId-1)+" "+ position);
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 08-RUNS "	 + (rowId-1)+"  ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 002-LIST 09-BALLS " +(rowId-1)+"  ");

				break;
			}
			
		}
	}
	
	private void populateLineUp(PrintWriter print_writer, MatchAllData match, CricketService cricketService,Integer TeamId) {
	
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-REF-NAME " + (match.getSetup().getHomeTeamId() == TeamId ? 
				match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4()));
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-REF-NAME " + (match.getSetup().getHomeTeamId() == TeamId ? 
				match.getSetup().getAwayTeam().getTeamName4() : match.getSetup().getHomeTeam().getTeamName4()));
		
		int rowId = 0;
		//team1
		if(TeamId==match.getSetup().getHomeTeamId()) {
			for(Player ply : match.getSetup().getHomeSquad()) {
				rowId++;
				DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 001-SCORE-POSITION-OMO " +(rowId-1) +" 0");
				DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 01-PLAYER-IMAGE " +(rowId-1)+ " C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+
				(TeamId== match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+ply.getPhoto()+CricketUtil.PNG_EXTENSION + "");
				//DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 02-ICON " +(rowId-1)+ " IMAGE*/Default/Essentials/Icons/"+getPlayerIconName(ply)+ "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 03-FIRST-NAME " +(rowId-1)+" "+ ply.getFirstname() + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 04-LAST-NAME " + (rowId-1)+" "+(ply.getSurname() == null ? "" : ply.getSurname()) + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 05-OPENER " +(rowId-1)+" " + getPlayerRole(ply));
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 06-POSITION " +(rowId-1)+" ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 07-RUNS "	 + (rowId-1)+"  ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 08-BALLS " +(rowId-1)+"  ");
	            DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 08-SELECT-CAPTAIN " + (rowId-1)+ " " + 
	            		(ply.getCaptainWicketKeeper() != null && ply.getCaptainWicketKeeper().equalsIgnoreCase(CricketUtil.CAPTAIN) ? "1" : "0"));

			}
		}else {
			for(Player ply : match.getSetup().getAwaySquad()) {
				rowId++;
				DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 001-SCORE-POSITION-OMO " +(rowId-1) +" 0");
				DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 01-PLAYER-IMAGE " +(rowId-1)+ " C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+
				(TeamId== match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+ply.getPhoto()+CricketUtil.PNG_EXTENSION + "");
				//DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 02-ICON " +(rowId-1)+ " IMAGE*/Default/Essentials/Icons/"+getPlayerIconName(ply)+ "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 03-FIRST-NAME " +(rowId-1)+" "+ ply.getFirstname() + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 04-LAST-NAME " + (rowId-1)+" "+(ply.getSurname() == null ? "" : ply.getSurname()) + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 05-OPENER " +(rowId-1)+" " + getPlayerRole(ply));
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 06-POSITION " +(rowId-1)+" ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 07-RUNS "	 + (rowId-1)+"  ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 004-LIST-1 08-BALLS " +(rowId-1)+"  ");
				DoadWriteToTrio(print_writer, "table:set_cell_value 004-LIST-1 08-SELECT-CAPTAIN " + (rowId-1)+ " " + 
		            (ply.getCaptainWicketKeeper() != null && ply.getCaptainWicketKeeper().equalsIgnoreCase(CricketUtil.CAPTAIN) ? "1" : "0"));
			}
		}
		//team2
		rowId = 0;
		if(TeamId == match.getSetup().getHomeTeamId()) {
			for(Player ply : match.getSetup().getAwaySquad()) {
				rowId++;
				DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 001-SCORE-POSITION-OMO " +(rowId-1) +" 0");
				DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 01-PLAYER-IMAGE " +(rowId-1)+ " C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+
				(TeamId== match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+ply.getPhoto()+CricketUtil.PNG_EXTENSION + "");
				//DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 02-ICON " +(rowId-1)+ " IMAGE*/Default/Essentials/Icons/"+getPlayerIconName(ply)+ "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 03-FIRST-NAME " +(rowId-1)+" "+ ply.getFirstname() + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 04-LAST-NAME " + (rowId-1)+" "+(ply.getSurname() == null ? "" : ply.getSurname()) + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 05-OPENER " +(rowId-1)+" " + getPlayerRole(ply));
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 06-POSITION " +(rowId-1)+" ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 07-RUNS "	 + (rowId-1)+"  ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 08-BALLS " +(rowId-1)+"  ");
	            DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 08-SELECT-CAPTAIN " + (rowId-1) + " " 
				+ (ply.getCaptainWicketKeeper() != null && ply.getCaptainWicketKeeper().equalsIgnoreCase(CricketUtil.CAPTAIN) ? "1" : "0"));

			}
		}else {
			for(Player ply : match.getSetup().getHomeSquad()) {
				rowId++;
				DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 001-SCORE-POSITION-OMO " +(rowId-1) +" 0");
				DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 01-PLAYER-IMAGE " +(rowId-1)+ " C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+
				(TeamId== match.getSetup().getHomeTeamId() ?match.getSetup().getHomeTeam().getTeamName4() : 
					match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+ply.getPhoto()+CricketUtil.PNG_EXTENSION + "");
				//DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 02-ICON " +(rowId-1)+ " IMAGE*/Default/Essentials/Icons/"+getPlayerIconName(ply)+ "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 03-FIRST-NAME " +(rowId-1)+" "+ ply.getFirstname() + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 04-LAST-NAME " + (rowId-1)+" "+(ply.getSurname() == null ? "" : ply.getSurname()) + "");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 05-OPENER " +(rowId-1)+" " + getPlayerRole(ply));
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 06-POSITION " +(rowId-1)+" ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 07-RUNS "	 + (rowId-1)+"  ");
				DoadWriteToTrio(print_writer,  "table:set_cell_value 005-LIST-2 08-BALLS " +(rowId-1)+"  ");
	            DoadWriteToTrio(print_writer, "table:set_cell_value 005-LIST-2 08-SELECT-CAPTAIN " + (rowId-1) + " " 
				+ (ply.getCaptainWicketKeeper() != null && ply.getCaptainWicketKeeper().equalsIgnoreCase(CricketUtil.CAPTAIN) ? "1" : "0"));

			}
		}
	}
	public static String getPlayerRole(Player as) {
		switch (as.getRole().toUpperCase()) {
		case "BATSMAN":case "BATTER":
			return "BAT";
		case "BOWLER":
			return "BOWL";
		default:
			return as.getRole().toUpperCase();
		}
	}
	public static String getPlayerIconName(Player as) {
	    String playerIcon = "";
	    
	    if (as.getRole().equalsIgnoreCase("BATSMAN")) {
	        if (as.getBattingStyle().equalsIgnoreCase("RHB")) {
	            playerIcon = "Batsman_RightHand";
	        } else if (as.getBattingStyle().equalsIgnoreCase("LHB")) {
	            playerIcon = "Batsman_LeftHand";
	        }
	    } else if (as.getRole().equalsIgnoreCase("BOWLER")) {
	        if (as.getBowlingStyle() == null) {
	            playerIcon = "Pace_Bowler";
	        } else {
	            switch (as.getBowlingStyle()) {
	                case "RF": case "RFM": case "RMF": case "RM": case "RSM":
	                case "LF": case "LFM": case "LMF": case "LM":
	                    playerIcon = "Pace_Bowler";
	                    break;
	                case "ROB": case "RLB": case "LSL": case "WSL":
	                case "LCH": case "RLG": case "WSR": case "LSO":
	                    playerIcon = "Off_Spinner";
	                    break;
	            }
	        }
	    } else if (as.getRole().equalsIgnoreCase("ALL-ROUNDER")) {
	    	 if (as.getBattingStyle().equalsIgnoreCase("RHB")) {
		            playerIcon = "All_RounderRightHand";
		        } else if (as.getBattingStyle().equalsIgnoreCase("LHB")) {
		            playerIcon = "All_RounderLeftHand";
		        }
	    }
	     if(as.getCaptainWicketKeeper()!=null && (as.getCaptainWicketKeeper().equalsIgnoreCase("CAPTAIN_WICKET_KEEPER")||
	    		 as.getCaptainWicketKeeper().equalsIgnoreCase(CricketUtil.WICKET_KEEPER))) {
			 playerIcon = "WicketKeeper";
		}

	    return playerIcon;  
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