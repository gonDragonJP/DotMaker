package takada.dotmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

public class GradationView extends View{
	
	class GradiationDots extends Dots{

		double[][] distanceMap;
		Point[][] samplingPoints;
		Rect[][] drawingRects;
		
		double minDistance,maxDistance;
		
		int startColor,endColor;
		
		public GradiationDots(int gridX, int gridY) {
			super(gridX, gridY);
			
			distanceMap= new double[gridX][gridY];
			samplingPoints = new Point[gridX][gridY];
			drawingRects = new Rect[gridX][gridY];
		}
		
		public void setupFromSampleRect(Rect sampleRect){
			
			double gridSize=sampleRect.width()/(double)gridX;
			
			for(int x=0; x<gridX; x++)
				for(int y=0; y<gridY; y++){
					
					samplingPoints[x][y] = new Point();
					drawingRects[x][y] = new Rect();
					
					drawingRects[x][y].left=(int)(sampleRect.left+gridSize*x);
					drawingRects[x][y].top=(int)(sampleRect.top+gridSize*y);
					drawingRects[x][y].right=(int)(drawingRects[x][y].left+gridSize);
					drawingRects[x][y].bottom=(int)(drawingRects[x][y].top+gridSize);
					
					samplingPoints[x][y].x=drawingRects[x][y].centerX();
					samplingPoints[x][y].y=drawingRects[x][y].centerY();
				}
		}
		
		public void renewalDistanceMap(Point origin){
			
			minDistance=Integer.MAX_VALUE; 
			maxDistance=0;
			
			for(int x=0; x<gridX; x++)
				for(int y=0; y<gridY; y++){
				
					double dx = samplingPoints[x][y].x-origin.x;
					double dy = samplingPoints[x][y].y-origin.y;
					double distance = distanceMap[x][y] = Math.sqrt(dx*dx + dy*dy);
					
					if (distance<minDistance) minDistance = distance;
					if (distance>maxDistance) maxDistance = distance;
				}
		}
		
		public void renewalDotsByGradationColor(){
			
			for(int x=0; x<gridX; x++)
				for(int y=0; y<gridY; y++){
					dot[x][y].setColor(getGradationColor(x, y));
				}
		}
		
		public void setColor(int startColor,int endColor){
			
			this.startColor = startColor;	this.endColor = endColor;
		}
		
		public int getGradationColor(int x, int y){
			
			double rate = (distanceMap[x][y]-minDistance)/(maxDistance-minDistance);
			
			int dRed = (int)((Color.red(endColor)-Color.red(startColor))*rate);
			int dGreen = (int)((Color.green(endColor)-Color.green(startColor))*rate);
			int dBlue = (int)((Color.blue(endColor)-Color.blue(startColor))*rate);
			
			int r = Color.red(startColor) + dRed;
			int g = Color.green(startColor) + dGreen;
			int b = Color.blue(startColor) + dBlue;
			
			return Color.rgb(r,g,b);
		}

	};
	
	static final double
		radian = 3.14159265/180;
	
	Paint paint = new Paint();
	
	int screenX,screenY;
	Point origin = new Point();
	Rect sampleRect = new Rect();
	int distance,direction;
	
	GradiationDots sampleDots;
	int startColor = Color.BLUE, endColor = Color.RED;
	
	public GradationView(Context context) {
		super(context);
		
		setupSampleDots(8,16);
		sampleDots.setColor(startColor, endColor);
	}
	
	private void setupSampleDots(int gridX,int gridY){
		
		sampleDots = new GradiationDots(gridX,gridY);
	}
	
	public void renewalSampleDots(int gridX,int gridY){
		
		sampleDots = null;
		sampleDots = new GradiationDots(gridX,gridY);
		sampleDots.setColor(startColor, endColor);
		
		renewalSampleRect();
		renewalOrigin();
		sampleDots.setupFromSampleRect(sampleRect);
		sampleDots.renewalDistanceMap(origin);
		invalidate();
	}
	
	public void renewalStartColor(int color){
		
		startColor = color;
		sampleDots.setColor(startColor, endColor);
		invalidate();
	}
	
	public void renewalEndColor(int color){
		
		endColor = color;
		sampleDots.setColor(startColor, endColor);
		invalidate();
	}
	
	public void setDistance(int distance){
		
		this.distance = distance;
		renewalOrigin();
		sampleDots.renewalDistanceMap(origin);
		invalidate();
	}
	
	public void setDirection(int direction){
		
		this.direction = direction;
		renewalOrigin();
		sampleDots.renewalDistanceMap(origin);
		invalidate();
	}
	
	private void renewalOrigin(){
		
		origin.x = sampleRect.centerX()+(int)(distance * Math.cos(radian * direction));
		origin.y = sampleRect.centerY()+(int)(distance * Math.sin(radian * direction));
	}
	
	public Dots getDotsOfGradation(){
		
		sampleDots.renewalDotsByGradationColor();
		Dots dots = (Dots)sampleDots;
		
		return dots;
	}
	
	public void onSizeChanged(int width,int height,int oldWidth,int oldHeight){
		
		screenX=width;
		screenY=height;

		renewalSampleRect();
		renewalOrigin();
		sampleDots.setupFromSampleRect(sampleRect);
		sampleDots.renewalDistanceMap(origin);
	}
	
	private void renewalSampleRect(){
		
		Point sampleCenter = new Point (screenX/2, (int)(screenY/2.2));
		
		int a=sampleCenter.x;
		int b=sampleCenter.y;
		int sampleLongEdge=((a>b)? b : a)/5*4*2;
		
		int gridX=sampleDots.gridX;
		int gridY=sampleDots.gridY;
		int sampleVerticalEdge,sampleHorizontalEdge;
		if(gridX>gridY){
			sampleVerticalEdge =(int)(sampleLongEdge * ((double)gridY/(double)gridX));
			sampleHorizontalEdge = sampleLongEdge;
		}
		else{
			sampleVerticalEdge = sampleLongEdge;
			sampleHorizontalEdge = (int)(sampleLongEdge * ((double)gridX/(double)gridY));
		}
		
		sampleRect.left = sampleCenter.x-sampleHorizontalEdge/2;
		sampleRect.top = sampleCenter.y-sampleVerticalEdge/2;
		sampleRect.right = sampleRect.left + sampleHorizontalEdge;
		sampleRect.bottom = sampleRect.top + sampleVerticalEdge;
	}
	
	public void onDraw(Canvas canvas){
		
		//paint.setColor(Color.BLACK);
		//canvas.drawRect(sampleRect, paint);
		
		drawDotsRect(canvas);
		
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(origin.x, origin.y, distance, paint);
	}
	
	private void drawDotsRect(Canvas canvas){
		
		int gridX=sampleDots.gridX;
		int gridY=sampleDots.gridY;
		
		paint.setStyle(Style.FILL);
		
		for(int x=0; x<gridX; x++)
			for(int y=0; y<gridY; y++){
				
				paint.setColor(sampleDots.getGradationColor(x, y));
				canvas.drawRect(sampleDots.drawingRects[x][y],paint);
		}
	}
}
