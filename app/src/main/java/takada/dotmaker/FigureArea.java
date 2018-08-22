package takada.dotmaker;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class FigureArea {

	int gridX,gridY;
	
	private Dots figureDots,stuckDotsForDrawingLayer;
	private Bitmap figureBitmap;
	private Paint myPaint = new Paint();
	private Canvas myCanvas = new Canvas();
	public LayerGroup.Layer drawingLayer,stuckLayer;
	
	public FigureArea(){
		
	}
	
	public void createArea(int gridX,int gridY){
		
		this.gridX = gridX;	this.gridY = gridY;
		
		figureDots = new Dots(gridX, gridY);
		figureBitmap = Bitmap.createBitmap(gridX, gridY, Config.ARGB_8888);
	}
	
	public void setDrawingColor(int c){
		
		myPaint.setColor(c);
	}
	
	public void setDrawingLayer(LayerGroup.Layer layer){
		
		drawingLayer = layer;
	}
	
	public void renewalDrawingLayer(LayerGroup.Layer layer){
		
		resetDrawingLayerDots();
		drawingLayer.requestSetupPreparedCompoLayers();
		drawingLayer = layer;
		stuckDrawingLayerDots();
	}
	
	public void setFillColorStyle(boolean isFillColorChecked){
		
		if(isFillColorChecked)
			myPaint.setStyle(Style.FILL);
		else 
			myPaint.setStyle(Style.STROKE);
	}
	
	public void prepareCanvas(){
		
		figureBitmap.eraseColor(Color.argb(0, 0, 0, 0));
		myCanvas.setBitmap(figureBitmap);
	}
	
	public void setLineFigure(int startX, int startY, int stopX, int stopY){
		
		myCanvas.drawLine(startX, startY, stopX, stopY, myPaint);
		invalidateDotsFromBitmap();
	}
	
	public void setBoxFigure(int left, int top, int right, int bottom){
		
		myCanvas.drawRect(left, top, right, bottom, myPaint);
		invalidateDotsFromBitmap();
	}
	
	public void setCircleFigure(int left, int top, int right, int bottom){
	
		RectF oval = new RectF(left,top,right,bottom);
		myCanvas.drawOval(oval, myPaint);	
		invalidateDotsFromBitmap();
	}
	
	public void setTextFigure(TextDialog textDialog, int width, int height){
		
		int textSize = getAdjustedTextSizeFromHeight(height);
		myPaint.setTextSize(textSize);
		
		String text = textDialog.getText();
		textDialog.modifyPaintAttribure(myPaint);
		
		int textTop =  (int)(-myPaint.ascent());
		int left = 0;
		myCanvas.drawText(text, left, textTop, myPaint);	
		invalidateDotsFromBitmap();
	}
	
	private int getAdjustedTextSizeFromHeight(int height){
		
		int textSize = 0;
		float textHeight=0;
		
		do{
			textSize ++;
			myPaint.setTextSize(textSize);
			FontMetrics metrics = myPaint.getFontMetrics();
			float top = metrics.ascent;
			float bottom = metrics.descent;
			textHeight = bottom - top;
		}while(textHeight <= height);
		
		return textSize-1;
	}
	
	public void invalidateDotsFromBitmap(){
		
		for(int x=0; x<gridX; x++)
			for(int y=0; y<gridY; y++){
				int color = figureBitmap.getPixel(x, y);
				figureDots.setColor(x, y, color);
			}
	}
	
	public void stuckDrawingLayerDots(){
		
		int gridX= drawingLayer.gridX;
		int gridY= drawingLayer.gridY;
		
		stuckDotsForDrawingLayer = new Dots(drawingLayer,gridX,gridY);
		stuckDotsForDrawingLayer.copy(drawingLayer.dots);
		
		stuckLayer = drawingLayer;
	}
	
	public void resetDrawingLayerDots(){
		
		stuckLayer.dots.copy(stuckDotsForDrawingLayer);	
	}
	
	public void pasteFigureDotsToDrawingLayer (int Xpos,int Ypos){
	
		drawingLayer.setRangeDotsEnabledTransparent(figureDots, Xpos, Ypos);
	}
}
