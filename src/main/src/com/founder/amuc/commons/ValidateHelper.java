package com.founder.amuc.commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.founder.e5.cat.CatReader;
import com.founder.e5.cat.Category;
import com.founder.e5.context.Context;
import com.founder.e5.context.E5Exception;

/** 
 * @created 2014年10月22日 下午5:37:14 
 * @author  leijj
 * 类说明 ： 
 */
public class ValidateHelper {
	/** 
	* @author  leijj 
	* 功能： 邮件格式验证
	* @param email
	* @return 
	*/ 
	public static boolean email(String email){
		String regular = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		return isValidate(regular, email);
	}
	public static boolean phone(String phone){
		String regular = "^((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
		return isValidate(regular, phone);
	}
	public static boolean mobilephone(String mobilephone){
		String regular = "^0?(13[0-9]|15[012356789]|17[0-9]|18[0-9]|14[0-9])[\\d]{8}$";
		return isValidate(regular, mobilephone);
	}
	private static boolean isValidate(String regular, String exp){
		if(exp == null || exp == "") return false;
		Pattern pattern = Pattern.compile(regular);
		Matcher matcher = pattern.matcher(exp);
		return matcher.matches();
	}
	public static boolean isExist(String value, String catName) throws E5Exception{
		CatReader catReader = (CatReader)Context.getBean(CatReader.class);
		boolean isValidate = false;
		if(value != null && value != ""){
			Category[] cats = catReader.getCats(catName);
			for(Category cat : cats){
				if(value.equals(cat.getCatName())){
					isValidate = true;
					break;
				}
			}
		} else {
			isValidate = false;
		}
		return isValidate;
	}
	public static boolean checkNumber(String num,String type){
		
		 String eL = "";   
	     if(type.equals("0+"))eL = "^\\d+$";//非负整数   
	     else if(type.equals("+"))eL = "^\\d*[1-9]\\d*$";//正整数   
	     else if(type.equals("-0"))eL = "^((-\\d+)|(0+))$";//非正整数   
	     else if(type.equals("-"))eL = "^-\\d*[1-9]\\d*$";//负整数   
	     else eL = "^-?\\d+$";//整数   
	     Pattern p = Pattern.compile(eL);   
	     Matcher m = p.matcher(num);   
	     boolean b = m.matches();   
	     return b;   
	}
	public static void main(String[] args){
		
		System.out.println();
	}
}