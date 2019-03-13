package com.founder.xy.ueditor;

public class Params {
    String imagePath;
    String command;
    String destName;
    Integer imagePumpW;
    Integer imagePumpH;
    Integer end_x;
    Integer end_y;
    Integer start_x;
    Integer start_y;

    Integer selectorW;
    Integer selectorH;
    Integer selectorX;
    Integer selectorY;

    Integer imageW;
    Integer imageH;
    String watermark;
    Integer position;
    Float transparency;
    Boolean isSameSetting;
    String imgList;

    String name;
    String path;
    String wholePath;

    Boolean keepRadio;

    private Integer imgWidth;
    private Integer imgHeight;

    String imgType;

    private Integer rotate;

    //drawText
    private String fontName;
    private Integer fontStyle;
    private Integer fontSize;
    private String color;
    private String content;

    private Integer siteID;
    private long docID;
    private Integer docLibID;

    public Boolean getSameSetting() {
        return isSameSetting;
    }

    public void setSameSetting(Boolean sameSetting) {
        isSameSetting = sameSetting;
    }

    public Integer getSiteID() {
        return siteID;
    }

    public void setSiteID(Integer siteID) {
        this.siteID = siteID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    public Integer getImagePumpW() {
        return imagePumpW;
    }

    public void setImagePumpW(Integer imagePumpW) {
        this.imgWidth = imagePumpW;
        this.imagePumpW = imagePumpW;
    }

    public Integer getImagePumpH() {
        return imagePumpH;
    }

    public void setImagePumpH(Integer imagePumpH) {
        this.imgHeight = imagePumpH;
        this.imagePumpH = imagePumpH;
    }

    public Integer getSelectorW() {
        return selectorW;
    }

    public void setSelectorW(Integer selectorW) {
        this.end_x = selectorW;
        this.selectorW = selectorW;
    }

    public Integer getSelectorH() {
        return selectorH;
    }

    public void setSelectorH(Integer selectorH) {
        this.end_y = selectorH;
        this.selectorH = selectorH;
    }

    public Integer getSelectorX() {
        return selectorX;
    }

    public void setSelectorX(Integer selectorX) {
        this.start_x = selectorX;
        this.selectorX = selectorX;
    }

    public Integer getSelectorY() {
        return selectorY;
    }

    public void setSelectorY(Integer selectorY) {
        this.start_y = selectorY;
        this.selectorY = selectorY;
    }

    public Integer getImageW() {
        return imageW;
    }

    public void setImageW(Integer imageW) {
        this.imageW = imageW;
    }

    public Integer getImageH() {
        return imageH;
    }

    public void setImageH(Integer imageH) {
        this.imageH = imageH;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Float getTransparency() {
        return transparency;
    }

    public void setTransparency(Float transparency) {
        this.transparency = transparency;
    }

    public Boolean getIsSameSetting() {
        return isSameSetting;
    }

    public void setIsSameSetting(Boolean isSameSetting) {
        this.isSameSetting = isSameSetting;
    }

    public String getImgList() {
        return imgList;
    }

    public void setImgList(String imgList) {
        this.imgList = imgList;
    }

    public String getWatermark() {
        return watermark;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public Integer getEnd_x() {
        return end_x;
    }

    public void setEnd_x(Integer end_x) {
        this.end_x = end_x;
    }

    public Integer getEnd_y() {
        return end_y;
    }

    public void setEnd_y(Integer end_y) {
        this.end_y = end_y;
    }

    public Integer getStart_x() {
        return start_x;
    }

    public void setStart_x(Integer start_x) {
        this.start_x = start_x;
    }

    public Integer getStart_y() {
        return start_y;
    }

    public void setStart_y(Integer start_y) {
        this.start_y = start_y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getWholePath() {
        return wholePath;
    }

    public void setWholePath(String wholePath) {
        this.wholePath = wholePath;
    }

    public Boolean isKeepRadio() {
        return keepRadio;
    }

    public void setKeepRadio(Boolean keepRadio) {
        this.keepRadio = keepRadio;
    }

    public Boolean getKeepRadio() {
        return keepRadio;
    }

    public Integer getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(Integer imgWidth) {
        this.imgWidth = imgWidth;
    }

    public Integer getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(Integer imgHeight) {
        this.imgHeight = imgHeight;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Integer getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(Integer fontStyle) {
        this.fontStyle = fontStyle;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRotate() {
        return rotate;
    }

    public void setRotate(Integer rotate) {
        this.rotate = rotate;
    }

	public long getDocID() {
		return docID;
	}

	public void setDocID(long docID) {
		this.docID = docID;
	}

	public Integer getDocLibID() {
		return docLibID;
	}

	public void setDocLibID(Integer docLibID) {
		this.docLibID = docLibID;
	}

	
}