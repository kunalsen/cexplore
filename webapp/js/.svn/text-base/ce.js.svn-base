jsonArray = new Array();
numChunks = 0;
isPerson = true;
compArray = new Array();
origuri = "";
origname = "";

propArray = new Array();
propOrigURI = "";
propOrigName = "";
propNumChunks = 0;
tempUri = "";
function ajax(url, index,max_num) {
	
	var xmlhttp;
	var ind = index;
	document.getElementById("redir").innerHTML="";	
	//document.getElementById("result").style.display = 'block';
	//document.getElementById("compare").style.display = 'none';
	document.getElementById("proc").innerHTML = 'Processing...';
	// document.getElementById("proc").style = 'background-color:green;';
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari

		xmlhttp = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	// if ((url.indexOf('cont=true') == -1) && !(index >= 1) &&
	// (url.indexOf('concept=') == -1)) {
	// url += "?concept=" + document.getElementById('concept').value;
	// }
	if (url.indexOf('concept=') == -1) {
		url += "?concept=" + document.getElementById('concept').value;
		postvar = "concept=" + document.getElementById('concept').value;
	}
	var t = url.match(/concept=((\w|\s|\(|\)|,|;|\.|:|\-)*)(&*)/i);
	if (t != null) {
		document.getElementById("disambtitle").innerHTML = 'Other uses of ' + t[1];
		postvar = "concept=" + t[1];
	} else {
		document.getElementById("disambtitle").innerHTML = 'Disambiguation';
	}

	// alert(index);
	xmlhttp.onreadystatechange = function() {

		if (xmlhttp.readyState == 4
				&& (xmlhttp.status == 200 || window.location.href
						.indexOf("http") == -1)) {

			if (xmlhttp.responseText == '') {
				document.getElementById("proc").innerHTML = 'Done';
				document.getElementById("proc").innerHTML = '';
				document.getElementById("redir").innerHTML = document
						.getElementById('concept').value + ' is not found or there is a problem in the connection.';
				return;
			} else {
				
				processOutput(xmlhttp.responseText, url, ind,max_num);

				// processJson(xmlhttp.responseText);
				document.getElementById("proc").innerHTML = 'Done';
				document.getElementById("proc").innerHTML = '';
				// document.getElementById("Classes").innerHTML=xmlhttp.responseText;
			}
		}

	}
	// alert(document.getElementById('concept').value);
	if (ind == undefined) {
		xmlhttp.open("POST", url, true);
	} else {
		xmlhttp.open("POST", url + '&chunk=' + ind, true);
	}
	xmlhttp.setRequestHeader("Content-type", "text/xml");
	// xmlhttp.send("concept=" + document.getElementById('concept').value);
	xmlhttp.send(postvar);

}

function processOutput(test, url, num,max_num) {
	// alert(num);
	if ((num == undefined)) {
		origuri = "";
		origname = "";
		num = 1;
		var myObject = eval('(' + test + ')');
		origuri = myObject.bindings[1].origuri;
		origname = myObject.bindings[1].orig;
		// alert(origuri);
		numChunks = myObject.bindings[0].val;
		jsonArray[0] = myObject;
	} else {
		var myObject = eval('(' + test + ')');
		if(jsonArray.length==0){
			jsonArray[0] = myObject;
			numChunks = myObject.bindings[0].val;
			
		}
		else{
			jsonArray[jsonArray.length] = myObject;
		}
	}
	if (num < numChunks && (max_num == undefined || num < max_num)) {
		
		ajax(url, num + 1,max_num);
	} else {
		//alert(max_num);
		if(max_num != undefined){
			var tempArray = jsonArray;			
			processInstance(tempArray,max_num);
			jsonArray = new Array();			
			return;		
		}
		var tempArray = jsonArray;
		jsonArray = new Array();
		processJson(tempArray);
		numChunks = 0;
		// if (isPerson) {
		processPerson(url);
		// processProperty(url); // we're moving processProperty inside
		// processPerson
		// alert('isPerson');
		// isPerson = false;
		// } else {
		// document.getElementById("links").innerHTML = "";
		// }
		// callFreebase(document.getElementById("concept").value,
		// 'processFreeBase', 'freeb');
	}

}

function processJson(jsonString) {
	// var myObject = eval('(' + jsonString + ')');
	// alert(myObject.bindings[0].displayname);

	var classesHTML = '<table>';
	var disHTML = '<table>';
	var topHTML = '<table>';
	var redHTML = '';
	var abstHTML = '';

	for (i = 0; i < jsonString.length; i++) {
		myObject = jsonString[i];
		var count = 0;
		for (j in myObject.bindings) {
			if (myObject.bindings[count].type == 'class') {
				var jstr = 'javascript:ajax(\''
						+ location.protocol
						+ '//'
						+ location.hostname
						+ ':'
						+ location.port
						+ '/cexplore/main?concept='
						+ myObject.bindings[count].uri
						+ '&cont=true&query=dbpedia_instance&camel=false\',1,5);tempUri=\''
						+ myObject.bindings[count].uri + '\';';
				classesHTML = classesHTML + '<tr>';
				classesHTML = classesHTML
						+ '<td><a href="javascript:void;" onclick="' + jstr
						+ '">' + myObject.bindings[count].displayname
						+ '</a></td>';
				classesHTML = classesHTML + '</tr>';
			}

			else if (myObject.bindings[count].type == 'disambiguation') {
				var jstr = 'javascript:ajax(\''
						+ location.protocol
						+ '//'
						+ location.hostname
						+ ':'
						+ location.port
						+ '/cexplore/main?concept='
						+ myObject.bindings[count].displayname
						+ '&cont=true&camel=false\');document.getElementById(\'concept\').value=\''
						+ myObject.bindings[count].displayname + '\';';
				disHTML = disHTML + '<tr>';
				disHTML = disHTML + '<td><a href="javascript:void;" onclick="'
						+ jstr + '">' + myObject.bindings[count].displayname
						+ '</a></td>';

				disHTML = disHTML + '</tr>';

			}

			else if (myObject.bindings[count].type == 'topics') {
				topHTML = topHTML + '<tr>';
				topHTML = topHTML + '<td>'
						+ myObject.bindings[count].displayname + '</td>';
				topHTML = topHTML + '</tr>';
			} else if (myObject.bindings[count].type == 'redirection') {
				var jstr1 = 'javascript:ajax(\'' + location.protocol + '//'
						+ location.hostname + ':' + location.port
						+ '/cexplore/main?concept='
						+ myObject.bindings[count].orig
						+ '&cont=true&camel=false\');';
				var jstr2 = 'javascript:ajax(\'' + location.protocol + '//'
						+ location.hostname + ':' + location.port
						+ '/cexplore/main?concept='
						+ myObject.bindings[count].displayname
						+ '&cont=true&camel=false\');';
				// alert(jstr);
				redHTML = 'The term <a href="javascript:void;" onclick="'
						+ jstr1
						+ '">'
						+ myObject.bindings[count].orig
						+ '</a> redirects to <a href="javascript:void;" onclick="'
						+ jstr2 + '">' + myObject.bindings[count].displayname
						+ '</a>';
				// alert(redHTML);
			} else if (myObject.bindings[count].type == 'abstract') {
				var jstr = 'javascript:ajax(\'' + location.protocol + '//'
						+ location.hostname + ':' + location.port
						+ '/cexplore/main?concept='
						+ myObject.bindings[count].displayname
						+ '&cont=true&camel=false\');';
				abstHTML = myObject.bindings[count].displayname;
			}
			count++;
		}
	}
	classesHTML = classesHTML + '</table>';
	disHTML = disHTML + '</table>';
	topHTML = topHTML + '</table>';
	// alert(topHTML);
	document.getElementById("Classes").innerHTML = classesHTML;
	document.getElementById("disamb").innerHTML = disHTML;
	document.getElementById("subject").innerHTML = topHTML;
	document.getElementById("redir").innerHTML = redHTML;
	document.getElementById("abstract").innerHTML = abstHTML;
	// document.getElementById("origterm").innerHTML = 'Queried term is ' +
	// myObject.bindings[0].orig;
	// document.getElementById("concept").value = myObject.bindings[0].orig;
	// alert(document.getElementById("concept").value);
	// alert(classesHTML);
}

function processPerson(url) {

	var concept = document.getElementById("concept").value;
	var t = url.match(/concept=((\w|\s|\(|\)|,|;|\.|:|\-)*)(&*)/i);
	if (t != null) {
		concept = t[1];
	}

	url = location.protocol + '//' + location.hostname + ':' + location.port
			+ '/cexplore/main?concept=' + concept
			+ '&query=dbpedia_foaf&camel=false';

	// document.getElementById("proc").style = 'background-color:green;';
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari

		xmlhttp = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	// alert(url);
	// alert(index);
	xmlhttp.onreadystatechange = function() {

		if (xmlhttp.readyState == 4
				&& (xmlhttp.status == 200 || window.location.href
						.indexOf("http") == -1)) {

			// alert(xmlhttp.responseText);
			var perHTML = '';
			var nickHTML = '';
			var pageHTML = '';
			var homepageHTML = '';
			var imgHTML = '';
			var thumbnailHTML = '';
			var myObject = eval('(' + xmlhttp.responseText + ')');
			var count = 0;
			var jstr = '';
			var maxfoafstrlen = 55;

			for (j in myObject.bindings) {
				// alert(myObject.bindings[count].type + '\n'
				// +myObject.bindings[count].uri);
				if (myObject.bindings[count].type == 'http://xmlns.com/foaf/0.1/page') {
					pageHTML = pageHTML + '<tr>';
					pageHTML = pageHTML + '<td><a href="'
							+ myObject.bindings[count].uri + '">';
					if (myObject.bindings[count].uri.length > maxfoafstrlen) {
						pageHTML = pageHTML
								+ myObject.bindings[count].uri.slice(0,
										maxfoafstrlen - 10) + '....</a></td>';
					} else {
						pageHTML = pageHTML + myObject.bindings[count].uri
								+ '</a></td>';
					}

					pageHTML = pageHTML + '</tr>';
				} else if (myObject.bindings[count].type == 'http://xmlns.com/foaf/0.1/homepage') {
					homepageHTML = homepageHTML + '<tr>';
					homepageHTML = homepageHTML + '<td><a href="'
							+ myObject.bindings[count].uri + '">'
					if (myObject.bindings[count].uri.length > maxfoafstrlen) {
						homepageHTML = homepageHTML
								+ myObject.bindings[count].uri.slice(0,
										maxfoafstrlen - 10) + '....</a></td>';
					} else {
						homepageHTML = homepageHTML
								+ myObject.bindings[count].uri + '</a></td>';
					}

					homepageHTML = homepageHTML + '</tr>';
				} else if (myObject.bindings[count].type == 'http://xmlns.com/foaf/0.1/nick') {
					nickHTML = nickHTML + '<tr>';
					nickHTML = nickHTML + '<td>'
							+ myObject.bindings[count].literal + '</td>';

					nickHTML = nickHTML + '</tr>';
				} else if (myObject.bindings[count].type == 'http://xmlns.com/foaf/0.1/depiction') {
					imgHTML = imgHTML + '<tr>';
					imgHTML = imgHTML + '<td><img width="250px" src="'
							+ myObject.bindings[count].uri + '"></img></td>';

					imgHTML = imgHTML + '</tr>';
				} else if (myObject.bindings[count].type == 'http://xmlns.com/foaf/0.1/thumbnail') {
					thumbnailHTML = thumbnailHTML + '<tr>';
					thumbnailHTML = thumbnailHTML + '<td><img src="'
							+ myObject.bindings[count].uri + '"></img></td>';

					thumbnailHTML = thumbnailHTML + '</tr>';
				} else if (myObject.bindings[count].type == 'http://dbpedia.org/ontology/wikiPageExternalLink') {
					perHTML = perHTML + '<tr>';
					perHTML = perHTML + '<td><a href="'
							+ myObject.bindings[count].uri + '">'
					if (myObject.bindings[count].uri.length > maxfoafstrlen) {
						perHTML = perHTML
								+ myObject.bindings[count].uri.slice(0,
										maxfoafstrlen - 10) + '....</a></td>';
					} else {
						perHTML = perHTML + myObject.bindings[count].uri
								+ '</a></td>';
					}
					perHTML = perHTML + '</tr>';
				}
				count++;
			}
			var emptyrow = '<tr><td>&nbsp;</td></tr>';
			var foafoutHTML = '<table>';
			if (thumbnailHTML != '') {
				foafoutHTML += thumbnailHTML + nickHTML + emptyrow;
			} else if (imgHTML != '') {
				foafoutHTML += imgHTML + nickHTML + emptyrow;

			}
			if (homepageHTML != '')
				foafoutHTML += homepageHTML + emptyrow;
			if (pageHTML != '')
				foafoutHTML += pageHTML + emptyrow;
			foafoutHTML += perHTML + '</table>'
			// alert(perHTML);
			document.getElementById("links").innerHTML = foafoutHTML;

			// / calling processProperty after processPerson finished.
			processAjaxProperty(url);
		}

	}
	xmlhttp.open("POST", url, true);
	xmlhttp.setRequestHeader("Content-type", "text/xml");
	// xmlhttp.send("concept=" + document.getElementById('concept').value);
	xmlhttp.send("concept=" + concept);
}

function processOutputProperty(jsontext, url, num) {
	if ((num == undefined) || (num == 1)) {
		propOrigURI = "";
		propOrigName = "";
		num = 1;
		var myObject = eval('(' + jsontext + ')');
		propOrigURI = myObject.bindings[1].origuri;
		propOrigName = myObject.bindings[1].orig;
		propNumChunks = myObject.bindings[0].val;
		propArray[0] = myObject;
	} else {
		var myObject = eval('(' + jsontext + ')');
		propArray[num - 1] = myObject;
	}
	// alert(num + ' ' + propNumChunks);
	if (num < propNumChunks) {
		processAjaxProperty(url, num + 1);
	} else {
		// alert(propArray);
		var tarray = propArray;
		propArray = new Array();
		propNumChunks = 0;
		processJSONProperty(tarray);
	}
}

function processAjaxProperty(url, index) {

	var concept = document.getElementById("concept").value;
	var camel = true;
	var ind = index;
	var t = url.match(/concept=((\w|\s|\(|\)|,|;|\.|:|\-)*)(&*)/i);
	if (t != null) {
		concept = t[1];
		camel = false;
	}

	url = location.protocol + '//' + location.hostname + ':' + location.port
			+ '/cexplore/main?concept=' + concept
			+ '&query=dbpedia_prop&camel=' + camel;

	// document.getElementById("proc").style = 'background-color:green;';
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
		xmlhttp = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
	}

	// alert(ind + '\n' + url);
	xmlhttp.onreadystatechange = function() {

		if (xmlhttp.readyState == 4
				&& (xmlhttp.status == 200 || window.location.href
						.indexOf("http") == -1)) {

			if (xmlhttp.responseText == '') {
				document.getElementById("properties").innerHTML = concept + ' is not found or there is a problem in the connection.';
				return;
			} else {
				processOutputProperty(xmlhttp.responseText, url, ind);
			}
		}

	}
	// alert(document.getElementById('concept').value);
	if (ind == undefined) {
		xmlhttp.open("POST", url, true);
	} else {
		xmlhttp.open("POST", url + '&chunk=' + ind, true);
	}
	xmlhttp.setRequestHeader("Content-type", "text/xml");
	xmlhttp.send(postvar);

}

function processJSONProperty(jsonPropertyArray) {
	var propHTML = '';

	// alert(jsonPropertyArray.length);
	for (i = 0; i < jsonPropertyArray.length; i++) {
		var myObject = jsonPropertyArray[i];
		var count = 0;
		for (j in myObject.bindings) {
			// alert(myObject.bindings[count].type);
			if (myObject.bindings[count].type != 'num') {
				propHTML = propHTML + '<tr>';
				if (myObject.bindings[count].uri != undefined
						&& myObject.bindings[count].uri != null) {
					propHTML += '<td><a href="'
							+ myObject.bindings[count].origuri + '">'
							+ myObject.bindings[count].orig + '</a>'
							+ ' <a href="' + myObject.bindings[count].typeuri
							+ '">' + myObject.bindings[count].type + '</a>'
							+ ' <a href="' + myObject.bindings[count].uri
							+ '">' + myObject.bindings[count].displayname
							+ '</a></td>';
				} else {
					propHTML += '<td><a href="'
							+ myObject.bindings[count].origuri + '">'
							+ myObject.bindings[count].orig + '</a>'
							+ ' <a href="' + myObject.bindings[count].typeuri
							+ '">' + myObject.bindings[count].type + '</a>'
							+ ' ' + myObject.bindings[count].displayname + '</td>';
				}
				propHTML += '</tr>';
			}
			count++;
		}

	}
	propHTML = '<table>' + propHTML + '</table>';

	document.getElementById("properties").innerHTML = propHTML;

}

function callFreebase(concept, function1, type) {
	// alert('called');
	if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari

		xmlhttp1 = new XMLHttpRequest();
	} else {// code for IE6, IE5
		xmlhttp1 = new ActiveXObject("Microsoft.XMLHTTP");
	}

	xmlhttp1.onreadystatechange = function() {

		if (xmlhttp1.readyState == 4
				&& (xmlhttp1.status == 200 || window.location.href
						.indexOf("http") == -1)) {
			// alert('back');
			if (function1 == 'processFreeBase') {
				processFreeBase(xmlhttp1.responseText);
			} else if (function1 == 'processComp') {
				processComp(xmlhttp1.responseText);
			}
		}

	}
	// alert(document.getElementById('concept').value);
	var url = location.protocol + '//' + location.hostname + ':'
			+ location.port + '/cexplore/main?concept=' + concept + '&type='
			+ type;
	// var url =
	// 'http://www.freebase.com/api/account/login?username=SemanticWeb&password=welcome';
	xmlhttp1.open("POST", url, true);

	xmlhttp1.setRequestHeader("Content-type", "text/xml");
	xmlhttp1.send();

}
function processFreeBase(responseTxt) {
	var myObject = eval('(' + responseTxt + ')');
	var count = 0;
	for (i in myObject.result) {
		// alert(myObject.result[count].name);
		// alert(myObject.result[count].type[0].name);
		count++;
	}
}

function addToCompare() {
	var person = new Object();
	person.uri = origuri;
	person.name = origname;
	// alert(compArray.length);
	document.getElementById("button").style.display = 'none';
	document.getElementById("saveInd").style.display = 'block';
	if (compArray.length != 0) {
		compArray[compArray.length] = person;
		if (compArray.length >= 2) {
			document.getElementById("add1").style.display = 'block';
		}
		return;
	}

	compArray[0] = person;

}

function testArray() {
	var count = 0;
	document.getElementById("add1").style.display = 'none';
	document.getElementById("saveInd").style.display = 'none';
	document.getElementById("result").style.display = 'none';
	var rowHTML = "<table>";
	for (i in compArray) {
		// alert(compArray[count].name+'::::' +compArray[count].uri);
		rowHTML = rowHTML + '<tr>';
		// rowHTML += '<td><input type="checkbox" name="comp"
		// id=\"'+compArray[count].uri+'\"value=\"'+compArray[count].uri+'\">'+compArray[count].name+'</input></td>';
		// rowHTML = rowHTML+'<td><input type="checkbox" name="comp" id="comp"
		// value=\"+compArray[count].uri+\">'+compArray[count].name+'</input></td>';
		// rowHTML += '<td>'+compArray[count].uri+'</td>';
		rowHTML = rowHTML + '<td><input type="checkbox" name="comp" value="'
				+ compArray[count].uri + '">' + compArray[count].name
				+ '</input></td>';
		rowHTML = rowHTML + '</tr>';
		count++;
	}
	rowHTML += '</table>';

	document.getElementById("compLinks").innerHTML = rowHTML;
	document.getElementById("compare").style.display = 'block';
}
function compare() {

	var requestString = "";
	for ( var i = 0; i < document.forms['check'].elements.length; i++) {
		if (document.forms['check'].elements[i].checked) {
			if (i != 0) {
				requestString += ";;;;";
			}
			requestString += document.forms['check'].elements[i].value;
		}
	}

	callFreebase(requestString, 'processComp', 'compare');
}
function processComp(resp) {
	// alert(resp);
	var categoryArray = '';
	var myObject = eval('(' + resp + ')');
	var htmlTxt = '<table border="o" cellspacing="20px">'
	var count = 0;
	var odd = true;
	for (j in myObject.bindings) {
		var name = myObject.bindings[count].type;

		if (categoryArray.indexOf(myObject.bindings[count].type) == -1) {
			if (odd) {
				htmlTxt += "<tr style='background-color:#AAEEAA;'>";
				odd = false;
			} else {
				odd = true;
				htmlTxt += "<tr style='background-color:#AAAAEE;'>";
			}
			htmlTxt += "<td>" + myObject.bindings[count].type + "</td>";

			htmlTxt += "<td>";

			var c = 0;
			var myObject1 = eval('(' + resp + ')');
			// alert(myObject1.bindings[0]);
			// alert(name);
			var first = true;
			for (i in myObject1.bindings) {
				// alert(myObject1.bindings[c].type);
				if (myObject1.bindings[c].type.indexOf(name) != -1) {
					if (!first) {
						htmlTxt += ", ";
					}
					first = false;
					htmlTxt += myObject1.bindings[c].displayname;

				}
				c++;
			}
			c = 0;
			categoryArray += myObject.bindings[count].type;
			htmlTxt += "</td>";

			htmlTxt += "</tr>";
		}
		count++;
	}
	// alert(c);
	htmlTxt += "</table>";
	document.getElementById('compResult').innerHTML = htmlTxt;
}
function reset() {
	compArray = new Array();
	testArray();
	document.getElementById('compResult').innerHTML = "";
}
function resetAllDiv(){
	numChunks = 0;	
	document.getElementById("button").style.display = 'block';
	document.getElementById("saveInd").style.display = 'none';	
	document.getElementById("disamb").innerHTML = "";
	document.getElementById("Classes").innerHTML = "";
	document.getElementById("subject").innerHTML = "";
	document.getElementById("links").innerHTML = "";
	document.getElementById("abstract").innerHTML = "";
	document.getElementById("properties").innerHTML = "";
	document.getElementById("result").style.display = 'block';
	document.getElementById("compare").style.display = 'none';	
}

function processInstance(tempArray,max_num){
	//numChunks = 0;
	document.getElementById("add1").style.display = 'none';
	document.getElementById("saveInd").style.display = 'none';
	document.getElementById("result").style.display = 'none';
	
	
	var rowHTML = "<table>";
	for (i = 0; i < tempArray.length; i++) {
		var myObject = tempArray[i];
		var count = 0;
		for (k in myObject.bindings) {
			if(count==0){
				count++;
				continue;			
			}
			var orig = myObject.bindings[count].orig;
			var jstr = 'javascript:ajax(\''
						+ location.protocol
						+ '//'
						+ location.hostname
						+ ':'
						+ location.port
						+ '/cexplore/main?concept='
						+ orig
						+ '&cont=true&camel=false\');document.getElementById(\'concept\').value=\''
						+ orig + '\';resetAllDiv();';	
			rowHTML = rowHTML + '<tr>';
			rowHTML = rowHTML + '<td><input type="checkbox" name="comp" value="'
					+ myObject.bindings[count].origuri + '"><a href=\"'+jstr+'\">' + myObject.bindings[count].orig
					+ '</input></td>';
			rowHTML = rowHTML + '</tr>';
			count++;
		}
	}
	var tmax=max_num+5;
	
	if(tmax > numChunks){
		tmax = numChunks;	
	}
	var jstrNext = 'javascript:ajax(\''
						+ location.protocol
						+ '//'
						+ location.hostname
						+ ':'
						+ location.port
						+ '/cexplore/main?concept='
						+ tempUri
						+ '&cont=true&query=dbpedia_instance&camel=false\','+(max_num+1)+','+tmax+');';
	var jstrPrev = 'javascript:ajax(\''
						+ location.protocol
						+ '//'
						+ location.hostname
						+ ':'
						+ location.port
						+ '/cexplore/main?concept='
						+ tempUri
						+ '&cont=true&query=dbpedia_instance&camel=false\','+(max_num-9)+','+(max_num-5)+');';


	rowHTML +='<tr>';
	if(max_num>5){
		rowHTML +='<td><a href='+jstrPrev+'>prev</a></td>';
	}
	if(tmax > 5){
	rowHTML +='<td><a href='+jstrNext+'>next</a></td>';
	}
	rowHTML +='<tr>';
	rowHTML += '</table>';
	
	document.getElementById("compLinks").innerHTML = rowHTML;
	document.getElementById("compare").style.display = 'block';	
}
