var match_data;
function processWaitingButtonSpinner(whatToProcess) 
{
	switch (whatToProcess) {
	case 'START_WAIT_TIMER': 
		$('.spinner-border').show();
		$(':button').prop('disabled', true);
		break;
	case 'END_WAIT_TIMER': 
		$('.spinner-border').hide();
		$(':button').prop('disabled', false);
		break;
	}
	
}
function onPageLoadEvent(whichPage) {
	switch (whichPage) {
		case 'OUTPUT':
			$("#select_graphic_options_div").empty();
			document.getElementById('selected_inning').innerHTML = 'Which KeyPress: 1';
			document.getElementById('which_keypress').value = parseInt(1);
			break;
	}
}
 function displayStats(data) {
    let container = document.getElementById("stats-container");

    if (!container) {
        container = document.createElement("div");
        container.id = "stats-container";
        document.body.appendChild(container);
    }

    container.innerHTML = ""; // Clear old data

    if (!data || !Array.isArray(data) || data.length === 0) {
        container.innerHTML = "<div>No stats available</div>";
        return;
    }

    // Player name
    const playerName = data[0] || "Unknown Player";
    const nameDiv = document.createElement("div");
    nameDiv.id = "player-name";
    nameDiv.textContent = playerName;
    container.appendChild(nameDiv);

    // Stats line
    const statsLine = document.createElement("div");
    statsLine.id = "stats-line";

    const statsText = data.slice(1)
        .filter(item => item && !item.toLowerCase().includes("undefined"))
        .map(item => {
            const [label, value] = item.split(",");
            return `<strong>${label}</strong> - ${value}`;
        })
        .join(" &nbsp; ");

    statsLine.innerHTML = statsText;
    container.appendChild(statsLine);
}

 function displayStats1(data) {
    let container = document.getElementById("stats-container");

    if (!container) {
        container = document.createElement("div");
        container.id = "stats-container";
        document.body.appendChild(container);
    }

    container.innerHTML = "";

    if (!data || !Array.isArray(data) || data.length === 0) {
        container.innerHTML = "<div>No stats available</div>";
        return;
    }

    let currentStatsLine = null;

    data.forEach(item => {
        if (!item) return;

        // 🔹 SEPARATOR → close current stats line
        if (item.startsWith("SEPARATOR")) {
            if (currentStatsLine) {
                container.appendChild(currentStatsLine);
                currentStatsLine = null;
            }
            return;
        }

        // 🔹 PLAYER NAME / HEADER (no comma OR ISPL keyword)
        if (!item.includes(",") || item.includes("ISPL")) {

            // close previous stats line
            if (currentStatsLine) {
                container.appendChild(currentStatsLine);
                currentStatsLine = null;
            }

            const nameDiv = document.createElement("div");
            nameDiv.className = "player-name";
            nameDiv.textContent = item;
            container.appendChild(nameDiv);
            return;
        }

        // 🔹 STAT LINE
        if (!currentStatsLine) {
            currentStatsLine = document.createElement("div");
            currentStatsLine.className = "stats-line";
        }

        const [label, value] = item.split(",");
        if (!value || value.toLowerCase() === "undefined") return;

        const statHTML = `<strong>${label}</strong> - ${value}`;

        if (currentStatsLine.innerHTML !== "") {
            currentStatsLine.innerHTML += " &nbsp; ";
        }
        currentStatsLine.innerHTML += statHTML;
    });

    // append last stats line
    if (currentStatsLine) {
        container.appendChild(currentStatsLine);
    }
}




function initialiseForm(whatToProcess,dataToProcess)
{
	switch (whatToProcess) {
	case 'initialise':
		processUserSelection($('#select_broadcaster'));
		break;
	}
}
function processUserSelectionData(whatToProcess,dataToProcess){
	//alert(dataToProcess);
	switch (whatToProcess) {
	case 'LOGGER_FORM_KEYPRESS':
		switch (dataToProcess) {
		case 49:
			document.getElementById('which_keypress').value = parseInt(1); // DJ
			document.getElementById('selected_inning').innerHTML = 'Which keypress: ' + (parseInt(1));
			
			break;
		case 50: 
			document.getElementById('which_keypress').value = parseInt(2); // DJ
			document.getElementById('selected_inning').innerHTML = 'Which keypress: ' + (parseInt(2));
			
			break;		
		case 32:
			processCricketProcedures('CLEAR-ALL');
			break;
		case 80://P
			processCricketProcedures('LOAD_GRAPHICS');
			addItemsToList('LOAD_GRAPHICS-OPTION',null)
			break;
		case 112://F1 - graphics_options
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('GRAPHIC_OPTIONS');
			break;
		/*case 90://Z - ISPL 50-50
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_50_50_GRAPHIC_OPTIONS');
			break;*/
		/*case 88://X - TAPE BALL
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_BALL_GRAPHIC_OPTIONS');
			break;*/
		case 66://B - COmparison
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_COMPARISION_OPTIONS',null);
			break;
		case 88://X - Boundaries
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_BOUNDARIES_OPTIONS',null);
			break;	
		/*case 78://N - TAPE BALL
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_NEXT_BAT_GRAPHIC_OPTIONS');
			break;*/
		/*case 113://F2 - LINEUP
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_LINEUP_GRAPHIC_OPTIONS');
			break;*/
		/*case 114://F3 - PREVIOUS MATCH SUMMARY
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_PREVIOUS_MATCH_SUMMARY_GRAPHIC_OPTIONS');
			break;*/
		case 114://F3 - PREVIOUS MATCH SUMMARY
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_DOUBLEMATCHID_OPTIONS',null);
			break;
		case 113://F2 - MVP
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_MVP_OPTIONS',null);
			break;
		case 86: // v - MVP LeaderBoard
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_MVP_OPTIONS_LEADERBOARD', null);
			break;		
		case 65://a - Projected
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_POJECTED_OPTIONS',null);
			break;		
		case 116://F5 - TARGET
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_TARGET_OPTIONS',null);
			break;
		case 117://F6 - TOSS
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_TOSS_OPTIONS',null);
			break;
		case 67://c equation
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('ISPL_EQUATION_OPTIONS',null);
			break;
		case 119://F8 Bat profile
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('PLAYERPROFILEBAT_OPTIONS',null);
			break;
			
			
		/*case 74://j Auction profile
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('PLAYERMOSTRUN_OPTIONS',null);
			break;	*/
		case 78://N - DOUBLEPLAYERPROFILE
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('DOUBLEOPENERPROFILEBAT_OPTIONS',null);
			break;	
		case 90://Z - Debu
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('DOUBLEOPENERPROFILEBAT_OPTIONS',null);
			break;	
		case 120://F9 Ball profile
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('PLAYERPROFILEBALL_OPTIONS',null);
			break;
		case 75://k Auction profile
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('PLAYERPROFILEBALLAUCTION_OPTIONS',null);
			break;
		case 74://j Auction profile
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('PLAYERPROFILEBATAUCTION_OPTIONS',null);
			break;
		case 70://F - Fixture
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('MATCHID_OPTIONS',null);
			break;		
		case 115://F4 - MATCH SUMMARY
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('POPULATE_FF_CURRENT_MATCH_SUMMARY',null);
			break;
		case 77://m - most runs
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('POPULATE_MOSTRUNS',null);
			break;	
		/*case 76: // l - most wickets
		    $("#captions_div").hide();
		    $("#cancel_match_setup_btn").hide();
		    $("#expiry_message").hide();
		    addItemsToList('POPULATE_MOSTWKTS', null);
		    break;*/
		case 76: //caption l (Alt_r) 
			processCricketProcedures('RE_READ_DATA');
			break;
		case 87: // w - most wickets
		    $("#captions_div").hide();
		    $("#cancel_match_setup_btn").hide();
		    $("#expiry_message").hide();
		    addItemsToList('POPULATE_MOSTWKTS', null);
		    break;	
		/*case 70://F - Fixture
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('FIXTURE_GRAPHIC_OPTIONS');
			break;	*/
		}
		
		break;
	}
}
function processUserSelection(whichInput)
{	
	switch ($(whichInput).attr('name')) {	
	case 'animateout_graphic_btn':
		is_scorebug_on_screen = false;
		if(confirm('It will Also Delete Your Preview from Directory...\r\n \r\nAre You Sure To Animate Out? ') == true){
			processCricketProcedures('ANIMATE-OUT');	
		}
		break;
	case 'clearall_graphic_btn':
		if(confirm('Are You Sure To Clear All Scenes? ') == true){
			$('#select_graphic_options_div').empty();
			document.getElementById('select_graphic_options_div').style.display = 'none';
			//$("#captions_div").show();
			$("#main_captions_div").show();
			
			processCricketProcedures('CLEAR-ALL');
		}
		break;
		
	case 'cancel_graphics_btn':
		$('#select_graphic_options_div').empty();
		document.getElementById('select_graphic_options_div').style.display = 'none';
		$("#captions_div").show();
		$("#main_captions_div").show();
		
		break;
	case 'save_graphics':
		processCricketProcedures('SAVE_GRAPHICS');
		break;	
	case 'populate_graphics':
		processCricketProcedures('POPULATE_GRAPHICS');
		break;
	case 'populate_graphics_ispl_50_50':
		processCricketProcedures('POPULATE_GRAPHICS_ISPL_50_50');
		break;
	case 'FF_MATCH_SUMMARY':
		processCricketProcedures('POPULATE_GRAPHICS_ISPL_FF_MATCH_SUMMARY');
		break;
	case 'player_profile_bat':
		processCricketProcedures('POPULATE_GRAPHICS_BATPROFILE');
		break;
	case 'player_auctionprofile_bat':
		processCricketProcedures('POPULATE_GRAPHICS_AUCTIONBATPROFILE');
		break;	
	case 'player_profile_prview':
		processCricketProcedures('POPULATE_PREVIEW_BATPROFILE');
		break;	
		
	case 'playerball_profile_prview':
		processCricketProcedures('POPULATE_PREVIEW_BALLLPROFILE');
		break;	
		
	case 'playeropener_profile_prview':
		processCricketProcedures('POPULATE_PREVIEW_OPENERRPROFILE');
		break;
	case 'player_doubleprofile_bat':
		processCricketProcedures('POPULATE_GRAPHICS_DOUBLEBATPROFILE');
		break;
	case 'opner_doubleprofile_bat':	
		processCricketProcedures('POPULATE_GRAPHICS_OPENERBATPROFILE');
		break;
	case 'player_doublematchId':
		processCricketProcedures('POPULATE_DOUBLEMATCHID');
		break;	
	case 'player_profile_ball':
		processCricketProcedures('POPULATE_GRAPHICS_BALLPROFILE');
		break;	
	case 'player_profile_auctionball':
	    processCricketProcedures('POPULATE_GRAPHICS_AUCTIONBALLPROFILE');
		break;
	case 'FF_CURRENT_MATCH_SUMMARY':
		processCricketProcedures("POPULATE_ISPL_FF_MATCH_SUMMARY");
		break;
	case 'populate_graphics_ispl_ball':
		processCricketProcedures('POPULATE_GRAPHICS_ISPL_TAPE');
		break;
	case"populate_graphics_COMPARISION":
		processCricketProcedures('POPULATE_GRAPHICS_COMPARISION');
		break;
	case "populate_graphics_target":
		processCricketProcedures('POPULATE_GRAPHICS_TARGET');
		break;	
	case "populate_graphics_projected":
		processCricketProcedures('POPULATE_GRAPHICS_PROJECTED');
		break;
	case "populate_graphics_toss":
		processCricketProcedures('POPULATE_GRAPHICS_TOSS');
		break;
	case "populate_graphics_matchid":
		processCricketProcedures('POPULATE_GRAPHICS_MATCHID');
		break;
	case "populate_graphics_mostruns":
		processCricketProcedures('POPULATE_GRAPHICS_MOSTRUNS');
		break;
	case "populate_graphics_mostwkt":
		processCricketProcedures('POPULATE_GRAPHICS_MOSTWKTS');
		break;	
	case "populate_equation":
		processCricketProcedures('POPULATE_GRAPHICS_EQUATION');
		break;	
	case "populate_playerprofile_bat":
		processCricketProcedures('POPULATE_GRAPHICS_PLAYERPROFILE');
		break;
	case "populate_mvp":
		processCricketProcedures('POPULATE_GRAPHICS_MVP');
		break;	
	case "populate_mvp_leaderboard":
		processCricketProcedures('POPULATE_GRAPHICS_MVP_LEADERBOARD');
		break;		
	case "populate_boundary":
		processCricketProcedures('POPULATE_GRAPHICS_BOUNDARIES');
		break;	
	case"populate_graphics_nextToBat":
		processCricketProcedures('POPULATE_GRAPHICS_NEXT_TO_BAT');
		break;
	case "Line_Up":
		processCricketProcedures('POPULATE_GRAPHICS_LINEUP');
		break;
	case 'FF_FIXTURE':
		processCricketProcedures('POPULATE_GRAPHICS_FIXTURE');
		break;
	case 'load_scene_btn':
	    
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
      	document.initialise_form.submit();
      	document.getElementById('which_keypress').value = parseInt(1);
		break;
	}
}
function processCricketProcedures(whatToProcess)
{
	var valueToProcess;
	switch(whatToProcess) {
	
	case 'SAVE_GRAPHICS':
		valueToProcess = $('#savePointsTable').val();
		break;	
	case 'POPULATE_GRAPHICS':
		valueToProcess = $('#selectGraphics').val();
		break;
	case 'POPULATE_GRAPHICS_ISPL_50_50':
		valueToProcess = $('#whichScene').val() + ',' + $('#whichPlayer').val() + ',' + $('#challengeRuns').val() + ',' + $('#savePointsTable').val();
		break;
	case "POPULATE_GRAPHICS_ISPL_FF_MATCH_SUMMARY":
			valueToProcess = $('#selectMatchPromo').val() + ',' + $('#savePointsTable').val();
		break;
	case "POPULATE_GRAPHICS_BATPROFILE": case "POPULATE_GRAPHICS_AUCTIONBATPROFILE":
		valueToProcess = $('#selectPlayerName').val() + ',' + $('#selectProfile').val()+ ','+ $('#selectGraphictype').val() + ','+ $('#savePointsTable').val()
		+ ',' + document.getElementById('which_keypress').value; 
		break;
	case 'POPULATE_PREVIEW_BATPROFILE':	
		valueToProcess = $('#selectPlayerName').val() + ',' + $('#selectProfile').val()+ ','+ $('#selectGraphictype').val() + ','+ $('#savePointsTable').val()
		+ ',' + document.getElementById('which_keypress').value; 
		break;
		
	case 'POPULATE_PREVIEW_BALLLPROFILE':	
		valueToProcess = $('#selectPlayerName').val() + ',' + $('#selectProfile').val()+ ',' +  $('#selectGraphicType').val()+',' + $('#savePointsTable').val()
		+ ',' + document.getElementById('which_keypress').value;
		break;	
	case "POPULATE_PREVIEW_OPENERRPROFILE":
		valueToProcess = $('#selectPlayerName1').val() + ','+$('#selectPlayerName2').val() + ',' + $('#selectProfile').val()+ ','+ $('#selectGraphicType').val()+',' + $('#savePointsTable').val() 
		+ ',' + document.getElementById('which_keypress').value;
		break;	
	case "POPULATE_GRAPHICS_DOUBLEBATPROFILE":
		valueToProcess = $('#selectPlayerName1').val() + ','+$('#selectPlayerName2').val() + ',' + $('#selectProfile').val()+ ',' +  $('#selectgraphictype').val()+',' 
		+ $('#savePointsTable').val();
		break;
		
	case "POPULATE_GRAPHICS_OPENERBATPROFILE":
		valueToProcess = $('#selectPlayerName1').val() + ','+$('#selectPlayerName2').val() + ',' + $('#selectProfile').val()+ ','+ $('#selectGraphicType').val()+',' + $('#savePointsTable').val() 
		+ ',' + document.getElementById('which_keypress').value;
		break;
		
	case "POPULATE_DOUBLEMATCHID":
		valueToProcess =  $('#selectday').val()+ ','+ $('#selectGraphicType').val() +',' + $('#savePointsTable').val();
		break;	
	 case "POPULATE_GRAPHICS_BALLPROFILE": case "POPULATE_GRAPHICS_AUCTIONBALLPROFILE":
		valueToProcess = $('#selectPlayerName').val() + ',' + $('#selectProfile').val()+ ',' +  $('#selectGraphicType').val()+',' + $('#savePointsTable').val()
		+ ',' + document.getElementById('which_keypress').value;
		break;		
	case 'POPULATE_GRAPHICS_ISPL_TAPE':
		valueToProcess = $('#whichScene').val() + ',' + $('#whichPlayer').val() + ','  + $('#savePointsTable').val();
		break;
	case"POPULATE_GRAPHICS_COMPARISION":case"POPULATE_GRAPHICS_NEXT_TO_BAT":case "POPULATE_ISPL_FF_MATCH_SUMMARY": case "POPULATE_GRAPHICS_TARGET":  case "POPULATE_GRAPHICS_MVP_LEADERBOARD":
	case "POPULATE_GRAPHICS_MATCHID": case "POPULATE_GRAPHICS_MVP": case "POPULATE_GRAPHICS_TOSS": case "POPULATE_GRAPHICS_EQUATION": case "POPULATE_GRAPHICS_PROJECTED": case "POPULATE_GRAPHICS_MOSTRUNS": case "POPULATE_GRAPHICS_MOSTWKTS":
			valueToProcess = $('#savePointsTable').val() + ',' + $('#selectgraphictype').val();
		break;
	case "POPULATE_GRAPHICS_BOUNDARIES":
			valueToProcess = $('#savePointsTable').val() + ',' + document.getElementById('which_keypress').value;
		break;	
	case "POPULATE_GRAPHICS_LINEUP":
		valueToProcess = $('#whichScene').val() + ',' +$('#savePointsTable').val();
		break;
	case 'POPULATE_GRAPHICS_FIXTURE':
		valueToProcess = $('#selectTeam').val() + ',' +$('#savePointsTable').val();
		break;
	case 'READ-MATCH-AND-POPULATE':
		valueToProcess = $('#matchFileTimeStamp').val();
		break;
	}

	$.ajax({    
        type : 'Get',     
        url : 'processCricketProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + valueToProcess, 
        dataType : 'json',
        success : function(data) {
        	switch(whatToProcess) {
			case 'READ-MATCH-AND-POPULATE': case "RE_READ_DATA":
				if(data){
					match_data = data;
				}
				if(whatToProcess == "RE_READ_DATA"){
					alert("Data is Loaded");
				}
				break;
			case 'SAVE_GRAPHICS':
				$('#select_graphic_options_div').empty();
				document.getElementById('select_graphic_options_div').style.display = 'none';
				$("#main_captions_div").show();
				break;
			case 'POPULATE_PREVIEW_BATPROFILE':	
			     addItemsToList('DISPLAYBATPREVIEW',data);
				break;
			case 'POPULATE_PREVIEW_OPENERRPROFILE':	
			     addItemsToList('DISPLAYBATPREVIEW1',data);
				break;
			case 'POPULATE_PREVIEW_BALLLPROFILE':
				addItemsToList('DISPLAYBATPREVIEW',data);
				break;
			case 'GRAPHIC_OPTIONS':
				addItemsToList('GRAPHICS',data);
				break;
        	}
			processWaitingButtonSpinner('END_WAIT_TIMER');
	    },    
	    error : function(e) {    
	  	 	console.log('Error occured in ' + whatToProcess + ' with error description = ' + e);     
	    }    
	});
}
function addItemsToList(whatToProcess, dataToProcess)
{
	var select,option,header_text,div,table,tbody,row;
	switch(whatToProcess){
		
		case 'DISPLAYBATPREVIEW':
			displayStats(dataToProcess);
		 	break;
		case 'DISPLAYBATPREVIEW1':
			displayStats1(dataToProcess);
		 	break;
		case 'ISPL_DOUBLEMATCHID_OPTIONS':
		  $('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'DOUBLE MATCH ID';
			
			 select = document.createElement('select');
			   select.id = 'selectday';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'Today';
				option.text = 'Today';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Tomorrow';
				option.text = 'Tomorrow';
				select.appendChild(option);

				/*option = document.createElement('option');
				option.value = 'Day After';
				option.text = 'Day After';
				select.appendChild(option);
				*/
				row.insertCell(0).appendChild(select); 
				
				select = document.createElement('select');
			   select.id = 'selectGraphicType';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);

				/*option = document.createElement('option');
				option.value = 'Day After';
				option.text = 'Day After';
				select.appendChild(option);
				*/
				row.insertCell(1).appendChild(select); 
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				 row.insertCell(2).appendChild(select);

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_doublematchId';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(3).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
		break;
		case 'PLAYERPROFILEBALL_OPTIONS':
		   $('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BALL PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					inn.bowlingCard.forEach(function(boc){
						if(boc.status == 'CURRENTBOWLER'){
							option = document.createElement('option');
							option.value = boc.player.playerId;
							option.text = boc.player.full_name;
							select.appendChild(option);
						}
					});
					
					if(inn.bowlingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
			 row.insertCell(0).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(1).appendChild(select); 
				
				select = document.createElement('select');
			   select.id = 'selectGraphicType';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select);
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				 row.insertCell(3).appendChild(select);
				 
				 option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'playerball_profile_prview';
				option.value = 'Preview';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    div = document.createElement('div');
			    div.append(option);
			    row.insertCell(4).appendChild(div);

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_profile_ball';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(5).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
		   break;
		   
		    case 'DOUBLEOPENERPROFILEBAT_OPTIONS':
	    	$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BAT PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName1';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(0).appendChild(select);
             
             select = document.createElement('select');
			select.id = 'selectPlayerName2';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(1).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select); 
				
				select = document.createElement('select');
			   select.id = 'selectGraphicType';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				
				row.insertCell(3).appendChild(select); 
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				 row.insertCell(4).appendChild(select);
				 
				 option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'playeropener_profile_prview';
				option.value = 'Preview';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    div = document.createElement('div');
			    div.append(option);
			    row.insertCell(5).appendChild(div);

		    	option = document.createElement('input');
			    option.type = 'button';

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'opner_doubleprofile_bat';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(6).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
			break;
		   case 'DOUBLEPLAYERPROFILEBAT_OPTIONS':
	    	$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BAT PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName1';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(0).appendChild(select);
             
             select = document.createElement('select');
			select.id = 'selectPlayerName2';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(1).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select); 
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				 row.insertCell(3).appendChild(select);

				select = document.createElement('select');
			   select.id = 'selectgraphictype';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				row.insertCell(4).appendChild(select);


		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_doubleprofile_bat';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(5).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
			break;
		case 'PLAYERPROFILEBAT_OPTIONS': 
	    	$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BAT PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(0).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(1).appendChild(select); 
				
				 select = document.createElement('select');
			   select.id = 'selectGraphictype';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select); 
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				row.insertCell(3).appendChild(select);
				 
				option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_profile_prview';
				option.value = 'Preview';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    div = document.createElement('div');
			    div.append(option);
			    row.insertCell(4).appendChild(div);

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_profile_bat';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(5).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
			break;
		case "PLAYERPROFILEBALLAUCTION_OPTIONS":
		 	 $('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BALL PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					inn.bowlingCard.forEach(function(boc){
						if(boc.status == 'CURRENTBOWLER'){
							option = document.createElement('option');
							option.value = boc.player.playerId;
							option.text = boc.player.full_name;
							select.appendChild(option);
						}
					});
					
					if(inn.bowlingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
			 row.insertCell(0).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(1).appendChild(select); 
				
				select = document.createElement('select');
			   select.id = 'selectGraphicType';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select);
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				 row.insertCell(3).appendChild(select);
				 
				 option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'playerball_profile_prview';
				option.value = 'Preview';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    div = document.createElement('div');
			    div.append(option);
			    row.insertCell(4).appendChild(div);

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_profile_auctionball';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(5).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
		   break;	
		case 'PLAYERPROFILEBATAUCTION_OPTIONS':
	    	$('#select_graphic_options_div').empty();
	
			header_text = document.createElement('h6');
			header_text.innerHTML = 'Select Graphic Options';
			document.getElementById('select_graphic_options_div').appendChild(header_text);
			
			table = document.createElement('table');
			table.setAttribute('class', 'table table-bordered');
					
			tbody = document.createElement('tbody');
	
			table.appendChild(tbody);
			document.getElementById('select_graphic_options_div').appendChild(table);
			
			row = tbody.insertRow(tbody.rows.length);
		    header_text.innerHTML = 'BAT PLAYER PROFILE';
			
			select = document.createElement('select');
			select.id = 'selectPlayerName';
			select.name = select.id;
			
			match_data.match.inning.forEach(function(inn){
				if(inn.inningNumber == document.getElementById('which_keypress').value){
					if(inn.battingTeamId == match_data.setup.homeTeamId){
						match_data.setup.homeSquad.forEach(function(hs){
							option = document.createElement('option');
							option.value = hs.playerId;
							option.text = hs.full_name;
							select.appendChild(option);
						});
						match_data.setup.homeOtherSquad.forEach(function(hos){
							option = document.createElement('option');
							option.value = hos.playerId;
							option.text = hos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}else {
						match_data.setup.awaySquad.forEach(function(as){
							option = document.createElement('option');
							option.value = as.playerId;
							option.text = as.full_name;
							select.appendChild(option);
						});
						match_data.setup.awayOtherSquad.forEach(function(aos){
							option = document.createElement('option');
							option.value = aos.playerId;
							option.text = aos.full_name  + ' (OTHER)';
							select.appendChild(option);
						});
					}
				}
			});
             
             row.insertCell(0).appendChild(select);
             
               select = document.createElement('select');
			   select.id = 'selectProfile';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'ISPL S1';
				option.text = 'ISPL S1';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL S2';
				option.text = 'ISPL S2';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'ISPL_CAREER';
				option.text = 'ISPL CAREER';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'THIS SERIES';
				option.text = 'THIS SERIES';
				select.appendChild(option);
				
				row.insertCell(1).appendChild(select); 
				
				 select = document.createElement('select');
			   select.id = 'selectGraphictype';
			   select.name = select.id;
			
				option = document.createElement('option');
				option.value = 'AR';
				option.text = 'AR';
				select.appendChild(option);
				
				option = document.createElement('option');
				option.value = 'Drone';
				option.text = 'Drone';
				select.appendChild(option);
				
				row.insertCell(2).appendChild(select); 
				
				select = document.createElement('input');
				select.type = "text";
				select.id = 'savePointsTable';
				select.value = '';
				
				header_text = document.createElement('label');
				header_text.innerHTML = 'Page No.';
				header_text.htmlFor = select.id;
				
				row.insertCell(3).appendChild(select);
				 
				option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_profile_prview';
				option.value = 'Preview';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    div = document.createElement('div');
			    div.append(option);
			    row.insertCell(4).appendChild(div);

		    	option = document.createElement('input');
			    option.type = 'button';
				
				option = document.createElement('input');
			    option.type = 'button';
				option.name = 'player_auctionprofile_bat';
				option.value = 'populate';
			    option.id = option.name;
			    option.setAttribute('onclick',"processUserSelection(this)");
			    
			    div = document.createElement('div');
			    div.append(option);
			
				option = document.createElement('input');
				option.type = 'button';
				option.name = 'cancel_graphics_btn';
				option.id = option.name;
				option.value = 'Cancel';
				option.setAttribute('onclick','processUserSelection(this)');
			
			    div.append(option);
			    
			    row.insertCell(5).appendChild(div);
			    
     			 document.getElementById('select_graphic_options_div').style.display = '';
			break;
		case 'LOAD_GRAPHICS-OPTION':case 'GRAPHICS': case 'ISPL_50_50_OPTIONS': case 'ISPL_BALL_OPTIONS':case 'ISPL_NEXT_BAT_OPTIONS':case'ISPL_COMPARISION_OPTIONS':
		case "ISPL_LINEUP_OPTIONS":case "ISPL_PREVIOUS_MATCH_SUMMARY_OPTIONS":case 'POPULATE_FF_CURRENT_MATCH_SUMMARY': case 'FIXTURE_OPTIONS': 
		case "ISPL_TARGET_OPTIONS": case "ISPL_POJECTED_OPTIONS": case "ISPL_TOSS_OPTIONS": case "ISPL_EQUATION_OPTIONS": case "ISPL_MVP_OPTIONS": case "MATCHID_OPTIONS": case "POPULATE_MOSTRUNS": case "POPULATE_MOSTWKTS":
		case "ISPL_BOUNDARIES_OPTIONS": case "ISPL_MVP_OPTIONS_LEADERBOARD":
			switch ($('#select_broadcaster').val().toUpperCase()){
				case 'DOAD_TRIO':
					$('#select_graphic_options_div').empty();
	
					header_text = document.createElement('h6');
					header_text.innerHTML = 'Select Graphic Options';
					document.getElementById('select_graphic_options_div').appendChild(header_text);
					
					table = document.createElement('table');
					table.setAttribute('class', 'table table-bordered');
							
					tbody = document.createElement('tbody');
			
					table.appendChild(tbody);
					document.getElementById('select_graphic_options_div').appendChild(table);
					
					row = tbody.insertRow(tbody.rows.length);
					switch(whatToProcess){
						case "ISPL_PREVIOUS_MATCH_SUMMARY_OPTIONS":
							select = document.createElement('select');
							select.id = 'selectMatchPromo';
							select.name = select.id;
							console.log(dataToProcess);
							dataToProcess.forEach(function(oop){	
								option = document.createElement('option');
					            option.value = oop.matchnumber;
					            option.text = oop.matchnumber + ' - ' +oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1 ;
					            select.appendChild(option);
					            console.log(oop.matchnumber + ' - ' + oop.home_Team.teamName1 + ' Vs ' + oop.away_Team.teamName1);
					        });
						    row.insertCell(0).appendChild(select);

							select = document.createElement('input');
							select.type = "text";
							select.id = 'savePointsTable';
							select.value = '';
							
							header_text = document.createElement('label');
							header_text.innerHTML = 'Page No.';
							header_text.htmlFor = select.id;
							
							row.insertCell(1).appendChild(header_text).appendChild(select);
							
							option = document.createElement('input');
						    option.type = 'button';
							option.name = 'FF_MATCH_SUMMARY';
							option.value = 'populate';
						    option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
						
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
						    
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
						case 'FIXTURE_OPTIONS':
							select = document.createElement('select');
							select.id = 'selectTeam';
							select.name = select.id;
							dataToProcess.forEach(function(tm){	
							option = document.createElement('option');
				            option.value = tm.teamId;
				            option.text = tm.teamName1;
				            select.appendChild(option);
				            console.log(tm.teamName1);
					        });
						    row.insertCell(0).appendChild(select);

							select = document.createElement('input');
							select.type = "text";
							select.id = 'savePointsTable';
							select.value = '';
							
							header_text = document.createElement('label');
							header_text.innerHTML = 'Page No.';
							header_text.htmlFor = select.id;
							
							row.insertCell(1).appendChild(header_text).appendChild(select);
							
							option = document.createElement('input');
						    option.type = 'button';
							option.name = 'FF_FIXTURE';
							option.value = 'populate';
						    option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
						
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
						    
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
						case "ISPL_LINEUP_OPTIONS":
							select = document.createElement('select');
						    select.style.width = '250px';
						    select.id = 'whichScene';
						    select.name = select.id;

						    option = document.createElement('option');
				         	option.value = dataToProcess.homeTeam.teamId;
				        	option.text = dataToProcess.homeTeam.teamName1;
				         	select.appendChild(option);
				         	
				         	option = document.createElement('option');
				         	option.value = dataToProcess.awayTeam.teamId;
				        	option.text = dataToProcess.awayTeam.teamName1;
				         	select.appendChild(option);
				         	
						    row.insertCell(0).appendChild(select);
					    
							select = document.createElement('input');
							select.type = "text";
							select.id = 'savePointsTable';
							select.value = '';
							
							header_text = document.createElement('label');
							header_text.innerHTML = 'Page No.';
							header_text.htmlFor = select.id;
							
							row.insertCell(1).appendChild(header_text).appendChild(select);
							
							option = document.createElement('input');
						    option.type = 'button';
							option.name = 'Line_Up';
							option.value = 'populate';
						    option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
						
						    div.append(option);
						    
						    row.insertCell(2).appendChild(div);
						    
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
						case 'LOAD_GRAPHICS-OPTION':
							select = document.createElement('input');
							select.type = "text";
							select.id = 'savePointsTable';
							select.value = '';
							
							header_text = document.createElement('label');
							header_text.innerHTML = 'Page No.';
							header_text.htmlFor = select.id;
							
							row.insertCell(0).appendChild(header_text).appendChild(select);
							
							option = document.createElement('input');
						    option.type = 'button';
							option.name = 'save_graphics';
							option.value = 'Save graphics';
						    option.id = option.name;
						    option.setAttribute('onclick',"processUserSelection(this)");
						    
						    div = document.createElement('div');
						    div.append(option);
						
							option = document.createElement('input');
							option.type = 'button';
							option.name = 'cancel_graphics_btn';
							option.id = option.name;
							option.value = 'Cancel';
							option.setAttribute('onclick','processUserSelection(this)');
						
						    div.append(option);
						    
						    row.insertCell(1).appendChild(div);
						    
							document.getElementById('select_graphic_options_div').style.display = '';
							break;
					case 'ISPL_NEXT_BAT_OPTIONS':case'ISPL_COMPARISION_OPTIONS':case 'POPULATE_FF_CURRENT_MATCH_SUMMARY': 
					case "ISPL_TARGET_OPTIONS": case "ISPL_TOSS_OPTIONS": case "ISPL_EQUATION_OPTIONS": case "ISPL_MVP_OPTIONS": case "ISPL_MVP_OPTIONS_LEADERBOARD":
					 case "MATCHID_OPTIONS":  case "ISPL_BOUNDARIES_OPTIONS": case "ISPL_POJECTED_OPTIONS": case "POPULATE_MOSTRUNS": case "POPULATE_MOSTWKTS":
					 	select = document.createElement('input');
						select.type = "text";
						select.id = 'savePointsTable';
						select.value = '';
						
						header_text = document.createElement('label');
						header_text.innerHTML = 'Page No.';
						header_text.htmlFor = select.id;
						row.insertCell(0).appendChild(header_text).appendChild(select);
						
						select = document.createElement('select');
					   select.id = 'selectgraphictype';
					   select.name = select.id;
					
						option = document.createElement('option');
						option.value = 'AR';
						option.text = 'AR';
						select.appendChild(option);
						
						option = document.createElement('option');
						option.value = 'Drone';
						option.text = 'Drone';
						select.appendChild(option);
			
						/*option = document.createElement('option');
						option.value = 'Day After';
						option.text = 'Day After';
						select.appendChild(option);
						*/
						row.insertCell(1).appendChild(select); 
				

				    	option = document.createElement('input');
					    option.type = 'button';
					    switch(whatToProcess){
						case "MATCHID_OPTIONS":	
							option.name = 'populate_graphics_matchid';
							option.value = 'populate';
							break;
						case "POPULATE_MOSTRUNS":
							option.name = 'populate_graphics_mostruns';
							option.value = 'populate';
							break;	
						case "POPULATE_MOSTWKTS":
							option.name = 'populate_graphics_mostwkt';
							option.value = 'populate';
							break;		
						case "ISPL_MVP_OPTIONS":
						    option.name = 'populate_mvp';
							option.value = 'populate';
							break;
						case "ISPL_MVP_OPTIONS_LEADERBOARD":	
							option.name = 'populate_mvp_leaderboard';
							option.value = 'populate';
							break;	
						case "ISPL_BOUNDARIES_OPTIONS":
							option.name = 'populate_boundary';
							option.value = 'populate';
							break;	
						case "POPULATE_PLAYERPRFILE_BAT":
						    option.name = 'populate_playerprofile_bat';
							option.value = 'populate';
							break;
						case "ISPL_EQUATION_OPTIONS":
						    option.name = 'populate_equation';
							option.value = 'populate';
							break;
						case "ISPL_TOSS_OPTIONS":
							option.name = 'populate_graphics_toss';
							option.value = 'populate';
							break;
						case "ISPL_TARGET_OPTIONS":
							option.name = 'populate_graphics_target';
							option.value = 'populate';
							break;
						case "ISPL_POJECTED_OPTIONS":
							option.name = 'populate_graphics_projected';
							option.value = 'populate';
							break;
						case 'ISPL_NEXT_BAT_OPTIONS': 
							option.name = 'populate_graphics_nextToBat';
							option.value = 'populate';
							break;
						case 'ISPL_COMPARISION_OPTIONS':
							option.name = 'populate_graphics_COMPARISION';
							option.value = 'populate';
							break;
						case 'POPULATE_FF_CURRENT_MATCH_SUMMARY':
							option.name = 'FF_CURRENT_MATCH_SUMMARY';
							option.value = 'populate';
						break;
						}
						
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
					
					    div.append(option);
					    
					    row.insertCell(2).appendChild(div);
					    
						document.getElementById('select_graphic_options_div').style.display = '';
					break;
					case 'ISPL_50_50_OPTIONS': case 'ISPL_BALL_OPTIONS':
						cellCount = 0;
						select = document.createElement('select');
					    select.style.width = '130px';
					    select.id = 'whichScene';
					    select.name = select.id;
					    
					    option = document.createElement('option');
			         	option.value = 'drone';
			        	option.text = 'Drone';
			         	select.appendChild(option);
			         	
			         	option = document.createElement('option');
			         	option.value = 'spider';
			        	option.text = 'Spider';
			         	select.appendChild(option);
			         	
					    row.insertCell(cellCount).appendChild(select);
					    cellCount++;
					    
					    select = document.createElement('select');
					    select.style.width = '130px';
					    select.id = 'whichPlayer';
					    select.name = select.id;
					    for (let i = 0; i < dataToProcess.length; i++) {
					         option = document.createElement('option');
					         option.value = dataToProcess[i].playerId;
					         option.text = dataToProcess[i].full_name;
					         select.appendChild(option);
					    }
					    row.insertCell(cellCount).appendChild(select);
					    cellCount++;
					    
					    if(whatToProcess == 'ISPL_50_50_OPTIONS'){
							select = document.createElement('input');
							select.type = "text";
							select.id = 'challengeRuns';
							select.value = '10';
							
							header_text = document.createElement('label');
							header_text.innerHTML = 'Challenge Runs';
							header_text.htmlFor = select.id;
							row.insertCell(cellCount).appendChild(header_text).appendChild(select);
						    cellCount++;
						}
					    
					    select = document.createElement('input');
						select.type = "text";
						select.id = 'savePointsTable';
						select.value = '';
						
						header_text = document.createElement('label');
						header_text.innerHTML = 'Page No.';
						header_text.htmlFor = select.id;
						row.insertCell(cellCount).appendChild(header_text).appendChild(select);
					    cellCount++;
					
				    	option = document.createElement('input');
					    option.type = 'button';
					    switch(whatToProcess){
						case 'ISPL_50_50_OPTIONS': 
							option.name = 'populate_graphics_ispl_50_50';
							option.value = 'populate graphics ispl 50-50';
							break;
						case 'ISPL_BALL_OPTIONS':
							option.name = 'populate_graphics_ispl_ball';
							option.value = 'populate graphics ispl Ball';
							break;
						}
						
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
					
					    div.append(option);
					    
					    row.insertCell(cellCount).appendChild(div);
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;
						
					case 'GRAPHICS':
					    cellCount = 0; 
					    select = document.createElement('select');
					    select.style.width = '130px';
					    select.id = 'selectGraphics';
					    select.name = select.id;

					    for (let i = 0; i < dataToProcess.length; i++) {
					         option = document.createElement('option');
					         option.value = dataToProcess[i];
					         option.text = dataToProcess[i];
					         select.appendChild(option);
					    }
					    row.insertCell(cellCount).appendChild(select);
					    cellCount++;
					
				    	option = document.createElement('input');
					    option.type = 'button';
						option.name = 'populate_graphics';
						option.value = 'populate graphics';
					    option.id = option.name;
					    option.setAttribute('onclick',"processUserSelection(this)");
					    
					    div = document.createElement('div');
					    div.append(option);
					
						option = document.createElement('input');
						option.type = 'button';
						option.name = 'cancel_graphics_btn';
						option.id = option.name;
						option.value = 'Cancel';
						option.setAttribute('onclick','processUserSelection(this)');
					
					    div.append(option);
					    
					    row.insertCell(1).appendChild(div);
					    
						document.getElementById('select_graphic_options_div').style.display = '';
						break;

					}
					break;
			}
			break;
	}

	
}
function checkEmpty(inputBox,textToShow) {

	var name = $(inputBox).attr('id');
	
	document.getElementById(name + '-validation').innerHTML = '';
	document.getElementById(name + '-validation').style.display = 'none';
	$(inputBox).css('border','');
	if(document.getElementById(name).value.trim() == '') {
		$(inputBox).css('border','#E11E26 2px solid');
		document.getElementById(name + '-validation').innerHTML = textToShow + ' required';
		document.getElementById(name + '-validation').style.display = '';
		document.getElementById(name).focus({preventScroll:false});
		return false;
	}
	return true;	
}