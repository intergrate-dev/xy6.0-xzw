package com.founder.xy.jpublish.template.component;

import com.founder.xy.jpublish.data.PubArticle;

public interface Component {
	public void setArticle(PubArticle article);
	
	public String getComponentResult() throws Exception;
	
	/**
	 * 组件实例在解析时生成的数据放在Map中，getData方法从Map中按Key取值
	 * @param key
	 * @return
	 */
	public Object getData(String key);
	
	/**
	 * 把一个变量放入Map，作为模板中可调用的变量
	 */
	public void setData(String key, Object data);
	
	/**
	 * 组件类型
	 */
	public int getType();
}
