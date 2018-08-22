package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class FigureDialog extends Dialog implements View.OnClickListener{

	Context context;
	MainGrid parentGrid;
	
	private int selectedFigureMenu;
	public final int
		FIGURE_LINE=0,
		FIGURE_BOX=1,
		FIGURE_CIRCLE=2,
		FIGURE_TEXT=3;
	
	CheckBox checkFillColor,checkFollowingRangeOpe;
	private boolean isFillColorChecked;
	public boolean isFollowingRangeOpeChecked;
	
	private Point startPos,endPos,relativeStartPos,relativeEndPos;
	private Rect figureRange;
	
	private FigureArea figureArea;
	private TextDialog textDialog;
	
	public FigureDialog(Context context,MainGrid parentGrid) {
		super(context);
		
		this.context = context;
		this.parentGrid = parentGrid;
		
		startPos = new Point();			endPos = new Point();
		relativeStartPos = new Point();	relativeEndPos = new Point();
		figureRange = new Rect();
		figureArea = new FigureArea();
		textDialog = new TextDialog(context);
		
		setTitle("Figure Drawing");
		
		setContentView(R.layout.figuredialog);
		setMenu();
		setButton();	
	}
	
	public void setStartPos(int Xpos,int Ypos){
		
		startPos.set(Xpos, Ypos);
	}
	
	public void setDrawingLayer(LayerGroup.Layer layer){
		
		figureArea.setDrawingLayer(layer);
	}
	
	public Rect getFigureRange(){
		
		return figureRange;
	}
	
	public void renewalDrawingLayer(LayerGroup.Layer layer){
		
		figureArea.renewalDrawingLayer(layer);
		makeCompoLayerWithDrawingFigure();
	}
	
	public void setDrawingColor(int c){
		
		figureArea.setDrawingColor(c);
	}
	
	public void makeFigureArea(int Xpos, int Ypos){
		
		endPos.set(Xpos, Ypos);	
		setFigureRange();
		setRelativeFloatPos();
		
		figureArea.createArea(figureRange.width()+1,figureRange.height()+1);
		figureArea.prepareCanvas();
		
		switch(selectedFigureMenu){
		
		case FIGURE_LINE:

			figureArea.setLineFigure(relativeStartPos.x, relativeStartPos.y,
											relativeEndPos.x, relativeEndPos.y);
			break;
			
		case FIGURE_BOX:

			int endX = figureRange.width(),	endY = figureRange.height();
			if(isFillColorChecked){ endX++; endY++; }
			figureArea.setBoxFigure(0, 0, endX, endY);
			break;
			
		case FIGURE_CIRCLE:
			
			figureArea.setCircleFigure(0,0,figureRange.width()+1,figureRange.height()+1);
			break;
			
		case FIGURE_TEXT:
			
			figureArea.setTextFigure(textDialog,figureRange.width()+1,figureRange.height()+1);
			break;
		}
	}
	
	private void setFigureRange(){
		
		int x1 = Math.min(startPos.x, endPos.x);
		int x2 = Math.max(startPos.x, endPos.x);
		int y1 = Math.min(startPos.y, endPos.y);
		int y2 = Math.max(startPos.y, endPos.y);
		
		figureRange.set(x1,y1,x2,y2);
	}
	
	private void setRelativeFloatPos(){
		
		relativeStartPos.x = startPos.x - figureRange.left;
		relativeStartPos.y = startPos.y - figureRange.top;
		
		relativeEndPos.x = endPos.x - figureRange.left;
		relativeEndPos.y = endPos.y - figureRange.top;

		if (startPos.x == figureRange.right) relativeStartPos.x +=1;
		else relativeEndPos.x +=1;
		if (startPos.y == figureRange.bottom) relativeStartPos.y +=1;
		else relativeEndPos.y +=1;
	}

	public void makeCompoLayerWithDrawingFigure(){
		
		figureArea.resetDrawingLayerDots();
		pasteFigureArea();
		figureArea.drawingLayer.requestInvalidateCompoLayerWithPrepared();
	}

	private void pasteFigureArea(){
		
		int dstXpos = figureRange.left;
		int dstYpos = figureRange.top;
		figureArea.pasteFigureDotsToDrawingLayer (dstXpos, dstYpos);
	}
	
	@Override
	public void show(){
		super.show();
		
		checkFillColor.setChecked(false);
		checkFollowingRangeOpe.setChecked(false);
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		switch(id){
		
		case R.id.figuredialog_ok:
			hide();
			if(selectedFigureMenu == FIGURE_TEXT) textDialog.show();
			break;
			
		case R.id.figuredialog_cancel:
			
			parentGrid.cancelFigureSetting();
			hide(); 
			break;
		}
	}

	public void startMenu(){
		
		figureArea.stuckDrawingLayerDots();	
		
		switch(selectedFigureMenu){
		
		case FIGURE_LINE:
			break;
		
		case FIGURE_BOX:
			checkFillColorBox();
			break;
			
		case FIGURE_CIRCLE:
			checkFillColorBox();
			break;
			
		case FIGURE_TEXT:
			break;
		}
	}
	
	private void checkFillColorBox(){
		
		figureArea.setFillColorStyle(isFillColorChecked);
	}

	public void cancelFigure(boolean isFigureStarted){
		
		if(isFigureStarted){
			figureArea.resetDrawingLayerDots();
			figureArea.drawingLayer.requestInvalidateCompoLayerWithPrepared();
		}
		parentGrid.Xpos = startPos.x;	parentGrid.Ypos = startPos.y;
	}
	
	LinearLayout rootLayout,menuLayout;
	
	private void setMenu(){
		
		rootLayout = (LinearLayout)findViewById(R.id.figuredialog_root);
		menuLayout = (LinearLayout)findViewById(R.id.figuredialog_menu);
		
		RadioGroup radioGroup=(RadioGroup)findViewById(R.id.figuredialog_radiogroup1);
		radioGroup.check(R.id.figuredialog_radioline);
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				checkFillColor.setEnabled(true);
				
				switch(checkedId){
				
				case R.id.figuredialog_radioline:selectedFigureMenu = FIGURE_LINE; 
					checkFillColor.setEnabled(false);
					break;
				case R.id.figuredialog_radiobox:selectedFigureMenu = FIGURE_BOX; break;
				case R.id.figuredialog_radiocircle:selectedFigureMenu = FIGURE_CIRCLE; break;
				case R.id.figuredialog_radiotext:selectedFigureMenu = FIGURE_TEXT; break;
				}
			}
		});
		checkFillColor = (CheckBox)findViewById(R.id.figuredialog_checkfillcolor);
		checkFollowingRangeOpe = (CheckBox)findViewById(R.id.figuredialog_checkfollowrangeope);
		
		checkFillColor.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isFillColorChecked = arg1;
			}
		});
		checkFollowingRangeOpe.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isFollowingRangeOpeChecked = arg1;
			}
		});
		
		checkFillColor.setEnabled(false);
	}
	
	private void setButton(){
		
		Button ok = (Button)findViewById(R.id.figuredialog_ok);
		Button cancel = (Button)findViewById(R.id.figuredialog_cancel);
		
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	
}
