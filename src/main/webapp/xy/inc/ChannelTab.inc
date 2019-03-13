<ul class="channels">
	<c:forEach var="ch" items="${channels}">
		<c:if test="${ch != null}">
		<li class="channelTab" ch="<c:out value="${ch.id}"/>"><c:out value="${ch.name}"/></li>
		</c:if>
	</c:forEach>
</ul>
<script>
	var channel_tab = {
		curTab : null,
		init : function() {
			var tabs = $(".channelTab");
			tabs.click(channel_tab._tabClick);
			if (tabs.length <= 1) {
				$(".channels").hide();
			}
			tabs.first().click();
		},
		//点击TAB页
		_tabClick : function(evt) {
			$(".channelTab").removeClass("select");
			
			channel_tab.curTab = $(evt.target);
			channel_tab.curTab.addClass("select");
			
			var ch = channel_tab.curTab.attr("ch");
			
			channel_tab.tabClick(ch);
		},
		//点击TAB页时的动作，由引用页面实现
		tabClick : function(ch) {
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
	});
</script>
