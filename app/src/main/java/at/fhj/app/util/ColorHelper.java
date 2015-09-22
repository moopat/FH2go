package at.fhj.app.util;

import at.fhj.app.R;

public class ColorHelper {
	
	public static String generateColor(){
		/*
		int red = Integer.Math.random * 255;
		int green = ;
		int blue = ;
		*/
		
		return "#aabbcc";
	}
	
	public static int getRandomColorResource(){
		int[] colors = {
				R.color.blue,
				R.color.bluegreen,
				R.color.darkblue,
				R.color.darkgreen,
				R.color.darkpink,
				R.color.darkpurple,
				R.color.darkyellow,
				R.color.froggreen,
				R.color.girlypink,
				R.color.gray,
				R.color.green,
				R.color.greenyellow,
				R.color.iceblue,
				R.color.lightblue,
				R.color.lightgreen,
				R.color.lightorange,
				R.color.oceanblue,
				R.color.orange,
				R.color.pink,
				R.color.purple,
				R.color.red,
				R.color.redpink,
				R.color.turquoise,
				R.color.ultrapink,
				R.color.yellow
		};
				
		return colors[(int) (Math.random()*colors.length)];
	}
	
}
