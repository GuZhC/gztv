package com.dfsx.openimage;

/**
 * @author  heyang  2015-7-16
 *
 */
public interface GestureImageViewListener {

	public void onTouch(float x, float y);
	
	public void onScale(float scale);
	
	public void onPosition(float x, float y);
	
}
