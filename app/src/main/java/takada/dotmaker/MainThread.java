package takada.dotmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class MainThread implements View.OnClickListener{
	
	Context context;
	MainView parentView;
	
	private SurfaceHolder holder;
	private MainGrid grid;
	private Indicator indicator;
	private Layout_MainConsole console;
	
	private SettingDialog settingDialog;
	private LayerDialog layerDialog;
	
	private LayerGroup layerGroup;
	private LayerGroup.Layer layer;
	
	private ZoomControls zoomControls;
	
	MainThread(Context context,MainView mainView,LayerDialog layerDialog){
		
		this.context = context;
		parentView = mainView;
		holder=parentView.getHolder();
		
		this.layerDialog = layerDialog;
		this.layerGroup=layerDialog.layerGroup;
		this.layer =layerGroup.getCurrentLayer();
		
		this.settingDialog=layerDialog.settingDialog;
		
		grid=new MainGrid(context,layerDialog);
		indicator=new Indicator(grid);
		
		setPenToGrid();
	}
	
	public void setConsole(Layout_MainConsole console){
		
		this.console=console;
	}
	
	public void renewalLayerGroup(LayerGroup newGroup){
		
		layerGroup=newGroup;
		grid.renewalLayerGroup(newGroup);
		renewalCurrentLayer();
	}
	
	public void renewalCurrentLayer(){
		
		this.layer=layerGroup.getCurrentLayer();
		grid.renewalCurrentLayer();
		grid.isPenDown=false;
	}
	
	public void setScreenSize(int x,int y){
		
		grid.setScreenSize(x,y);
	}
	
	int blinkingRed=0;
	int padLag=0;
	
	public void periodicProcess(){
	
		countPadLag();
		setBlinkingPointer();
		indicator.movePosition();
	}
	
	private void countPadLag(){
	
		padLag=(padLag<0)? 0 : padLag-1;
	}
	
	private void setBlinkingPointer(){
		
		int a = Math.abs(blinkingRed+=1 % 400 - 200);
		grid.pointerColor = Color.rgb(a+55, 0, 0);
	}
	
	public void setPenToGrid(){
		
		grid.setPen(settingDialog.pen);
	}
	
	public void drawScreen(){
		
		Canvas canvas;
		canvas=holder.lockCanvas();
		
		canvas.drawColor(Color.BLACK);
		grid.draw(canvas);
		if(grid.isEditMode && settingDialog.isIndicatorActive){
			indicator.draw(canvas);
		}
		
		holder.unlockCanvasAndPost(canvas);
	}
	
	public void onPad(MotionEvent event){
		
		boolean isTouchedIndicator=false;
		if(settingDialog.isIndicatorActive)
			if((isTouchedIndicator = indicator.checkTouch(event)) == true) padLag=3;
		if(padLag <1) grid.checkTouch(event);
	}

	public void onClick(View v) {
		
		if(v==console.lower_layout.button[0]) grid.figureSetting();
		if(v==console.lower_layout.button[1]) grid.onColorDialog(0);
		if(v==console.lower_layout.button[2]) grid.rangeSelect();
		if(v==console.lower_layout.button[3]) {grid.switchEditMode(); setMoveLayoutMode();}
		
		if(v==console.upper_layout.button[0]) grid.putDot();
		if(v==console.upper_layout.button[1]) grid.switchPenMode();
		
		if(v==console.upper_layout.button[2]) moveCursor(0);
		if(v==console.upper_layout.button[3]) moveCursor(2);
		if(v==console.upper_layout.button[4]) moveCursor(1);
		if(v==console.upper_layout.button[5]) moveCursor(3);
	}
	
	private void moveCursor(int direction){
		
		grid.moveGrid(direction, grid.gridSize);	
		grid.moveOnGrid(direction, 1);	
		grid.checkFigure();
	}
	
	RelativeLayout mainLayout;
	LinearLayout subLayout,moveConsole;
	boolean isMoveLayoutMode = false;
	
	public void onBackPressed(){
		
		if(isMoveLayoutMode){
			mainLayout.removeView(moveConsole);
			subLayout.addView(console);
			isMoveLayoutMode = false;
			grid.switchEditMode();
		}
		else grid.onBackPressed();
	}
	public void setMoveLayoutMode(){
		
		mainLayout.addView(moveConsole);
		subLayout.removeView(console);
		isMoveLayoutMode = true;
	}

	int fillParent=LinearLayout.LayoutParams.FILL_PARENT;
	int wrapContent=LinearLayout.LayoutParams.WRAP_CONTENT;
	
	public void setMainLayout(RelativeLayout mainLayout){
		
		this.mainLayout = mainLayout;
		
		subLayout=new LinearLayout(context);
        subLayout.setLayoutParams(getLParam(fillParent,wrapContent));
        subLayout.setOrientation(LinearLayout.VERTICAL);
        
        parentView.setLayoutParams(addWeight(getLParam(fillParent,0),1)); 
        
        Layout_MainConsole console=new Layout_MainConsole(context,parentView);
        console.setLayoutParams(getLParam(fillParent,wrapContent));
        
        subLayout.addView(parentView);
        subLayout.addView(console);
        
        moveConsole=new LinearLayout(context);
        moveConsole.setLayoutParams(getLParam(wrapContent,wrapContent));
        moveConsole.setOrientation(LinearLayout.VERTICAL);
        
        setZoomControls();
        moveConsole.addView(zoomControls);
        
        RelativeLayout.LayoutParams param = 
          new RelativeLayout.LayoutParams(wrapContent, wrapContent);
        param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        param.addRule(RelativeLayout.CENTER_HORIZONTAL);
        moveConsole.setLayoutParams(param);
        
        mainLayout.addView(subLayout);
	}
	
	private void setZoomControls(){
		
		zoomControls = new ZoomControls(context);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				grid.zoomGrid(grid.getCenterPoint(), grid.gridSize/2, true);
			}
		});
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				grid.zoomGrid(grid.getCenterPoint(), grid.gridSize/3, false);
			}
		});
        zoomControls.setLayoutParams(getLParam(wrapContent,wrapContent));
	}
	
	 private LinearLayout.LayoutParams getLParam(int arg0,int arg1){
		    
	    	LinearLayout.LayoutParams param 
	    					= new LinearLayout.LayoutParams(arg0,arg1);
	    	return param;
	    }
	 
	 private LinearLayout.LayoutParams addWeight
		(LinearLayout.LayoutParams param, int i){

		 param.weight = i;
		 return param;
	 }
	 
	 private LinearLayout.LayoutParams addGravity
		(LinearLayout.LayoutParams param, int i){

		 param.gravity = i;
		 return param;
	 }
}
