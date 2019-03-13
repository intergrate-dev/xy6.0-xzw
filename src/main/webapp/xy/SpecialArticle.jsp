<%@include file="../e5include/IncludeTag.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<%@include file="inc/MainHeader.inc" %>
<style type="text/css">
    #wrapMain {
        margin-left: 0px;
        padding-left: 0px;
        width: 98%;
    	margin: 0 auto;
    }
    #panContent {
        height: 350px;
    }
    .div{
    	border: 1px solid #ddd;
    }
    #main{
    	height:400px;
    }
    .doclistframe{
    	height:420px;
    }
</style>
<body>
<div id="wrapMain">
    <div>
    	<div class="div">
        	<%@include file="inc/SpecialSearchArticle.inc" %>
        </div>
        <div id="main">
            <div id="panContent" class="panContent">
				<%@include file="inc/Statusbar0.inc"%>
				<script type="text/javascript" src="../xy/script/StatusbarSpecial.js"></script>
            </div>
        </div>
    </div>
</div>
</body>