package com.dfsx.lzcms.liveroom.model;

import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class FunItem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2471326544407668195L;
	private String text ;
	private  String name;
	private int icon ;
	private Intent intent ;
	private boolean enabled ;
	public ClickLisener listener ;
	private int newCount ;
	private int number ;
	private int rImage ; 
	private long Id;
	private  String logo;
	private int colorType;
	private String title;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	//新加的
	private boolean isEnableArrow;
	private String editText;
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	private boolean isDivide;
	private String flag ;

	public static final int TYPE_CUSTOM = -1;
	public static final int TYPE_NORMAL = 0; 
	public static final int TYPE_SHOWCHECK = 1;
	public static final int TYPE_SHOWTRAGLE = 2;
	public static final int TYPE_SHOWRIMAGE = 3;
	
	private int type;
	private Object object;
	private boolean checked; 
	private int whichToShow;
	private String[] selectArray;
	/*
	 * 底部显示文字
	 */
	private String bottomText;
	/**
	 * ID传
	 */
	private String ids;
	/**
	 * str数组
	 */
	private ArrayList<String> stringsList ;
	
	public String getBottomText() {
		return bottomText;
	}
	public void setBottomText(String bottomText) {
		this.bottomText = bottomText;
	}
	public String[] getSelectArray() {
		return selectArray;
	}
	public void setSelectArray(String[] selectArray) {
		this.selectArray = selectArray;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public boolean isDivide() {
		return isDivide;
	}
	public void setDivide(boolean isDivide) {
		this.isDivide = isDivide;
	}
	public int getNewCount() {
		return newCount;
	}
	public void setNewCount(int newCount) {
		this.newCount = newCount;
	}
	private FunItem fi ;
	public FunItem(String text ,int icon ,ClickLisener listener ,boolean enabled){
		this.text = text ;
		this.icon = icon ;
		this.listener = listener ;
		this.enabled = enabled ;
		fi = this ;
	}
	public FunItem(String text,int icon, ClickLisener listener ,int rImage){
		this.text = text ;
		this.icon = icon ;
		this.listener = listener ;
		this.rImage = rImage ;
		fi = this ;
	}
	public FunItem(){
	}
	public FunItem(String name,String text){
		this.text = text ;
		this.name=name;
	}
	public FunItem(String name,String text,int icon){
		this.icon = icon ;
		this.text = text ;
		this.name=name;
	}
	public FunItem(String name,String text,ClickLisener listener,boolean enabled){
		this.name=name;
		this.text=text;
		this.listener=listener;
		this.enabled=enabled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FunItem(String text,ClickLisener clickLisener){
		this.text = text ;
		this.listener = clickLisener ;
	}
	public FunItem(String text ,ClickLisener listener,String flag){
		this.text = text ;
		this.listener = listener ;
		this.flag = flag;
	}
	public FunItem(boolean isDivide){
		this.isDivide = isDivide;
	};
	public ClickLisener getListener() {
		return listener;
	}
	public void setListener(ClickLisener listener) {
		this.listener = listener;
	}
	public interface ClickLisener{
		public abstract void onclick();
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
	public Intent getIntent() {
		return intent;
	}
	public void setIntent(Intent intent) {
		this.intent = intent;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public void clear(){
		fi = null;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public int getWhichToShow() {
		return whichToShow;
	}
	public void setWhichToShow(int whichToShow) {
		this.whichToShow = whichToShow;
	}
	public int getrImage() {
		return rImage;
	}
	public void setrImage(int rImage) {
		this.rImage = rImage;
	}
	public boolean isEnableArrow() {
		return isEnableArrow;
	}
	public void setEnableArrow(boolean isEnableArrow) {
		this.isEnableArrow = isEnableArrow;
	}
	public String getEditText() {
		return editText;
	}
	public void setEditText(String editText) {
		this.editText = editText;
	}

	public int getColorType() {
		return colorType;
	}

	public void setColorType(int colorType) {
		this.colorType = colorType;
	}

	public ArrayList<String> getStringsList() {
		return stringsList;
	}

	public void setStringsList(ArrayList<String> stringsList) {
		this.stringsList = stringsList;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
}
