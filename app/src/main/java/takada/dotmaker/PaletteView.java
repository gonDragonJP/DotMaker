package takada.dotmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class PaletteView extends View{
	
	Palette selectedPalette,basicColorPalette,layerGroupPalette;
	LayerGroup layerGroup;
	boolean isSelectedBasicColorPalette=false;
	
	public PaletteView(Context context,LayerGroup layerGroup) {
		super(context);

		basicColorPalette = new Palette(true);
		
		this.layerGroup = layerGroup;
		selectedPalette = layerGroupPalette = layerGroup.palette;	
	}
	
	public void renewalLayerGroup(LayerGroup newGroup){
		
		layerGroup=newGroup;
		selectedPalette = layerGroupPalette= layerGroup.palette;
		invalidate();
	}
	
	public int getColor(){
		
		return Palette.getCurrentColor();
	}
	
	public int[] getRGB(){
		
		return Palette.getCurrentRGB();
	}
	
	public void setGridCursorColor(int color){
		
		selectedPalette.setGridCursorColor(color);
	}
	
	public void setRGBToHSV(HSVView hsvView){
		
		int[] rgb = getRGB();
		hsvView.setHSVFromRGB(rgb[0],rgb[1],rgb[2]);
	}
	
	public void copyToClipPallete(){
		
		selectedPalette.clip();
	}
	
	public void pasteFromClipPallete(){
		
		selectedPalette.paste();
		invalidate();
	}

	public void selectPalette(boolean newSelected){
		
		isSelectedBasicColorPalette = newSelected;
		
		if(isSelectedBasicColorPalette)
			selectedPalette = basicColorPalette;
		else 
			selectedPalette = layerGroupPalette;
		invalidate();
	}
	
	public void addRecordColor(){
		
		selectedPalette.addRecordColor();
	}

	public void onDraw(Canvas canvas){
		
		selectedPalette.draw(canvas);
	}
	
	public boolean onTouchEvent(MotionEvent event){
		
		int touchX=(int)event.getX();
		int touchY=(int)event.getY();

		for(int i=0; i<selectedPalette.palNum; i++){
			if(selectedPalette.checkTouched(i, touchX, touchY)){
					selectedPalette.selectedPal=i; 
					selectedPalette.invalidateCurrentColor();
					invalidate();
					break;
				}
		}
		return true;
	}

}

