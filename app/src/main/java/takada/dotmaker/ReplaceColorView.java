package takada.dotmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class ReplaceColorView extends View{
	
	Paint paint = new Paint();
	
	int screenX,screenY;
	Point origin = new Point();
	Rect sampleRect = new Rect();
	
	ReplaceColorDots sampleDots;
	int srcColor,dstColor;
	
	int gridX,gridY,Xpos,Ypos;
	float gridSize;
	
	public ReplaceColorView(Context context) {
		super(context);
		
	}
	
	public void renewalSampleDots(Dots newDots){
		
		this.gridX = newDots.gridX;	this.gridY = newDots.gridY;
		
		sampleDots = null;
		sampleDots = new ReplaceColorDots(gridX, gridY);
		sampleDots.copy(newDots);
		initialSrcDstColors();
		
		renewalSampleRect();
		sampleDots.setupFromSampleRect(sampleRect);
		
		invalidate();
	}
	
	public void initialSrcDstColors(){
		
		srcColor = dstColor = Color.BLACK;
		sampleDots.setColor(srcColor, dstColor);
		invalidate();
	}
	
	public void renewalSrcColor(int color){
		
		srcColor = color;
		sampleDots.setColor(srcColor, dstColor);
		invalidate();
	}
	
	public void renewalDstColor(int color){
		
		dstColor = color;
		sampleDots.setColor(srcColor, dstColor);
		invalidate();
	}
	
	public Dots getDotsOfReplaceColor(){
		
		sampleDots.renewalDotsByReplaceColor();
		Dots dots = (Dots)sampleDots;
		
		return dots;
	}
	
	public void onSizeChanged(int width,int height,int oldWidth,int oldHeight){
		
		screenX=width;
		screenY=height;

		renewalSampleRect();
		sampleDots.setupFromSampleRect(sampleRect);
	}
	
	private void renewalSampleRect(){
		
		Point sampleCenter = new Point (screenX/2, (int)(screenY/2.2));
		
		int a=sampleCenter.x;
		int b=sampleCenter.y;
		int sampleLongEdge=((a>b)? b : a)/5*4*2;
	
		int sampleVerticalEdge,sampleHorizontalEdge;
		if(gridX>gridY){
			sampleVerticalEdge =(int)(sampleLongEdge * ((double)gridY/(double)gridX));
			sampleHorizontalEdge = sampleLongEdge;
			
			gridSize = (float)sampleHorizontalEdge / gridX;
		}
		else{
			sampleVerticalEdge = sampleLongEdge;
			sampleHorizontalEdge = (int)(sampleLongEdge * ((double)gridX/(double)gridY));
		
			gridSize = (float)sampleVerticalEdge / gridY;
		}
		
		sampleRect.left = sampleCenter.x-sampleHorizontalEdge/2;
		sampleRect.top = sampleCenter.y-sampleVerticalEdge/2;
		sampleRect.right = sampleRect.left + sampleHorizontalEdge;
		sampleRect.bottom = sampleRect.top + sampleVerticalEdge;
		
	}
	
	public void onDraw(Canvas canvas){
		
		drawDotsRect(canvas);
		drawPointerRect(canvas);
		drawSourceColorIndicator(canvas);
	}
	
	private void drawDotsRect(Canvas canvas){
		
		paint.setStyle(Style.FILL);
		
		for(int x=0; x<gridX; x++)
			for(int y=0; y<gridY; y++){
				
				int drawColor = sampleDots.getReplaceColor(x, y);
				
				if(Color.alpha(drawColor)==0)
					drawTransparentRect(x, y, canvas);
				else{
					paint.setColor(drawColor);
					canvas.drawRect(sampleDots.drawingRects[x][y],paint);
				}
		}
	}
	
	private void drawPointerRect(Canvas canvas){
		
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.RED);
		
		int x1 = (int)(sampleRect.left + Xpos * gridSize);
		int y1 = (int)(sampleRect.top + Ypos *gridSize);
		int x2 = (int)(sampleRect.left + (Xpos+1) * gridSize);
		int y2 = (int)(sampleRect.top + (Ypos+1) *gridSize);
		
		canvas.drawRect(new Rect(x1, y1, x2, y2), paint);
	}
	
	private void drawSourceColorIndicator(Canvas canvas){
		
		int top = sampleRect.bottom + 10;
		int left = screenX / 2 - 100;
		int feed = 30;
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(20);
		canvas.drawText("Soucre Color", left, top-(int)paint.ascent(), paint);
		canvas.drawText("Destination Color", left, top-(int)paint.ascent()+feed, paint);
		
		int radius = 10;
		int cx = left + 190;
		int cy = top + radius;
		
		canvas.drawCircle(cx, cy, radius+1, paint);
		canvas.drawCircle(cx, cy+feed, radius+1, paint);
		
		paint.setStyle(Style.FILL);
		
		if(Color.alpha(srcColor)==0){
			
			drawTransparentCircle(cx, cy, radius, canvas);
		}
		else{
			paint.setColor(srcColor);
			canvas.drawCircle(cx, cy, radius, paint);
		}
		
		if(Color.alpha(dstColor)==0){
			
			drawTransparentCircle(cx, cy+feed, radius, canvas);
		}
		else{
			paint.setColor(dstColor);
			canvas.drawCircle(cx, cy+feed, radius, paint);
		}
	}
	
	private void drawTransparentRect(int x, int y, Canvas canvas){
		
		Rect rect = sampleDots.drawingRects[x][y];
		
		int col0=Color.argb(255,40,40,40);
		int col1=Color.argb(255,80,80,80);
		int xm=rect.centerX();
		int ym=rect.centerY();
		
		for(int xa=0; xa<2; xa++)
			for(int ya=0; ya<2; ya++){
				
				if((xa+ya)%2==0) paint.setColor(col0); else paint.setColor(col1);
				
				int x1=(xa==0)? rect.left : xm;
				int y1=(ya==0)? rect.top : ym;
				int x2=(xa==0)? xm : rect.right;
				int y2=(ya==0)? ym : rect.bottom;
				
				canvas.drawRect(new Rect(x1,y1,x2,y2),paint);
			}
	}
	
	private void drawTransparentCircle(int x, int y, int r, Canvas canvas){
		
		paint.setStyle(Style.FILL);
		
		int col0=Color.argb(255,40,40,40);
		int col1=Color.argb(255,80,80,80);
		
		RectF oval = new RectF(x-r,y-r,x+r,y+r);
		
		for(int xa=0; xa<2; xa++)
			for(int ya=0; ya<2; ya++){
				
				if(ya==0) paint.setColor(col0); else paint.setColor(col1);
				
				int startAngle = xa * 180 + ya * 90;
				canvas.drawArc(oval, startAngle, 90, true, paint);
			}
	}
		
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		float touchX = event.getX();
		float touchY = event.getY();
	
		Xpos = (int)((touchX - sampleRect.left) / gridSize); 
		Ypos = (int)((touchY - sampleRect.top) / gridSize);
		limitPosRange();
			
		srcColor = sampleDots.getColor(Xpos, Ypos);
		sampleDots.setColor(srcColor, dstColor);
		invalidate();
		
		return true;
	}
	
	public void limitPosRange(){
	
		if(Xpos > gridX-1) Xpos=gridX-1;
		if(Xpos <0) Xpos=0;
		if(Ypos > gridY-1) Ypos=gridY-1;
		if(Ypos <0) Ypos=0;
	}
	
	class ReplaceColorDots extends Dots{

		Rect[][] drawingRects;
		
		int srcColor, dstColor;
		
		public ReplaceColorDots(int gridX, int gridY) {
			super(gridX, gridY);
		
			drawingRects = new Rect[gridX][gridY];
		}
		
		public void setupFromSampleRect(Rect sampleRect){
			
			double gridSize=sampleRect.width()/(double)gridX;
			
			for(int x=0; x<gridX; x++)
				for(int y=0; y<gridY; y++){
					
					drawingRects[x][y] = new Rect();
					
					drawingRects[x][y].left=(int)(sampleRect.left+gridSize*x);
					drawingRects[x][y].top=(int)(sampleRect.top+gridSize*y);
					drawingRects[x][y].right=(int)(sampleRect.left+gridSize*(x+1));
					drawingRects[x][y].bottom=(int)(sampleRect.top+gridSize*(y+1));
				}
		}
		
		public void renewalDotsByReplaceColor(){
			
			for(int x=0; x<gridX; x++)
				for(int y=0; y<gridY; y++){
					dot[x][y].setColor(getReplaceColor(x, y));
				}
		}
		
		public void setColor(int srcColor,int dstColor){
			
			this.srcColor = srcColor;	this.dstColor = dstColor;
		}
		
		public int getReplaceColor(int x, int y){
			
			int dotColor = dot[x][y].getColor();
			
			if (dotColor == srcColor) dotColor = dstColor;  
			
			return dotColor;
		}

	};
}
