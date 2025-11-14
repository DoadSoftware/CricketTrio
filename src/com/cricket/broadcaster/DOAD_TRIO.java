package com.cricket.broadcaster;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.cricket.containers.Scene;
import com.cricket.controller.IndexController;
import com.cricket.model.BattingCard;
import com.cricket.model.BowlingCard;
import com.cricket.model.Event;
import com.cricket.model.EventFile;
import com.cricket.model.Fixture;
import com.cricket.model.Ground;
import com.cricket.model.Inning;
import com.cricket.model.Match;
import com.cricket.model.MatchAllData;
import com.cricket.model.Player;
import com.cricket.model.Setup;
import com.cricket.model.Team;
import com.cricket.model.VariousText;
import com.cricket.service.CricketService;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DOAD_TRIO extends Scene{
	
	public char return_key = (char) 13;
	public char line_feed = (char) 10;
	
	public Inning inning;
	public List<Fixture> FixturesList = new ArrayList<Fixture>();
	public Team team;
	
	public DOAD_TRIO() {
		super();
	}
	
	public Object ProcessGraphicOption(String whatToProcess, CricketService cricketService, MatchAllData match, PrintWriter print_writer, List<Scene> scenes, 
			String valueToProcess) throws Exception{
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
		case "POPULATE_GRAPHICS_ISPL_FF_MATCH_SUMMARY": case "POPULATE_ISPL_FF_MATCH_SUMMARY":
			DoadWriteToTrio(print_writer, "read_template FF_Match_Summary");
			MatchAllData previous_match = new MatchAllData();
			if(whatToProcess.equalsIgnoreCase("POPULATE_GRAPHICS_ISPL_FF_MATCH_SUMMARY")) {
				Fixture fixture = cricketService.getFixtures().stream().filter(fix -> fix.getMatchnumber() == Integer.valueOf(valueToProcess.split(",")[0])).findAny().orElse(null);
				if(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.SETUP_DIRECTORY + fixture.getMatchfilename() + ".json").exists()) {
					previous_match.setSetup(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.SETUP_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), Setup.class));
					previous_match.setMatch(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.MATCHES_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), Match.class));
				}
				if(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.EVENT_DIRECTORY + fixture.getMatchfilename() + ".json").exists()) {
					previous_match.setEventFile(new ObjectMapper().readValue(new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.EVENT_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), EventFile.class));
				}
				
				previous_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ, 
						CricketUtil.SETUP + "," + CricketUtil.MATCH, previous_match));
			}else {
				previous_match = match;
			}
			popualteIspl_MATCH_SUMMARY(print_writer,previous_match, cricketService);
			DoadWriteToTrio(print_writer, "saveas " + (valueToProcess.split(",").length >1 ? valueToProcess.split(",")[1] :valueToProcess.split(",")[0]));
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
		case"POPULATE_GRAPHICS_FIXTURE":
			DoadWriteToTrio(print_writer, "read_template FF_Team_Schedule");
			System.out.println("valueToProcess = " + valueToProcess);
			populateFixture(print_writer, match, cricketService, Integer.valueOf(valueToProcess.split(",")[0]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[1]);
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
	private void popualteIspl_MATCH_SUMMARY(PrintWriter print_writer, MatchAllData previous_match, CricketService cricketService) throws Exception {		
		//HEADER
		List<String> inningData  = new ArrayList<>();
		
		 inningData.add("tabfield:set_value_no_update 001-TEAM-REF-NAME-1 " +  (previous_match.getSetup().getHomeTeamId()== previous_match.getMatch().getInning().get(0).getBattingTeamId()? previous_match.getSetup().getHomeTeam().getTeamName1():previous_match.getSetup().getAwayTeam().getTeamName1()));
		 inningData.add("tabfield:set_value_no_update 002-TEAM-REF-NAME-2 " +  (previous_match.getSetup().getHomeTeamId()== previous_match.getMatch().getInning().get(1).getBattingTeamId()? previous_match.getSetup().getHomeTeam().getTeamName1():previous_match.getSetup().getAwayTeam().getTeamName1()));
		 inningData.add("tabfield:set_value_no_update 005-TEAM-NAME " + (previous_match.getSetup().getHomeTeamId()== previous_match.getMatch().getInning().get(0).getBattingTeamId()? previous_match.getSetup().getHomeTeam().getTeamName1():previous_match.getSetup().getAwayTeam().getTeamName1()));
		 inningData.add("tabfield:set_value_no_update 012-TEAM-NAME " + (previous_match.getSetup().getHomeTeamId()== previous_match.getMatch().getInning().get(1).getBattingTeamId()? previous_match.getSetup().getHomeTeam().getTeamName1():previous_match.getSetup().getAwayTeam().getTeamName1()));
		 inningData.add("tabfield:set_value_no_update 003-HEADER " + "SUMMARY");
		 inningData.add("tabfield:set_value_no_update 004-SUB-HEADER " + previous_match.getSetup().getTournament() + " - " + previous_match.getSetup().getMatchIdent());

		
		//BODY
		 inningData.add("tabfield:set_value_no_update 005-OVERS-VALUE " + CricketFunctions.OverBalls(previous_match.getMatch().getInning().get(0).getTotalOvers(),previous_match.getMatch().getInning().get(0).getTotalBalls()));
		 inningData.add("tabfield:set_value_no_update 005-SCORE-VALUE " + CricketFunctions.getTeamScore(previous_match.getMatch().getInning().get(0),"-", false));
		 inningData.add("tabfield:set_value_no_update 012-OVERS-VALUE " + CricketFunctions.OverBalls(previous_match.getMatch().getInning().get(1).getTotalOvers(),previous_match.getMatch().getInning().get(1).getTotalBalls()));
		 inningData.add("tabfield:set_value_no_update 012-SCORE-VALUE " + CricketFunctions.getTeamScore(previous_match.getMatch().getInning().get(1),"-", false));
		for(int i = 1; i <= 2 ; i++) {
			inningData.add("tabfield:set_value_no_update "+(i==1?"008":"012")+"-SELECT-CHALLENGE 0");
			String tapeData = getBowlerRunsOverbyOver(i, previous_match.getEventFile().getEvents(), previous_match);
			if(i == 1) {
				if(previous_match.getSetup().getTargetOvers() != null && !previous_match.getSetup().getTargetOvers().trim().isEmpty()) {
					inningData.add("tabfield:set_value_no_update 005-DLS-VALUE " +"(" + previous_match.getSetup().getTargetOvers() + ")");
				}
				if(previous_match.getMatch().getInning().get(i-1).getBattingTeamId() == previous_match.getSetup().getTossWinningTeam()) {
					inningData.add("tabfield:set_value_no_update 005-SELECT-TOSS 1");

				}else {
					inningData.add("tabfield:set_value_no_update 005-SELECT-TOSS 0");
				}
			}else {
				if(previous_match.getSetup().getTargetOvers() != null && !previous_match.getSetup().getTargetOvers().trim().isEmpty()) {
					if((Integer.valueOf(previous_match.getSetup().getTargetOvers())*6) != 
							((previous_match.getMatch().getInning().get(0).getTotalOvers()*6)+previous_match.getMatch().getInning().get(0).getTotalBalls())) {
						inningData.add("tabfield:set_value_no_update 005-DLS-VALUE " +"(" + previous_match.getSetup().getTargetOvers() + ")");

					}else {
						inningData.add("tabfield:set_value_no_update 005-DLS-VALUE " +"(" + previous_match.getSetup().getTargetOvers() + ")");
						inningData.add("tabfield:set_value_no_update 012-DLS-VALUE " +"(" + previous_match.getSetup().getTargetOvers() + ")");
					}
				}else {
					inningData.add("tabfield:set_value_no_update 005-DLS-VALUE " +"");
					inningData.add("tabfield:set_value_no_update 012-DLS-VALUE " +"");
	
				}
				if(previous_match.getMatch().getInning().get(i-1).getBattingTeamId() == previous_match.getSetup().getTossWinningTeam()) {
					inningData.add("tabfield:set_value_no_update 012-SELECT-TOSS 1");
				}else {
					inningData.add("tabfield:set_value_no_update 012-SELECT-TOSS 0");
				}
			}
			if(previous_match.getMatch().getInning().get(i-1).getBattingCard() != null) {
				Collections.sort(previous_match.getMatch().getInning().get(i-1).getBattingCard(),new CricketFunctions.BatsmenScoreComparator());
				int rowId =0;
				for(BattingCard bc : previous_match.getMatch().getInning().get(i-1).getBattingCard()) {
					if(rowId >=3) break;
					if(bc.getRuns() > 0) {
						rowId++;
						inningData.add("tabfield:set_value_no_update 0"+(previous_match.getMatch().getInning().get(i-1).getInningNumber()==1?"0":"")+((6*previous_match.getMatch().getInning().get(i-1).getInningNumber())+(rowId-1))
								+"-BATSMAN-NAME "+ bc.getPlayer().getTicker_name());
						inningData.add("tabfield:set_value_no_update 0"+(previous_match.getMatch().getInning().get(i-1).getInningNumber()==1?"0":"")+((6*previous_match.getMatch().getInning().get(i-1).getInningNumber())+(rowId-1))
								+"-BATSMAN-RUNS "+ bc.getRuns()+(bc.getStatus().equalsIgnoreCase("OUT")?"":"* "));
						inningData.add("tabfield:set_value_no_update 0"+(previous_match.getMatch().getInning().get(i-1).getInningNumber()==1?"0":"")+((6*previous_match.getMatch().getInning().get(i-1).getInningNumber())+(rowId-1))
								+"-BATSMAN-BALLS "+ bc.getBalls());
			
						String photo = "";
						if(IndexController.session_Configurations.getPrimaryIpAddress().equalsIgnoreCase("LOCALHOST")) {
							photo = "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+(bc.getPlayer().getTeamId()==previous_match.getSetup().getHomeTeamId() ?previous_match.getSetup().getHomeTeam().getTeamName4() : 
								previous_match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+bc.getPlayer().getPhoto()+CricketUtil.PNG_EXTENSION;
						}else {
							photo = "\\\\\\\\"+IndexController.session_Configurations.getPrimaryIpAddress()+"\\\\\\\\c\\\\\\\\Images\\\\\\\\ISPL\\\\\\\\PHOTOS\\\\\\\\"+
									(bc.getPlayer().getTeamId()==previous_match.getSetup().getHomeTeamId() ?previous_match.getSetup().getHomeTeam().getTeamName4() : 
										previous_match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\\\\\"+bc.getPlayer().getPhoto()+CricketUtil.PNG_EXTENSION;
						}
						inningData.add("tabfield:set_value_no_update 0"+(previous_match.getMatch().getInning().get(i-1).getInningNumber()==1?"0":"")+((6*previous_match.getMatch().getInning().get(i-1).getInningNumber())+(rowId-1))
								+"-BATSMAN-IMG "+photo);

					}
				}
				ArrayList<Event> tapeBall = new ArrayList<Event>();
				int log_50_bowlerNo = 0;
				for(int j= previous_match.getEventFile().getEvents().size()-1;j >= 0 ; j--) {
					if(previous_match.getEventFile().getEvents().get(j).getEventInningNumber() == previous_match.getMatch().getInning().get(i-1).getInningNumber()) {
						if(previous_match.getEventFile().getEvents().get(j).getEventType().equalsIgnoreCase(CricketUtil.LOG_50_50)) {
							log_50_bowlerNo = previous_match.getEventFile().getEvents().get(j).getEventBowlerNo();
							if(previous_match.getEventFile().getEvents().get(j).getEventExtra().equalsIgnoreCase("-")) {
								inningData.add("tabfield:set_value_no_update "+(i==1?"008":"014")+"-SELECT-CHALLENGE 1");
								inningData.add("tabfield:set_value_no_update " + (i == 1 ? "008-NEG-CHALLENGE-VALUE" : "014-CHALLENGE-VALUE") + " "
										+ previous_match.getEventFile().getEvents().get(j).getEventExtraRuns());
							}
							else if(previous_match.getEventFile().getEvents().get(j).getEventExtra().equalsIgnoreCase("+")) {
								inningData.add("tabfield:set_value_no_update "+(i==1?"008":"014")+"-SELECT-CHALLENGE 2");
								inningData.add("tabfield:set_value_no_update " + (i == 1 ? "008-POS-CHALLENGE-VALUE" : "014-CHALLENGE-VALUE") + " "
										+ previous_match.getEventFile().getEvents().get(j).getEventExtraRuns());
							}
						}
						if(previous_match.getEventFile().getEvents().get(j).getEventExtra() != null) {
							if(previous_match.getEventFile().getEvents().get(j).getEventExtra().equalsIgnoreCase("TAPE")) {
								tapeBall.add(previous_match.getEventFile().getEvents().get(j));
							}
						}
					}
				}
				if(tapeBall.size()>0) {
					inningData.add("tabfield:set_value_no_update "+(i==1?"011":"017")+"-TAPEBALL-VALUE "+tapeData.split(",")[1]);
					inningData.add("tabfield:set_value_no_update "+(i==1?"011":"017")+"-TAPEBALL-OVERS "+tapeBall.size());
				}else {
					inningData.add("tabfield:set_value_no_update "+(i==1?"011":"017")+"-TAPEBALL-VALUE 0");
					inningData.add("tabfield:set_value_no_update "+(i==1?"011":"017")+"-TAPEBALL-OVERS 0");
				}
				if(previous_match.getMatch().getInning().get(i-1).getBowlingCard() != null) {
					Collections.sort(previous_match.getMatch().getInning().get(i-1).getBowlingCard(),new CricketFunctions.BowlerFiguresComparator());
					 rowId = 0;
					for(BowlingCard boc : previous_match.getMatch().getInning().get(i-1).getBowlingCard()) {
						if(rowId >=3) break;
							rowId++;
							
							for(Event evnt : tapeBall) {
								String formattedValue = ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1) > 9) ? 
				                         "" + ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1)) : 
				                         "" + ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1));

					
								if(boc.getPlayerId() == log_50_bowlerNo) {
									inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 2");
									if(tapeBall.get(0).getEventBowlerNo() == boc.getPlayerId()) {
										inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 1");
									}
									if(tapeBall.size() > 1) {
										if(tapeBall.get(1).getEventBowlerNo() == boc.getPlayerId()) {
											inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 1");
										}
									}
									break;
								}else {
									if(evnt.getEventBowlerNo() == boc.getPlayerId()) {
										inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 1");

										if(tapeBall.size() == 2) {
											if(tapeBall.get(0).getEventBowlerNo() == tapeBall.get(1).getEventBowlerNo()) {
												inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 1");
											}
										}
										break;
									}else {
										inningData.add("tabfield:set_value_no_update 0"+formattedValue+"-SELECT-OVERTYPE 0");
									}
								}
							}
						String formattedValue = ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1) > 9) ? 
		                         "" + ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1)) : 
		                         "0" + ((previous_match.getMatch().getInning().get(i - 1).getInningNumber()==1?9:15) + (rowId - 1));
						
						inningData.add("tabfield:set_value_no_update 0" + formattedValue +"-BOWLER-NAME "+ boc.getPlayer().getTicker_name());
						inningData.add("tabfield:set_value_no_update 0" + formattedValue +"-BOWLER-RUNS "+ (boc.getWickets()+"-" + boc.getRuns()));
						inningData.add("tabfield:set_value_no_update 0" + formattedValue +"-BOWLER-BALLS "+ CricketFunctions.OverBalls(boc.getOvers(), boc.getBalls()));
			
						String photo = "";
						if(IndexController.session_Configurations.getPrimaryIpAddress().equalsIgnoreCase("LOCALHOST")) {
							photo = "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"+(boc.getPlayer().getTeamId()==previous_match.getSetup().getHomeTeamId() ?previous_match.getSetup().getHomeTeam().getTeamName4() : 
								previous_match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\"+boc.getPlayer().getPhoto()+CricketUtil.PNG_EXTENSION;
						}else {
							photo = "\\\\\\\\"+IndexController.session_Configurations.getPrimaryIpAddress()+"\\\\\\\\c\\\\\\\\Images\\\\\\\\ISPL\\\\\\\\PHOTOS\\\\\\\\"+
									(boc.getPlayer().getTeamId()==previous_match.getSetup().getHomeTeamId() ?previous_match.getSetup().getHomeTeam().getTeamName4() : 
										previous_match.getSetup().getAwayTeam().getTeamName4() )+ "\\\\\\\\"+boc.getPlayer().getPhoto()+CricketUtil.PNG_EXTENSION;
						}
						inningData.add("tabfield:set_value_no_update 0" + formattedValue + "-BOWLER-IMG "+photo);

					}
				}
			}
		}
		
		//FOOTER
		inningData.add("tabfield:set_value_no_update 018-SELECT-FOOTER 2");
		inningData.add("tabfield:set_value_no_update 019-FOOTER-TEXT " + CricketFunctions.GenerateMatchSummaryStatus(
				2, previous_match,CricketUtil.FULL, "", IndexController.session_Configurations.getBroadcaster(), true)
				.replace("win", "won").toUpperCase());
		
		for(String str : inningData) {
			DoadWriteToTrio(print_writer, str);
		}
	}
	public void PopulateGraphics(PrintWriter print_writer,String[] str) {
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

 	private String populateFixture(PrintWriter print_writer,MatchAllData match, CricketService cricketService, int teamId) {
 		
 		FixturesList.clear();
		for(Fixture fixture : CricketFunctions.processAllFixtures(cricketService)) {
			if(fixture.getHometeamid() == Integer.valueOf(teamId) || 
					fixture.getAwayteamid() == Integer.valueOf(teamId)) {
				FixturesList.add(fixture);
			}
		}
		
		if(FixturesList == null) {
			return "populateFixturesAndResults : FixturesList is returning NULL";
		}
		
		team = cricketService.getTeams().stream().filter(tm -> tm.getTeamId() == Integer.valueOf(teamId)).findAny().orElse(null);
		if(team == null) {
			return "populatePlayingXI: Team id [" + Integer.valueOf(teamId) + "] from database is returning NULL";
		}
		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEADER1 " + "");
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEADER2 " + "FIXTURES & RESULTS");
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  001-SELECT-HEADER-STYLE " + "0");
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-LEFTLOGO " + "IMAGE*Default/Essentials/Logos/TLogo");
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TEAM-LOGO " + "IMAGE*Default/Essentials/Logos_BW/" + team.getTeamBadge() + "");
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update   004-NumberOfMathes " + (FixturesList.size()-1));
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-SUB-HEADER " + match.getSetup().getTournament());
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-SUB-HEADER02 " + team.getTeamName1());
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-STAT-HEAD-NAME " + "");
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-STAT-VALUE-NAME-1 " + "DATE/RESULTS");
 		
 		Calendar cal_npl = Calendar.getInstance();
		cal_npl.add(Calendar.DATE, 0);
		
		for(int i=0;i<= FixturesList.size()-1;i++) {
			 
			DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 001-MATCH " + i + " " + FixturesList.get(i).getMatchfilename());
			 
			DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 002-TeamName " + i + " " + (team.getTeamId() == FixturesList.get(i).getHometeamid() ? FixturesList.get(i).getAway_Team().getTeamName1() 
					: FixturesList.get(i).getHome_Team().getTeamName1()));
			
			DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 003-VS " + i + " v");
			 
			if(FixturesList.get(i).getMargin() != null && !FixturesList.get(i).getMargin().isEmpty()) {
				if(FixturesList.get(i).getWinnerteam() != null && !FixturesList.get(i).getWinnerteam().isEmpty()) {
					if(FixturesList.get(i).getWinnerteam().equalsIgnoreCase(team.getTeamName1())) {
						DoadWriteToTrio(print_writer, "table:set_cell_value  003-LEADER-BOARD-DATA 004-RESULT " + i + 
								" WON BY " + FixturesList.get(i).getMargin());
					}else {
						DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 004-RESULT " + i + 
								" LOST BY " + FixturesList.get(i).getMargin());
					}
				}else {
					DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 004-RESULT " + i + " " +
							FixturesList.get(i).getMargin());
				}
			}else {
				
				if(FixturesList.get(i).getDate().equalsIgnoreCase(new SimpleDateFormat("dd-MM-yyyy").format(cal_npl.getTime()))) {
					DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 004-RESULT " + i + 
							" TODAY");
				}else {
					DoadWriteToTrio(print_writer, "table:set_cell_value 003-LEADER-BOARD-DATA 004-RESULT " + i + " " +
							CricketFunctions.ordinal(Integer.valueOf(FixturesList.get(i).getDate().split("-")[0]))
					+ " " + Month.of(Integer.valueOf(FixturesList.get(i).getDate().split("-")[1])));
				}
			}
		}
		
		return null;
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
	public String getBowlerRunsOverbyOver(int inning,List<Event> event, MatchAllData matchAllData) {
		
		int bowlerId = 0,runs = 0,wicket = 0;
		String name = "";
		boolean bowler_found = false;
		
		if ((matchAllData.getEventFile().getEvents() != null) && (matchAllData.getEventFile().getEvents().size() > 0)) {
			for(Event evnt: matchAllData.getEventFile().getEvents()) {
				if(evnt.getEventInningNumber() == inning) {
					if(evnt.getEventExtra() != null) {
						if(evnt.getEventExtra().equalsIgnoreCase("TAPE")) {
							bowlerId = evnt.getEventBowlerNo();
							bowler_found = true;
						}
					}
					if(bowler_found && evnt.getEventBowlerNo() == bowlerId) {
						switch(evnt.getEventType()) {
						case CricketUtil.ONE : case CricketUtil.TWO: case CricketUtil.THREE:  case CricketUtil.FIVE : case CricketUtil.DOT:
		            	case CricketUtil.FOUR: case CricketUtil.SIX: case CricketUtil.NINE:
		            		runs += evnt.getEventRuns();
		                    break;
		            	case CricketUtil.WIDE: case CricketUtil.NO_BALL: case CricketUtil.BYE: case CricketUtil.LEG_BYE: case CricketUtil.PENALTY:
		            		runs += evnt.getEventRuns();
		                    break;

		            	case CricketUtil.LOG_WICKET:
		                    if (evnt.getEventRuns() > 0)
		                    {
		                    	runs += evnt.getEventRuns();
		                    }
		                    wicket += 1;
		                    break;

		            	case CricketUtil.LOG_ANY_BALL:
		            		runs += evnt.getEventRuns();
		                    if (evnt.getEventExtra() != null)
		                    {
		                    	runs += evnt.getEventExtraRuns();
		                    }
		                    if (evnt.getEventSubExtra() != null)
		                    {
		                    	runs += evnt.getEventSubExtraRuns();
		                    }
		                    break;										
						}
					}else if(evnt.getEventBowlerNo() != bowlerId && evnt.getEventBowlerNo() != 0) {
						bowler_found = false;
					}
				}
			}
		}
		
		for (BowlingCard boc : matchAllData.getMatch().getInning().get(inning - 1).getBowlingCard()) {
			if(boc.getPlayerId() == bowlerId) {
				name = boc.getPlayer().getTicker_name();
			}
		}
		
		return name + "," + runs + "," + wicket;
		
	}
	
}