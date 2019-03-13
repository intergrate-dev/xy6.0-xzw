package com.founder.xy.jpublish.template.component;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.founder.e5.commons.StringUtils;
import com.founder.xy.jpublish.ColParam;
import com.founder.xy.jpublish.JsonHelper;
import com.founder.xy.jpublish.data.BareArticle;
import com.founder.xy.jpublish.data.PubArticle;
import com.founder.xy.redis.RedisKey;
import com.founder.xy.redis.RedisManager;

/**
 * 分页稿件列表组件
 * @author Gong Lijie
 */
public class ArticleListPageComponent extends ArticleListComponent {

	public ArticleListPageComponent(ColParam param,JSONObject comJson){
		this(param, comJson, false);
	}
	public ArticleListPageComponent(ColParam param, JSONObject comJson, boolean preview) {
		super(param, comJson, preview);
	}
	
	//分页时，返回组件的每页内容
	public String[] getComponentResults() throws Exception {
		List<String> result = new ArrayList<>();
		
		componentData.put("currentColumn", param.getColLibID() + "," + param.getColID());//当前所在的栏目ID
		//先读出所有的稿件
		List<PubArticle> articles = null;
		boolean needRefresh = false;
		
		int page = getPage();
		int countPerPage = getCountPerPage();
		for (int pageIndex = 0; pageIndex < page; pageIndex++) {
			//得到redis里保存稿件列表json的key：co.articlelist.<当前页数>.<组件实例ID>.<colIDs>
			String key = getKey(pageIndex);
			String value = RedisManager.get(key);
			
			//对于多个页，只需要在第一页时判断是否有栏目刷新即可
			if (pageIndex == 0)
				needRefresh = preview || needRefresh(value);
			
			if (needRefresh) {
				if (articles == null) articles = getListData();
				
				// 从全部（10页）的稿件中截取出本页内的稿件列表
				List<BareArticle> part = new ArrayList<>();
				int start = pageIndex * countPerPage;
				for (int j = start; j < articles.size() && j < start + countPerPage; j++) {
					part.add(articles.get(j));
				}
				if (part.size() > 0) {
					componentData.put("articles", part);
					
					String pageContent = process(key);
					
					result.add(pageContent);
				}
			} else {
				if(value!=null){
				JSONObject json = new JSONObject(value);
				String pageContent = JsonHelper.getString(json, "data");
				result.add(pageContent);
				}
			}
		}
		return result.toArray(new String[0]);
	}
	
	@Override
	protected void getComponentData() {
		throw new RuntimeException("不应该走到这个getComponentData()方法");
	}
	@Override
	protected String process() {
		throw new RuntimeException("不应该走到这个process()方法");
	}
	
	@Override
	protected int getCount() {
		//模板中指定每页显示的个数，乘以页数，得到总个数
		int countPerPage = getCountPerPage();
		int page = getPage();
		
		return countPerPage * page;
	}

	/** 每页的个数 */
	private int getCountPerPage() {
		int countPerPage = JsonHelper.getInt(dataJSON, "count", 20); //每页条数
		return countPerPage;
	}

	/** 需要显示的页数 */
	private int getPage() {
		return JsonHelper.getInt(dataJSON, "page", 10); //页数，默认为10页
	}

	//得到redis里保存稿件列表json的key：co.articlelist.<当前页数>.<组件实例ID>.<指定栏目ID的串>
	private String getKey(int page) {
		return RedisKey.CO_ARTICLELIST_KEY +  "." + page + "." + comID + "." + StringUtils.join(colIDs);
	}

	private String process(String key) throws Exception{
		String result = super.processForSub();

		//缓存
		JSONObject resultJson = new JSONObject();
		resultJson.put("time", System.currentTimeMillis());
		resultJson.put("data", result);

		if(!preview) {//预览时，图片地址取的内网地址，不放入到redis中
			RedisManager.setLonger(key, resultJson.toString());
		}
		
		return result;
	}
}
