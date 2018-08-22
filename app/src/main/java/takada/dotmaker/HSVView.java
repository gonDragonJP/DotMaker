package takada.dotmaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class HSVView extends View {

	final double radian=3.14159/180;
	
	int screenX,screenY;
	int cx,cy;
	final int circleRad=120;
	final int innerCircleRad=90;
	final int SVRectSize=100;
	final int SVRectAngle=225;
	final int intCos=(int)(Math.cos(-SVRectAngle*radian) * 1024);
	final int intSin=(int)(Math.sin(-SVRectAngle*radian) * 1024);
	
	double hue=0,saturation=1,value=1;
	
	ColorSlidebar[] slidebar=new ColorSlidebar[3];
	Bitmap bitmapHRing;
	
	Paint paint = new Paint();
	
	public HSVView(Context context) {
		super(context);
	
		setColorSlideBar();
		setHRingBitmap(context);
	}
	
	
	static public int getColorFromHSV(double H,double S,double V){
		
		int[] rgb=getRGBFromHSV(H,S,V);
		return Color.argb(255,rgb[0],rgb[1],rgb[2]);
	}
	
	static public int[] getRGBFromHSV(double H,double S,double V){
		
		double red=0,green=0,blue=0;
		
		int Hi=(int)(H/60) % 6;
		double f=H/60 -Hi;
		double p=V*(1-S);
		double q=V*(1-f*S);;
		double t=V*(1-(1-f)*S);
		
		switch(Hi){
			
		case 0:
			red=V;	green=t;	blue=p;	break;
		case 1:
			red=q;	green=V;	blue=p;	break;
		case 2:
			red=p;	green=V;	blue=t;	break;
		case 3:
			red=p;	green=q;	blue=V;	break;
		case 4:
			red=t;	green=p;	blue=V;	break;
		case 5:
			red=V;	green=p;	blue=q;	break;
			
		}
		int[] rgb=new int[3];
		rgb[0]=(int)(red*255); 
		rgb[1]=(int)(green*255); 
		rgb[2]=(int)(blue*255);
		return rgb;
	}
	
	public void setRGBToPallete(){
		
		int[] rgb=getRGBFromHSV(hue,saturation,value);
		
		Palette.setCurrentRGB(rgb);
	}
	
	public void setClipRGBToPalette(){
		
		int[] rgb=getRGBFromHSV(hue,saturation,value);
		
		Palette.setClipRGB(rgb);
	}
	
	static final int
	 Red=0,
	 Green=1,
	 Blue=2;
	
	public void setHSVFromRGB(int r,int g,int b){
		
		double max=0,min=0;
		int maxElement;
		
		if(r>g){
			if(r>b){
				maxElement=Red;
				max=r;
				if(g>b) min=b; else min=g;
			}
			else{
				maxElement=Blue;
				max=b; min=g;
			}
		}
		else if(g>b){
			maxElement=Green;
			max=g;
			if(r>b) min=b; else min=r;
		}
		else{
			maxElement=Blue;
			max=b; min=r;
		}
		
		if(max==min) hue=0;
		else{
			switch(maxElement){
			
			case Red:
				hue=60*(g-b)/(max-min);
				break;
			case Green:
				hue=60*(b-r)/(max-min)+120;
				break;
			case Blue:
				hue=60*(r-g)/(max-min)+240;
				break;
			}
			hue=(hue+360)%360;
		}
		
		if(max!=0) saturation=(max-min)/max; else saturation=0;
		value=max/255;
	}
	
	private void setRGBToSlidebars(){
		int[] rgb=getRGBFromHSV(hue,saturation,value);
		for(int i=0; i<3; i++)
			slidebar[i].setRGB(rgb[0], rgb[1], rgb[2]);
		slidebar[0].setCurrentValue(rgb[0]);
		slidebar[1].setCurrentValue(rgb[1]);
		slidebar[2].setCurrentValue(rgb[2]);
			
	}
	
	public void onDraw(Canvas canvas){
		
		screenX=getWidth();	screenY=getHeight();
		cx=screenX/2;	cy=(int)(screenY/3);

		drawHRing(canvas);
		drawSVRect(canvas);
		drawPointer(canvas);
		
		int slidebarTop=cy+circleRad+40;
		int red=slidebar[0].red;
		int green=slidebar[1].green;
		int blue=slidebar[2].blue;
		
		for(int i=0; i<3; i++){
		slidebar[i].setRGB(red, green, blue);
		slidebar[i].setPosition(cx,slidebarTop+i*30, 200, 20);
		slidebar[i].onDraw(canvas);
		}
	}
	
	private void drawHRing(Canvas canvas){
	
		paint.setStyle(Style.FILL);
		
		canvas.drawBitmap(bitmapHRing, cx-circleRad, cy-circleRad, paint);
		/*
		paint.setStyle(Style.FILL);
		RectF oval = new RectF(cx-circleRad,cy-circleRad
								,cx+circleRad,cy+circleRad);
		
		for(int angle=0; angle<360; angle++){
			
			paint.setColor(getColorFromHSV(angle,1,1));
			canvas.drawArc(oval, angle, 1, true, paint);
		}@*/// Bitmap“\‚è•t‚¯‚É‚Ä‘ã—p
	
		paint.setColor(getColorFromHSV(hue,saturation,value));
		canvas.drawCircle(cx, cy, innerCircleRad, paint);
	}
	
	private void drawSVRect(Canvas canvas){
		
		paint.setStyle(Style.FILL);
		
		for(int x=0; x<=SVRectSize; x++){
			for(int y=0; y<=SVRectSize; y++){
				
				if((y<3 | y>SVRectSize-3 | x<3 | x>SVRectSize-3)
					|((x % 2)==0 && (y % 2)==0))
				{
				
					Point p=transPositionFromSVRect(x,y);
					double S = (double)x / SVRectSize;
					double V = (double)y / SVRectSize;
				
					paint.setColor(getColorFromHSV(hue, S ,V));
					canvas.drawRect(new Rect(p.x, p.y, p.x+3, p.y+3),paint);
				}
				
			}
		}
	}
	
	private void drawPointer(Canvas canvas){
		
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.WHITE);
		int radius=10;
		int px=cx+(int)(Math.cos(hue*radian)*circleRad);
		int py=cy+(int)(Math.sin(hue*radian)*circleRad);
		
		canvas.drawCircle(px, py, radius, paint);
		
		Point p=transPositionFromSVRect
					((int)(saturation * SVRectSize),(int)(value * SVRectSize));
		
		canvas.drawCircle(p.x, p.y, radius, paint);
	}

	private Point transPositionFromSVRect(int x, int y){
		
		int x1= x - SVRectSize / 2;
		int y1= y - SVRectSize / 2;
		int x2=(intCos * x1 - intSin * y1) >> 10;
		int y2=(intSin * x1 + intCos * y1) >> 10;
		
		int px = cx + x2;
		int py = cy + y2;
		
		return new Point(px,py);
	}
	
	private Point getInnerCirclePosition(double angle){
	
		int x=(int)(cx+Math.cos(angle*radian)*innerCircleRad);
		int y=(int)(cy+Math.sin(angle*radian)*innerCircleRad);
		
		return new Point(x,y);
	}
	
	public boolean onTouchEvent(MotionEvent event){
		
		int touchX=(int)event.getX();
		int touchY=(int)event.getY();
		int touchXa=touchX-cx;
		int touchYa=touchY-cy;
		int d=touchXa*touchXa+touchYa*touchYa;
		int squareR1=circleRad*circleRad;
		int squareR2=innerCircleRad*innerCircleRad;
		
		if((d>squareR2)&&(d<squareR1)){
			
			double angle=Math.atan2((double)touchYa,(double)touchXa)/radian;
			
			hue = (angle+360)%360;
			setRGBToSlidebars();
			invalidate();
		}
		
		if(d<squareR2){
			
			double x= (intCos * touchXa + intSin * touchYa) >> 10;
			double y= (-intSin * touchXa + intCos * touchYa) >> 10;
			
			double S=(x+SVRectSize/2)/SVRectSize;
			double V=(y+SVRectSize/2)/SVRectSize;
			
			if(S>=0 && S<=1){
				if(V>=0 && V<=1){
					saturation=S;
					value=V;
					setRGBToSlidebars();
					invalidate();
				}
			}
		}
		for(int i=0; i<3; i++)
			if(slidebar[i].checkTouch(touchX, touchY)){
				int red=slidebar[0].red;
				int green=slidebar[1].green;
				int blue=slidebar[2].blue;
				setHSVFromRGB(red,green,blue);
				invalidate();
			}

		return true;
	}
	
	private void setColorSlideBar(){
	
		for(int i=0; i<3; i++){
			
			slidebar[i]=new ColorSlidebar();
			slidebar[i].setLimitValue(0,255);
			slidebar[i].setCurrentValue(0);
		}
		slidebar[0].accessElement=ColorSlidebar.Red;
		slidebar[1].accessElement=ColorSlidebar.Green;
		slidebar[2].accessElement=ColorSlidebar.Blue;	
	}

	private void setHRingBitmap(Context context){
	
		Resources res = context.getResources();
		bitmapHRing = BitmapFactory.decodeResource(res, R.drawable.huecircle);
	}
}
