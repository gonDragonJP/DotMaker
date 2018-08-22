package takada.dotmaker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;

public class ColorSlidebar {
	
	int cx,cy,Width,Height;
	int Left,Top,Right,Bottom;
	double cursorRate;
	int maxValue,minValue,rangeValue;
	int currentValue;
	
	static final int
		Red=0,
		Green=1,
		Blue=2;
	
	int accessElement=Red;
	int red,green,blue;

	public ColorSlidebar(){
		

	}
	
	public void setRGB(int r,int g,int b){
		
		red=r; green=g; blue=b;
	}
	
	public void setPosition(int cx,int cy,int Width,int Height){
		
		this.cx=cx;	this.cy=cy;	
		this.Width=Width;	this.Height=Height;
		Left=cx-Width/2;	Right=Left+Width;
		Top=cy-Height/2;	Bottom=Top+Height;
	}
	
	public void setLimitValue(int min,int max){
		
		maxValue=max;	minValue=min;
		rangeValue=max-min;
	}
	
	public void setCurrentValue(int value){
		
		currentValue=value;	
		setCursorRateFromValue(value);
		switch(accessElement){
		
		case Red:
			red=value; break;
		case Green:
			green=value; break;
		case Blue:
			blue=value; break;
		}
	}
	
	public boolean checkTouch(int touchX,int touchY){
		
		if(touchX>Left-10 && touchX<Right+10)
			if(touchY>Top && touchY<Bottom){
				
				if(touchX<Left) touchX=Left;
				if(touchX>Right) touchX=Right;
				cursorRate=(double)(touchX-Left)/(double)Width;
				setCurrentValue(minValue+(int)(rangeValue*cursorRate));
				return true;
			}
				
		return false;
	}
	
	public void setCursorRateFromValue(int value){
		
		cursorRate=(double)(value-minValue)/(double)(maxValue-minValue);
	}
	
	public void onDraw(Canvas canvas){
		
		Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		
		canvas.drawRect(new Rect(Left,Top,Right,Bottom), paint);
		
		
		paint.setStyle(Style.FILL);
		double a = 255/Width;
		
		for(int i=0; i<Width; i+=2){
			
			switch(accessElement){
			
			case Red:
				paint.setColor(Color.argb(255,(int)(a*i),green,blue));
				break;
			
			case Green:
				paint.setColor(Color.argb(255,red,(int)(a*i),blue));
				break;
			
			case Blue:
				paint.setColor(Color.argb(255,red,green,(int)(a*i)));
				break;
			}
			canvas.drawRect(new Rect(Left+i, Top, Left+i+2, Bottom), paint);
		}
		
		paint.setColor(Color.argb(150,150,150,150));
		int cursorX=Left+(int)(Width*cursorRate);
		canvas.drawCircle(cursorX, Top+Height/2, 10, paint);
	}
}
