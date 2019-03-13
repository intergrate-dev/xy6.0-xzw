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
import com.founder.xy.weibo.html.xpath.model.JXNode;
import com.founder.xy.weibo.html.xpath.util.CommonUtil;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xpath��������֧�ֵ�ȫ������ϣ�������չ����ʽ��Ӽ���
 * @author: ����� [ et.tw@163.com ]
 * Date: 14-3-15
 */
public class Functions {
    /**
     * ֻ��ȡ�ڵ���������ı�
     * @param context
     * @return
     */
    public List<JXNode> text(Elements context){
        List<JXNode> res = new LinkedList<JXNode>();
        if (context!=null&&context.size()>0){
            for (Element e:context){
                if (e.nodeName().equals("script")){
                    res.add(JXNode.t(e.data()));
                }else {
                    res.add(JXNode.t(e.ownText()));
                }
            }
        }
        return res;
    }

    /**
     * �ݹ��ȡ�ڵ���ȫ���Ĵ��ı�
     * @param context
     * @return
     */
    public List<JXNode> allText(Elements context){
        List<JXNode> res = new LinkedList<JXNode>();
        if (context!=null&&context.size()>0){
            for (Element e:context){
                res.add(JXNode.t(e.text()));
            }
        }
        return res;
    }

    /**
     * ��ȡȫ���ڵ���ڲ���html
     * @param context
     * @return
     */
    public List<JXNode> html(Elements context){
        List<JXNode> res = new LinkedList<JXNode>();
        if (context!=null&&context.size()>0){
            for (Element e:context){
                res.add(JXNode.t(e.html()));
            }
        }
        return res;
    }

    /**
     * ��ȡȫ���ڵ�� ��ڵ㱾�����ڵ�ȫ��html
     * @param context
     * @return
     */
    public List<JXNode> outerHtml(Elements context){
        List<JXNode> res = new LinkedList<JXNode>();
        if (context!=null&&context.size()>0){
            for (Element e:context){
                res.add(JXNode.t(e.outerHtml()));
            }
        }
        return res;
    }

    /**
     * ��ȡȫ���ڵ�
     * @param context
     * @return
     */
    public List<JXNode> node(Elements context){
        return html(context);
    }

    /**
     * ��ȡ�ڵ������ı���ȫ������
     * @param context
     * @return
     */
    public List<JXNode> num(Elements context){
        List<JXNode> res = new LinkedList<JXNode>();
        if (context!=null){
            Pattern pattern = Pattern.compile("\\d+");
            for (Element e:context){
                Matcher matcher = pattern.matcher(e.ownText());
                if (matcher.find()){
                    res.add(JXNode.t(matcher.group()));
                }
            }
        }
        return res;
    }

    /**
     * =====================
     * ���������ڹ������ĺ���
     */

    /**
     * ��ȡԪ���Լ������ı�
     * @param e
     * @return
     */
    public String text(Element e){
        return e.ownText();
    }

    /**
     * ��ȡԪ�������ȫ���ı�
     * @param e
     * @return
     */
    public String allText(Element e){
        return e.text();
    }

    /**
     * �ж�һ��Ԫ���ǲ������һ��ͬ��ͬ���е�
     * @param e
     * @return
     */
    public boolean last(Element e){
        return CommonUtil.getElIndexInSameTags(e)==CommonUtil.sameTagElNums(e);
    }
    /**
     * �ж�һ��Ԫ���ǲ���ͬ��ͬ���еĵ�һ��
     * @param e
     * @return
     */
    public boolean first(Element e){
        return CommonUtil.getElIndexInSameTags(e)==1;
    }

    /**
     * ����һ��Ԫ����ͬ���ֵܽڵ��е�λ��
     * @param e
     * @return
     */
    public int position(Element e){
        return CommonUtil.getElIndexInSameTags(e);
    }

    /**
     * �ж��Ƿ��
     * @param left
     * @param right
     * @return
     */
    public boolean contains(String left,String right){
       return left.contains(right);
    }

}
