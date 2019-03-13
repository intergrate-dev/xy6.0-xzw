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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * ͨ����ѡ����Ӧ�������ȫ���ڵ�
 * ȥ����ʵ�õ��ᣬ��֧��namespace��attribute������ /@*���棩��preceding(preceding-sibling֧��)��following(following-sibling֧��)
 * ��� preceding-sibling-one��following-sibling-one,��ֻѡǰһ�����һ���ֵܽڵ㣬��� sibling ѡȡȫ���ֵܽڵ�
 * @author: ����� [ et.tw@163.com ]
 * Date: 14-3-15
 */
public class AxisSelector {
    /**
     * ����
     * @param e
     * @return
     */
    public Elements self(Element e){
        return new Elements(e);
    }

    /**
     * ���ڵ�
     * @param e
     * @return
     */
    public Elements parent(Element e){
        return new Elements(e.parent());
    }

    /**
     * ֱ���ӽڵ�
     * @param e
     * @return
     */
    public Elements child(Element e){
        return e.children();
    }

    /**
     * ȫ�����Ƚڵ� ���ף�үү �� үү�ĸ���...
     * @param e
     * @return
     */
    public Elements ancestor(Element e){
        return e.parents();
    }

    /**
     * ȫ�����Ƚڵ������ڵ�
     * @param e
     * @return
     */
    public Elements ancestorOrSelf(Element e){
        Elements rs=e.parents();
        rs.add(e);
        return rs;
    }

    /**
     * ȫ���Ӵ�ڵ� ���ӣ����ӣ����ӵĶ���...
     * @param e
     * @return
     */
    public Elements descendant(Element e){
        return e.getAllElements();
    }

    /**
     * ȫ���Ӵ�ڵ������
     * @param e
     * @return
     */
    public Elements descendantOrSelf(Element e){
        Elements rs = e.getAllElements();
        rs.add(e);
        return rs;
    }

    /**
     * �ڵ�ǰ���ȫ��ͬ��ڵ㣬preceding-sibling
     * @param e
     * @return
     */
    public Elements precedingSibling(Element e){
        Elements rs = new Elements();
        Element tmp = e.previousElementSibling();
        while (tmp!=null){
            rs.add(tmp);
            tmp = tmp.previousElementSibling();
        }
        return rs;
    }

    /**
     * ����ǰһ��ͬ��ڵ㣨��չ�����﷨ preceding-sibling-one
     * @param e
     * @return
     */
    public Elements precedingSiblingOne(Element e){
        Elements rs = new Elements();
        if (e.previousElementSibling()!=null){
            rs.add(e.previousElementSibling());
        }
        return rs;
    }

    /**
     * �ڵ�����ȫ��ͬ��ڵ�following-sibling
     * @param e
     * @return
     */
    public Elements followingSibling(Element e){
        Elements rs = new Elements();
        Element tmp = e.nextElementSibling();
        while (tmp!=null){
            rs.add(tmp);
            tmp = tmp.nextElementSibling();
        }
        return rs;
    }

    /**
     * ������һ��ͬ��ڵ�(��չ) �﷨ following-sibling-one
     * @param e
     * @return
     */
    public Elements followingSiblingOne(Element e){
        Elements rs = new Elements();
        if (e.nextElementSibling()!=null){
            rs.add(e.nextElementSibling());
        }
        return rs;
    }

    /**
     * ȫ��ͬ����չ��
     * @param e
     * @return
     */
    public Elements sibling(Element e){
        return e.siblingElements();
    }

}
