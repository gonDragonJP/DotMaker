package takada.dotmaker;

import android.graphics.Rect;

public class SelectedArea {
	
	LayerGroup.Layer selectedLayer;
	LayerGroup.Layer movingRangeLayer;
	
	Rect selectedRange = new Rect();
	Dots selectedDots, stuckForCutDots, cuttingTransparentDots, fillBoxDots;
	Dots expandDots,rotateDots;
	Dots stuckDotsForMovingRangeLayer;
	
	public SelectedArea(){

	}
	
	public void setRange(LayerGroup.Layer layer,Rect range){
		
		selectedLayer = movingRangeLayer = layer;
		selectedRange.set(range);
	}
	
	public void setDotsToSelectedDots(Dots dots){
		
		selectedDots.copy(dots);
	}

	public void verticalMirroringSelectedDots(){
	
		selectedDots.verticalMirroring();
	}
	
	public void horizontalMirroringSelectedDots(){
		
		selectedDots.horizontalMirroring();
	}
	
	public void expandSelectedDots(int x, int y){
		
		expandDots = new Dots(x,y);
		selectedDots.expandTo(expandDots);
	}
	
	public void rotateSelectedDots
		(Rect requiredRotateRange,int rotateAngle,boolean isInterpolated){
		
		rotateDots = new Dots(requiredRotateRange.width()+1,requiredRotateRange.height()+1);
		rotateDots.maskALL();
		if(expandDots==null)
			if(isInterpolated)
				selectedDots.rotateTo(rotateDots, rotateAngle);
			else
				selectedDots.rotate2To(rotateDots, rotateAngle, false);
		else
			if(isInterpolated)
				expandDots.rotateTo(rotateDots, rotateAngle);
			else
				expandDots.rotate2To(rotateDots, rotateAngle, false);
	}
	
	public void renewalMovingRangeLayer(LayerGroup.Layer newLayer){
		
		resetMovingRangeLayerDots();
		selectedLayer.requestSetupPreparedCompoLayers();
		movingRangeLayer = newLayer;
		stuckMovingRangeLayerDots();
	}
	
	public void preCopyProcess(){
		
		createSelectedDots();
		initialSelectedDots();
		killExpandAndRotateDots();
		stuckMovingRangeLayerDots();
	}
	
	public void preCutProcess(){
		
		createSelectedDots();
		initialSelectedDots();
		createStuckForCutDots();
		setStuckForCutDots();
		createCuttingTransparentDots();
		setCuttingTransparentDots();
		killExpandAndRotateDots();
		
		pasteCuttingTransparentDotsToSelectedLayer();
		stuckMovingRangeLayerDots();
	}
	
	public void preFillBoxProcess(){
		
		createSelectedDots();
		stuckMovingRangeLayerDots();
		createFillBoxDots();
		killExpandAndRotateDots();
	}
	
	public void preGradationProcess(){
		
		createSelectedDots();
		killExpandAndRotateDots();
		stuckMovingRangeLayerDots();
	}
	
	public void preReplaceColorProcess(){
		
		createSelectedDots();
		initialSelectedDots();
		killExpandAndRotateDots();
		stuckMovingRangeLayerDots();
	}
	
	private void createSelectedDots(){
		
		int gridX = selectedRange.width() + 1;
		int gridY = selectedRange.height() + 1;
		selectedDots = new Dots(gridX,gridY);
	}
	
	private void initialSelectedDots(){
		
		int gridX = selectedDots.gridX;
		int gridY = selectedDots.gridY;
		
		for(int x=0; x<gridX; x++)
			for(int y=0; y<gridY; y++){
				
				int srcX = selectedRange.left + x;
				int srcY = selectedRange.top + y;
				int color = selectedLayer.dots.getColor(srcX, srcY);
				selectedDots.setColor(x, y, color);
			}
	}
	
	private void createStuckForCutDots(){
		
		int gridX = selectedRange.width() + 1;
		int gridY = selectedRange.height() + 1;
		stuckForCutDots = new Dots(gridX,gridY);
	}
	
	private void setStuckForCutDots(){
		
		stuckForCutDots.copy(selectedDots);
	}
	
	private void createCuttingTransparentDots(){
		
		int gridX = selectedRange.width() + 1;
		int gridY = selectedRange.height() + 1;
		cuttingTransparentDots = new Dots(gridX,gridY);
	}
	
	private void setCuttingTransparentDots(){
		
		cuttingTransparentDots.initialDots();
	}
	
	private void createFillBoxDots(){
		
		int gridX = selectedRange.width() + 1;
		int gridY = selectedRange.height() + 1;
		fillBoxDots = new Dots(gridX,gridY);
	}
	
	public void setFillBoxDots(int color){
		
		fillBoxDots.fill(color);
	}

	private void pasteCuttingTransparentDotsToSelectedLayer(){
		
		selectedLayer.setRangeDots
				(cuttingTransparentDots, selectedRange.left, selectedRange.top);
	}
	
	private void killExpandAndRotateDots(){
		
		expandDots = null;
		rotateDots = null;
	}
	
	private void stuckMovingRangeLayerDots(){
		
		int gridX= movingRangeLayer.gridX;
		int gridY= movingRangeLayer.gridY;
		
		stuckDotsForMovingRangeLayer = null;
		
		stuckDotsForMovingRangeLayer =
			new Dots(movingRangeLayer,gridX,gridY);
		
		stuckDotsForMovingRangeLayer.copy(movingRangeLayer.dots);
	}

	public void resetMovingRangeLayerDots(){
	
		movingRangeLayer.dots.copy(stuckDotsForMovingRangeLayer);	
	}
	
	public void pasteRangeDotsToMovingRangeLayer
		(int left,int top,boolean isTransPasteChecked,boolean isIgnoreTransChecked){
		
		int a =  ((isTransPasteChecked==true)  ? 1 : 0)
				+((isIgnoreTransChecked==true) ? 2 : 0);
		
		Dots rangeDots;
		rangeDots = (expandDots==null)? selectedDots : expandDots;
		rangeDots = (rotateDots==null)? rangeDots : rotateDots;
		
		switch(a){
		
		case 0:
			movingRangeLayer.setRangeDots(rangeDots, left, top);
			break;
		case 1:	
			movingRangeLayer.setRangeDotsEnabledTransparent(rangeDots, left, top);
			break;
		case 2:
			movingRangeLayer.setRangeDotsIgnoreTransparentArea(rangeDots, left, top);
			break;
		case 3:	
			movingRangeLayer.setRangeDotsEnableTransAndIgnoreTrans(rangeDots, left, top);
			break;	
		}
	}
	
	public void resetCutDotsToSelectedLayer(){
		
		selectedLayer.setRangeDots
				(stuckForCutDots, selectedRange.left, selectedRange.top);
	}
	
}
