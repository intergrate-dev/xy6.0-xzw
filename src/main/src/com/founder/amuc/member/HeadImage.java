package com.founder.amuc.member;

/**
 * @author Ren Yanfang
 * 2014-7-3
 */
public class HeadImage {
	//===源图片路径名称如:c:\1.jpg 
    private String srcPath ;
         
    //===剪切图片存放路径名称.如:c:\2.jpg
    private String subPath ;
    
    //===剪切点x坐标
    private int x ;
    
    private int y ;    
      
    //===剪切点宽度
    private int width ;
     
    private int height ;
    
    public HeadImage(){
            
    }  
    public HeadImage(int x,int y,int width,int height){
         this.x = x ;
         this.y = y ;
         this.width = width ;   
         this.height = height ;
    }
    
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSrcPath(){
        return srcPath;
    }

    public void setSrcPath(String srcPath){
        this.srcPath = srcPath;
    }

    public String getSubPath(){
        return subPath;
    }

    public void setSubPath(String subPath){
        this.subPath = subPath;
    }

    public int getWidth(){
        return width;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public int getX(){
        return x;
    }

    public void setX(int x){
        this.x = x;
    }

    public int getY(){
        return y;
    }

    public void setY(int y){
        this.y = y;
    } 
}
