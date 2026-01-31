package com.cricket.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
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

import org.springframework.beans.BeanUtils;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cricket.broadcaster.DOAD_TRIO;
import com.cricket.containers.Excel;
import com.cricket.containers.Scene;
import com.cricket.service.CricketService;
import com.cricket.model.*;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
public class IndexController 
{
	@Autowired
	CricketService cricketService;
	
	public static String expiry_date = "2026-12-31";
	public static String current_date = "";
	public static String error_message = "";
	public static long last_match_time_stamp = 0;
	public static DOAD_TRIO this_DOAD_TRIO;
	public static Excel this_Excel=new Excel();
	public static MatchAllData session_match = new MatchAllData();
	public static EventFile session_event = new EventFile();
	public static HeadToHead headToHead = new HeadToHead ();
	List<Statistics> session_statistics = new ArrayList<Statistics>();
	public static Configuration session_Configurations = new Configuration();
	public static MatchStats matchstats ;
	
	
	List<StatsType> st = new ArrayList<StatsType>();
	List<Statistics> stacs = new ArrayList<Statistics>();
	public Tournament tournament;
	public Tournament tournament1;
	public List<Tournament> this_series = new ArrayList<Tournament>();
	List<Scene> session_selected_scenes = new ArrayList<Scene>();
	public static String session_selected_broadcaster;
	public static Socket session_socket;
	public static PrintWriter print_writer;
	public static List<Tournament> pasttornament = new ArrayList<Tournament>();
	public static Inning inning;
	public List<Integer> PlayerId, PlayerIdIn;
	
	public Player player;
	public Player player2;
	public Statistics stat;
	public Statistics stat2;
	public StatsType statsType;
	public StatsType statsType2;
	
	public List<BestStats> top_batsman_beststats = new ArrayList<BestStats>();
	public List<BestStats> top_bowler_beststats = new ArrayList<BestStats>();
	public List<BestStats> tapeBall_beststats = new ArrayList<BestStats>();

	
	
	
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
			
			last_match_time_stamp = new File(CricketUtil.CRICKET_SERVER_DIRECTORY + CricketUtil.MATCHES_DIRECTORY 
					+ selectmatch).lastModified();
			
			//comented for noew
			session_match.getMatch().setMatchFileName(selectmatch);
//			session_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ, 
//					CricketUtil.SETUP + "," + CricketUtil.MATCH + "," + CricketUtil.EVENT, session_match));			
			session_match.getSetup().setMatchFileTimeStamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			
			JAXBContext.newInstance(Configuration.class).createMarshaller().marshal(session_Configurations, 
					new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.CONFIGURATIONS_DIRECTORY + CricketUtil.TRIO_XML));
			
			getDataFromExcelFile();
			st = cricketService.getAllStatsType();
			stacs = cricketService.getAllStats();
			headToHead = CricketFunctions.extractHeadToHead(session_match, cricketService);
			
			pasttornament =CricketFunctions.extractTournamentData("PAST_MATCHES_DATA", false,headToHead.getH2hPlayer(), 
					cricketService, session_match, null);
		//	past_tournament_stats = CricketFunctions.extractTournamentData("PAST_MATCHES_DATA", false, headToHead.getH2hPlayer(), cricketService, session_match, null);
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
					throws Exception 
	{
		//System.out.println("session_selected_broadcaster = " + session_selected_broadcaster);
		switch (whatToProcess.toUpperCase()) {
		case "GRAPHIC_OPTIONS":
			Map<String,String> map =getDataFromExcelFile();
			return JSONArray.fromObject(map.keySet()).toString();
		case "RE_READ_DATA":
			headToHead = CricketFunctions.extractHeadToHead(session_match, cricketService);
			pasttornament = CricketFunctions.extractTournamentData("PAST_MATCHES_DATA", false, headToHead.getH2hPlayer(), cricketService, session_match, null);
			
			matchstats = CricketFunctions.getAllEvents(session_match ,session_selected_broadcaster, session_match.getEventFile().getEvents());
			session_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ,
					CricketUtil.SETUP + "," + CricketUtil.MATCH + "," + CricketUtil.EVENT, session_match,true));
			st = cricketService.getAllStatsType();
			stacs = cricketService.getAllStats();
			return JSONObject.fromObject(session_match).toString();
		case "POPULATE_PREVIEW_BATPROFILE": case "POPULATE_PREVIEW_BALLLPROFILE": case "POPULATE_PREVIEW_OPENERRPROFILE":
			return JSONArray.fromObject(GetPreviewData(valueToProcess,null,session_match,whatToProcess)).toString();
		case "READ-MATCH-AND-POPULATE":
			if(session_match == null) {
				return JSONObject.fromObject(null).toString();
			}
			if(last_match_time_stamp != new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.MATCHES_DIRECTORY 
				+ session_match.getMatch().getMatchFileName()).lastModified()) {
				session_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ,
						 CricketUtil.MATCH + "," + CricketUtil.EVENT, session_match,true));
				last_match_time_stamp = new File(CricketUtil.CRICKET_DIRECTORY + CricketUtil.MATCHES_DIRECTORY 
						+ session_match.getMatch().getMatchFileName()).lastModified();
			}
			return JSONObject.fromObject(session_match).toString();
		default:
			switch (session_selected_broadcaster) {
			case CricketUtil.DOAD_TRIO:
//				session_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ,
//						CricketUtil.SETUP + "," + CricketUtil.MATCH + "," + CricketUtil.EVENT, session_match,true));
				this_DOAD_TRIO.ProcessGraphicOption(whatToProcess, cricketService, session_match, print_writer, session_selected_scenes, headToHead, pasttornament, valueToProcess);
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
	    @SuppressWarnings("unchecked")
		public <T> List<T> GetPreviewData(String whatToProcess,Configuration session_configuration, MatchAllData matchAllData,String valuetoproces) throws JsonMappingException, 
			JsonProcessingException, InterruptedException {
	    	
			List<String> statsData = new ArrayList<String>();
			List<String> statsData2 = new ArrayList<String>();
			stat = new Statistics();
			statsType = new StatsType();
			int k =0;
			String best = "-";
			String best1 = "-";
			
			switch ((valuetoproces.contains(",") ? valuetoproces.split(",")[0] : valuetoproces)) {
			case "POPULATE_PREVIEW_BATPROFILE": case "POPULATE_PREVIEW_BALLLPROFILE":
				System.out.println(whatToProcess);
				
				player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(whatToProcess.split(",")[0]), matchAllData);

				if(player == null) {
					statsData.add("PlayerProfile: Player Id not found [" + whatToProcess.split(",")[0] + "]");
					return (List<T>) statsData;
				}
				
				if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S1") || whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
					statsType = st.stream().filter(st -> st.getStats_short_name().equalsIgnoreCase(whatToProcess.split(",")[1])).findAny().orElse(null);
					if(statsType == null) {
						statsData.add("InfoBarPlayerProfile: Stats Type not found for profile [" + whatToProcess.split(",")[1] + "]");
						return (List<T>) statsData;
					}
					
					stat =stacs.stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
					if(stat == null) {
						statsData.add("InfoBarPlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[0] + "]");
						return (List<T>) statsData;
					}
					stat.setStats_type(statsType);
				}	
//				else if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
//					
//					Statistics statS1 = null, statS2=null; 
//					
//					statS1 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[0]), "ISPL S1", st, cricketService.getAllStats());
//				    statS2 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[0]), "ISPL S2", st, cricketService.getAllStats());
//				    
//				    if (statS1 == null && statS2 == null) {
//				    	statsData.add("InfoBarPlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[0] + "]");
//				    }
//				    
//				    BeanUtils.copyProperties(statS1, stat);
//				    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
//				    
//					statsType =st.stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
//					stat.setStats_type(statsType);
//					
//					stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), matchAllData, CricketUtil.FULL);
//					stat = CricketFunctions.updateStatisticsWithMatchData(stat, matchAllData, CricketUtil.FULL);
//				}
//				}else if(whatToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
//					
//					this_series = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, matchAllData, pasttornament);
//					tournament = this_series.stream().filter(st -> st.getPlayerId() == Integer.valueOf(whatToProcess.split(",")[0])).findAny().orElse(null);
//					
//					for(Tournament tourn : this_series) {
//						for(BestStats bs : tourn.getBatsman_best_Stats()) {
//							top_batsman_beststats.add(bs);
//						}
//						for(BestStats bfig : tourn.getBowler_best_Stats()) {
//							top_bowler_beststats.add(bfig);
//						}
//						for(BestStats tapeBall : tourn.getTapeBall_best_Stats()) {
//							tapeBall_beststats.add(tapeBall);
//						}
//					}
//					
//					Collections.sort(top_batsman_beststats, new CricketFunctions.PlayerBestStatsComparator());
//					Collections.sort(top_bowler_beststats, new CricketFunctions.PlayerBestStatsComparator());
//					Collections.sort(tapeBall_beststats, new CricketFunctions.PlayerBestStatsComparator());
//					
//					switch (valuetoproces.split(",")[0]) {
//					case "POPULATE_PREVIEW_BATPROFILE":
//						for(int j=0;j<= top_batsman_beststats.size()-1;j++) {
//							if(top_batsman_beststats.get(j).getPlayerId() == Integer.valueOf(whatToProcess.split(",")[0])) {
//								if(k == 0) {
//									k += 1;
//									if(top_batsman_beststats.get(j).getBestEquation() % 2 == 0) {
//										if(top_batsman_beststats.get(j).getBestEquation()/2 == 0) {
//											best = "-";
//										}else {
//											best = String.valueOf(top_batsman_beststats.get(j).getBestEquation()/2);
//										}
//									}else {
//										best = (top_batsman_beststats.get(j).getBestEquation()-1) / 2 + "*";
//									}
//									break;
//								}
//							}else {
//								best = "-";
//							}
//						}
//						break;
//					case "POPULATE_PREVIEW_BALLLPROFILE":
//						if(whatToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
//							for(int j=0;j<= top_bowler_beststats.size()-1;j++) {
//								if(top_bowler_beststats.get(j).getPlayerId() == Integer.valueOf(whatToProcess.split(",")[0])) {
//									if(k == 1) {
//										break;
//									}
//									if(k == 0) {
//										k += 1;
//										if(top_bowler_beststats.get(j).getBestEquation() % 1000 > 0) {
//											best = ((top_bowler_beststats.get(j).getBestEquation() / 1000) +1) + "-" + (1000 - (top_bowler_beststats.get(j).getBestEquation() % 1000));
//											break;
//										}
//										else if(top_bowler_beststats.get(j).getBestEquation() % 1000 < 0) {
//											best = (top_bowler_beststats.get(j).getBestEquation() / 1000) + "-" + Math.abs(top_bowler_beststats.get(j).getBestEquation());
//											break;
//										}
//										else if(top_bowler_beststats.get(j).getBestEquation() != 0) {
//											if(top_bowler_beststats.get(j).getBestEquation() % 1000 == 0) {
//												best = (top_bowler_beststats.get(j).getBestEquation() / 1000) + "-" + "0";
//												break;
//											}
//										}
//										break;
//									}
//								}else if(top_bowler_beststats.get(j).getPlayerId() != Integer.valueOf(whatToProcess.split(",")[0])) {
//									best = "-";
//								}
//							}
//						}
//						break;
//					}
//				}
				
//				if(whatToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 3");
//				}else if(whatToProcess.split(",")[1].equalsIgnoreCase("THIS_SERIES_TAPE_BALL")) {
//					statsData.add(player.getFull_name() + " - " + "IN ISPL SWING BALL OVERS");
//				}else if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 2");
//				}else if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 1");
//				}else {
//					statsData.add(player.getFull_name() + " - " + whatToProcess.split(",")[1].replace("_", " "));
//				}
				
				if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 2");
				}else if(whatToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")) {
					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 1");
				}else {
					statsData.add(""+ " - " + " NO PREVIEW ONLY SEASON 1 & 2 ");
				}
					
				
				
				switch (whatToProcess.split(",")[1]) {
//				case "THIS SERIES":
//					statsData.add("MATCHES," + tournament.getMatches());
//					switch ((valuetoproces.contains(",") ? valuetoproces.split(",")[0] : valuetoproces)) {
//					case "POPULATE_PREVIEW_BATPROFILE":
//						statsData.add("RUNS," + tournament.getRuns());
//						if(!CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0).isEmpty()) {
//							statsData.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
//						}else {
//							statsData.add("STRIKE RATE," + "-");
//						}
//						statsData.add("BEST," + best);
//						break;
//					case "POPULATE_PREVIEW_BALLLPROFILE":
//						statsData.add("WICKETS," + tournament.getWickets());
//						statsData.add("ECONOMY," + CricketFunctions.getEconomy(tournament.getRunsConceded(), tournament.getBallsBowled(), 2, "-"));
//						statsData.add("BEST," + best);
//						break;
//					}
//					break;
				default:
					statsData.add("MATCHES," + stat.getMatches());
					switch ((valuetoproces.contains(",") ? valuetoproces.split(",")[0] : valuetoproces)) {
					case "POPULATE_PREVIEW_BATPROFILE":
						statsData.add("RUNS," + stat.getRuns());
						statsData.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
						statsData.add("BEST," + stat.getBest_score());
						break;
					case "POPULATE_PREVIEW_BALLLPROFILE":
						statsData.add("WICKETS," + stat.getWickets());
						statsData.add("ECONOMY," + CricketFunctions.getEconomy(stat.getRuns_conceded(), stat.getBalls_bowled(), 2, "-"));
						statsData.add("BEST," + stat.getBest_figures());
						break;
					}
					break;
				
				}
				return (List<T>) statsData;
			case "POPULATE_PREVIEW_OPENERRPROFILE":
				
				player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(whatToProcess.split(",")[0]), matchAllData);
				player2 = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(whatToProcess.split(",")[1]), matchAllData);
				stat = new Statistics();
				stat2 = new Statistics();
				if(player == null) {
					statsData.add("PlayerProfile: Player Id not found [" + whatToProcess.split(",")[0] + "]");
				}
				if(player2 == null) {
					statsData2.add("PlayerProfile: Player Id not found [" + whatToProcess.split(",")[1] + "]");
				}
				
				if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S1") || whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
					statsType = st.stream().filter(st -> st.getStats_short_name().equalsIgnoreCase(whatToProcess.split(",")[2])).findAny().orElse(null);
					if(statsType == null) {
						statsData.add("PlayerProfile: Stats Type not found for profile [" + whatToProcess.split(",")[2] + "]");
					}
					
					stat = stacs.stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
					if(stat == null) {
						statsData.add("PlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[0] + "]");
					}
					stat.setStats_type(statsType);
					
					stat2 = stacs.stream().filter(st -> st.getPlayer_id() == player2.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
					if(stat2 == null) {
						statsData2.add("PlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[1] + "]");
						statsData.addAll(statsData2);
					}
					stat2.setStats_type(statsType);
					if(stat2 == null && stat == null) {
						return (List<T>) statsData;
					}
					
				}
//				else if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
//					
//					Statistics statS1 = null, statS2=null; 
//					
//					statS1 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[0]), "ISPL S1", st, cricketService.getAllStats());
//				    statS2 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[0]), "ISPL S2", st, cricketService.getAllStats());
//				    
//				    if (statS1 == null && statS2 == null) {
//				    	statsData.add("PlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[0] + "]");
//				    }
//				    
//				    BeanUtils.copyProperties(statS1, stat);
//				    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
//				    
//					statsType =st.stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
//					stat.setStats_type(statsType);
//					
//					stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), matchAllData, CricketUtil.FULL);
//					stat = CricketFunctions.updateStatisticsWithMatchData(stat, matchAllData, CricketUtil.FULL);
//					
//                     Statistics statS1P2 = null, statS2P2=null; 
//					
//                     statS1P2 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[1]), "ISPL S1", st, cricketService.getAllStats());
//                     statS2P2 = CricketFunctions.getStatsByType(Integer.valueOf(whatToProcess.split(",")[1]), "ISPL S2", st, cricketService.getAllStats());
//				    
//				    if (statS1P2 == null && statS2P2 == null) {
//				    	statsData.add("PlayerProfile: Stats not found for Player Id [" + whatToProcess.split(",")[1] + "]");
//				    }
//				    
//				    BeanUtils.copyProperties(statS1P2, stat2);
//				    stat2 = CricketFunctions.mergeIsplCareerStats(stat2, statS2P2);
//				    
//					statsType2 =st.stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
//					stat2.setStats_type(statsType2);
//					
//					stat2 = CricketFunctions.updateTournamentWithH2h(stat2, headToHead.getH2hPlayer(), matchAllData, CricketUtil.FULL);
//					stat2 = CricketFunctions.updateStatisticsWithMatchData(stat2, matchAllData, CricketUtil.FULL);
//				}else if(whatToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")){
//					
//					this_series = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, matchAllData, pasttornament);
//					tournament = this_series.stream().filter(st -> st.getPlayerId() == Integer.valueOf(whatToProcess.split(",")[0])).findAny().orElse(null);
//					tournament1 = this_series.stream().filter(st -> st.getPlayerId() == Integer.valueOf(whatToProcess.split(",")[1])).findAny().orElse(null);
//					
//					for(Tournament tourn : this_series) {
//						for(BestStats bs : tourn.getBatsman_best_Stats()) {
//							top_batsman_beststats.add(bs);
//						}
//						for(BestStats bfig : tourn.getBowler_best_Stats()) {
//							top_bowler_beststats.add(bfig);
//						}
//					}
//					
//					Collections.sort(top_batsman_beststats, new CricketFunctions.PlayerBestStatsComparator());
//					Collections.sort(top_bowler_beststats, new CricketFunctions.PlayerBestStatsComparator());
//					
//					switch (valuetoproces.split(",")[0]) {
//					case "POPULATE_PREVIEW_OPENERRPROFILE":
//						for(int j=0;j<= top_batsman_beststats.size()-1;j++) {
//							if(top_batsman_beststats.get(j).getPlayerId() == Integer.valueOf(whatToProcess.split(",")[0])) {
//								if(k == 0) {
//									k += 1;
//									if(top_batsman_beststats.get(j).getBestEquation() % 2 == 0) {
//										if(top_batsman_beststats.get(j).getBestEquation()/2 == 0) {
//											best = "-";
//										}else {
//											best = String.valueOf(top_batsman_beststats.get(j).getBestEquation()/2);
//										}
//									}else {
//										best = (top_batsman_beststats.get(j).getBestEquation()-1) / 2 + "*";
//									}
//									break;
//								}
//							}else {
//								best = "-";
//							}
//							if(top_batsman_beststats.get(j).getPlayerId() == Integer.valueOf(whatToProcess.split(",")[1])) {
//								if(k == 0) {
//									k += 1;
//									if(top_batsman_beststats.get(j).getBestEquation() % 2 == 0) {
//										if(top_batsman_beststats.get(j).getBestEquation()/2 == 0) {
//											best1 = "-";
//										}else {
//											best1 = String.valueOf(top_batsman_beststats.get(j).getBestEquation()/2);
//										}
//									}else {
//										best1 = (top_batsman_beststats.get(j).getBestEquation()-1) / 2 + "*";
//									}
//									break;
//								}
//							}else {
//								best1 = "-";
//							}
//						}
//						break;
//					}
//				}
				
				
				
				
				if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 2");
					statsData2.add(player2.getFull_name() + " - " + "ISPL SEASON 2");
				}else if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S1")) {
					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 1");
					statsData2.add(player2.getFull_name() + " - " + "ISPL SEASON 1");
				}else {
					statsData.add(""+ " - " + " NO PREVIEW ONLY SEASON 1 & 2 ");
					statsData2.add("" + " - " + " NO PREVIEW ONLY SEASON 1 & 2 ");
				}
				
				
				
				
				
				
				
				
//				if(whatToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 3");
//					statsData2.add(player2.getFull_name() + " - " + "ISPL SEASON 3");
//				}else if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 2");
//					statsData2.add(player2.getFull_name() + " - " + "ISPL SEASON 2");
//				}else if(whatToProcess.split(",")[2].equalsIgnoreCase("ISPL S1")) {
//					statsData.add(player.getFull_name() + " - " + "ISPL SEASON 1");
//					statsData2.add(player2.getFull_name() + " - " + "ISPL SEASON 1");
//				}else {
//					statsData.add(player.getFull_name() + " - " + whatToProcess.split(",")[0].replace("_", " "));
//					statsData2.add(player2.getFull_name() + " - " + whatToProcess.split(",")[1].replace("_", " "));
//				}
				
				switch (whatToProcess.split(",")[2]) {
//				case "THIS SERIES":
//					statsData.add("MATCHES," + tournament.getMatches());
//					statsData2.add("MATCHES," + tournament1.getMatches());
//					switch ((valuetoproces.contains(",") ? valuetoproces.split(",")[0] : valuetoproces)) {
//					case "POPULATE_PREVIEW_OPENERRPROFILE":
//						statsData.add("RUNS," + tournament.getRuns());
//						if(!CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0).isEmpty()) {
//							statsData.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
//						}else {
//							statsData.add("STRIKE RATE," + "-");
//						}
//						statsData.add("BEST," + best);
//						
//						statsData.add("SEPARATOR,---");
//						statsData2.add("RUNS," + tournament1.getRuns());
//						if(!CricketFunctions.generateStrikeRate(tournament1.getRuns(), tournament1.getBallsFaced(), 0).isEmpty()) {
//							statsData2.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(tournament1.getRuns(), tournament1.getBallsFaced(), 0));
//						}else {
//							statsData2.add("STRIKE RATE," + "-");
//						}
//						statsData2.add("BEST," + best1);
//						statsData.addAll(statsData2);
//						break;
//					}
//					break;
				default:
					statsData.add("MATCHES," + stat.getMatches());
					statsData2.add("MATCHES," + stat2.getMatches());
					
					switch ((valuetoproces.contains(",") ? valuetoproces.split(",")[0] : valuetoproces)) {
					case "POPULATE_PREVIEW_OPENERRPROFILE":
						statsData.add("RUNS," + stat.getRuns());
						statsData.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
						statsData.add("BEST," + stat.getBest_score());
						statsData.add("SEPARATOR,---");
						statsData2.add("RUNS," + stat2.getRuns());
						statsData2.add("STRIKE RATE," + CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 0));
						statsData2.add("BEST," + stat2.getBest_score());
						statsData.addAll(statsData2);
						break;
					}
					break;
				
				}
				return (List<T>) statsData;
			}
			return null;
		}
}
