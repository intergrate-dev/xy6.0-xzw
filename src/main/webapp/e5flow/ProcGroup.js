function onRestore() {
	document.getElementById("divGroup").innerHTML = "";
	initVar();
}
//重置一些页面变量的值
function initVar(){
	mousedown = false;
	beginnum = 0;
	endnum = 0;
	groupNum = 1;
	procID = 0;
	groupID = 0;
	beginID = 0;
	changeProcID = 0;
	isSort = false;
	groupArray.length = 0;
	initCell();
	initProc();
}
function doMouseDown(trID, tdID) {
    var obj = window.document.getElementsByTagName("tr");
    var num = 0;
    beginID = tdID;
    for (var i = 0; i < obj.length; i++) {
        var tmp = obj[i];
        if (tmp.id == trID) {
            var tds = tmp.childNodes;
            for (var j = 0; j < tds.length; j++) {
                if (tds[j].id == tdID) {
                    beginnum = num - 1;
                    changeProcID = tds[j].procID;
                }
                num++;
            }
        }
    }
    isSort = true;
}
function doDbClick(groupID, procID, procArray) {
    var obj = window.document.getElementsByTagName("tr");
    for (var i = 0; i < obj.length; i++) {
        var tmp = obj[i];
        if (tmp.id == groupID) {
            var tds = tmp.childNodes;
            var procNum = tmp.procNum;
            for (var j = 0; j < tds.length; j++) {
                if (tds[j].id == procID) {
                    tmp.deleteCell(j);
                    procArray.splice(j - 1, 1);//从操作组中删除操作
                    tmp.setAttribute("procNum", procNum - 1);//该行的操作个数-1
                }
            }
        }
    }
}
function doMouseUp2(){
	initProc();
}
function doMouseUp1(trID, procArray) {
    if (procID > 0 && mousedown) {
        var obj = window.document.getElementsByTagName("tr");
        for (var j = 0; j < obj.length; j++) {
            var tmp = obj[j];
            var procNum = tmp.procNum;//获取该行的操作数
            var maxProcNum = tmp.maxProcNum;//获取该行曾经添加的最大操作个数
            if (tmp.id == trID) {//定位到鼠标所在的行
                for (var i = 0; i < procs.length; i++) {
                    if (procID == procs[i].procID) {
                        var text = "<img id='img" + procNum + "' src='../" + procs[i].procFileName + "' alt=''/><br/>";
                        text = text.concat("<span id='procName" + procNum + "'>");
                        text = text.concat(procs[i].procName + "</span>");
                        var tds = tmp.childNodes;
                        for (var k = 0; k < tds.length; k++) {
                            if (tds[k].procID == procID) {
                                alert("该组中已存在此操作！");
                                mousedown = false;
                                return;
                            }
                        }
                        var oCell = tmp.insertCell(++procNum);
                        maxProcNum++;
                        tmp.setAttribute("procNum", procNum);//该行的操作个数+1
                        tmp.setAttribute("maxProcNum", maxProcNum);
                        oCell.setAttribute("id", "td" + maxProcNum);
                        oCell.setAttribute("width", "50px");
                        oCell.setAttribute("align", "center");
                        oCell.setAttribute("procID", procID);
                        oCell.innerHTML = text;
                        procArray.push(procs[i].procID);//往操作组里增加一个操作
                        oCell.onmouseover = function () {
                            doMouseOver(trID, oCell.id);
                        };
                        oCell.ondblclick = function () {
                            doDbClick(trID, oCell.id, procArray);
                        };
                        oCell.onmousedown = function () {
                            doMouseDown(trID, oCell.id);
                        };
                        oCell.onmouseup = function () {
                            doMouseUp(trID, oCell.id, procArray);
                        };
                    }
                }
                initProc();
            }
        }
    }
}
function doMouseUp(trID, tdID, procArray) {
    if (!isSort || changeProcID == 0) {
    	initCell();
        return;
    }
    var obj = window.document.getElementsByTagName("tr");
    var num = 0;
    for (var i = 0; i < obj.length; i++) {
        var tmp = obj[i];
        if (tmp.id == trID) {
            var tds = tmp.childNodes;
            for (var j = 0; j < tds.length; j++) {
                if (tds[j].id == tdID) {
                    endnum = num - 1;
                }
                num++;
            }
        }
    }
	//操作数组重排序
    if (beginnum > endnum) {
        var tmpProc = procArray[beginnum];
        for (var i = beginnum; i > endnum; i--) {
            procArray[i] = procArray[i - 1];
        }
        procArray[endnum] = tmpProc;
    } else if (beginnum < endnum) {
        var tmpProc = procArray[beginnum];
        for (var i = beginnum; i < endnum; i++) {
            procArray[i] = procArray[i + 1];
        }
        procArray[endnum] = tmpProc;
    } else {
    	initCell();
    	return;
    }
	//界面上的重排序
    var obj2 = window.document.getElementsByTagName("tr");
    for (var i = 0; i < obj2.length; i++) {
        var tmp = obj2[i];
        if (tmp.id == trID) {
            tmp.deleteCell(beginnum + 1);
            for (var i = 0; i < procs.length; i++) {
                if (changeProcID == procs[i].procID) {
                    var procNum = tmp.procNum;
                    var text = "<img id='img" + procNum + "' src='../" + procs[i].procFileName + "' alt=''/><br/>";
                    text = text.concat("<span id='procName" + procNum + "'>");
                    text = text.concat(procs[i].procName + "</span>");
                    var oCell = tmp.insertCell(endnum + 1);
                    oCell.setAttribute("id", beginID);
                    oCell.setAttribute("width", "50px");
                    oCell.setAttribute("align", "center");
                    oCell.setAttribute("procID", changeProcID);
                    oCell.innerHTML = text;
                    oCell.ondblclick = function () {
                        doDbClick(trID, oCell.id, procArray);
                    };
                    oCell.onmousedown = function () {
                        doMouseDown(trID, oCell.id);
                    };
                    oCell.onmouseup = function () {
                        doMouseUp(trID, oCell.id, procArray);
                    };
                }
            }
        }
    }
    initCell();
}
function initCell(){
    isSort = false;
    changeProcID = 0;
    beginnum = 0;
    endnum = 0;
}
function initProc(){
	mousedown = false;
    procID = 0;
}
function doMouseOver(trID, tdID) {
    
}
function doMouseDown1(idx) {
    for (var i = 0; i < procs.length; i++) {
        if (idx == procs[i].procID) {
            if (groupNum > 1) {
                procID = procs[i].procID;
            }
        }
    }
    mousedown = true;
}
function doMouseOver1(gid) {
    groupID = gid.substr(5, 1);
}
function doSubmit() {
    var ret = "";
    for (var i = 0; i < groupArray.length; i++) {
        ret += groupArray[i];
        if (i < groupArray.length - 1) {
            ret += ";";
        }
    }
    if (ret == "") {
        alert("无效操作！");
        return;
    } else {
        postForm.ProcID.value = ret;
        postForm.submit();
    }
}
function addGroup() {
    var div = document.getElementById("divGroup");
    var tbs = div.childNodes;
    if (groupNum == 1 && tbs.length > 0) {
        groupNum = tbs.length;
    }
    var groupID = "group" + groupNum;
    var trID = "tr" + groupNum;
    var newTB = document.createElement("table");//创建table
    newTB.setAttribute("id", groupID);
    newTB.setAttribute("border", "1");
    newTB.setAttribute("width", "100%");
    newTB.setAttribute("class", "withWidth");
    newTB.className = "withWidth";
    newTB.style.cursor = "hand";
    
    var newTR = newTB.insertRow();
    newTR.setAttribute("id", trID);
    newTR.setAttribute("height", "40px");
    newTR.setAttribute("procNum", "0");
    newTR.setAttribute("maxProcNum", "0");
    var procArray = new Array();//创建一个操作组
    groupArray.push(procArray);
    newTR.onmouseover = function () {
        doMouseOver1(groupID);
    };
    newTR.onmouseup = function () {
        doMouseUp1(trID, procArray);
    };
    div.appendChild(newTB);
    var oCell = newTR.insertCell();
    oCell.setAttribute("id", "td0");
    oCell.setAttribute("width", "50px");
    oCell.setAttribute("align", "center");
    oCell.innerText = "组" + groupNum;
    
    var oCell2 = newTR.insertCell();
    oCell2.id = "tdx";
    oCell2.setAttribute("align", "center");
    oCell2.innerText = " ";
    groupNum++;
}

