package takada.dotmaker;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.MotionEvent;

public class Indicator {
	
	int Xsize=180,Ysize=80;
	int Left=0,Top=0;
	int Right=Left+Xsize;
	int Bottom=Top+Ysize;
	
	int viewerSize=64;
	int viewLeft,viewTop,viewRight,viewBottom;
	int bitmapLeft,bitmapTop;
	
	int oldTouchX,oldTouchY;
	int touchX,touchY;
	int displaceX;
	int displaceY;
	
	MainGrid maingrid;
	private Paint paint = new Paint();
	
	Indicator(MainGrid maingrid){
		
		this.maingrid=maingrid;
	}
	
	public void draw(Canvas canvas){
		
		paint.setStyle(Style.FILL);
		paint.setColor(Color.argb(200, 200, 200, 200));
		
		canvas.drawRoundRect(
		new RectF(Left,Top,Right,Bottom),10,10,paint);
		
		drawViewer(canvas);
		drawTextInfo(canvas);
		drawColorInfo(canvas);
	}
	
	private void drawTextInfo(Canvas canvas){
		
		int LeftMargin=6, TopMargin=8;
		int TextHeight= (int)(-paint.ascent()+paint.descent());
		int TextLeft = Left + LeftMargin;
		int TextTop = Top + TopMargin - (int)paint.ascent();
		
		paint.setTextSize(20);
		paint.setTextSkewX(-0.2f);
		paint.setColor(Color.BLACK);
		
		int totalfeed = TextTop;
		int feed = TextHeight + 2;
		
		/*int index = maingrid.undoDialog.currentMemoryIndex;// maingrid.undodialog private
		int number = maingrid.undoDialog.memoryList.size();//undodialog.memoryList private
		canvas.drawText
		("id:"+index+"size:"+number, TextLeft, feed, paint);
		feed += TextHeight;*/
		
		canvas.drawText
			("X:"+maingrid.Xpos+" Y:"+maingrid.Ypos, TextLeft, totalfeed, paint);
		
		totalfeed += feed;
		
		if(maingrid.isRangeMode){
			
			canvas.drawText("Range", TextLeft, totalfeed, paint);
			totalfeed += feed;
			
			if(maingrid.isRangeStarted){
			
				canvas.drawText((maingrid.rangeRect.width()+1)+" x "
					+(maingrid.rangeRect.height()+1),TextLeft, totalfeed, paint);
			}
			else{
				canvas.drawText("Set Start", TextLeft, totalfeed, paint);
			}
		}
		
		if(maingrid.isPenDown && maingrid.isEditMode)
			canvas.drawText("PenMode", TextLeft, totalfeed, paint);
		
		if(maingrid.isFigureTypeDecided){
			
			canvas.drawText("Figure", TextLeft, totalfeed, paint);
			totalfeed += feed;
			
			if(!maingrid.isFigureStarted)
				canvas.drawText("Set Start", TextLeft, totalfeed, paint);
			else
				canvas.drawText("Set End", TextLeft, totalfeed, paint);
		}
		
		renewalViwerPosition();
		int x=viewLeft-15;
		int y=viewBottom;
		paint.setTextSize(16);
		
		if (maingrid.hasUndoMemory()) canvas.drawText("U", x, y, paint);
	}
	
	private void drawColorInfo(Canvas canvas){
		
		paint.setColor(maingrid.drawColor);
		
		renewalViwerPosition();
		int palleteLeft=viewLeft-12;
		int palleteTop=viewTop;
		int palleteRight=palleteLeft+10;
		int palleteBottom=palleteTop+20;
		
		canvas.drawRect(
				new Rect(palleteLeft,palleteTop
						,palleteRight,palleteBottom),paint);
	}
	
	private void drawViewer(Canvas canvas){
		
		renewalViwerPosition();
		
		paint.setColor(Color.BLACK);
		
		canvas.drawRect(
		new Rect(viewLeft,viewTop,viewRight,viewBottom),paint);
		
		canvas.drawBitmap(maingrid.layerGroup.compoLayer.bitmap
						,bitmapLeft,bitmapTop, paint);
	}
	
	private void renewalViwerPosition(){
		
		viewLeft=Right-viewerSize-8;
		viewTop=Top+8;
		viewRight=viewLeft+viewerSize;
		viewBottom=viewTop+viewerSize;
		
		bitmapLeft=viewLeft+(viewerSize-maingrid.gridX)/2;
		bitmapTop=viewTop+(viewerSize-maingrid.gridY)/2;
	}
	
	public boolean checkTouch(MotionEvent event){
		int x=(int)event.getX();
		int y=(int)event.getY();
		
		if(x>Left && x<Right){
			if(y>Top && y<Bottom){
				
				touchX=x;	
				touchY=y;
				
				if(event.getAction()==MotionEvent.ACTION_MOVE){
					displaceX=(touchX-oldTouchX)*5;
					displaceY=(touchY-oldTouchY)*5;
					movePosition();
				}
				
				oldTouchX=x; 
				oldTouchY=y;
			
				return true;
			}
		}
		return false;
	}
	
	public void movePosition(){
		
		Left+=displaceX; 	Right+=displaceX;
		Top+=displaceY;		Bottom+=displaceY;
		
		checkPosition();
	}
	
	private void checkPosition(){
		
		int maxX = maingrid.screenX;
		int maxY = maingrid.screenY;
		
		if (Left<0) Left=0;
		if (Top<0) Top=0;
		if (Right>maxX) Left=maxX-Xsize;
		if (Bottom>maxY) Top=maxY-Ysize;
		
		Right=Left+Xsize;
		Bottom=Top+Ysize;
	}

}
