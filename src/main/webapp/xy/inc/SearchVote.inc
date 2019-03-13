<%@ page pageEncoding="UTF-8"%>
<div id="searchBtnArea">
	<div class="div">
		投票分组：
			<select id="VoteGroup" >
				<option value=''></option>
				<c:forEach var="group" items="${voteGroups}">
					<option value="${group.catID}"><c:out value="${group.catName}"/></option>
				</c:forEach>
			</select>
		<input type='button' id="doCancel" value='取消'/>		
	</div>
</div>

<script type="text/javascript" src="script/SearchVote.js"></script>
<script>
	var siteID = "<c:out value="${siteID}"/>";
	main_param["vSiteIDField"] = "<c:out value="${vSiteIDField}"/>";
	main_param["vGroupIDField"] = "<c:out value="${vGroupIDField}"/>";
	
	$('#VoteGroup').change(function(){ 
		var type = $("#VoteGroup").val();
		if(type != ''){
			e5.mods["workspace.search"].searchClick(type);
		}
	})
</script>