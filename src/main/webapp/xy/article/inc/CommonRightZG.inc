<style type="text/css">
	.clear{
		clear: both;
	}
	.templateSelect{
		width: 143px;
	    display: none;
	    height: 22px;
	    margin-top: -2px;
	    padding: 0;

        border: 1px solid #ccc;
        border-radius: 2px;
	}
	.stemplate{
		margin-left: 3px;
	}
	
	#topicPicSmallDiv{
		margin-top: 10px; 
		width: 160px;
	}
	#topicPicMidDiv{
		width: 240px;
	}
	#topicPicBigDiv{
		width:306px;
	}
	
	.model li{
		height: 40px;
		line-height: 45px;
	}
	#label{
		float: left;
	    margin-right: 5px;
	}
	.li1{
		margin-top:-10px; 
		margin-bottom:1px;
		border: 1px solid #ddd;
		border-left: 2px solid #2690e6;
		padding-top: 10px;	
		background-color: #fff	
	}

	#topicPicBigDiv,#topicPicMidDiv,#topicPicSmallDiv{
		position: relative;
	}
	.icon-remove{
		position: absolute;
		right: 0;
		top: 0;
		z-index: 100;
		display: none;
	}
	#a_attr{
	    padding: 0;
    	border-radius: 0 !important;
    	height:30px;
    	width:314px;
    	font-family: "微软雅黑"; 
    	font-size: 12px;
	}
	#a_multimediaLink{
		width: 305px;
		height: 60px;
		resize:none;
	}
	.linkArt{
		border-bottom:1px solid #000;
		padding: 5px;
	}
	#possible li{
		text-align: left;
	}
	#possible li a{
		color: #454545;
	    text-decoration: none;
	    font-size: 13px;
	    font-weight: bold;
	}
	#possible > ul:hover{
		background: rgb(232, 245, 251);
	}
	#possible > ul{
		position: relative;
	}
	.dosure{
		width: 20px;
	    height: 20px;
	    background: #000;
	    color: #fff !important;
	    border-radius: 50%;
	    font-size: 15px;
	    display: block;
	    text-align: center;
	    position:absolute;
	    right: 0;
	    bottom: 0;
	    cursor: pointer;
	    z-index: 99;
	    display: none;
	}
</style>
<c:if test="${article.columnID > 0 or channelCount > 0}">
		<li class="li1">
			<label id="label" style=" margin-top: 4px !important; margin-left: -50px;" for="pubTimer"><input style="margin-left:69px;" type="checkbox" id="pubTimer" name="pubTimer"/>定时发布</label>
			<input type="text" data-notmodifyleft="true" placeholder="格式：2015-05-30 14:30"  name="pubTime" id="pubTime" class="" />
			<img style="margin-top: -9px;" src="../../images/timekeeper.png" alt="" />
		</li>
	</c:if>
<div style="border: 1px solid #ddd; background-color: #fff;">
<ul class="tabs">
	<li class="select" tab="0">稿签</li>
	<li  tab="1">标题图片</li>
	<c:if test="${article.columnID > 0 and article.type < 3}">
		<li class="tab" id="relarts" tab="2">相关稿件</li>
	</c:if>
	<c:if test="${article.columnID > 0}">
		<li tab="4" id="commentTab">其它</li>
	</c:if>
    <c:if test="${article.columnID > 0 and article.type < 3 or article.type > 4}">
        <li  tab="3" id="tabComment" >批注</li>
    </c:if>
</ul>
<div class="rightTab" id="tab0">
  <c:if test="${article.type < 3}">
	<li>摘要：
		<span id="abstractCount"></span>
		<c:if test="${hasKeyword}">
			<c:if test="${canEditStyle}">
			<input class="btn smallBtn yellowbtn" type="button" id="btnEditAbstract" value="编辑样式" />
			</c:if>
			<input class="btn smallBtn" type="button" id="findSummaryButton" value="提取摘要" title="根据文章内容提取摘要" onclick="article_form.findSummary()" /></td> 
		</c:if>
	</li>
	<li><textarea class=" smallTextarea" id="a_abstract" name="a_abstract"><c:out value="${article.summary}"/></textarea>
	</li>

	<li class="left">关键字：</li>
	<li>
	<c:if test="${hasSummary}">
		<input class="btn smallBtn" type="button" id="findKeywordButton" value="提取关键字" title="根据文章内容提取关键字" onclick="article_form.findKeyword()" /></td>
	</c:if>
	</li>
	<li><input type="text" id="a_keyword" name="a_keyword" value="<c:out value="${article.keyword}"/>"
				class="validate[maxSize[255],required]"> 
  </c:if>

	<c:if test="${article.channel == 1}">
		<li class="left">链接标题：<span id="linkTitleCount"></span></li>
		<li class="left">&nbsp;&nbsp;<input style="margin-left:10px;margin:0px 0px 0px" type="checkbox" id="syncLinkTitle" name="syncLinkTitle"  checked="checked" />同步至关联栏目</li>
		<c:if test="${canEditStyle}">
        		  <input class="btn smallBtn yellowbtn" type="button" id="btnEditLinkTitle" value="编辑样式" />
        	  </c:if>
		<li><input type="text" id="a_linkTitle" name="a_linkTitle" value="<c:out value="${article.linkTitle}"/>"
					></li>
		
		<li class="left">短标题：<span id="shortTitleCount"></span></li>
		<c:if test="${canEditStyle}">
        		  <input class="btn smallBtn yellowbtn" type="button" id="btnEditShortTitle" value="编辑样式" />
        	  </c:if>
		<li><input type="text" id="a_shortTitle" name="a_shortTitle" value="<c:out value="${article.shortTitle}"/>"
					></li>
		<li >
		  <select  name="a_attr" id="a_attr" style="">
			  <option <c:if test="${article.attr=='63'or empty article.attr}">selected="selected"</c:if>  value ="63">普通新闻(63)</option>
			  <option <c:if test="${article.attr=='62'}">selected="selected"</c:if>  value ="62">头条新闻(62)</option>
			  <option <c:if test="${article.attr=='61'}">selected="selected"</c:if>  value ="61">图片新闻(61)</option>
			  <option <c:if test="${article.attr=='64'}">selected="selected"</c:if>  value ="64">重要新闻(64)</option>
			  <option <c:if test="${article.attr=='65'}">selected="selected"</c:if>  value ="65">其它新闻(65)</option>
		  </select>
		</li>

    <c:if test="${article.type < 3 and (article.columnID > 0 and article.channel == 1)}">
        <input type="hidden" name="a_templatePadID" id="a_templatePadID" value="<c:out value="${article.templatePadID}"/>"/>
        <input type="hidden" name="a_templatePad" id="a_templatePad" value="<c:out value="${article.templatePad}"/>"/>
        <input type="hidden" name="a_templateID" id="a_templateID" value="<c:out value="${article.templateID}"/>"/>
        <input type="hidden" name="a_template" id="a_template" value="<c:out value="${article.template}"/>"/>
        <input type="hidden" name="a_siteID" id="a_siteID" value="<c:out value="${siteID}"/>">
        <ul class="model">
            <li>WEB版模板：
                <span id="tptNameSpan_pc">
                    <c:out value="${article.template}"/>
                </span>
                <input  id="btnChgTpt_pc_clear" data-dc="pc" class="btn btnswitch" type="button" value="清空"/>
                <input  id="btnChgTpt_pc" data-dc="pc" class="btn btnswitch"  type="button" value="切换"/>
                <input id='templateSelect_pc' class="templateSelect" value="${article.template}" readonly>
                <input id="btnCrmTpt_pc" data-dc="pc" class="btn btnswitch hide" type="button" value="确定"/>
            </li>
            <li>触屏版模板：
                <span id="tptNameSpan_app" class="stemplate">
                    <c:out value="${article.templatePad}"/>
                </span>
                <input  id="btnChgTpt_app_clear" data-dc="app" class="btn btnswitch" type="button" value="清空"/>
                <input id="btnChgTpt_app" data-dc="app" class="btn btnswitch" type="button" value="切换"/>
                <input id='templateSelect_app' class="templateSelect" value="${article.templatePad}" readonly>
                <input id="btnCrmTpt_app" data-dc="app" class="btn btnswitch hide" style="" type="button" value="确定"/>
            </li>
        </ul>
    </c:if>

	  <li class="left">副题：<span id="subTitleCount"></span></li>
	  <c:if test="${canEditStyle}">
		  <input class="btn smallBtn yellowbtn" type="button" id="btnEditSubTitle" value="编辑样式" />
	  </c:if>
	  <li><input type="text" id="a_subTitle" name="a_subTitle" value="<c:out value="${article.subTitle}"/>">
	  </li>

	  <li class="left">引题：<span id="leadTitleCount"></span></li>
	  <li><input type="text" id="a_leadTitle" name="a_leadTitle" value="<c:out value="${article.leadTitle}"/>">
	  </li>
	  <li class="left">标签：</li>
      	<li><input type="text" id="a_tag" name="a_tag" value="<c:out value="${article.tag}"/>"
      			 readonly1="readonly">
      	</li>
	</c:if>
	<c:if test="${article.channel == 2}">
		<li class="left">列表角标：</li>
		<li><input type="text" id="a_mark" name="a_mark" value="<c:out value="${article.mark}"/>"
				class="validate[maxSize[255]]"> 
		</li>
<li class="left" style="display:none">&nbsp;&nbsp;<input style="margin-left:10px;margin:0px 0px 0px" type="checkbox" id="syncLinkTitle" name="syncLinkTitle"  />同步至关联栏目</li>
	</c:if>


<c:choose>

   <c:when test="${article.editor == '' and article.liability == ''}">
         <li class="left compile">编辑：</li>
            <li class="left"><input class="editors"  type="text" id="a_editor" name="a_editor" value="<c:out value="${userName}"/>"
                        class="validate[maxSize[255]]">
            </li>
            <li class="left compile" style="margin-left: 13px;">责任编辑：</li>
            <li class="left"><input class="editors"  type="text" id="a_liability" name="a_liability" value="<c:out value="${userPanName}"/>"
                        class="validate[maxSize[255]]">
            </li>
   </c:when>

   <c:otherwise>
        <li class="left compile">编辑：</li>
        <li class="left"><input class="editors"  type="text" id="a_editor" name="a_editor" value="<c:out value="${article.editor}"/>"
                    class="validate[maxSize[255]]">
        </li>
        <li class="left compile" style="margin-left: 13px;">责任编辑：</li>
        <li class="left"><input class="editors"  type="text" id="a_liability" name="a_liability" value="<c:out value="${article.liability}"/>"
                    class="validate[maxSize[255]]">
        </li>
   </c:otherwise>
</c:choose>

	<li class="left compile">初始阅读数：</li>
		<li class="left"><input style="width: 52px !important;" class="editors"  type="text" id="a_countClickInitial" name="a_countClickInitial" title="稿件发布后显示的阅读数初始值"
			value="<c:out value="${article.countClickInitial}"/>" class="validate[custom[integer,min[0],max[10000000]]]">
		</li>



    <li class="left closed1">
			<label for="a_isExclusive">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" id="a_isExclusive" name="a_isExclusive"
					<c:if test="${article.exclusive}">checked</c:if>>独家
			</label>
		  <c:if test="${article.type < 3 or article.type == 6 or article.type == 7}">
			<label for="a_discussClosed">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" id="a_discussClosed" name="a_discussClosed"
					<c:if test="${article.discussClosed}">checked</c:if>>关闭评论
			</label>
		  </c:if>
		  <c:if test="${article.channel == 2}">
			<label for="a_isBigPic" title="影响前端稿件列表的布局">
				<input style="margin-top: -2px; margin-right: 5px;" type="checkbox" id="a_isBigPic" name="a_isBigPic"
					<c:if test="${article.bigPic}">checked</c:if>>作为大图稿件
			</label>
		  </c:if>
		</li>

    <div class="clear"></div>
</div>

<div class="rightTab" id="tab1" style="display:none;">
	<div id="topicPicSmallDiv" itype="small" class="picTopic" title="列表图片">
	<c:choose>
		<c:when test="${article.picSmall != null and article.picSmall != ''}">
			<img id="picSmall" itype="small" src="../image.do?path=<c:out value="${article.picSmall}"/>"/>
			<span class="icon-remove"></span>
		</c:when>
		<c:otherwise>
			<p class="plus">+</p>
			<p class="word">小图</p>
			<span class="icon-remove"></span>
		</c:otherwise>
	</c:choose>
	</div>

    <div  id="topicPicMidDiv" itype="mid" class="picTopic" title="标题图片">
    <c:choose>
        <c:when test="${article.picMiddle != null and article.picMiddle != ''}">
            <img id="picMiddle"  src="../image.do?path=<c:out value="${article.picMiddle}"/>"/>
            <span class="icon-remove"></span>
        </c:when>
        <c:otherwise>
            <p class="plus">+</p>
            <p class="word">中图</p>
            <span class="icon-remove"></span>
        </c:otherwise>
    </c:choose>
    </div>

	<div id="topicPicBigDiv" itype="big" class="picTopic" title="焦点图片">
	<c:choose>
		<c:when test="${article.picBig != null and article.picBig != ''}">
			<img id="picBig"  src="../image.do?path=<c:out value="${article.picBig}"/>"/>
			<span class="icon-remove"></span>
		</c:when>
		<c:otherwise>
			<p class="plus">+</p>
			<p class="word">大图</p>
			<span class="icon-remove"></span>
		</c:otherwise>
	</c:choose>
	</div>



	<div id="localFileDiv"></div>
</div>

<c:if test="${article.columnID > 0 and article.type < 3}">
<div class="rightTab" id="tab2" style="display:none;">
	<div id="relDiv" class="relDiv">
		<div id="divRelBtns">
			<span class="left choiced">已选相关稿件</span>
			<input  type='button' id="linkliRelbtn" class="linkliRel" value='添加'/>
			<input  type='button' id="linkliRelclear" class="linkliRel" value='清空'/>
		</div>
		<div id="ul5" align=center width=60%></div>
		
		<div class="add" id="divRelInitBtn" >还没有相关稿件，马上<input type='button' id="linklirels" value='添加'/>
		</div>
		
		<div id="" class='clearfix linkArt'>
			<span class="left choiced">可能需要关联的稿件</span>
		</div>
		<div id="possible" align=center width=60%></div>
	</div>
</div>
</c:if>
<div class="rightTab" id="tab3" style="display:none;">
    <div class="list-group">
    </div>
</div>
<div class="rightTab" id="tab4" style="display:none;">
	<div class="clear"></div>
	<c:if test="${article.channel == 1}">
		<li class="left">多媒体链接：<span id="multimediaLinkCount"></span></li>
		<li>
			<textarea  id="a_multimediaLink" name="a_multimediaLink" ><c:out value="${article.multimediaLink}"/></textarea>
		</li>
	</c:if>

    <li class="left">行业分类：</li>
    <input class="btn smallBtn" type="button" id="btnTrade" value="设置" />
    <li>
        <input type="text" id="a_trade" name="a_trade" value="${article.trade}" readonly/>
        <input type="hidden" name="a_tradeID" id="a_tradeID" value="${article.tradeID}"/>
    </li>

	<li class="left">合作者：</li>
	<li><input type="text" id="a_collaborator" name="a_collaborator" value="<c:out value="${article.collaborator}"/>"
			class="validate[maxSize[255]]">
	</li>

	<div class="divTitle clear">
		<span id="spanExtControl" class="plusControl" title="展开/收缩">扩展字段：</span>
		<span id="extNameSpan">
				${article.extFieldGroup==null||article.extFieldGroup==""?"":article.extFieldGroup}
		</span>
		<input id="oldExtFieldGroup" type="hidden" value='${article.extFieldGroup==null||article.extFieldGroup==""?"":article.extFieldGroup}' />

		<input id="extChgBtn" class="btn" style="width:40px;" type="button" value="设置"/>
		<select id="selectExtField" style="display:none;" >
			<option value="">点击“切换”后才出现，显示扩展字段组供选择</option>
		</select>
		<input id="extCancelBtn" class="btn" style="width: 30px;display: none;" type="button" value="取消"/>

		<input type="hidden" name="a_extFieldGroup" id="a_extFieldGroup" data-val='<c:out value="${article.extFieldGroup}"/>' value="<c:out value="${article.extFieldGroup}"/>"/>
		<input type="hidden" name="a_extFieldGroupID" id="a_extFieldGroupID" data-val='<c:out value="${article.extFieldGroupID}"/>' value="<c:out value="${article.extFieldGroupID}"/>"/>
	</div>
	<div id="extContentDiv"></div>
</div>

<script>
	var right_tab = {
        init : function() {
            $(".tabs li").click(right_tab._tabClick);
            if($("#tab1 img").length != 0){
                for(var i = 0; i<$(".tabs").find('li').length; i++){
                   if( $(".tabs li").eq(i).attr('tab') != 1 ){
                        continue;
                   }
                    //$(".tabs li").eq(i).click();
                    $(".tabs li").eq(i).addClass('selectImg')
                }
            }
        },
        //点击TAB页
        _tabClick : function(evt) {
            $(".tabs li").removeClass("select");

            var src = $(evt.target);
            src.addClass("select");

            var tabID = src.attr("tab");

            $(".rightTab").hide();
            $("#tab" + tabID)
                .attr("expanded", "true") //是否曾被展开
                .show();
        }
    }
    $(function(){
        right_tab.init();
    });
    $('#pubTimer').on('click',function(){
       if($(this).attr('checked')){
           $("#btnSave").hide();
       }else{
           $("#btnSave").show();
       }
    });
    $('#btnChgTpt_pc_clear').on('click',function(){
        $('#templateSelect_pc').val('');
        $('#a_template').val('');
        $('#tptNameSpan_pc').text('');

    });
    $('#btnChgTpt_app_clear').on('click',function(){
        $('#templateSelect_app').val('');
        $('#a_templatePad').val('');
        $('#tptNameSpan_app').text('');
    });
</script>
</div>	