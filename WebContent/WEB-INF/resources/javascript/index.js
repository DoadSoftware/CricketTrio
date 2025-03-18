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
		case 90://Z - ISPL 50-50
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_50_50_GRAPHIC_OPTIONS');
			break;
		case 88://X - TAPE BALL
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_BALL_GRAPHIC_OPTIONS');
			break;
		case 67://C - TAPE BALL
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('COMPARISION_GRAPHIC_OPTIONS');
			break;
		case 78://N - TAPE BALL
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_NEXT_BAT_GRAPHIC_OPTIONS');
			break;
		case 113://F2 - LINEUP
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_LINEUP_GRAPHIC_OPTIONS');
			break;
		case 114://F3 - PREVIOUS MATCH SUMMARY
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			processCricketProcedures('ISPL_PREVIOUS_MATCH_SUMMARY_GRAPHIC_OPTIONS');
			break;
		case 115://F4 - MATCH SUMMARY
			$("#captions_div").hide();
			$("#cancel_match_setup_btn").hide();
			$("#expiry_message").hide();
			addItemsToList('POPULATE_FF_CURRENT_MATCH_SUMMARY',null);
			break;
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
	case 'FF_CURRENT_MATCH_SUMMARY':
		processCricketProcedures("POPULATE_ISPL_FF_MATCH_SUMMARY");
		break;
	case 'populate_graphics_ispl_ball':
		processCricketProcedures('POPULATE_GRAPHICS_ISPL_TAPE');
		break;
	case"populate_graphics_COMPARISION":
		processCricketProcedures('POPULATE_GRAPHICS_COMPARISION');
		break;
	case"populate_graphics_nextToBat":
		processCricketProcedures('POPULATE_GRAPHICS_NEXT_TO_BAT');
		break;
	case "Line_Up":
		processCricketProcedures('POPULATE_GRAPHICS_LINEUP');
		break;
	case 'load_scene_btn':
		/*if(checkEmpty($('#vizIPAddress'),'IP Address Blank') == false
			|| checkEmpty($('#vizPortNumber'),'Port Number Blank') == false) {
			return false;
		}*/
      	document.initialise_form.submit();
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
	case 'POPULATE_GRAPHICS_ISPL_TAPE':
		valueToProcess = $('#whichScene').val() + ',' + $('#whichPlayer').val() + ','  + $('#savePointsTable').val();
		break;
	case"POPULATE_GRAPHICS_COMPARISION":case"POPULATE_GRAPHICS_NEXT_TO_BAT":case "POPULATE_ISPL_FF_MATCH_SUMMARY":
			valueToProcess = $('#savePointsTable').val();
	break;
	case "POPULATE_GRAPHICS_LINEUP":
		valueToProcess = $('#whichScene').val() + ',' +$('#savePointsTable').val();
	break;
	}

	$.ajax({    
        type : 'Get',     
        url : 'processCricketProcedures.html',     
        data : 'whatToProcess=' + whatToProcess + '&valueToProcess=' + valueToProcess, 
        dataType : 'json',
        success : function(data) {
        	switch(whatToProcess) {
			case 'SAVE_GRAPHICS':
				$('#select_graphic_options_div').empty();
				document.getElementById('select_graphic_options_div').style.display = 'none';
				$("#main_captions_div").show();
				break;
			
			case 'GRAPHIC_OPTIONS':
				addItemsToList('GRAPHICS',data);
				break;
			case 'ISPL_50_50_GRAPHIC_OPTIONS':
				addItemsToList('ISPL_50_50_OPTIONS',data);
				break;
			case 'ISPL_BALL_GRAPHIC_OPTIONS':
				addItemsToList('ISPL_BALL_OPTIONS',data);
				break;
			case'ISPL_NEXT_BAT_GRAPHIC_OPTIONS':
				addItemsToList('ISPL_NEXT_BAT_OPTIONS',data);
				break;
			case'COMPARISION_GRAPHIC_OPTIONS':
				addItemsToList('ISPL_COMPARISION_OPTIONS',data);
				break;
			case "ISPL_LINEUP_GRAPHIC_OPTIONS":
				addItemsToList('ISPL_LINEUP_OPTIONS',data);
				break;
			case "ISPL_PREVIOUS_MATCH_SUMMARY_GRAPHIC_OPTIONS":
				addItemsToList('ISPL_PREVIOUS_MATCH_SUMMARY_OPTIONS',data);
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
		case 'LOAD_GRAPHICS-OPTION':case 'GRAPHICS': case 'ISPL_50_50_OPTIONS': case 'ISPL_BALL_OPTIONS':case 'ISPL_NEXT_BAT_OPTIONS':case'ISPL_COMPARISION_OPTIONS':
		case "ISPL_LINEUP_OPTIONS":case "ISPL_PREVIOUS_MATCH_SUMMARY_OPTIONS":case 'POPULATE_FF_CURRENT_MATCH_SUMMARY':
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
					    switch(whatToProcess){
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
					    
					    row.insertCell(1).appendChild(div);
					    
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