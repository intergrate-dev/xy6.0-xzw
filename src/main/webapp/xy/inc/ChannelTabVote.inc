<ul class="channels">
			<li class="channelTab" id="commonPoll">
			稿内投票
			</li>
			<li class="channelTab" id="activityPoll">
			活动投票
			</li>
</ul>
<script>
	var channel_tab = {
		curTab : null,
		ch : "",
		html1:"",
		html2:"",
		init : function() {
		    ch = "稿内投票";
			var tabs = $(".channelTab");
			tabs.click(channel_tab._tabClick);
			if (tabs.length <= 1) {
				$(".channels").hide();
			}
			if(document.getElementsByName("flag")[0].value !== '1'){
			if($("iframe[src^='../amuc/MainSimple.do?t=memvote']",parent.document).length==0){
						createIframe();
			}
            $("iframe[src^='../amuc/MainSimple.do?t=memvote']",parent.document)[0].src = '../amuc/MainSimple.do?t=memvote&siteID='+siteID;
			}
		},
		//点击TAB页
		_tabClick : function(evt) {
			$(".channelTab").removeClass("select");
			
			channel_tab.curTab = $(evt.target);
			channel_tab.curTab.addClass("select");

			var id = channel_tab.curTab.attr("id");
			channel_tab.tabClick(id);
		},
		//点击TAB页时的动作，由引用页面实现
		tabClick : function(id) {
		
		  if(id == "commonPoll") {
		  ch = "稿内投票";
		  $("iframe[showname$='投票'][src^='../xy/MainGroupVote.do?t=nisvote']",parent.document)[0].src = '../xy/MainGroupVote.do?t=nisvote&siteID='+siteID+'&flag=1&ch=0';
		  $("#main_resourcetree").css('display','block');
		  $("div#rs_tree").css('margin-top','-5px!important');
		  $("#commonPoll").addClass("select");
		  }
		  if(id == "activityPoll") {
		  ch = "活动投票";
		  if($("iframe[src^='../amuc/MainSimple.do?t=memvote']",parent.document).length==0){
						createIframe();
			}
		  $("iframe[src^='../amuc/MainSimple.do?t=memvote']",parent.document)[0].src = "../amuc/MainSimple.do?t=memvote&siteID="+siteID;
          $("iframe[showname$='投票'][src^='../xy/MainGroupVote.do?t=nisvote']",parent.document).contents().find("#main")[0].replaceWith($("iframe[src^='../amuc/MainSimple.do?t=memvote']",parent.document)[0]);
        $("#mem").css('display','');
        $("#mem").css('width','100%');
        $("#mem").css('height','92%');
        $("#mem").css('border','none');
        $("#wrapMain").css('height','100%');
        $("div#listing").css('height','100%');
        $("div#listing").css('overflow','scroll');
        $("#main_resourcetree").css('display','none');
        $("#activityPoll").addClass("select");
		  }

		},
		//取当前渠道
		getChannelType : function() {
			if (channel_tab.curTab == null)
				return 0;
			else
				return channel_tab.curTab.attr("ch");
		}
	}
	$(function(){
		channel_tab.init();
		$("#commonPoll").addClass("select");
		$("div#rs_tree").css('margin-top','-5px!important');
	});
	function createIframe() {
      var i = parent.document.createElement("iframe");
      i.src = "../amuc/MainSimple.do?t=memvote&siteID="+siteID;
      i.showname = "投票";
      i.id= "mem";
      i.class = "tabFrame";
      i.style = "display:none;";
      parent.document.getElementById("nisvoteFrm").appendChild(i);
 };
</script>
