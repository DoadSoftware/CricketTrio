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
		//$("#captions_div").show();
		$("#main_captions_div").show();
		
		break;
	case 'save_graphics':
		processCricketProcedures('SAVE_GRAPHICS');
		break;	
	case 'populate_graphics':
		processCricketProcedures('POPULATE_GRAPHICS');
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
		case 'LOAD_GRAPHICS-OPTION':case 'GRAPHICS':
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