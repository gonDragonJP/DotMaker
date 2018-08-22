package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

public class ColorDialog extends Dialog
				implements View.OnClickListener{
	
	private MainGrid parentGrid;
	
	private LinearLayout paletteLayout;
	private LinearLayout HSVlayout;
	
	public PaletteView paletteView;
	public Layout_PalleteConsole consolePalette;
	public HSVView HSVview;
	public Layout_HSVConsole consoleHSV;
	
	private boolean isHSVView = false;
	
	static final int
		MAINGRID_COLOR_INVALIDATE = 0,
		FILLBOX_GET_COLOR = 1,
		GRADATION_GET_STARTCOLOR = 2,
		GRADATION_GET_ENDCOLOR = 3,
		REPLACE_GET_SRCCOLOR =4,
		REPLACE_GET_DSTCOLOR =5;
	private int finalCallProcess;
	
	public ColorDialog(Context context,MainGrid parentGrid){
		super(context);
		
		this.parentGrid = parentGrid;

		paletteView=new PaletteView(context,parentGrid.layerGroup);
		HSVview=new HSVView(context);
		
		consolePalette=new Layout_PalleteConsole(context,this);
		consoleHSV=new Layout_HSVConsole(context,this);
		
	    setPaletteLayout(context);
	    setHSVLayout(context);   
	}

	public boolean isTransparentColor(){
		
		int color = paletteView.getColor();
		return (Color.alpha(color)==0);
	}
	
	public int[] getRGB(){
		
		return paletteView.getRGB();
	}

	public void renewalLayerGroup(LayerGroup newGroup){
		
		paletteView.renewalLayerGroup(newGroup);
	}
	
	public void onShow(int gridCursorColor,int finalCallProcess){

		this.finalCallProcess = finalCallProcess;
		paletteView.setGridCursorColor(gridCursorColor);
		setPaletteView();
		setLayerGroupPalette();
		paletteView.selectedPalette.invalidateCurrentColor();
		show();
	}
	
	private void switchColorDialogView(){
		
		if(isHSVView){
			
			HSVview.setClipRGBToPalette(); 
			setLayerGroupPalette();
			setPaletteView();
		}
		else{
			paletteView.setRGBToHSV(HSVview);
			setHSVView();
		}
	}
	
	private void setPaletteView(){
		
		setTitle("Color Pallete");
		setContentView(paletteLayout);
		isHSVView=false;
	}
	
	private void setHSVView(){
		
		setTitle("HSV");
		setContentView(HSVlayout);
		isHSVView=true;
	}
	
	private void switchSelectedPalette(){
		
		if(paletteView.isSelectedBasicColorPalette)
			setLayerGroupPalette();
		else
			setBasicColorPalette();
	}
	
	private void setBasicColorPalette(){
		
		boolean isBasicColor = true;
		paletteView.selectPalette(isBasicColor);
		consolePalette.button[1].setText("My Palette");
	}
	
	private void setLayerGroupPalette(){
		
		boolean isBasicColor = false;
		paletteView.selectPalette(isBasicColor);
		consolePalette.button[1].setText("Basic Color");
	}

	private void onHide(){
			
		if(isHSVView) HSVview.setRGBToPallete();
		else
			paletteView.selectedPalette.invalidateCurrentColor();
			
		paletteView.selectedPalette.addRecordColor();
		setPaletteView();
		
		switch(finalCallProcess){
		
		case MAINGRID_COLOR_INVALIDATE:
			parentGrid.invalidateDrawColorFromColorDialog();
			break;
		case FILLBOX_GET_COLOR:
			parentGrid.sendColorToFillBox();
			parentGrid.requestPasteSelectedArea();
			break;
		case GRADATION_GET_STARTCOLOR:
			parentGrid.sendStartColorToGradation();
			break;
		case GRADATION_GET_ENDCOLOR:
			parentGrid.sendEndColorToGradation();
			break;
		case REPLACE_GET_SRCCOLOR:
			parentGrid.sendSrcColorToReplace();
			break;
		case REPLACE_GET_DSTCOLOR:
			parentGrid.sendDstColorToReplace();
			break;
		}
		hide();
	}
		
	public void onClick(View v) {
		
		if((v==consolePalette.button[0])||(v==consoleHSV.button[0])) onHide();
		if((v==consolePalette.button[1])||(v==consoleHSV.button[1])) switchSelectedPalette();
		if((v==consolePalette.button[2])||(v==consoleHSV.button[1])) switchColorDialogView();
		if(v==consolePalette.button[3]) paletteView.copyToClipPallete();
		if(v==consolePalette.button[4]) paletteView.pasteFromClipPallete();
	}
	
	public void onBackPressed(){
		
		if(finalCallProcess == MAINGRID_COLOR_INVALIDATE){
			if(isHSVView){ 
				switchColorDialogView();
			}
			hide();
		}
	}
	
	int fillParent = LinearLayout.LayoutParams.FILL_PARENT;
	int wrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	public void setPaletteLayout(Context context){

		paletteLayout=new LinearLayout(context);
	    paletteLayout.setOrientation(LinearLayout.VERTICAL);
	    paletteLayout.setLayoutParams(param(fillParent,wrapContent));
	    
		paletteView.setLayoutParams(weight(param(fillParent,0),1));   
		consolePalette.setLayoutParams(param(fillParent,wrapContent));
		
	    paletteLayout.addView(paletteView);
	    paletteLayout.addView(consolePalette);
	}
	
	public void setHSVLayout(Context context){
		
		HSVlayout=new LinearLayout(context);
		HSVlayout.setOrientation(LinearLayout.VERTICAL);
		HSVlayout.setLayoutParams(param(fillParent,wrapContent));
	    
		HSVview.setLayoutParams(weight(param(fillParent,0),1));
		consoleHSV.setLayoutParams(param(fillParent,wrapContent));
   
	    HSVlayout.addView(HSVview);
	    HSVlayout.addView(consoleHSV);
	}
	
	private LinearLayout.LayoutParams param(int arg1, int arg2){
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(arg1,arg2);
		
		return params;
	}
	
	private LinearLayout.LayoutParams weight(LinearLayout.LayoutParams params,int arg){
		
		params.weight = arg;
		
		return params;
	}
}
