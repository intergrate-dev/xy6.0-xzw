
<c:if test="${article.columnID > 0 and (article.type < 3 or article.type == 9)}">
	
	<div class="divWidget plusControl" id="spanWidgetControl" style="position: relative;">
		<div style="height: 10px; background-color: #f8f8f8; position: absolute; left: 0; top:-12px; width: 100%;"></div>
		<span style="margin-top: 3px; display: block;" class="" title="展开/收缩">挂件</span>
		<span class="icon-chevron-down show"></span>
		<span class="icon-chevron-up"></span>
	</div>
	<div id="widgetDiv" style="display:none; min-height:284px; background-color: #fff;" >
		<ul class="nav nav-tabs" role="tablist" id="setting">
			
			<li role="presentation" class="active">
				<a href="#Pic" aria-controls="Pic" role="tab" data-toggle="tab">组图</a>
			</li>
			<li role="presentation">
				<a href="#Video" aria-controls="Video" role="tab" data-toggle="tab">视频</a>
			</li>
			<li role="presentation">
				<a href="#Atta" aria-controls="Atta" role="tab" data-toggle="tab">附件</a>
			</li>
			<c:if test="${article.channel == 2}">
			<li role="presentation">
				<a href="#Vote" aria-controls="Vote" role="tab" data-toggle="tab">投票</a>
			</li>
			</c:if>
		</ul>

		<div class="tab-content addsource">
			<div role="tabpane" class="tab-pane active" id="Pic" >
                <div class="add" id="widgetPicDiv"><span class="picnone">还没有组图挂件，马上</span>
                    <input type="button" id="widgetPicBtn" value="+添加">
                </div>

                <div id="widgetPicShowDiv"  style="display: none;">
                    <div style="text-align: right; padding: 10px; padding-bottom: 0;">组图挂件只能选择一个<input class="btn" type="button" onclick="article_widget._picSelect()" value="重新选择"></div>
                    <div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;" >
						<div style="text-align: right;"><a class="close" herf="#" style="display: block;">×</a></div>
                        <div style="display: inline-block;"><img id="wp_img" style="max-width: 130px;max-height: 150px;" src=""/></div>
                        <div style="display: inline-block;margin-left: 15px;">
                            <ul>
								<li id="wp_Li_topic" style="font-size: large;" ><strong></strong></li>
                                <li id="wp_Li_author">编辑</li>
                                <li id="wp_Li_createDate">时间</li>

							</ul>
                        </div>
                    </div>
                </div>
				<div style="clear:Both;"></div>
			</div>

			<div role="tabpane" class="tab-pane " id="Video">
				<div class="add" id="widgetVideoDiv"><span class="picnone">还没有视频挂件，马上</span>
					<input type="button" id="widgetVideoBtn" value="+添加">
				</div>

				<div id="widgetVideoShowDiv"  style="display: none;">
					<div style="text-align: right;padding: 10px; padding-bottom: 0; ">视频挂件只能选择一个<input class="btn" type="button" onclick="article_widget._videoSelect()" value="重新选择"></div>
					<div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;" >
						<div style="text-align: right;"><a class="close" herf="#" style="display: block;">×</a></div>
						<div style="display: inline-block;"><img id="wvd_img" style="max-width: 206px;max-height: 150px;" src=""/></div>
						<div style="display: inline-block;margin-left: 15px;">
							<ul>
								<li id="wvd_Li_topic" style="font-size: large;" ><strong></strong></li>
								<li id="wvd_Li_author">编辑</li>
								<li id="wvd_Li_createDate">时间</li>

							</ul>
						</div>
					</div>
				</div>
				<div style="clear:Both;"></div>
				<!--
				<ul id="ul3">
					<li id="linklivideo">
						<img class="top1" alt="上传视频" src="../pic/img/uploadfile.png">
					</li>
				</ul>
				<div style="clear:Both;"></div>-->
			</div>

			<div role="tabpane" class="tab-pane " id="Atta">
				<tr><th>已选附件：</th><td>
				<ul id="ul4">
					<div >
						<ul id="InputsWrapper"></ul>
					</div>
    				<input type="file" name="fileName" id="file_upload"/>
    			</ul>
			</div>

			<div role="tabpane" class="tab-pane " id="Vote">
				<div class="add" id="widgetVoteDiv"><span class="picnone">还没有投票挂件，马上</span>
					<input type="button" id="widgetVoteBtn" value="+添加">
				</div>

				<div id="widgetVoteShowDiv"  style="display: none;">
					<div style="text-align: right; padding: 10px 10px 0 10px;">投票挂件只能选择一个<input class="btn" type="button" onclick="article_widget._voteSelect()" value="重新选择"></div>
					<div style="border: 1px solid #ddd; margin: 10px; text-align: left; padding: 10px;" >
						<div style="text-align: right;"><a class="close" herf="#" style="display: block;">×</a></div>
						<div style="display: inline-block;margin-left: 15px;">
							<ul>
								<li id="wv_Li_topic" style="font-size: large;" ><strong></strong></li>
								<li id="wv_Li_author">编辑</li>
								<li id="wv_Li_createDate">时间</li>

							</ul>
						</div>
					</div>
				</div>
				<div style="clear:Both;"></div>
			</div>
		</div>
	</div>
</c:if>