package com.founder.xy.workspace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.founder.e5.commons.ArrayUtils;
import com.founder.e5.workspace.ProcHelper;
import com.founder.xy.column.Column;
import com.founder.xy.column.ColumnReader;
import com.founder.xy.commons.LibHelper;
import com.founder.xy.system.site.SiteUserReader;
import com.founder.xy.workspace.MainHelper;

import com.founder.e5.commons.DomHelper;
import com.founder.e5.commons.StringUtils;
import com.founder.e5.context.BaseField;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;
import com.founder.e5.flow.Flow;
import com.founder.e5.flow.FlowNode;
import com.founder.e5.flow.FlowReader;
import com.founder.e5.permission.FlowPermissionReader;
import com.founder.e5.workspace.service.DefaultDocListService;
import com.founder.xy.commons.DocTypes;
import com.founder.xy.commons.InfoHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * 列表查询的服务类，继承e5。
 * @author Gong Lijie
 */
public class DocListService extends DefaultDocListService{
	@Autowired
	SiteUserReader siteUserReader;
	@Autowired
	private ColumnReader colReader;
	@Override
	public String getDocList() {
		int docTypeID = DomHelper.getDocTypeIDByLibID(param.getDocLibID());
		if(checkSQL()) return EMPTY_XML;

		//是否需要Solr检索. 稿件和原稿，可能走Solr全文检索
		boolean needSolr = docTypeID == DocTypes.ARTICLE.typeID()
				&& (param.getCondition().contains("SYS_TOPIC=") || param.getCondition().contains("a_source="));

		//发布库查询，需从栏目关联表或Solr检索库得到稿件ID再查稿件库。
		boolean needRel = (docTypeID == DocTypes.ARTICLE.typeID() && param.getCatTypeID() > 0);

		//查看话题稿件,特殊处理
        boolean topicsRel = (docTypeID == DocTypes.ARTICLE.typeID() && param.getFvID() == 999888);
        //话题库查看话题稿件特殊处理
		boolean topicArticle = (docTypeID == DocTypes.ARTICLE.typeID() && param.getFvID() == 999887);

//		boolean topics = (docTypeID == DocTypes.TOPICS.typeID()&& param.getFvID() != 999886);

		//查看话题列表，通用方法
        boolean topicsMerge = (docTypeID == DocTypes.TOPICS.typeID());

		if (needRel || needSolr) {
			DocListTwoSteps docListService = new DocListTwoSteps();
			docListService.init(param);
			return docListService.getDocList(needRel, needSolr);
		} else if (topicsRel){
            DocListTopicArticle docListTopicArticle = new DocListTopicArticle();
            param.setFvID(1);//将话题稿件特殊处理的folderID修改回来
            docListTopicArticle.init(param);
            return docListTopicArticle.getDocList(topicsRel);
        } else if(topicArticle){
            DocListTopicsTopicArticle docListTopicsTopicArticle = new DocListTopicsTopicArticle();
            param.setFvID(1);//将话题组话题稿件特殊处理的folderID修改回来
            docListTopicsTopicArticle.init(param);
            return docListTopicsTopicArticle.getDocList(topicsRel);
		}
//		else if(topics){
//            DocListTopicsTopic docListTopicsTopic = new DocListTopicsTopic();
//            docListTopicsTopic.init(param);
//            return docListTopicsTopic.getDocList(topicsRel);
//        }
        else if(topicsMerge){//查看话题列表，通用方法
            DocListTopicMerge docListTopicMerge = new DocListTopicMerge();
            String type = "2";//1不需要处理where条件，2需要处理where条件
            if(param.getFvID()==999886){
                type = "1";
            }
            param.setFvID(110);//将话题组话题稿件特殊处理的folderID修改回来
            docListTopicMerge.init(param);
            return docListTopicMerge.getDocList(topicsRel, type);
        } else {
			//检索用户时 可以跨部门
			if(param.getCondition().contains("@QUERYCODE@=qUserExt")&&(param.getCondition().contains("u_code=")||param.getCondition().contains("u_name=")))
				param.setRuleFormula(param.getRuleFormula().replaceAll("u_orgID_EQ_\\d*(_AND_)?",""));
			return super.getDocList();
		}
	}

	private boolean checkSQL() {
		String reg = "(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|"
				+ "(\\b(select|update|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
		Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		return (sqlPattern.matcher(param.getCondition()).find()|| sqlPattern.matcher(param.getRuleFormula()).find());
	}

	//栏目稿件查询时，查的是关联表，此时E5平台的sql中使用distinct，本项目中不需要。
	//为图方便直接改变本方法的返回值，则sql中不再使用distinct
	public boolean isRelationTable() {
		return false;
	}

	public String getWhere() {
		if (where != null) return where;
		//规则过滤器条件
		String rule_filter = ruleFormulaString();
		//检索条件
		rule_filter += queryString();


		//最后加上DELETEFLAG
		if (rule_filter.indexOf(BaseField.DELETEFLAG.getName()) < 0)
			rule_filter += " AND " + BaseField.DELETEFLAG.getName() + "=0";

		//去掉最前面的AND
		if (rule_filter.startsWith(" AND ")) rule_filter = rule_filter.substring(5);

		where = rule_filter;
		return where;
	}

	/**
	 * 规则公式需要解析
	 */
	public String ruleFormulaString() {
		String rule = super.ruleFormulaString();

		return parseWhere(rule);
	}

	protected String parseWhere(String formula) {

		if (formula.contains("@AUDIT@")) {
			String rule;
			if(formula.contains("SYS_CURRENTNODE")){
				int nodeID = Integer.valueOf(formula.substring(formula.indexOf("SYS_CURRENTNODE")+16,formula.indexOf(" and @AUDIT@")));
				String cols = getNodeCol(nodeID);

				if(StringUtils.isBlank(cols) )
					rule = "1 = 2 ";
				else if(StringUtils.getIntArray(cols).length>999)
					rule = "1 = 1";
				else
					rule = "a_columnID IN ( " +cols+" )";
			}else {
				//待审批稿件公式，替换为实际的公式
				int[] myNodes = getMyAuditNodes();
				if (myNodes == null || myNodes.length == 0) {
					rule = "0=1";
				} else if (myNodes.length == 1) {
					rule = "SYS_CURRENTNODE=" + myNodes[0];
				} else {
					rule = "(SYS_CURRENTNODE in (" + StringUtils.join(myNodes, ",") + "))";
				}
			}
			formula = formula.replace("@AUDIT@", rule);
		}
		return formula;
	}

	private String getNodeCol(int nodeID) {
		//1 取流程ID
		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		StringBuffer result =new StringBuffer() ;
		try {
			FlowNode node = flowReader.getFlowNode(nodeID);
			HttpServletRequest request = param.getRequest();
			int siteID = MainHelper.getSiteEnable(request);
			int userID = ProcHelper.getUserID(request);
			int userLibID = LibHelper.getUserExtLibID(request);
			// 取出本站点下所有的审批栏目
			int colLibID = LibHelper.getColumnLibID(request);
			List<Column> cols = colReader.getAuditColumns(colLibID, siteID);

			// 用户Web版可操作的栏目ID
			long[] ids = siteUserReader.getRelated(userLibID, userID, siteID, 0);
			filter(result, cols, ids,node.getFlowID());

			// 用户App版可操作的栏目ID
			ids = siteUserReader.getRelated(userLibID, userID, siteID, 4);
			filter(result, cols, ids,node.getFlowID());
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		if(result.length()>0)
			//去掉结尾逗号
			return result.toString().substring(0,result.length()-1);
		else
			return "";
	}

	private void filter(StringBuffer result, List<Column> cols, long[] ids, int flowID) {
		for (Column col : cols) {
			if(col.getFlowID() != flowID)
				continue;
			// 栏目的父路径。任意一级路径有权限即可
			int[] path = StringUtils.getIntArray(col.getCasIDs(), "~");

			for (long colID : path) {
				if (ArrayUtils.contains(ids, colID)) {
					result.append(col.getId()).append(",");
					break;
				}
			}
		}
	}

	//取出有权限的审批节点的ID
	private int[] getMyAuditNodes() {
		List<Integer> result = new ArrayList<Integer>();

		int roleID = param.getUser().getRoleID();

		FlowPermissionReader fpReader = (FlowPermissionReader)Context.getBean(FlowPermissionReader.class);
		FlowNode[] nodes = getAuditFlowNodes();
		if (nodes != null) {
			try {
				for (int j = 0; j < nodes.length; j++) {
					//过滤出有权限的流程节点ID集合
					int permission = fpReader.get(roleID, nodes[j].getFlowID(), nodes[j].getID());
					if (permission > 0)
						result.add(nodes[j].getID());
				}
			} catch (E5Exception e) {
				e.printStackTrace();
			}
		}
		return InfoHelper.getIntArray(result);
	}

	//取出流程的审批节点
	private FlowNode[] getAuditFlowNodes() {
		List<FlowNode> result = new ArrayList<FlowNode>();

		int docTypeID = InfoHelper.getArticleTypeID();

		FlowReader flowReader = (FlowReader)Context.getBean(FlowReader.class);
		try {
			Flow[] flows = flowReader.getFlows(docTypeID);
			if (flows != null) {
				//找出稿件的所有流程，不包括第一个“无审批流程”
				for (int i = 1; i < flows.length; i++) {
					int flowID = flows[i].getID();
					//得到这些流程的中间流程节点ID，也就是去掉第一个节点（第一个是未发布阶段，不是审批阶段）
					//和最后两个节点（在发布阶段、已发布阶段）
					FlowNode[] nodes = flowReader.getFlowNodes(flowID);
					if (nodes != null) {
						for (int j = 1; j < nodes.length - 2; j++) {
							result.add(nodes[j]);
						}
					}
				}
			}
		} catch (E5Exception e) {
			e.printStackTrace();
		}
		return result.toArray(new FlowNode[0]);
	}
}
