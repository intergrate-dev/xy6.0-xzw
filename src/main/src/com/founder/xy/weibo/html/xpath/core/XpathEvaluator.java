package com.founder.xy.weibo.html.xpath.core;
/*
   Copyright 2014 Wang Haomiao<et.tw@163.com>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import com.founder.e5.commons.StringUtils;
import com.founder.xy.weibo.html.xpath.exception.NoSuchAxisException;
import com.founder.xy.weibo.html.xpath.exception.NoSuchFunctionException;
import com.founder.xy.weibo.html.xpath.model.JXNode;
import com.founder.xy.weibo.html.xpath.model.Node;
import com.founder.xy.weibo.html.xpath.model.Predicate;
import com.founder.xy.weibo.html.xpath.util.CommonUtil;
import com.founder.xy.weibo.html.xpath.util.ScopeEm;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author ����� [ et.tw@163.com ]
 * @since 14-3-12
 */
public class XpathEvaluator {
    private Map<String, Method> emFuncs;
    private Map<String, Method> axisFuncs;

    public XpathEvaluator() {
        emFuncs = new HashMap<String, Method>();
        axisFuncs = new HashMap<String, Method>();
        for (Method m : Functions.class.getDeclaredMethods()) {
            emFuncs.put(renderFuncKey(m.getName(), m.getParameterTypes()), m);
        }
        for (Method m : AxisSelector.class.getDeclaredMethods()) {
            axisFuncs.put(renderFuncKey(m.getName(), m.getParameterTypes()), m);
        }
    }

    /**
     * xpath������������ڣ�ͬʱԤ���?�确|��
     *
     * @param xpath
     * @param root
     * @return
     */
    public List<JXNode> xpathParser(String xpath, Elements root) throws NoSuchAxisException, NoSuchFunctionException {
        if (xpath.contains("|")) {
            List<JXNode> rs = new LinkedList<JXNode>();
            String[] chiXpaths = xpath.split("\\|");
            for (String chiXp : chiXpaths) {
                if (chiXp.length() > 0) {
                    rs.addAll(evaluate(chiXp.trim(), root));
                }
            }
            return rs;
        } else {
            return evaluate(xpath, root);
        }
    }

    /**
     * ��ȡxpath�����﷨��
     *
     * @param xpath
     * @return
     */
    public List<Node> getXpathNodeTree(String xpath) {
        NodeTreeBuilderStateMachine st = new NodeTreeBuilderStateMachine();
        while (st.state != NodeTreeBuilderStateMachine.BuilderState.END) {
            st.state.parser(st, xpath.toCharArray());
        }
        return st.context.xpathTr;
    }

    /**
     * ���xpath������
     *
     * @param xpath
     * @param root
     * @return
     */
    public List<JXNode> evaluate(String xpath, Elements root) throws NoSuchAxisException, NoSuchFunctionException {
        List<JXNode> res = new LinkedList<JXNode>();
        Elements context = root;
        List<Node> xpathNodes = getXpathNodeTree(xpath);
        for (int i = 0; i < xpathNodes.size(); i++) {
            Node n = xpathNodes.get(i);
            LinkedList<Element> contextTmp = new LinkedList<Element>();
            if (n.getScopeEm()== ScopeEm.RECURSIVE || n.getScopeEm() == ScopeEm.CURREC) {
                if (n.getTagName().startsWith("@")) {
                    for (Element e : context) {
                        //��������������ڵ�
                        String key = n.getTagName().substring(1);
                        if (key.equals("*")) {
                            res.add(JXNode.t(e.attributes().toString()));
                        } else {
                            String value = e.attr(key);
                            if (!StringUtils.isBlank(value)) {
                                res.add(JXNode.t(value));
                            }
                        }
                        //�����������Ӵ�ڵ�
                        for (Element dep : e.getAllElements()) {
                            if (key.equals("*")) {
                                res.add(JXNode.t(dep.attributes().toString()));
                            } else {
                                String value = dep.attr(key);
                                if (!StringUtils.isBlank(value)) {
                                    res.add(JXNode.t(value));
                                }
                            }
                        }
                    }
                } else if (n.getTagName().endsWith("()")) {
                    //�ݹ�ִ�з���Ĭ��ֻ֧��text()
                    res.add(JXNode.t(context.text()));
                } else {
                    Elements searchRes = context.select(n.getTagName());
                    for (Element e : searchRes) {
                        Element filterR = filter(e, n);
                        if (filterR != null) {
                            contextTmp.add(filterR);
                        }
                    }
                    context = new Elements(contextTmp);
                    if (i == xpathNodes.size() - 1) {
                        for (Element e : contextTmp) {
                            res.add(JXNode.e(e));
                        }
                    }
                }

            } else {
                if (n.getTagName().startsWith("@")) {
                    for (Element e : context) {
                        String key = n.getTagName().substring(1);
                        if (key.equals("*")) {
                            res.add(JXNode.t(e.attributes().toString()));
                        } else {
                            String value = e.attr(key);
                            if (!StringUtils.isBlank(value)) {
                                res.add(JXNode.t(value));
                            }
                        }
                    }
                } else if (n.getTagName().endsWith("()")) {
                    res = (List<JXNode>) callFunc(n.getTagName().substring(0, n.getTagName().length() - 2), context);
                } else {
                    for (Element e : context) {
                        Elements filterScope = e.children();
                        if (!StringUtils.isBlank(n.getAxis())) {
                            filterScope = getAxisScopeEls(n.getAxis(), e);
                        }
                        for (Element chi : filterScope) {
                            Element fchi = filter(chi, n);
                            if (fchi != null) {
                                contextTmp.add(fchi);
                            }
                        }
                    }
                    context = new Elements(contextTmp);
                    if (i == xpathNodes.size() - 1) {
                        for (Element e : contextTmp) {
                            res.add(JXNode.e(e));
                        }
                    }
                }
            }
        }
        return res;
    }
    
    private String substringAfter(String str,String label)
    {
    	int index = str.indexOf(label);
    	if(index==-1)
    	{
    		return "";
    	}
    	index+=label.length();
    	return str.substring(index);
    }

    /**
     * Ԫ�ع�����
     *
     * @param e
     * @param node
     * @return
     */
    public Element filter(Element e, Node node) throws NoSuchFunctionException, NoSuchAxisException {
        if (node.getTagName().equals("*") || node.getTagName().equals(e.nodeName())) {
            if (node.getPredicate() != null && !StringUtils.isBlank(node.getPredicate().getValue())) {
                Predicate p = node.getPredicate();
                if (p.getOpEm() == null) {
                    if (p.getValue().matches("\\d+") && getElIndex(e) == Integer.parseInt(p.getValue())) {
                        return e;
                    } else if (p.getValue().endsWith("()") && (Boolean) callFilterFunc(p.getValue().substring(0, p.getValue().length() - 2), e)) {
                        return e;
                    } else if (p.getValue().startsWith("@") && e.hasAttr(substringAfter(p.getValue(), "@"))) {
                        return e;
                    }
                    //todo p.value ~= contains(./@href,'renren.com')
                } else {
                    if (p.getLeft().matches("[^/]+\\(\\)")) {
                        Object filterRes = p.getOpEm().excute(callFilterFunc(p.getLeft().substring(0, p.getLeft().length() - 2), e).toString(), p.getRight());
                        if (filterRes instanceof Boolean && (Boolean) filterRes) {
                            return e;
                        } else if (filterRes instanceof Integer && e.siblingIndex() == Integer.parseInt(filterRes.toString())) {
                            return e;
                        }
                    } else if (p.getLeft().startsWith("@")) {
                        String lValue = e.attr(p.getLeft().substring(1));
                        Object filterRes = p.getOpEm().excute(lValue, p.getRight());
                        if ((Boolean) filterRes) {
                            return e;
                        }
                    } else {
                        // ��������߲��Ǻ�������Ĭ�Ͼ���xpath���ʽ��
                        List<Element> eltmp = new LinkedList<Element>();
                        eltmp.add(e);
                        List<JXNode> rstmp = evaluate(p.getLeft(), new Elements(eltmp));
                        if ((Boolean) p.getOpEm().excute(joinJXNOdeList(rstmp, ""), p.getRight())) {
                            return e;
                        }
                    }
                }
            } else {
                return e;
            }
        }
        return null;
    }
    
    private String joinJXNOdeList(List<JXNode> rstmp,String jionLabel)
    {
    	StringBuffer buf = new StringBuffer();
    	boolean first=true;
    	for(JXNode node:rstmp)
    	{
    		if(first)
    		{
    			first=false;
    		}
    		else
    		{
    			buf.append(jionLabel);
    		}
    		buf.append(node);
    	}
    	return buf.toString();
    	
    }

    /**
     * ������ѡ����
     *
     * @param axis
     * @param e
     * @return
     * @throws NoSuchAxisException
     */
    public Elements getAxisScopeEls(String axis, Element e) throws NoSuchAxisException {
        try {
            String functionName = CommonUtil.getJMethodNameFromStr(axis);
            Method axisSelector = axisFuncs.get(renderFuncKey(functionName, e.getClass()));
            return (Elements) axisSelector.invoke(SingletonProducer.getInstance().getAxisSelector(), e);
        } catch (Exception e1) {
            throw new NoSuchAxisException("this axis is not supported,plase use other instead of '" + axis + "'");
        }
    }

    /**
     * ����xpath�����ϵĺ���
     *
     * @param funcname
     * @param context
     * @return
     * @throws NoSuchFunctionException
     */
    public Object callFunc(String funcname, Elements context) throws NoSuchFunctionException {
        try {
            Method function = emFuncs.get(renderFuncKey(funcname, context.getClass()));
            return function.invoke(SingletonProducer.getInstance().getFunctions(), context);
        } catch (Exception e) {
            throw new NoSuchFunctionException("This function is not supported");
        }
    }

    /**
     * ����ν���к���
     *
     * @param funcname
     * @param el
     * @return
     * @throws NoSuchFunctionException
     */
    public Object callFilterFunc(String funcname, Element el) throws NoSuchFunctionException {
        try {
            Method function = emFuncs.get(renderFuncKey(funcname, el.getClass()));
            return function.invoke(SingletonProducer.getInstance().getFunctions(), el);
        } catch (Exception e) {
            throw new NoSuchFunctionException("This function is not supported");
        }
    }

    public int getElIndex(Element e) {
        if (e != null) {
            return CommonUtil.getElIndexInSameTags(e);
        }
        return 1;
    }

    private String renderFuncKey(String funcName, Class... params) {
        return funcName + "|" + StringUtils.join(params, ",");
    }

}
