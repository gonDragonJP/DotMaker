package takada.dotmaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;

public class MainGrid {
	
	int screenX,screenY;
	
	int gridX,gridY;
	int Xpos=0,Ypos=0;
	int oldXpos,oldYpos;
	int transXpos,transYpos;
	int transOldXpos,transOldYpos;
	int selectedXpos,selectedYpos;
	Rect rangeRect=new Rect();
	
	int gridSize=6;
	int Left=0,Top=0;
	int Right,Bottom;
	int Width,Height;
	
	boolean isEditMode=true;
	boolean isRangeMode=false;
	boolean isRangeStarted=false;
	boolean hasSelectedArea=false;
	boolean isPenDown=false;
	boolean isFirstTouch=true;
	boolean isFirstTouchby2=true;
	boolean isFigureTypeDecided = false;
	boolean isFigureStarted=false;
	
	int oldTouchX,oldTouchY;
	int touchX,touchY;
	int oldDistance;
	
	int drawColor = Color.WHITE;
	int cursorColor;
	private Pens pen;
	int pointerColor;
	
	RotateRange rotateRange = new RotateRange();;
	int gripR=20;
	Point rotateGripPoint=new Point();
	Point zoomGripPoint=new Point();
	int rotateAngle=0;
	Point rotatePasteDstDev=new Point();
	Bitmap rotateIcon,expandIcon;
	
	public LayerGroup layerGroup;
	public LayerGroup.Layer layer;
	public Dots dots;
	
	private RangeMenuDialog rangeDialog;
	private ColorDialog colorDialog;
	private FigureDialog figureDialog;
	private LayerDialog layerDialog;
	private UndoDialog undoDialog;
	public SettingDialog settingDialog;
	
	MainGrid(Context context,LayerDialog layerDialog){
		
		this.layerDialog = layerDialog; // ダイアログアクセス制限のため必要
		this.undoDialog = layerDialog.undoDialog; // Undo処理のため必要
		this.settingDialog = layerDialog.settingDialog;
		this.layerGroup=layerDialog.layerGroup;
		gridX=layerGroup.gridX; gridY=layerGroup.gridY;
		
		rangeDialog = new RangeMenuDialog(context,this);
		colorDialog = new ColorDialog(context,this);
		figureDialog = new FigureDialog(context,this);
		
		renewalCurrentLayer();
		undoDialog.setupLayerMemory();
		
		setRangeIcon(context);
	}
	
	public void renewalLayerGroup(LayerGroup newGroup){
		
		layerGroup=newGroup; // レイヤー更新はMainThreadから
		
		gridX=layerGroup.gridX; gridY=layerGroup.gridY;
		Xpos=0; Ypos=0;
		isRangeStarted=false;
		isPenDown=false;
		isFirstTouch=true;
		isFirstTouchby2=true;
		
		colorDialog.renewalLayerGroup(newGroup);
		undoDialog.setupLayerMemory();
	}
	
	public void renewalCurrentLayer(){
		
		layer=layerGroup.getCurrentLayer();
		dots = layer.dots;
		renewalGrid();
		setCursorColor();
		if(hasSelectedArea) rangeDialog.renewalMovingRangeLayer(layer);
		if(isFigureStarted) figureDialog.renewalDrawingLayer(layer);
	}
	
	public void setPen(Pens pen){
		
		this.pen = pen;
	}
	
	public void setScreenSize(int x,int y){
	
		screenX=x; screenY=y;
	}
	
	public void setDrawColor(int color){
		
		drawColor=color;
	}
	
	public void setCursorColor(){
		
		cursorColor=dots.getColor(Xpos, Ypos);
	}
	
	public void onColorDialog(int finalRecallProcess){ 

		isPenDown=false;
		colorDialog.onShow(cursorColor,finalRecallProcess);
	}
	
	public void invalidateDrawColorFromColorDialog(){
		
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		setDrawColor(Color.argb(a, rgb[0], rgb[1], rgb[2]));
	}
	
	public void sendColorToFillBox(){
	
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		rangeDialog.setFillBoxDotsToSelectedDots(Color.argb(a, rgb[0], rgb[1], rgb[2]));
	}
	
	public void sendStartColorToGradation(){
		
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		rangeDialog.setGradationStartColor(Color.argb(a, rgb[0], rgb[1], rgb[2]));
		rangeDialog.gradationDialog.show();
	}
	
	public void sendEndColorToGradation(){
		
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		rangeDialog.setGradationEndColor(Color.argb(a, rgb[0], rgb[1], rgb[2]));
		rangeDialog.gradationDialog.show();
	}
	
	public void sendSrcColorToReplace(){
		
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		rangeDialog.setReplaceSrcColor(Color.argb(a, rgb[0], rgb[1], rgb[2]));
		rangeDialog.replaceColorDialog.show();
	}
	
	public void sendDstColorToReplace(){
		
		int a=colorDialog.isTransparentColor() ? 0 : layer.alpha ;
		int[] rgb = colorDialog.getRGB();
		rangeDialog.setReplaceDstColor(Color.argb(a, rgb[0], rgb[1], rgb[2]));
		rangeDialog.replaceColorDialog.show();
	}

	public void requestPasteSelectedArea(){
		
		rangeDialog.makeCompoLayerWithMovingRange();
	}
	
	public boolean hasUndoMemory(){
		
		return layerDialog.hasUndoMemory();
	}
	
	public void draw(Canvas canvas){
		
		if(isEditMode) drawBackgroundRect(canvas);
		
		drawDots(canvas);
		
		if(settingDialog.isGridLineVisible) drawGridLines(canvas);
		drawPointerRect(canvas);
		if(hasSelectedArea && isEditMode) drawPointerGrip(canvas);
		if(isRangeStarted) drawRangeRect(canvas);
	}
	
	private void drawDots(Canvas canvas){
		
		Paint paint = new Paint();
		int col0=Color.argb(255,120,120,120);
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++){
				
				int x1=Left+x*gridSize;
				int y1=Top+y*gridSize;
				int x2=x1+gridSize-1;
				int y2=y1+gridSize-1;
				
				paint.setStyle(Style.FILL);
				paint.setColor(layerGroup.compoLayer.dots.getColor(x,y));
				canvas.drawRect(new Rect(x1,y1,x2,y2),paint);
			}
		}
	}
	
	private void drawGridLines(Canvas canvas){
		
		Paint paint = new Paint();
		paint.setColor(Color.argb(255,120,120,120));
		paint.setStyle(Style.STROKE);
		
		float[] pts = new float [(gridX+gridY+2)*4];
		int index = 0;
		
		for(int x=0; x<gridX+1; x++){
			pts[index] = pts[index+2] = Left+x*gridSize-1;
			pts[index+1]=Top;
			pts[index+3]=Bottom;	
			index += 4;
		}
		
		for(int y=0; y<gridY+1; y++){
			pts[index+1] = pts[index+3] = Top+y*gridSize-1;
			pts[index]=Left;
			pts[index+2]=Right;	
			index += 4;
		}
		canvas.drawLines(pts, paint);
	}

	private void drawBackgroundRect(Canvas canvas){
	
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		
		int col0=Color.argb(255,40,40,40);
		int col1=Color.argb(255,80,80,80);
		
		paint.setColor(col0);
		canvas.drawRect(new Rect(Left,Top,Right,Bottom),paint);
		paint.setColor(col1);
		
		for(int x=0; x<gridX*2; x++)
			for(int y=0; y<gridY*2; y++){
				if((x+y)%2==0) continue;
			
				int x1=Left+x*gridSize/2;
				int y1=Top+y*gridSize/2;
				int x2=Left+(x+1)*gridSize/2-1;
				int y2=Top+(y+1)*gridSize/2-1;
			
				canvas.drawRect(new Rect(x1,y1,x2,y2),paint);
			}
	}
	
	private void drawRangeRect(Canvas canvas){
		
		if(hasSelectedArea) return;
		
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(Color.argb(100,200,200,200));
		
		if(Xpos>selectedXpos){
			rangeRect.left = selectedXpos; 
			rangeRect.right = Xpos;
		}
		else{
			rangeRect.left = Xpos; 
			rangeRect.right = selectedXpos;
		}
		
		if(Ypos>selectedYpos){
			rangeRect.top = selectedYpos;
			rangeRect.bottom = Ypos;
		}
			
		else{
			rangeRect.top = Ypos;
			rangeRect.bottom = selectedYpos;
		}
		
		int x1,y1,x2,y2;
		
		x1=Left+rangeRect.left*gridSize;
		x2=x1+(rangeRect.width()+1)*gridSize;
		y1=Top+rangeRect.top*gridSize;
		y2=y1+(rangeRect.height()+1)*gridSize;
		
		canvas.drawRect(new Rect(x1,y1,x2,y2),paint);	
	}
	
	private void drawPointerRect(Canvas canvas){
		
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		int col = isEditMode ? pointerColor : Color.rgb(150, 0, 0);
		paint.setColor(col);
		
		int x1,y1,x2,y2;
		Rect rect;
		
		if(hasSelectedArea){
			
			rect = getScreenRangeRect();
			rotateRange.setRangeRect(rect);
			
			rotateGripPoint.set
				(rect.right+gripR/2, rect.top-gripR/2);
			zoomGripPoint.set
				(rect.right+gripR/2, rect.bottom+gripR/2);
		
			Path path = new Path();
			path.moveTo(rotateRange.left_top.x, rotateRange.left_top.y);
			path.lineTo(rotateRange.left_bottom.x, rotateRange.left_bottom.y);
			path.lineTo(rotateRange.right_bottom.x, rotateRange.right_bottom.y);
			path.lineTo(rotateRange.right_top.x, rotateRange.right_top.y);
			path.lineTo(rotateRange.left_top.x, rotateRange.left_top.y);
			
			canvas.drawPath(path, paint);
		}
		else{
			x1=Left+Xpos*gridSize;
			y1=Top+Ypos*gridSize;
			x2=Left+(Xpos+1)*gridSize-1;
			y2=Top+(Ypos+1)*gridSize-1;
			
			canvas.drawRect(new Rect(x1,y1,x2,y2), paint);
		}
		
	}
	
	private void drawPointerGrip(Canvas canvas){
	
		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(pointerColor);
		
		Point point1 = rotateRange.mapToRotateCoord(rotateGripPoint);
		Point point2 = rotateRange.mapToRotateCoord(zoomGripPoint);
		
		final int iconHalfSize = 8;
		canvas.drawBitmap(rotateIcon,point1.x-iconHalfSize,point1.y-iconHalfSize, paint);
		canvas.drawBitmap(expandIcon,point2.x-iconHalfSize,point2.y-iconHalfSize, paint);
	}
	
	public void checkFigure(){
		
		if(isEditMode && isFigureStarted) drawFigure();
	}
	
	private void drawFigure(){
		
		figureDialog.setDrawingColor(drawColor);
		figureDialog.makeFigureArea(Xpos, Ypos);
		figureDialog.makeCompoLayerWithDrawingFigure();
	}
	
	public void checkTouch(MotionEvent event){
		
		int action = event.getAction();
		touchX=(int)event.getX();
		touchY=(int)event.getY();
		Xpos =(touchX-Left)/gridSize;
		Ypos =(touchY-Top)/gridSize;
		checkPositionLimit();
		
		if(action==MotionEvent.ACTION_UP){
			
			isFirstTouch=true;
			isFirstTouchby2=true;
			
			if(isEditMode){
				if(isPenDown) undoDialog.inclementMemory();
			
				if(hasSelectedArea){
				
					if(rotatePasteDstDev==null)
						rangeDialog.setDstPosition(rangeRect.left, rangeRect.top);
					else
						rangeDialog.setDstPosition
							(rangeRect.left + rotatePasteDstDev.x, rangeRect.top + rotatePasteDstDev.y);
				
					rangeDialog.makeCompoLayerWithMovingRange();
				}
			
				if(isFigureStarted){
				
					drawFigure();
				}
			}
		}
		
		if(action==MotionEvent.ACTION_MOVE){
		
			int count=event.getPointerCount();
			
			switch(count){
			
			case 1:
				onTouchMoveby1();
				break;
		
			case 2:
				onTouchMoveBy2(event);
				break;
			}
		}
		
		if(action==MotionEvent.ACTION_DOWN){	
			if(isEditMode){	
				
				if(hasSelectedArea){
					
					rotateRange.setRangeRect(getScreenRangeRect());
					Point point = rotateRange.mapToBaseCoord(touchX, touchY);
					
					transOldXpos= transXpos = (point.x-Left)/gridSize;
					transOldYpos= transYpos = (point.y-Top)/gridSize;
					
				}
				else{
					
					checkPen();
					setCursorColor();
				}
			}
			
			isFirstTouch=false;	
		}
		
		oldXpos = Xpos;
		oldYpos = Ypos;
		oldTouchX=touchX;	oldTouchY=touchY;
	}
	
	private void onTouchMoveby1(){
		
		if(isEditMode) {
	
			if(hasSelectedArea){
				
				rotateRange.setRangeRect(getScreenRangeRect());
				Point point = rotateRange.mapToBaseCoord(touchX, touchY);
				
				transXpos=(point.x-Left)/gridSize;
				transYpos=(point.y-Top)/gridSize;
				
				checkSelectedAreaMove();
				checkZoomGripMove();
				checkRotateGripMove();
				
				transOldXpos = transXpos;
				transOldYpos = transYpos;
			}
			else{
				checkPositionLimit();
				checkPen();
				interpolatePen();	
			}

			return;
		}
		
		int xa=Math.abs(touchX-oldTouchX);
		int ya=Math.abs(touchY-oldTouchY);
		if(touchX>oldTouchX) moveGrid(0,xa);
		if(touchY>oldTouchY) moveGrid(1,ya);
		if(touchX<oldTouchX) moveGrid(2,xa);
		if(touchY<oldTouchY) moveGrid(3,ya);
	}
	
	private void checkSelectedAreaMove(){
		
		if(!checkTouchedSelectedArea()) return;
		
		int xa=Math.abs(Xpos-oldXpos);
		int ya=Math.abs(Ypos-oldYpos);
		if(Xpos>oldXpos) moveSelectedArea(0,xa);
		if(Ypos>oldYpos) moveSelectedArea(1,ya);
		if(Xpos<oldXpos) moveSelectedArea(2,xa);
		if(Ypos<oldYpos) moveSelectedArea(3,ya);
	}

	private boolean checkTouchedSelectedArea(){
		
		if(oldXpos>=rangeRect.left && oldXpos<=rangeRect.right)
			if(oldYpos>=rangeRect.top && oldYpos<=rangeRect.bottom){
				
				return true;
			}
		return false;
	}
	
	private void checkZoomGripMove(){
		
		if(!checkTouchedZoomGrip()) return;
		
		int xa=Math.abs(transXpos-transOldXpos);
		int ya=Math.abs(transYpos-transOldYpos);
		if(transXpos>transOldXpos) zoomSelectedArea(0,xa);
		if(transYpos>transOldYpos) zoomSelectedArea(1,ya);
		if(transXpos<transOldXpos) zoomSelectedArea(2,xa);
		if(transYpos<transOldYpos) zoomSelectedArea(3,ya);
		
		rangeDialog.expandSelectedDots(rangeRect.width()+1, rangeRect.height()+1);
		if(rotatePasteDstDev!=null) {
			
			setupRotate();
		}
	}

	private boolean checkTouchedZoomGrip(){
		
		Point point = rotateRange.mapToBaseCoord(oldTouchX, oldTouchY);
		
		int distance = (int)(Math.pow(point.x-zoomGripPoint.x,2)
								+Math.pow(point.y-zoomGripPoint.y,2));
		
		if(distance<gripR*gripR){
				
				return true;
			}
		return false;
	}
	
	private void checkRotateGripMove(){
		
		if(!checkTouchedRotateGrip()) return;
		
		double radian = 3.1415926 / 180;
	
		double cx = getScreenRangeRect().exactCenterX();
		double cy = getScreenRangeRect().exactCenterY();
		
		double oldAngle=Math.atan2((double)oldTouchX-cx, (double)oldTouchY-cy);
		double angle=Math.atan2((double)touchX-cx, (double)touchY-cy);
		rotateAngle = (rotateAngle + (int)((angle - oldAngle)/radian) + 360)%360;
		
		setupRotate();
	}
	
	private void setupRotate(){
		
		rotateRange.setAngle(rotateAngle);
		rotateRange.setRangeRect(getScreenRangeRect());
		Rect rect = rotateRange.getRequiredRangeRect(Left, Top, gridSize);
		rangeDialog.rotateSelectedDots(rect,rotateAngle);
		rotatePasteDstDev = new Point(rect.left-rangeRect.left,rect.top-rangeRect.top);
	}
	
	private boolean checkTouchedRotateGrip(){
		
		Point point = rotateRange.mapToBaseCoord(oldTouchX, oldTouchY);
		
		int distance = (int)(Math.pow(point.x-rotateGripPoint.x,2)
								+Math.pow(point.y-rotateGripPoint.y,2));
		
		if(distance<gripR*gripR){
			
				return true;
			}
		return false;
	}
	
	private Rect getScreenRangeRect(){
		
		int x1,x2,y1,y2;
		
		x1=Left+rangeRect.left*gridSize;
		y1=Top+rangeRect.top*gridSize;
		x2=Left+(rangeRect.right+1)*gridSize-1;
		y2=Top+(rangeRect.bottom+1)*gridSize-1;
		
		return new Rect (x1,y1,x2,y2);	
	}
	
	public void moveSelectedArea(int direction,int step){	
		
		switch(direction){
		case 0:
			rangeRect.left+=step;	rangeRect.right+=step;		break;
		case 1:
			rangeRect.top+=step;	rangeRect.bottom+=step;		break;
		case 2:
			rangeRect.left+=-step;	rangeRect.right+=-step;		break;
		case 3:
			rangeRect.top+=-step;	rangeRect.bottom+=-step;	break;
		}
	}
	
	private void zoomSelectedArea(int direction,int step){	
		
		switch(direction){
		case 0:
			rangeRect.right+=step;		break;
		case 1:
			rangeRect.bottom+=step;		break;
		case 2:
			rangeRect.right+=-step;		break;
		case 3:
			rangeRect.bottom+=-step;	break;
		}
		
		checkZoomSelectedAreaLimit();
	}

	private void onTouchMoveBy2(MotionEvent event){
		
		if(isEditMode) return;
		
		int id=event.findPointerIndex(1);
		int x1=touchX;
		int y1=touchY;
		int x2=(int)event.getHistoricalX(id, 0);
		int y2=(int)event.getHistoricalY(id, 0);
	
		int distance=(x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
		Point center=new Point((x1+x2)/2,(y1+y2)/2);
		
		if(isFirstTouchby2){
			oldDistance=distance; 
			isFirstTouchby2=false; 
			return;
		}
		
		if(distance>oldDistance) zoomGrid(center,1,true);
		if(distance<oldDistance) zoomGrid(center,1,false);
	
		oldDistance=distance;
	}
	
	public Point getCenterPoint(){
		
		return new Point(screenX/2,screenY/2);
	}
	
	public void zoomGrid(Point center, int step, boolean isZoomIn){
		
		int oldWidth=Width, oldLeft=Left, oldTop=Top;
		if(isZoomIn) gridSize+=step; else gridSize+=-step;
		if(gridSize>32) gridSize=32;
		if(gridSize<4) gridSize=4;
		renewalGrid();
		double scale = (double)Width/(double)oldWidth;
		
		Left=(int)((double)(oldLeft-center.x)*scale)+center.x;
		Top=(int)((double)(oldTop-center.y)*scale)+center.y;
		
		checkGridLimit();
		renewalGrid();
	}
	
	public void moveOnGrid(int direction,int step){
		
		switch(direction){
		case 0:
			Xpos+=-1;	break;
		case 1:
			Ypos+=-1;	break;
		case 2:
			Xpos+=1;	break;
		case 3:
			Ypos+=1;	break;
		}
		
		checkPositionLimit();
		checkPen();
	}
	
	public void moveGrid(int direction,int step){	
		
		switch(direction){
		case 0:
			Left+=step;		break;
		case 1:
			Top+=step;		break;
		case 2:
			Left+=-step;	break;
		case 3:
			Top+=-step;		break;
		}
		checkGridLimit();
		renewalGrid();
	}
	
	public void switchEditMode(){
		
		isEditMode = (isEditMode) ? false : true;
		isPenDown=false;
		isFirstTouch=true;
	}

	public void switchPenMode(){
		
		if(isRangeStarted | isFigureTypeDecided) return;
		if(!isEditMode) switchEditMode();
		
		isPenDown = (isPenDown) ? false : true;
		isFirstTouch=true;
	}
	
	public void rangeSelect(){
		
		if(!isEditMode | isFigureTypeDecided) return;
		
		if(!isRangeMode) { 
			
			isRangeMode = true; 
			isPenDown=false;
			return;
		}
		
		if(!isRangeStarted){
			
			selectedXpos=Xpos;
			selectedYpos=Ypos;
			isRangeStarted=true;
		}
		else if(!hasSelectedArea){
			
			hasSelectedArea=true;
			layerDialog.setConsoleAccessLimit(true);
			layerDialog.layerGroup.setupPreparedCompoLayers();
			
			rangeDialog.setRange(layer, rangeRect);
			rangeDialog.setDstPosition(rangeRect.left, rangeRect.top);
			rotateRange.setAngle(rotateAngle = 0);
			rotatePasteDstDev = null;
			rangeDialog.show();	
		}
			else{
				rangeDialog.show();
			}
	}
	
	public void endRangeSelect(){
	
		layerDialog.setConsoleAccessLimit(false);
		hasSelectedArea=false;
		isRangeStarted=false;
		isRangeMode = false;
		undoDialog.inclementMemory();
	}
	
	public void cancelRangeSelect(){
		
		layerDialog.setConsoleAccessLimit(false);
		hasSelectedArea = false;
		isRangeStarted =false;
		isRangeMode = false;
	}
	
	public void figureSetting(){
		
		if(!isEditMode | isRangeMode) return;
		
		if(!isFigureTypeDecided) {
			
			figureDialog.show();
			isFigureTypeDecided = true;
			isPenDown = false;
		}
		else if(!isFigureStarted){
				
				layerDialog.setConsoleAccessLimit(true);
				startFigure();
			}
			else{
				layerDialog.setConsoleAccessLimit(false);
				endFigure();
			}
	}
	
	public void startFigure(){
		
		isFigureStarted = true;
		
		layerDialog.layerGroup.setupPreparedCompoLayers();
		
		figureDialog.setDrawingLayer(layer);
		figureDialog.setStartPos(Xpos, Ypos);
		figureDialog.startMenu();
	}
	
	public void backwardFigureSetting(){
		
		figureDialog.cancelFigure(isFigureStarted);
		cancelFigureSetting();
		figureSetting();
	}
	
	public void endFigure(){
		
		isFigureStarted = false;
		isFigureTypeDecided = false;
		undoDialog.inclementMemory();
		
		if(figureDialog.isFollowingRangeOpeChecked){
		
			Rect figureRange = figureDialog.getFigureRange();
			isRangeMode = true; 
			selectedXpos=figureRange.left;
			selectedYpos=figureRange.top;
			isRangeStarted=true;
			hasSelectedArea=false;
			rangeRect.set(figureRange);
			rangeSelect();
		}
	}
	
	public void cancelFigureSetting(){
		
		layerDialog.setConsoleAccessLimit(false);
		isFigureStarted = false;
		isFigureTypeDecided = false;
	}
	
	public void onBackPressed(){
		
		if(isFigureTypeDecided) {
			backwardFigureSetting();
			return;
		}
		
		if(isRangeMode){
			
			if(!hasSelectedArea){
		
				cancelRangeSelect();
				Xpos = selectedXpos; Ypos = selectedYpos;
				return;
			}
			else{
				rangeDialog.cancelRangeSelect();
				hasSelectedArea = false;
				rangeSelect();
				return;
			}
		}
		
		layerDialog.undoDialog.show();
	}

	public void checkPen(){
		
		if(isPenDown) putPen(Xpos,Ypos);
	}
	
	public void putPen(int cx, int cy){
		
		int penSize = pen.penSize;
		for(int x=0; x<penSize; x++){
			for(int y=0; y<penSize; y++){
				
				int dx=cx+x-penSize/2;
				int dy=cy+y-penSize/2;
				
				if(dx<0 | dy<0 | dx>=gridX | dy>=gridY) continue;
				
				if(pen.isActivePenPoint(x,y)) setDot(dx,dy);
			}
		}
	}
	
	public void putDot(){
		
		if(!isEditMode | isRangeStarted) return;
		
		setDot(Xpos,Ypos);
		isFirstTouch=false;
		undoDialog.inclementMemory();
	}
	
	private void setDot(int x, int y){
		
		layer.setDotWithBMP(x,y,drawColor);
		dots.requestRenewalCompoLayer(x,y);
	}
	
	private void interpolatePen(){
	
		
		if(!isPenDown | isRangeStarted) return;
		
		int oldXpos=(oldTouchX-Left)/gridSize;
		int oldYpos=(oldTouchY-Top)/gridSize;
		
		recursiveCheck(oldXpos,oldYpos,Xpos,Ypos);
	}
	
	private boolean recursiveCheck(int x1,int y1, int x2, int y2){
		
		int distance = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
		
		if (distance>2){

			int midXpos = (x1+x2)/2;
			int midYpos = (y1+y2)/2;

			putPen(midXpos,midYpos);
			
			recursiveCheck(midXpos,midYpos,x1,y1);
			recursiveCheck(midXpos,midYpos,x2,y2);
		}
		return false;
	}
	
	private void checkPositionLimit(){
		
		if(Xpos<0) Xpos=0;
		if(Ypos<0) Ypos=0;
		if(Xpos>=gridX) Xpos=gridX-1;
		if(Ypos>=gridY) Ypos=gridY-1;
	}
	
	private void checkGridLimit(){
		
		int limitMaxLeft=screenX/2;
		int limitMaxTop=screenY/2;
		int limitMinLeft=limitMaxLeft-gridX*gridSize;
		int limitMinTop=limitMaxTop-gridY*gridSize;
		
		if(Left>limitMaxLeft) Left=limitMaxLeft;
		if(Left<limitMinLeft) Left=limitMinLeft;
		if(Top>limitMaxTop) Top=limitMaxTop;
		if(Top<limitMinTop) Top=limitMinTop;
	}
	
	private void checkZoomSelectedAreaLimit(){
		
		if (rangeRect.right < rangeRect.left) rangeRect.right = rangeRect.left;
		if (rangeRect.bottom < rangeRect.top) rangeRect.bottom = rangeRect.top;
	}
	
	private void renewalGrid(){
		
		Right=Left+(gridX*gridSize)-1;
		Bottom=Top+(gridY*gridSize)-1;
		Width=Right-Left+1;
		Height=Bottom-Top+1;
	}
	
	private void setRangeIcon(Context context){
		
		Resources res = context.getResources();
		rotateIcon = BitmapFactory.decodeResource(res, R.drawable.roticon);
		expandIcon = BitmapFactory.decodeResource(res, R.drawable.expicon);
	}
}
