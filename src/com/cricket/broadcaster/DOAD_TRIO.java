package com.cricket.broadcaster;

import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.cricket.containers.Scene;
import com.cricket.controller.IndexController;
import com.cricket.ispl.mvp_leaderBoard;
import com.cricket.model.BattingCard;
import com.cricket.model.BestStats;
import com.cricket.model.BowlingCard;
import com.cricket.model.Event;
import com.cricket.model.EventFile;
import com.cricket.model.Fixture;
import com.cricket.model.HeadToHead;
import com.cricket.model.Inning;
import com.cricket.model.Match;
import com.cricket.model.MatchAllData;
import com.cricket.model.Player;
import com.cricket.model.Setup;
import com.cricket.model.Statistics;
import com.cricket.model.StatsType;
import com.cricket.model.Team;
import com.cricket.model.Tournament;
import com.cricket.service.CricketService;
import com.cricket.util.CricketFunctions;
import com.cricket.util.CricketUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DOAD_TRIO extends Scene{
	
	public String mainCricketDirectory = CricketUtil.CRICKET_DIRECTORY; 
	public String secondCricketDirectory = CricketUtil.CRICKET2_DIRECTORY;
	public char return_key = (char) 13;
	public char line_feed = (char) 10;
	
	public Inning inning;
	public List<Fixture> FixturesList = new ArrayList<Fixture>();
	public Team team;
	public Player player;
	public Player player2;
	public mvp_leaderBoard mvp;
	public StatsType statsType;
	public StatsType statsType2;
	public List<StatsType> statsTypes;
	public List<StatsType> statsTypes2;
	public Statistics stat;
	public Statistics stat2;
	public List<Statistics> statistics;
	public List<Statistics> statistics2;
	public List<Tournament> PastDataToCrr;
	public List<Tournament> PastDataToCrr2;
	public List<BestStats> top_Batsman,topbowler= new ArrayList<BestStats>();
	public List<BestStats> top_Batsman2,topbowler2= new ArrayList<BestStats>();
	public List<Tournament> tournament_stats = new ArrayList<Tournament>();
	public Tournament tournament;
	public Tournament tournament2;
	
	double average = 0 ,average2 =0;
	String Data = "",hundred = "",fifty = "",strikeRate = "", thirty = "",
		batAverage = "",economy = "",best = "",runs = "",short_name = "", surName = "",profile ="",debut1 = "",debut2 = "" ,debut = "",debut3 ="";
	String Data2 = "",strikeRate2 = "", runs2 = "",  best2 = "",
			batAverage2 = "";
	
	public DOAD_TRIO() {
		super();
	}
	
	public DOAD_TRIO(String mainCricketDirectory) {
		super();
		this.mainCricketDirectory = mainCricketDirectory;
	}

	public Object ProcessGraphicOption(String whatToProcess, CricketService cricketService, MatchAllData match, PrintWriter print_writer, 
		HeadToHead headToHead, List<Tournament> pasttornament, String valueToProcess) throws Exception
	{
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
				if(new File(mainCricketDirectory + CricketUtil.SETUP_DIRECTORY + fixture.getMatchfilename() + ".json").exists()) {
					previous_match.setSetup(new ObjectMapper().readValue(new File(mainCricketDirectory + CricketUtil.SETUP_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), Setup.class));
					previous_match.setMatch(new ObjectMapper().readValue(new File(mainCricketDirectory + CricketUtil.MATCHES_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), Match.class));
				}
				if(new File(mainCricketDirectory + CricketUtil.EVENT_DIRECTORY + fixture.getMatchfilename() + ".json").exists()) {
					previous_match.setEventFile(new ObjectMapper().readValue(new File(mainCricketDirectory + CricketUtil.EVENT_DIRECTORY + 
							fixture.getMatchfilename() + ".json"), EventFile.class));
				}
				
				previous_match = CricketFunctions.populateMatchVariables(cricketService, CricketFunctions.readOrSaveMatchFile(CricketUtil.READ, 
						CricketUtil.SETUP + "," + CricketUtil.MATCH, previous_match,false));
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
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template Comparison");
			}else {
				DoadWriteToTrio(print_writer, "read_template Comparison-Drone");
			}
			
			populateComparison(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_TARGET":
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template Target");
			}else {
				DoadWriteToTrio(print_writer, "read_template Target_Drone");
			}
			
			populateTarget(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_PROJECTED":
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template Projected_Score");
			}else {
				DoadWriteToTrio(print_writer, "read_template Projected_Score_Drone");
			}
			
			populateProjected(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_TOSS":
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template Toss");
			}else {
				DoadWriteToTrio(print_writer, "read_template Toss_Drone");
			}
			
			populateToss(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_MATCHID":
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template MatchID");
			}else {
				DoadWriteToTrio(print_writer, "read_template MatchID_Drone");
			}
			populateMatchID(print_writer, match,cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_MOSTRUNS":
			tournament_stats = new ArrayList<Tournament>();
			tournament_stats = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, 
					match, pasttornament);
			//System.out.println( "pastdata" +  pasttornament.size());
			Collections.sort(tournament_stats,new CricketFunctions.BatsmenMostRunComparator());
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			populateMOSTRUNS(print_writer, match,cricketService,tournament_stats);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
				
		case "POPULATE_GRAPHICS_MOSTWKTS":
			tournament_stats = new ArrayList<Tournament>();
			tournament_stats = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, 
					match, pasttornament);
			Collections.sort(tournament_stats,new CricketFunctions.BowlerWicketsComparator());
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			populateMOSTWKTS(print_writer, match,cricketService,tournament_stats);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;	
		case "POPULATE_GRAPHICS_MOSTNINE":
			tournament_stats = new ArrayList<Tournament>();
			tournament_stats = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, 
					match, pasttornament);
			Collections.sort(tournament_stats,new CricketFunctions.BatsmanNinesComparator());
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			populateMOSTNINES(print_writer, match,cricketService,tournament_stats);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;	
		case "POPULATE_GRAPHICS_MOSTFOURS":
			tournament_stats = new ArrayList<Tournament>();
			tournament_stats = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, 
					match, pasttornament);
			Collections.sort(tournament_stats,new CricketFunctions.BatsmanFoursComparator());
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			populateMOSTFOURS(print_writer, match,cricketService,tournament_stats);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_MOSTSIXES":
			tournament_stats = new ArrayList<Tournament>();
			tournament_stats = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), cricketService, 
					match, pasttornament);
			Collections.sort(tournament_stats,new CricketFunctions.BatsmanSixesComparator());
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			populateMOSTSIXES(print_writer, match,cricketService,tournament_stats);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;			
		case "POPULATE_GRAPHICS_EQUATION":
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template Equation");
			}else {
				DoadWriteToTrio(print_writer, "read_template Equation_Drone");
			}
			
			populateEquation(print_writer, match,IndexController.headToHead);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_MVP":
			if(new File(mainCricketDirectory + CricketUtil.MVP).exists()) {
				mvp = (new ObjectMapper().readValue(new File(secondCricketDirectory + CricketUtil.MVP), mvp_leaderBoard.class));
			}
			
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template MVP_LEADERBOARD_New");
			}else {
				DoadWriteToTrio(print_writer, "read_template MVP_LEADERBOARD_New_Drone");
			}
			
			populateMVP(print_writer, match,cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;
		case "POPULATE_GRAPHICS_MVP_LEADERBOARD":	
			if(new File(mainCricketDirectory + CricketUtil.MVP).exists()) {
				mvp = (new ObjectMapper().readValue(new File(secondCricketDirectory + CricketUtil.MVP), mvp_leaderBoard.class));
			}
			
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")){
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template LeaderBoard_ActionImage_Drone");
			}
			
			populateMVPLEADERBOARD(print_writer, match,cricketService);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;	
		case "POPULATE_GRAPHICS_BOUNDARIES":
			DoadWriteToTrio(print_writer, "read_template BOUNDARIES");
			populateBoundaries(print_writer, match,cricketService,Integer.valueOf(valueToProcess.split(",")[1]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[0]);
			break;	
			
		case "POPULATE_DOUBLEMATCHID":	
			//System.out.println("Valuetoprocess " + valueToProcess);
			if(valueToProcess.split(",")[1].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template DoubleMatchID");
			}else {
				DoadWriteToTrio(print_writer, "read_template DoubleMatchID_Drone");
			}
			populateDoublematchid(print_writer, match,cricketService,valueToProcess.split(",")[0]);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[2]);
			break;	
		case "POPULATE_GRAPHICS_BATPROFILE":
			debut = "NO";
			for(Player ply: cricketService.getAllPlayer()) {
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[0])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut = "YES";
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
					}else {
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), 
									cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							topbowler = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
								for(BestStats bfig: tour.getBowler_best_Stats()) {
									topbowler.add(bfig);
								}
							}
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							Collections.sort(topbowler,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
						}
						
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[1])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[1] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
						}
						
						if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
							//System.out.println("CP");
							for(int i=0;i<=top_Batsman.size()-1;i++) {
								if(top_Batsman.get(i).getPlayerId() == player.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best = "-";
										}else {
											best = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best = "-";
								}
							}
						}else {
							if(stat.getRuns_conceded() == 0 || stat.getWickets() == 0) {
								Data = "-";
							}else {
								average = stat.getRuns_conceded()/stat.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data = df_bo.format(average);
							}
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(stat.getRuns() == 0) {
								runs = "-";
							}else {
								runs = String.format("%,d\n", stat.getRuns());
							}
							
							if(CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-").equalsIgnoreCase("0.00")) {
								batAverage = "-";
							}else {
								batAverage = CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-");
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_score().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_score();
							}
						}
					}
						}
					}
			
			if(valueToProcess.split(",")[2].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template Profile");
			}else {
				DoadWriteToTrio(print_writer, "read_template Profile_Drone");
			}
			
			populateProfile(print_writer, match,Integer.valueOf(valueToProcess.split(",")[4]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[3]);
			break;
		case "POPULATE_GRAPHICS_AUCTIONBATPROFILE":
			debut = "NO";
			for(Player ply: cricketService.getAllPlayer()) {
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[0])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut = "YES";
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
					}else {
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false, headToHead.getH2hPlayer(), 
									cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							topbowler = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
								for(BestStats bfig: tour.getBowler_best_Stats()) {
									topbowler.add(bfig);
								}
							}
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							Collections.sort(topbowler,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
						}
						
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[1])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[1] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
						}
						
						if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
							//System.out.println("CP");
							for(int i=0;i<=top_Batsman.size()-1;i++) {
								if(top_Batsman.get(i).getPlayerId() == player.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best = "-";
										}else {
											best = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best = "-";
								}
							}
						}else {
							if(stat.getRuns_conceded() == 0 || stat.getWickets() == 0) {
								Data = "-";
							}else {
								average = stat.getRuns_conceded()/stat.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data = df_bo.format(average);
							}
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(stat.getRuns() == 0) {
								runs = "-";
							}else {
								runs = String.format("%,d\n", stat.getRuns());
							}
							
							if(CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-").equalsIgnoreCase("0.00")) {
								batAverage = "-";
							}else {
								batAverage = CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-");
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_score().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_score();
							}
						}
					}
						}
					}
			
			if(valueToProcess.split(",")[2].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template Profile_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template Profile_ActionImageDrone");
			}
			
			populateProfileAuction(print_writer, match,Integer.valueOf(valueToProcess.split(",")[4]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[3]);
			break;
		case "POPULATE_GRAPHICS_OPENERBATPROFILE":
			//System.out.println("whattoprocess" + valueToProcess);
			debut1 ="NO";
			debut2 ="NO";
			for(Player ply: cricketService.getAllPlayer()) {
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[0])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut1 = "YES";
						
					}else {
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						player2 = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[1]), match);
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false,  headToHead.getH2hPlayer(), cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
							}
							
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
							tournament2 = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player2.getPlayerId()).findAny().orElse(null);
						}
						
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
							statsType2 = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
							
							//for player2
							if(statsType2 == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
							}
							stat2 = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player2.getPlayerId() && statsType2.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat2 == null) {
								return "populatePlayerProfile: No stats found for player id [" + player2.getFull_name() + "] from database is returning NULL";
							}
							stat2.setStats_type(statsType2);
						}
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
							
							//2
							statsType2 = new StatsType();
							stat2 = new Statistics();
							
							Statistics statSE1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statSE2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statSE1 == null && statSE2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[1] + "]";
						    }
						    stat2 = statSE1 != null ? statSE1 : statSE2;
						    stat2 = CricketFunctions.mergeIsplCareerStats(stat2, statSE2);
							
							statsType2 = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat2.setStats_type(statsType2);
							
							//System.out.println(stat2.getMatches());
							stat2 = CricketFunctions.updateTournamentWithH2h(stat2, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat2 = CricketFunctions.updateStatisticsWithMatchData(stat2, match, CricketUtil.FULL);
						}
						
						if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")) {
							for(int i=0;i<=top_Batsman.size()-1;i++) {
								if(top_Batsman.get(i).getPlayerId() == player.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best = "-";
										}else {
											best = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best = "-";
								}
								
								//2
								if(top_Batsman.get(i).getPlayerId() == player2.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best2 = "-";
										}else {
											best2 = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best2 = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best2 = "-";
								}
							}
						}else {
							if(stat.getRuns_conceded() == 0 || stat.getWickets() == 0) {
								Data = "-";
							}else {
								average = stat.getRuns_conceded()/stat.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data = df_bo.format(average);
							}
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(stat.getRuns() == 0) {
								runs = "-";
							}else {
								runs = String.format("%,d\n", stat.getRuns());
							}
							
							if(CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-").equalsIgnoreCase("0.00")) {
								batAverage = "-";
							}else {
								batAverage = CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-");
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_score().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_score();
							}
							
							//2
							
							if(stat2.getRuns_conceded() == 0 || stat2.getWickets() == 0) {
								Data2 = "-";
							}else {
								average2 = stat2.getRuns_conceded()/stat2.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data2 = df_bo.format(average2);
							}
							
							
							if(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate2 = "-";
							}else {
								strikeRate2 = String.valueOf(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 0));
							}
							
							if(stat2.getRuns() == 0) {
								runs2 = "-";
							}else {
								runs2 = String.format("%,d\n", stat2.getRuns());
							}
							
							if(stat2.getBest_score().equalsIgnoreCase("0")) {
								best2 = "-";
							}else {
								best2 = stat2.getBest_score();
							}

						}
					}
					
				}
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[1])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut2 = "YES";
					}else {
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						player2 = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[1]), match);
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false,  headToHead.getH2hPlayer(), cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
							}
							
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
							tournament2 = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player2.getPlayerId()).findAny().orElse(null);
						}
						
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
							statsType2 = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
							
							//for player2
							if(statsType2 == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
							}
							stat2 = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player2.getPlayerId() && statsType2.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat2 == null) {
								return "populatePlayerProfile: No stats found for player id [" + player2.getFull_name() + "] from database is returning NULL";
							}
							stat2.setStats_type(statsType2);
						}
						if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
							
							//2
							statsType2 = new StatsType();
							stat2 = new Statistics();
							
							Statistics statSE1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statSE2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statSE1 == null && statSE2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[1] + "]";
						    }
						    stat2 = statSE1 != null ? statSE1 : statSE2;
						    stat2 = CricketFunctions.mergeIsplCareerStats(stat2, statSE2);
							
							statsType2 = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat2.setStats_type(statsType2);
							
							//System.out.println(stat2.getMatches());
							stat2 = CricketFunctions.updateTournamentWithH2h(stat2, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat2 = CricketFunctions.updateStatisticsWithMatchData(stat2, match, CricketUtil.FULL);
						}
						
						if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")) {
							for(int i=0;i<=top_Batsman.size()-1;i++) {
								if(top_Batsman.get(i).getPlayerId() == player.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best = "-";
										}else {
											best = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best = "-";
								}
								
								//2
								if(top_Batsman.get(i).getPlayerId() == player2.getPlayerId()) {
									if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
										if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
											best2 = "-";
										}else {
											best2 = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
										}
									}else {
										best2 = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
									}
									break;
								}else {
									best2 = "-";
								}
							}
						}else {
							if(stat.getRuns_conceded() == 0 || stat.getWickets() == 0) {
								Data = "-";
							}else {
								average = stat.getRuns_conceded()/stat.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data = df_bo.format(average);
							}
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(stat.getRuns() == 0) {
								runs = "-";
							}else {
								runs = String.format("%,d\n", stat.getRuns());
							}
							
							if(CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-").equalsIgnoreCase("0.00")) {
								batAverage = "-";
							}else {
								batAverage = CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-");
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_score().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_score();
							}
							
							//2
							if(stat2.getRuns_conceded() == 0 || stat2.getWickets() == 0) {
								Data2 = "-";
							}else {
								average2 = stat2.getRuns_conceded()/stat2.getWickets();
								DecimalFormat df_bo = new DecimalFormat("0.00");
								Data2 = df_bo.format(average2);
							}
							
							
							if(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate2 = "-";
							}else {
								strikeRate2 = String.valueOf(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 0));
							}
							
							if(stat2.getRuns() == 0) {
								runs2 = "-";
							}else {
								runs2 = String.format("%,d\n", stat2.getRuns());
							}
							
							if(stat2.getBest_score().equalsIgnoreCase("0")) {
								best2 = "-";
							}else {
								best2 = stat2.getBest_score();
							}

						}
					}
				}
			}
			if(valueToProcess.split(",")[3].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template OpenerProfile");
			}else {
				DoadWriteToTrio(print_writer, "read_template OpenerProfile_Drone");
			}
			
			populateOpenerProfile(print_writer, match,Integer.valueOf(valueToProcess.split(",")[5]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[4]);
			break;
		case "POPULATE_GRAPHICS_DOUBLEBATPROFILE":
			//System.out.println("Valuetoprocess" + valueToProcess);
			//System.out.println("whattoprocess" + valueToProcess);
			player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
			player2 = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[1]), match);
			if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
				profile = "ISPL CAREER";
			}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1")){
				profile = "ISPL SEASON 1";
			}else if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")){
				profile = "ISPL SEASON 2";
			}else if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")){
				profile = "ISPL SEASON 3";
				PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false,  headToHead.getH2hPlayer(), cricketService, match, pasttornament);
				
				top_Batsman = new ArrayList<BestStats>();
				for(Tournament tour: PastDataToCrr) {
					for(BestStats bs:tour.getBatsman_best_Stats()) {
						top_Batsman.add(bs);
					}
				}
				
				//System.out.println(PastDataToCrr.size());
				Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
				
				tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
				tournament2 = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player2.getPlayerId()).findAny().orElse(null);
			}
			
			if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
				team = match.getSetup().getHomeTeam();
			} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
				team = match.getSetup().getAwayTeam();
			}
			if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[2].equalsIgnoreCase("ISPL S2")) {
				statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
				statsType2 = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[2])).findAny().orElse(null);
				if(statsType == null) {
					return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
				}
				stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
				if(stat == null) {
					return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
				}
				stat.setStats_type(statsType);
				
				//for player2
				if(statsType2 == null) {
					return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[2] + "]";
				}
				stat2 = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player2.getPlayerId() && statsType2.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
				if(stat2 == null) {
					return "populatePlayerProfile: No stats found for player id [" + player2.getFull_name() + "] from database is returning NULL";
				}
				stat2.setStats_type(statsType2);
			}
			if(valueToProcess.split(",")[2].equalsIgnoreCase("ISPL_CAREER")) {
				statsType = new StatsType();
				stat = new Statistics();
				
				Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
			    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
			    
			    if (statS1 == null && statS2 == null) {
			        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
			    }
			    stat = statS1 != null ? statS1 : statS2;
			    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
				
				statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
				stat.setStats_type(statsType);
				
				//System.out.println(stat.getMatches());
				stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
				stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
				
				//2
				statsType2 = new StatsType();
				stat2 = new Statistics();
				
				Statistics statSE1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
			    Statistics statSE2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[1]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
			    
			    if (statSE1 == null && statSE2 == null) {
			        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[1] + "]";
			    }
			    stat2 = statSE1 != null ? statSE1 : statSE2;
			    stat2 = CricketFunctions.mergeIsplCareerStats(stat2, statSE2);
				
				statsType2 = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
				stat2.setStats_type(statsType2);
				
				//System.out.println(stat2.getMatches());
				stat2 = CricketFunctions.updateTournamentWithH2h(stat2, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
				stat2 = CricketFunctions.updateStatisticsWithMatchData(stat2, match, CricketUtil.FULL);
			}
			
			if(valueToProcess.split(",")[2].equalsIgnoreCase("THIS SERIES")) {
				for(int i=0;i<=top_Batsman.size()-1;i++) {
					if(top_Batsman.get(i).getPlayerId() == player.getPlayerId()) {
						if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
							if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
								best = "-";
							}else {
								best = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
							}
						}else {
							best = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
						}
						break;
					}else {
						best = "-";
					}
					
					//2
					if(top_Batsman.get(i).getPlayerId() == player2.getPlayerId()) {
						if(top_Batsman.get(i).getBestEquation() % 2 == 0) {
							if(top_Batsman.get(i).getBestEquation() / 2  == 0) {
								best2 = "-";
							}else {
								best2 = String.valueOf(top_Batsman.get(i).getBestEquation() /2);
							}
						}else {
							best2 = (top_Batsman.get(i).getBestEquation()-1) / 2 + "*";
						}
						break;
					}else {
						best2 = "-";
					}
				}
			}else {
				if(stat.getRuns_conceded() == 0 || stat.getWickets() == 0) {
					Data = "-";
				}else {
					average = stat.getRuns_conceded()/stat.getWickets();
					DecimalFormat df_bo = new DecimalFormat("0.00");
					Data = df_bo.format(average);
				}
				if(stat.getFifties() == 0) {
					fifty = "-";
				}else {
					fifty = String.valueOf(stat.getFifties());
				}
				
				if(stat.getThirties() == 0) {
					thirty = "-";
				}else {
					thirty = String.valueOf(stat.getThirties());
				}
				
				if(stat.getHundreds() == 0) {
					hundred = "-";
				}else {
					hundred = String.valueOf(stat.getHundreds());
				}
				
				if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
					fifty = "0";
				}
				
				if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
					hundred = "0";
				}
				
				if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
					strikeRate = "-";
				}else {
					strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
				}
				
				if(stat.getRuns() == 0) {
					runs = "-";
				}else {
					runs = String.format("%,d\n", stat.getRuns());
				}
				
				if(CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-").equalsIgnoreCase("0.00")) {
					batAverage = "-";
				}else {
					batAverage = CricketFunctions.getAverage(stat.getInnings(), stat.getNot_out(), stat.getRuns(), 2, "-");
				}
				
				if(player.getSurname() == null) {
					surName = "";
				}else {
					surName = player.getSurname();
				}
				
				if(stat.getBest_score().equalsIgnoreCase("0")) {
					best = "-";
				}else {
					best = stat.getBest_score();
				}
				
				//2
				if(stat2.getRuns_conceded() == 0 || stat2.getWickets() == 0) {
					Data2 = "-";
				}else {
					average2 = stat2.getRuns_conceded()/stat2.getWickets();
					DecimalFormat df_bo = new DecimalFormat("0.00");
					Data2 = df_bo.format(average2);
				}
				
				
				if(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 1).trim().isEmpty()) {
					strikeRate2 = "-";
				}else {
					strikeRate2 = String.valueOf(CricketFunctions.generateStrikeRate(stat2.getRuns(), stat2.getBalls_faced(), 0));
				}
				
				if(stat2.getRuns() == 0) {
					runs2 = "-";
				}else {
					runs2 = String.format("%,d\n", stat2.getRuns());
				}
				
				if(stat2.getBest_score().equalsIgnoreCase("0")) {
					best2 = "-";
				}else {
					best2 = stat2.getBest_score();
				}

			}
			
			if(valueToProcess.split(",")[3].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template OpenerProfile");
			}else {
				DoadWriteToTrio(print_writer, "read_template OpenerProfile_Drone");
			}
			
			populateDoubleProfile(print_writer, match);
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[4]);
			break;	
		case "POPULATE_GRAPHICS_BALLPROFILE":
			
			debut3 = "NO";
			for(Player ply: cricketService.getAllPlayer()) {
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[0])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut3 = "YES";
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
					}else {
						//System.out.println("whattoprocess" + valueToProcess);
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false,  headToHead.getH2hPlayer(), cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							topbowler = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
								for(BestStats bfig: tour.getBowler_best_Stats()) {
									topbowler.add(bfig);
								}
							}
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							Collections.sort(topbowler,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
						}
						
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[1])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[1] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
						}
						
						
						
						if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
							for(int i=0;i<=topbowler.size()-1;i++) {
								if(topbowler.get(i).getPlayerId() == player.getPlayerId()) {
									if(topbowler.get(i).getBestEquation() % 1000 > 0) {
										best = ((topbowler.get(i).getBestEquation() / 1000) +1 ) + "-" + (1000-(topbowler.get(i).getBestEquation() % 1000));
									}else if(topbowler.get(i).getBestEquation() % 1000 < 0) {
										best = (topbowler.get(i).getBestEquation() / 1000) + "-" + Math.abs(topbowler.get(i).getBestEquation());
									}
									break;
								}else if(topbowler.get(i).getPlayerId() != player.getPlayerId()){
									best = "-";
								}
							}
						}else {
							if(CricketFunctions.getEconomy(stat.getRuns_conceded(), stat.getBalls_bowled(),2,"-").equalsIgnoreCase("0.00")) {
								economy = "-";
							}else {
								economy = CricketFunctions.getEconomy(stat.getRuns_conceded(), stat.getBalls_bowled(),2,"-");
							}
							
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_figures().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_figures();
							}
						}
					}
				}
			}
			if(valueToProcess.split(",")[2].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template Profile");
			}else {
				DoadWriteToTrio(print_writer, "read_template Profile_Drone");
			}
			populateBallProfile(print_writer, match,Integer.valueOf(valueToProcess.split(",")[4]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[3]);
			break;	
			
		case "POPULATE_GRAPHICS_AUCTIONBALLPROFILE":
			
			debut3 = "NO";
			for(Player ply: cricketService.getAllPlayer()) {
				if(ply.getPlayerId() == Integer.valueOf(valueToProcess.split(",")[0])) {
					if(ply.getDebut().equalsIgnoreCase("YES")) {
						debut3 = "YES";
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
					}else {
						//System.out.println("whattoprocess" + valueToProcess);
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							profile = "ISPL CAREER";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1")){
							profile = "ISPL SEASON 1";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")){
							profile = "ISPL SEASON 2";
						}else if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")){
							profile = "ISPL SEASON 3";
							
							PastDataToCrr = CricketFunctions.extractTournamentData("CURRENT_MATCH_DATA", false,  headToHead.getH2hPlayer(), cricketService, match, pasttornament);
							
							top_Batsman = new ArrayList<BestStats>();
							topbowler = new ArrayList<BestStats>();
							for(Tournament tour: PastDataToCrr) {
								for(BestStats bs:tour.getBatsman_best_Stats()) {
									top_Batsman.add(bs);
								}
								for(BestStats bfig: tour.getBowler_best_Stats()) {
									topbowler.add(bfig);
								}
							}
							//System.out.println(PastDataToCrr.size());
							Collections.sort(top_Batsman,new CricketFunctions.PlayerBestStatsComparator());
							Collections.sort(topbowler,new CricketFunctions.PlayerBestStatsComparator());
							
							tournament = PastDataToCrr.stream().filter(tour->tour.getPlayerId() == player.getPlayerId()).findAny().orElse(null);
						}
						
						player = CricketFunctions.getPlayerFromMatchData(Integer.valueOf(valueToProcess.split(",")[0]), match);
						if(match.getSetup().getHomeTeamId() == player.getTeamId()) {
							team = match.getSetup().getHomeTeam();
						} else if(match.getSetup().getAwayTeamId() == player.getTeamId()) {
							team = match.getSetup().getAwayTeam();
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S1") || valueToProcess.split(",")[1].equalsIgnoreCase("ISPL S2")) {
							statsType = cricketService.getAllStatsType().stream().filter(stype -> stype.getStats_short_name().equalsIgnoreCase(valueToProcess.split(",")[1])).findAny().orElse(null);
							if(statsType == null) {
								return "PopulateL3rdPlayerProfile: Stats Type not found for profile [" + valueToProcess.split(",")[1] + "]";
							}
							stat = cricketService.getAllStats().stream().filter(st -> st.getPlayer_id() == player.getPlayerId() && statsType.getStats_id() == st.getStats_type_id()).findAny().orElse(null);
							if(stat == null) {
								return "populatePlayerProfile: No stats found for player id [" + player.getFull_name() + "] from database is returning NULL";
							}
							stat.setStats_type(statsType);
						}
						if(valueToProcess.split(",")[1].equalsIgnoreCase("ISPL_CAREER")) {
							statsType = new StatsType();
							stat = new Statistics();
							
							Statistics statS1 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S1", cricketService.getAllStatsType(), cricketService.getAllStats());
						    Statistics statS2 = CricketFunctions.getStatsByType(Integer.valueOf(valueToProcess.split(",")[0]), "ISPL S2", cricketService.getAllStatsType(), cricketService.getAllStats());
						    
						    if (statS1 == null && statS2 == null) {
						        return "InfoBarPlayerProfile: Stats not found for Player Id [" + valueToProcess.split(",")[0] + "]";
						    }
						    stat = statS1 != null ? statS1 : statS2;
						    stat = CricketFunctions.mergeIsplCareerStats(stat, statS2);
							
							statsType = cricketService.getAllStatsType().stream().filter(st -> st.getStats_short_name().equalsIgnoreCase("D10")).findAny().orElse(null);
							stat.setStats_type(statsType);
							
							//System.out.println(stat.getMatches());
							stat = CricketFunctions.updateTournamentWithH2h(stat, headToHead.getH2hPlayer(), match, CricketUtil.FULL);
							stat = CricketFunctions.updateStatisticsWithMatchData(stat, match, CricketUtil.FULL);
						}
						
						
						
						if(valueToProcess.split(",")[1].equalsIgnoreCase("THIS SERIES")) {
							for(int i=0;i<=topbowler.size()-1;i++) {
								if(topbowler.get(i).getPlayerId() == player.getPlayerId()) {
									if(topbowler.get(i).getBestEquation() % 1000 > 0) {
										best = ((topbowler.get(i).getBestEquation() / 1000) +1 ) + "-" + (1000-(topbowler.get(i).getBestEquation() % 1000));
									}else if(topbowler.get(i).getBestEquation() % 1000 < 0) {
										best = (topbowler.get(i).getBestEquation() / 1000) + "-" + Math.abs(topbowler.get(i).getBestEquation());
									}
									break;
								}else if(topbowler.get(i).getPlayerId() != player.getPlayerId()){
									best = "-";
								}
							}
						}else {
							if(CricketFunctions.getEconomy(stat.getRuns_conceded(), stat.getBalls_bowled(),2,"-").equalsIgnoreCase("0.00")) {
								economy = "-";
							}else {
								economy = CricketFunctions.getEconomy(stat.getRuns_conceded(), stat.getBalls_bowled(),2,"-");
							}
							
							if(stat.getFifties() == 0) {
								fifty = "-";
							}else {
								fifty = String.valueOf(stat.getFifties());
							}
							
							if(stat.getThirties() == 0) {
								thirty = "-";
							}else {
								thirty = String.valueOf(stat.getThirties());
							}
							
							if(stat.getHundreds() == 0) {
								hundred = "-";
							}else {
								hundred = String.valueOf(stat.getHundreds());
							}
							
							if(fifty.equalsIgnoreCase("-") && !hundred.equalsIgnoreCase("-")) {
								fifty = "0";
							}
							
							if(hundred.equalsIgnoreCase("-") && !fifty.equalsIgnoreCase("-")) {
								hundred = "0";
							}
							
							if(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 1).trim().isEmpty()) {
								strikeRate = "-";
							}else {
								strikeRate = String.valueOf(CricketFunctions.generateStrikeRate(stat.getRuns(), stat.getBalls_faced(), 0));
							}
							
							if(player.getSurname() == null) {
								surName = "";
							}else {
								surName = player.getSurname();
							}
							
							if(stat.getBest_figures().equalsIgnoreCase("0")) {
								best = "-";
							}else {
								best = stat.getBest_figures();
							}
						}
					}
				}
			}
			if(valueToProcess.split(",")[2].equalsIgnoreCase("AR")) {
				DoadWriteToTrio(print_writer, "read_template Profile_ActionImage");
			}else {
				DoadWriteToTrio(print_writer, "read_template Profile_SctionImageDrone");
			}
			populateBallProfileAuction(print_writer, match,Integer.valueOf(valueToProcess.split(",")[4]));
			DoadWriteToTrio(print_writer, "saveas " + valueToProcess.split(",")[3]);
			break;
		case"POPULATE_GRAPHICS_FIXTURE":
			DoadWriteToTrio(print_writer, "read_template FF_Team_Schedule");
			//System.out.println("valueToProcess = " + valueToProcess);
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
			Map<String, String> mp = IndexController.getDataFromExcelFile(mainCricketDirectory);
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
				2, previous_match,CricketUtil.FULL, "", IndexController.session_Configurations.getBroadcaster(), true));
		
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
 		
 		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TEAM-REF-NAME-1 " + (match.getSetup().getHomeTeamId()==
 				 match.getMatch().getInning().get(1).getBowlingTeamId() ? match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4() ));
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME-2 "+(match.getSetup().getHomeTeamId()==
 				 match.getMatch().getInning().get(1).getBattingTeamId() ? match.getSetup().getHomeTeam().getTeamName4() : match.getSetup().getAwayTeam().getTeamName4() ));			 
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-MATCH-NUMBER "+"AFTER "+
		 CricketFunctions.OverBalls(match.getMatch().getInning().get(1).getTotalOvers(), match.getMatch().getInning().get(1).getTotalBalls())+" OVERS");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-DATA01 "+ CricketFunctions.compareInningData(match, "-", 1, match.getEventFile().getEvents()));
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-DATA02 "+ CricketFunctions.compareInningData(match, "-", 2, match.getEventFile().getEvents()));
	}
 	private void populateTarget(PrintWriter print_writer,MatchAllData match) {
 		
 		//targetrun
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TARGET "+  CricketFunctions.GetTargetData(match).getTargetRuns());
 		
 		//teamname
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+ match.getMatch().getInning().get(1).getBatting_team().getTeamBadge());
 		
 		//Target
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TARGET-HEAD "+  "TARGET");
 		
	}
 	private void populateProjected(PrintWriter print_writer,MatchAllData match) {
 		
 		String[] proj_score_rate = new String[CricketFunctions.projectedScore(match).size()];
	    for (int i = 0; i < CricketFunctions.projectedScore(match).size(); i++) {
	    	proj_score_rate[i] = CricketFunctions.projectedScore(match).get(i);
	    	//System.out.println(proj_score_rate[i]);
        }
 		for(Inning inn : match.getMatch().getInning()) {
			
			
			if(inn.getInningNumber() == 1 & inn.getIsCurrentInning().equalsIgnoreCase("YES")) {
				
				
				//team name 
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 100-TeamRefName "+  inn.getBatting_team().getTeamBadge());
		 		
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-HEAD "+ " @CRR (" +  proj_score_rate[0] + ")");
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 202-RUNS "+ proj_score_rate[1]);
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 301-HEAD "+ " @ " + proj_score_rate[2] + " RPO");
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 302-RUNS "+ proj_score_rate[3]);
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 401-HEAD "+ " @ " + proj_score_rate[4] + " RPO");
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 402-RUNS "+ proj_score_rate[5]);
		 		
		 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-HEADER "+  "PROJECTED SCORES");
		 		
			}
 		}
 		
 	}
 	private void populateToss(PrintWriter print_writer,MatchAllData match) {
 		
 		
 		if(match.getSetup().getTossWinningTeam() == match.getSetup().getHomeTeamId()) {
 			//hometeam
 	 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  match.getSetup().getHomeTeam().getTeamBadge());
 		}else {
 			//awayteam
 	 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  match.getSetup().getAwayTeam().getTeamBadge());
 		}
 		//elected
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TARGET-HEAD "+  "CHOSE TO");
 		
 		//result
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TARGET "+  match.getSetup().getTossWinningDecision());
 	}
 	private void populateMatchID(PrintWriter print_writer,MatchAllData match,CricketService cricketService) {
 		
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER "+  match.getSetup().getMatchIdent());
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TEAM-REF-NAME-1 "+  match.getSetup().getHomeTeam().getTeamBadge());
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME-2 "+  match.getSetup().getAwayTeam().getTeamBadge());

 	}
 	
 	private void populateMOSTRUNS(PrintWriter print_writer,MatchAllData match,CricketService cricketService, List<Tournament> tournament_stats) {
 		
 		 int rowId = 0;
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST RUNS");
 		
 		
 		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 1");
 		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
 		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
 		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
 		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
 		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
 		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD MTS");
 		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 802-STAT-HEAD RUNS");
 		for(int i = 0; i <= tournament_stats.size() - 1 ; i++) {
			  rowId = rowId + 1;
			if(rowId <= 5) {
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 4-SELECT_VALUE " + (rowId-1) + " " + 1);
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getMatches());
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 6-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getRuns());
				
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + tournament_stats.get(i).getPlayer().getFull_name());
				
				
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(i).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + tm.getTeamName1());
						
					}
				}
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(0).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  tm.getTeamBadge());
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
				      			 + tm.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ tournament_stats.get(0).getPlayer().getPhoto() + CricketUtil.PNG_EXTENSION);
						
					}
				}
			}
		}
 	}
 	
 	
 	private void populateMVPLEADERBOARD(PrintWriter print_writer,MatchAllData match,CricketService cricketService) {
		
		
 	  	List<mvp_leaderBoard.Player> finalFivePlayers = new ArrayList<>();
 	  	
 	  	//topdatalist
 	  	for (int i = 0; i < mvp.getData().getTop().size(); i++) {
 	  	    finalFivePlayers.add(mvp.getData().getTop().get(i));
 	  	}
 	  	for (int i = 0; i < 2 && i < mvp.getData().getList().size(); i++) {
 	  	    finalFivePlayers.add(mvp.getData().getList().get(i));
 	  	}
 	  	
 	  	 int rowId = 0;
 			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST VALUABLE PLAYER");
 			
 			
 			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 0");
 			 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
 			 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
 			 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
 			 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
 			 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
 			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD FINAL POINTS");
 			 
 	  	for (int j = 0; j < finalFivePlayers.size(); j++) {
 	  		String player_id = finalFivePlayers.get(j).getPlayerId();
 	  		rowId = rowId + 1;
 	  		
 	  		if(rowId <= 5) {
 	  			DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
 	  			
 	  			
 	  			DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + finalFivePlayers.get(j).getFinalPoints());
 	  			
 	  			player = cricketService.getAllPlayer().stream().filter(plyr -> Long.valueOf(player_id).equals(plyr.getOnlineId())).findAny().orElse(null);
 	  	  		
 	  	  		team = cricketService.getTeams().stream().filter(tm -> tm.getTeamId() == player.getTeamId()).findAny().orElse(null);
 	  	  		

 	  			DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + player.getFull_name());
 	  			
 	  			DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + team.getTeamName1());
 	  			
 	  			if(rowId == 1) {
	  				DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  team.getTeamBadge());
	  				
	  				DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
 			      			 + team.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ player.getPhoto() + CricketUtil.PNG_EXTENSION);	
	  			}
 	  		}
 	  	}
 	}
 	
 	
 	private void populateMOSTNINES(PrintWriter print_writer,MatchAllData match,CricketService cricketService, List<Tournament> tournament_stats) {
 		
		 int rowId = 0;
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST 9 STREET RUNS");
		
		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD MTS");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 802-STAT-HEAD NINES");
		for(int i = 0; i <= tournament_stats.size() - 1 ; i++) {
			  rowId = rowId + 1;
			if(rowId <= 5) {
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 4-SELECT_VALUE " + (rowId-1) + " " + 1);
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getMatches());
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 6-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getNines());
				
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + tournament_stats.get(i).getPlayer().getFull_name());
				
				
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(i).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + tm.getTeamName1());
						
					}
				}
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(0).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  tm.getTeamBadge());
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
				      			 + tm.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ tournament_stats.get(0).getPlayer().getPhoto() + CricketUtil.PNG_EXTENSION);
						
					}
				}
			}
		}
	}
 	
 	private void populateMOSTSIXES(PrintWriter print_writer,MatchAllData match,CricketService cricketService, List<Tournament> tournament_stats) {
 		
		 int rowId = 0;
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST SIXES");
		
		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD MTS");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 802-STAT-HEAD SIXES");
		for(int i = 0; i <= tournament_stats.size() - 1 ; i++) {
			  rowId = rowId + 1;
			if(rowId <= 5) {
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 4-SELECT_VALUE " + (rowId-1) + " " + 1);
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getMatches());
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 6-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getSixes());
				
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + tournament_stats.get(i).getPlayer().getFull_name());
				
				
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(i).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + tm.getTeamName1());
						
					}
				}
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(0).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  tm.getTeamBadge());
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
				      			 + tm.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ tournament_stats.get(0).getPlayer().getPhoto() + CricketUtil.PNG_EXTENSION);
						
					}
				}
			}
		}
	}
 	private void populateMOSTFOURS(PrintWriter print_writer,MatchAllData match,CricketService cricketService, List<Tournament> tournament_stats) {
 		
		 int rowId = 0;
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST FOURS");
		
		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD MTS");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 802-STAT-HEAD FOURS");
		for(int i = 0; i <= tournament_stats.size() - 1 ; i++) {
			  rowId = rowId + 1;
			if(rowId <= 5) {
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 4-SELECT_VALUE " + (rowId-1) + " " + 1);
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getMatches());
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 6-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getFours());
				
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + tournament_stats.get(i).getPlayer().getFull_name());
				
				
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(i).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + tm.getTeamName1());
						
					}
				}
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(0).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  tm.getTeamBadge());
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
				      			 + tm.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ tournament_stats.get(0).getPlayer().getPhoto() + CricketUtil.PNG_EXTENSION);
						
					}
				}
			}
		}
	}
 	
 	private void populateMOSTWKTS(PrintWriter print_writer,MatchAllData match,CricketService cricketService, List<Tournament> tournament_stats) {
 		
		 int rowId = 0;
		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update  000-MATCH-NUMBER MOST WICKETS");
		
		
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 700-SELECT-STAT-HEAD 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 0 1");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 1 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 2 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 3 0");
		 DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 0-SELECT_HIGHLIGHT 4 0");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 801-STAT-HEAD MTS");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 802-STAT-HEAD WKTS");
		for(int i = 0; i <= tournament_stats.size() - 1 ; i++) {
			  rowId = rowId + 1;
			if(rowId <= 5) {
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 4-SELECT_VALUE " + (rowId-1) + " " + 1);
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 1-SELECT_TEAM_NAME " + (rowId-1) + " " + 1);
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 5-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getMatches());
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 6-STAT-VALUE " + (rowId-1) + " " + tournament_stats.get(i).getWickets());
				
				
				DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 2-PLAYER-NAME " + (rowId-1) + " " + tournament_stats.get(i).getPlayer().getFull_name());
				
				
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(i).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "table:set_cell_value 900-DATA 3-TEAM-NAME " + (rowId-1) + " " + tm.getTeamName1());
						
					}
				}
				
				for(Team tm:cricketService.getTeams()) {
					if(tm.getTeamId() == tournament_stats.get(0).getPlayer().getTeamId()) {
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  tm.getTeamBadge());
						DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 501-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
				      			 + tm.getTeamBadge().toUpperCase().substring(0, 3) + "_"+ tournament_stats.get(0).getPlayer().getPhoto() + CricketUtil.PNG_EXTENSION);
						
					}
				}
			}
		}
		
	}
    private void populateEquation(PrintWriter print_writer,MatchAllData match,HeadToHead headToHeads) {
 		
 		
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName "+  match.getMatch().getInning().get(1).getBatting_team().getTeamBadge());

 		//run
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-DATA01 "+ CricketFunctions.GetTargetData(match).getRemaningRuns());
 		
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 006-DATA02 BALL"+ CricketFunctions.Plural(CricketFunctions.GetTargetData(match).getRemaningBall()).toUpperCase());
 		
 		//balls
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 005-DATA01 "+  CricketFunctions.GetTargetData(match).getRemaningBall());
 		DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-DATA02 RUN"+ CricketFunctions.Plural(CricketFunctions.GetTargetData(match).getRemaningRuns()).toUpperCase());
 	}
    
    private void populateOpenerProfile(PrintWriter print_writer,MatchAllData match,int inn) throws InterruptedException {
 		
	 
	 
	 if(debut1.equalsIgnoreCase("YES") && debut2.equalsIgnoreCase("YES")) {
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 500-SELECT-DATA-TYPE 0");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD ");
		 TimeUnit.MILLISECONDS.sleep(500);
	 }else if(debut1.equalsIgnoreCase("YES") && debut2.equalsIgnoreCase("NO")) {
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 500-SELECT-DATA-TYPE 1");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
		 if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
	 	 		
	 	 	 	//2
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  (tournament2.getMatches() != 0?String.valueOf(tournament2.getMatches()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  (tournament2.getRuns() != 0?String.valueOf(tournament2.getRuns()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  CricketFunctions.generateStrikeRate(tournament2.getRuns(), tournament2.getBallsFaced(), 0));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	}else {
	 	 		
	 	 	 	//2
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  stat2.getMatches());
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  runs2);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  strikeRate2);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	}
	 }else if(debut1.equalsIgnoreCase("NO") && debut2.equalsIgnoreCase("YES")) {
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 500-SELECT-DATA-TYPE 2");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
		 TimeUnit.MILLISECONDS.sleep(500);
		 
		 if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
	 	 		//System.out.println("Coming inside");
	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  (tournament.getRuns() != 0?String.valueOf(tournament.getRuns()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	 	
	 	 	}else {
	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  stat.getMatches());
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  runs);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  strikeRate);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	 	
	 	 	}
	 }else {
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 500-SELECT-DATA-TYPE 3");
		 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
		 if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
	 	 		//System.out.println("Coming inside");
	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  (tournament.getRuns() != 0?String.valueOf(tournament.getRuns()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
	 	 	 	
	 	 	 	//2
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  (tournament2.getMatches() != 0?String.valueOf(tournament2.getMatches()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  (tournament2.getRuns() != 0?String.valueOf(tournament2.getRuns()):"-"));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  CricketFunctions.generateStrikeRate(tournament2.getRuns(), tournament2.getBallsFaced(), 0));
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	}else {
	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  stat.getMatches());
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  runs);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  strikeRate);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
	 	 	 	
	 	 	 	//2
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  stat2.getMatches());
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  runs2);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  strikeRate2);
	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
	 	 	}
	 }
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 302-NAME "+  player.getTicker_name());
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 301-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
    			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
 	 	//2
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 402_NAME "+  player2.getTicker_name());
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 401-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
    			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player2.getPhoto()+ CricketUtil.PNG_EXTENSION);
 	 	
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
 		//head
 	 	
 	 	TimeUnit.MILLISECONDS.sleep(500);
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 0" +  " MTS");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 1" +  " RUNS");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 2" +  " SR");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 3" +  " BEST");
 	 	
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 503-SELECT-IMPACT 0");
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 504-SELECT-IMPACT 0");
 	 	
		if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
			switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
			case "IMP_IN":
				DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 503-SELECT-IMPACT 1");
				break;
			}
		}
		if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player2.getPlayerId()).isEmpty()) {
			switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player2.getPlayerId())) {
			case "IMP_IN":
				DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 504-SELECT-IMPACT 1");
				break;
			}
		}
 	}
   private void populateDoubleProfile(PrintWriter print_writer,MatchAllData match) {
 		
 		//System.out.println("COMONH INSIDE MAIn");
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 302-NAME "+  player.getTicker_name());
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 301-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
    			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
 	 	//2
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 402_NAME "+  player2.getTicker_name());
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 401-IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
    			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player2.getPhoto()+ CricketUtil.PNG_EXTENSION);
 	 	
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
 		//head
 	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
 		
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 0" +  " MTS");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 1" +  " RUNS");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 2" +  " SR");
 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATHEAD 3" +  " BEST");
 	 	
 	 	if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
 	 		//System.out.println("Coming inside");
 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  (tournament.getRuns() != 0?String.valueOf(tournament.getRuns()):"-"));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
 	 	 	
 	 	 	//2
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  (tournament2.getMatches() != 0?String.valueOf(tournament2.getMatches()):"-"));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  (tournament2.getRuns() != 0?String.valueOf(tournament2.getRuns()):"-"));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  CricketFunctions.generateStrikeRate(tournament2.getRuns(), tournament2.getBallsFaced(), 0));
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
 	 	}else {
 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 0 " +  stat.getMatches());
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 1 " +  runs);
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 2 " +  strikeRate);
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE1 3 " +  best);
 	 	 	
 	 	 	//2
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 0 " +  stat2.getMatches());
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 1 " +  runs2);
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 2 " +  strikeRate2);
 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 501-DATA-ALL STATVALUE2 3 " +  best2);
 	 	}
 	 	
 	}
   
      private void populateProfile(PrintWriter print_writer,MatchAllData match,int inn) throws InterruptedException {
    	  
    	 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update NAME "+  player.getTicker_name());
   	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
      			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
   	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
   	 	TimeUnit.MILLISECONDS.sleep(500);
   	    DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 0");
    	  
    	  
    	  if(debut.equalsIgnoreCase("YES")) {
    			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 0");
    			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ "" );
    			 if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
     				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
     				case "IMP_IN":
     					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
     					break;
     				}
     			}
    	  }else {
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 1");
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
    	 	 	TimeUnit.MILLISECONDS.sleep(500);
    	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 0" +  " MTS");
    	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 1" +  " RUNS");
    	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 2" +  " SR");
    	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 3" +  " BEST");
    	 	 	TimeUnit.MILLISECONDS.sleep(500);
    	 	 	
    	 	 	if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
    				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
    				case "IMP_IN":
    					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
    					break;
    				}
    			}
    	 	 	if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
    	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  (tournament.getRuns() != 0?String.valueOf(tournament.getRuns()):"-"));
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
    	 	 	}else {
    	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  stat.getMatches());
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  runs);
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  strikeRate);
    	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
    	 	 	}
    	  }
    }
      
      private void populateProfileAuction(PrintWriter print_writer,MatchAllData match,int inn) throws InterruptedException {
    	  
     	 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update NAME "+  player.getTicker_name());
    	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
       			+ team.getTeamBadge().toUpperCase().substring(0, 3) + "_" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
    	 	
    	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
    	 	TimeUnit.MILLISECONDS.sleep(500);
    	    DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 0");
     	  
     	  
     	  if(debut.equalsIgnoreCase("YES")) {
     			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 0");
     			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ "" );
     			 if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
      				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
      				case "IMP_IN":
      					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
      					break;
      				}
      			}
     	  }else {
     		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 1");
     		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
     	 	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 0" +  " MTS");
     	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 1" +  " RUNS");
     	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 2" +  " SR");
     	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 3" +  " BEST");
     	 	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	 	
     	 	 	if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
     				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
     				case "IMP_IN":
     					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
     					break;
     				}
     			}
     	 	 	if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
     	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  (tournament.getRuns() != 0?String.valueOf(tournament.getRuns()):"-"));
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  CricketFunctions.generateStrikeRate(tournament.getRuns(), tournament.getBallsFaced(), 0));
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
     	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	 	}else {
     	 	 		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  stat.getMatches());
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  runs);
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  strikeRate);
     	 	 	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
     	 	 	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	 	}
     	  }
     }
      
      private void populateBallProfileAuction(PrintWriter print_writer,MatchAllData match,int inn) throws InterruptedException {
     		
    	 //imagetobecorrected 
    	  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update NAME "+  player.getTicker_name());
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update IMAGE "+ "C:\\\\Images\\\\ISPL\\\\Action_Images\\"
        			+ team.getTeamBadge().toUpperCase().substring(0, 3) + "_" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
     	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 0");
    	  if(debut3.equalsIgnoreCase("YES")) {
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 0");
 			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ "" );
 			if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
				case "IMP_IN":
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
					break;
				}
			}
    	  }else {
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 1");
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
    		  TimeUnit.MILLISECONDS.sleep(500);
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 0" +  " MTS");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 1" +  " WKTS");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 2" +  " ECON");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 3" +  " BEST");
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 	
    		 	
    		 	if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
    				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
    				case "IMP_IN":
    					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
    					break;
    				}
    			}
    		 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
    	   		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  (tournament.getWickets() != 0?String.valueOf(tournament.getWickets()):"-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  CricketFunctions.getEconomy(tournament.getRunsConceded(), tournament.getBallsBowled(), 2, "-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 }else {
    	   		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  stat.getMatches());
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  stat.getWickets());
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  economy);
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 }
    	  }
   		}
     
      private void populateBallProfile(PrintWriter print_writer,MatchAllData match,int inn) throws InterruptedException {
   		
    	  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update NAME "+  player.getTicker_name());
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update IMAGE "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
        			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
     	 	TimeUnit.MILLISECONDS.sleep(500);
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-TeamRefName " + team.getTeamBadge() );
     	 	DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 0");
    	  if(debut3.equalsIgnoreCase("YES")) {
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 0");
 			 DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ "" );
 			if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
				case "IMP_IN":
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
					break;
				}
			}
    	  }else {
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 105-SELECT-DATA-TYPE 1");
    		  DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-HEAD "+ profile );
    		  TimeUnit.MILLISECONDS.sleep(500);
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 0" +  " MTS");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 1" +  " WKTS");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 2" +  " ECON");
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATHEAD 3" +  " BEST");
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 	
    		 	
    		 	if(!CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn, player.getPlayerId()).isEmpty()) {
    				switch(CricketFunctions.checkBatAndBallImpactInOutPlayerISPL(match.getEventFile().getEvents(), inn ,player.getPlayerId())) {
    				case "IMP_IN":
    					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 201-SELECT-IMPACT 1");
    					break;
    				}
    			}
    		 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 if(profile.equalsIgnoreCase("ISPL SEASON 3")) {
    	   		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  (tournament.getMatches() != 0?String.valueOf(tournament.getMatches()):"-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  (tournament.getWickets() != 0?String.valueOf(tournament.getWickets()):"-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  CricketFunctions.getEconomy(tournament.getRunsConceded(), tournament.getBallsBowled(), 2, "-"));
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 }else {
    	   		DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 0 " +  stat.getMatches());
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 1 " +  stat.getWickets());
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 2 " +  economy);
    	   	 	DoadWriteToTrio(print_writer, "table:set_cell_value 103-DATA-ALL STATVALUE 3 " +  best);
    	   	 	TimeUnit.MILLISECONDS.sleep(500);
    	   	 }
    	  }
   		}
    
      private void populateDoublematchid(PrintWriter print_writer,MatchAllData match,CricketService cricketService , String day) {
    	 int row_id = 1;
    	 String Date = "";
    	 Calendar cal = Calendar.getInstance();
 		if(day.toUpperCase().equalsIgnoreCase("Today")) {
			Date =  new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
		}
		else if(day.toUpperCase().equalsIgnoreCase("Tomorrow")) {
			cal.add(Calendar.DATE, +1);

		}
		for(int i = 0; i <= cricketService.getFixtures().size()-1; i++) {
			if(cricketService.getFixtures().get(i).getDate().equalsIgnoreCase(Date)) {
				DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 000-MATCH-NUMBER"+row_id + " "+  "MATCH " + cricketService.getFixtures().get(i).getMatchnumber());
				
				if(row_id == 1 ) {
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TEAM-REF-NAME-1 " + 
							cricketService.getTeams().get(cricketService.getFixtures().get(i).getHometeamid()-1).getTeamBadge());	
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME-2 "+ 
							cricketService.getTeams().get(cricketService.getFixtures().get(i).getAwayteamid()-1).getTeamBadge());		
				}else if(row_id == 2 ) {
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 003-TEAM-REF-NAME-3 " + 
							cricketService.getTeams().get(cricketService.getFixtures().get(i).getHometeamid()-1).getTeamBadge());	
					DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 004-TEAM-REF-NAME-4 "+ 
							cricketService.getTeams().get(cricketService.getFixtures().get(i).getAwayteamid()-1).getTeamBadge());		
				}
				
				
				row_id = row_id+1;
			}
		}
 	}
    
  private void populateBoundaries(PrintWriter print_writer,MatchAllData match,CricketService cricketService,int innn) {
	for(Inning inn : match.getMatch().getInning()) {
		if(inn.getInningNumber() == (innn-1)) {
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 001-TEAM-REF-NAME-1 " + inn.getBatting_team().getTeamBadge());
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 002-TEAM-REF-NAME-2 " + inn.getBatting_team().getTeamBadge());
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 107-TEXT " + "FOUR"+ CricketFunctions.Plural(inn.getTotalFours()).toUpperCase() +": " + inn.getTotalFours());
			DoadWriteToTrio(print_writer, "tabfield:set_value_no_update 108-TEXT " + "SIXES"+ ": " + inn.getTotalSixes());
		}
	}
 		
    
 	}
  
  
  
  
  
  
 private void populateMVP(PrintWriter print_writer,MatchAllData match,CricketService cricketService) {
 		
 		
    	List<mvp_leaderBoard.Player> finalFivePlayers = new ArrayList<>();
    	
    	//topdatalist
    	for (int i = 0; i < mvp.getData().getTop().size(); i++) {
    	    finalFivePlayers.add(mvp.getData().getTop().get(i));
    	}
    	for (int i = 0; i < 2 && i < mvp.getData().getList().size(); i++) {
    	    finalFivePlayers.add(mvp.getData().getList().get(i));
    	}

    	for (int j = 0; j < finalFivePlayers.size(); j++) {
    		String player_id = finalFivePlayers.get(j).getPlayerId();
    		
    		
    		DoadWriteToTrio(print_writer, "table:set_cell_value 001-PLAYERS POINTS "+ j + " "+ finalFivePlayers.get(j).getFinalPoints());
    		DoadWriteToTrio(print_writer, "table:set_cell_value 001-PLAYERS RANK "+ j + " " +  (j+1));
    		
    		player = cricketService.getAllPlayer().stream().filter(plyr -> Long.valueOf(player_id).equals(plyr.getOnlineId())).findAny().orElse(null);
    		
    		team = cricketService.getTeams().stream().filter(tm -> tm.getTeamId() == player.getTeamId()).findAny().orElse(null);

    		
    		DoadWriteToTrio(print_writer, "table:set_cell_value 001-PLAYERS NAME "+ j + " "+ player.getTicker_name());
    		
    		
    		DoadWriteToTrio(print_writer, "table:set_cell_value 001-PLAYERS IMAGE "+ j + " "+ "C:\\\\Images\\\\ISPL\\\\PHOTOS\\"
        			+ team.getTeamName4()+ "\\\\STRAIGHT_1024\\\\" +player.getPhoto()+ CricketUtil.PNG_EXTENSION);
    	}
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