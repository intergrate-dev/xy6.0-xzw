<form id="form">
	<%@include file="CommonUpper.inc"%>
	<div class="underTop">
		<div id="divMain">
			<%@include file="CommonMiddle.inc"%>
			
			<c:if test="${ch == 1}">
			<li class="link1"><span class="require star">*</span>栏目选择：
				<input id="a_linkName" name="a_linkName" class="validate[maxSize[255],required]" value="<c:out value="${article.linkName}"/>" readonly></input>
				<input class="btn" type="button" id="btnlinkColName" value="选择栏目" />
				<input type="hidden" id="a_linkID" name="a_linkID" value="<c:out value="${article.linkID}"/>">  
			</li>
			<li class="link1" style="overflow:hidden;">
			    <span class="require star" style="float:left; visibility:hidden;">*</span>
			    <span id="picLoad" style="float:left;">头图上传：</span>
			    <div class="container kv-main" style="position: relative;">
                    <form enctype="multipart/form-data">
                        <div class="form-group">
                            <input id="uploadInput-special" type="file" name="file" style="height: 30px;" multiple>
                        </div>
                         <span style="position: absolute; bottom: 5px; right: 28px; color: #999;">(建议图片大小60k以内,比例4:1)</span>	
                    </form>
                </div>
			    <input type="hidden" name="columnIcon" id="columnIcon" value="${columnIcon}"/>
			   
            </li>
            <li class="mgl abstract1" style="position:relative;"><span class="left mgr">摘要：</span>
                <span id="abstractNum" style="position:absolute;bottom:12px;left:450px;color:#666;font-size:12px;">摘要字数:0/2000</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[1024]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			</c:if>
			</li>
			<c:if test="${ch == 0}">
			<li class="mgl abstract1"><span class="left mgr">摘要：</span>
				<textarea id="a_abstract" name="a_abstract" class="validate[maxSize[2000]] a_abstract"><c:out value="${article.summary}"/></textarea>
				<c:if test="${canEditStyle}">
				<input class="left btn" type="button" id="btnEditAbstract" value="编辑样式" />
				</c:if>
			<li class="link1">
				<span class="left require star">*</span>
				<span class="left mgr">专题设计：</span>
				<input class="specialModelInput validate[required]" type="text" name="a_template" id="a_template" value="<c:out value="${article.template}"/>" readonly/>
				<input type="hidden" name="a_templateID" id="a_templateID" value="<c:out value="${article.templateID}"/>"/>
				<input type="hidden" name="a_templatePadID" id="a_templatePadID" value="<c:out value="${article.templatePadID}"/>"/>
				<input type="hidden" name="a_templatePad" id="a_templatePad" value="<c:out value="${article.templatePad}"/>"/>
				<input class="btn " type="button" id="btnSpecialTemplate" value="选择" />
				<input class="btn " type="button" id="btnSpeciaPreview" value="预览" />
			</li>
			</c:if>
		</div>
		<div id="divRight">
			<%@include file="CommonRight.inc"%>
		</div>
	</div>
</form>
<style type="text/css">
	.specialModelInput{
		width: 500px !important;
		float: left;
		margin-right: 10px;
	}
	.specialModel{
		margin-left: 20px;
	
	}
	.modelBtn{
		width: 185px !important;
    	height: 45px;
    	font-size: 25px;
	}
	.abstract1{
		margin-top: 24px;
		height: 160px;
	}
	#a_linkName{
		box-shadow: inset 0 1px 1px rgba(0,0,0,0.075);
		width: 513px;
		border: 1px solid #ddd;
		height: 28px;
		margin-left: 0px;
	}
	#btnlinkColName{
		margin-left: 5px
	}
	
	.abstract2 {
		margin-right: 18px;
	}
	
	
	.star{
		margin-left: 10px;
		margin-top: 0;
	}
	.container{
        width:304px;
        margin:0;
        float:left;
    }
	.modal{
		display: none;
	}
	.btn-default{
		padding: 1px 6px;
		font-size:12px;
	}
	.kv-zoom-actions button{
		display: none;
	}
	.kv-zoom-actions button:last-child{
		display: block;
	}
	/*@-webkit-keyframes progress-bar-stripes{from{background-position:40px 0}to{background-position:0 0}}@-o-keyframes progress-bar-stripes{from{background-position:40px 0}to{background-position:0 0}}@keyframes progress-bar-stripes{from{background-position:40px 0}to{background-position:0 0}}.progress{height:20px;margin-bottom:20px;overflow:hidden;background-color:#f5f5f5;border-radius:4px;-webkit-box-shadow:inset 0 1px 2px rgba(0,0,0,.1);box-shadow:inset 0 1px 2px rgba(0,0,0,.1)}.progress-bar{float:left;width:0;height:100%;font-size:12px;line-height:20px;color:#fff;text-align:center;background-color:#337ab7;-webkit-box-shadow:inset 0 -1px 0 rgba(0,0,0,.15);box-shadow:inset 0 -1px 0 rgba(0,0,0,.15);-webkit-transition:width .6s ease;-o-transition:width .6s ease;transition:width .6s ease}.progress-bar-striped,.progress-striped .progress-bar{background-image:-webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:-o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);-webkit-background-size:40px 40px;background-size:40px 40px}.progress-bar.active,.progress.active .progress-bar{-webkit-animation:progress-bar-stripes 2s linear infinite;-o-animation:progress-bar-stripes 2s linear infinite;animation:progress-bar-stripes 2s linear infinite}.progress-bar-success{background-color:#5cb85c}.progress-striped .progress-bar-success{background-image:-webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:-o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent)}.progress-bar-info{background-color:#5bc0de}.progress-striped .progress-bar-info{background-image:-webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:-o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent)}.progress-bar-warning{background-color:#f0ad4e}.progress-striped .progress-bar-warning{background-image:-webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:-o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent)}.progress-bar-danger{background-color:#d9534f}.progress-striped .progress-bar-danger{background-image:-webkit-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:-o-linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent);background-image:linear-gradient(45deg,rgba(255,255,255,.15) 25%,transparent 25%,transparent 50%,rgba(255,255,255,.15) 50%,rgba(255,255,255,.15) 75%,transparent 75%,transparent)}*/
</style>