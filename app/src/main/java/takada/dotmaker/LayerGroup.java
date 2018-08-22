package takada.dotmaker;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.widget.Toast;

public class LayerGroup {
	
	List<Layer> layers = new ArrayList<Layer>();
	int gridX,gridY;
	int  currentLayer=0;
	Layer compoLayer,copyLayer,mergeLayer;
	Layer preparedCompoLayer1,preparedCompoLayer2;//‘I‘ð”ÍˆÍˆÚ“®ŽžŽg—p
	
	Palette palette;
	
	
	static final int maxLayerNum=8;
	
	public LayerGroup(int gridX,int gridY){
		
		this.gridX=gridX;	this.gridY=gridY;
		
		addLayer(1);
		compoLayer = new Layer(this);
		copyLayer = new Layer(this);
		mergeLayer = new Layer(this);
		preparedCompoLayer1 = new Layer(this);
		preparedCompoLayer2 = new Layer(this);
		
		boolean isBasicColor=false;
		palette=new Palette(isBasicColor);
		
		setCurrentLayer(currentLayer);
	}
	
	public Layer getCurrentLayer(){
		
		return (Layer)layers.get(currentLayer);
	}
	
	public Layer getLayerClone(int layerNumber){
		
		Layer layer = new Layer(this);
		layer.copy(layers.get(layerNumber));
		return layer;
	}
	
	public void setLayerClone(Layer cloneLayer, int layerNumber){
		
		layers.get(layerNumber).copy(cloneLayer);
		invalidateCompoLayer();
	}
	
	public void setCurrentLayer(int i){
		
		currentLayer=i;
		getCurrentLayer().isActivated = true;
	}
	
	public void addLayer(int num){
		
		if(layers.size()+num > maxLayerNum) return;
		
		for(int i=0; i<num; i++)
			layers.add(new Layer(this));
	}
	
	public void copyLayer(){
		
		copyLayer.copy(getCurrentLayer());	
	}
	
	public void pasteLayer(){
		
		getCurrentLayer().copy(copyLayer);
		invalidateCompoLayer();
	}
	
	public void removeCurrentLayer(){
		
		if(layers.size()==1) return;
		layers.remove(currentLayer);
		int maxLayer = layers.size() -1 ;
		if (maxLayer < currentLayer) currentLayer = maxLayer;
	}
	
	public void exchangeNextLayer(){
		
		int maxLayer = layers.size() -1 ;
		if (currentLayer == maxLayer) return;
		
		Layer layer1,layer2;
		
		layer1 = getCurrentLayer();
		layer2 = (Layer)layers.get(currentLayer+1);
		
		layers.set(currentLayer,   layer2);
		layers.set(currentLayer+1, layer1);
		
		currentLayer +=1;
	}

	public void invalidateCompoLayer(){
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++){
				
				renewalCompoLayer(x,y);
			}
		}
	}
	
	public boolean renewalCompoLayer(int x,int y){
		
		if(!compoLayer.setDotWithBMP(x, y, Color.argb(0,0,0,0))) return false;
		
		for(int i=0; i<layers.size(); i++){
			
			Layer layer=(Layer)layers.get(i);
			if(layer.isActivated){
				
				layer.dots.compoDotWithBMP(compoLayer, x, y);
			}
		}
		compoLayer.setBitmapPixel(x, y);
		
		return true;
	}
	
	public void invalidateCompoLayerWithPrepared(){
		
		compoLayer.copy(preparedCompoLayer1);
		getCurrentLayer().compo(compoLayer);
	    preparedCompoLayer2.compo(compoLayer);
	}
	
	public void setupPreparedCompoLayers(){
		
		makePreparedCompoLayer(preparedCompoLayer1,0,currentLayer-1);
		makePreparedCompoLayer(preparedCompoLayer2,currentLayer+1,layers.size()-1);
	}
	
	private void makePreparedCompoLayer
				(Layer preCompoLayer,int startLayer,int endLayer){
		
		preCompoLayer.initialLayerDots();
		
		for(int i=startLayer; i<=endLayer; i++){

			Layer layer = (Layer)layers.get(i); 
			if(!layer.isActivated) continue;
			
			layer.compo(preCompoLayer);
		}
	}

	public void createMergeLayer(){
		
		mergeLayer.initialLayerDots();
		
		boolean[] removeFlag = new boolean[layers.size()];
		
		int startMargeLayer = -1;
		
		for(int i=0; i<layers.size(); i++){

			Layer layer = (Layer)layers.get(i); 
			if(!layer.isActivated) continue;
			
			layer.compo(mergeLayer);
			if(startMargeLayer==-1) startMargeLayer = i;
			else removeFlag[i] = true;
		}
		
		if(startMargeLayer==-1) return;
		
		currentLayer = startMargeLayer;
		getCurrentLayer().copy(mergeLayer);
		
		for(int i=layers.size()-1; i>=0; i--){
			if(removeFlag[i] == true){
				layers.remove(i);
			}
		}
		setCurrentLayer(currentLayer);
	}
	
	public void renewalAllLayersAlpha(){
		
		for(int i=0; i<layers.size(); i++){
			
			Layer layer=(Layer)layers.get(i);
			layer.renewalAlpha();
		}
	}
	
	public void invalidateAllLayersBitmap(){
		
		for(int i=0; i<layers.size(); i++){
			
			Layer layer=(Layer)layers.get(i);
			layer.invalidateBitmap();
		}
	}
	
	public byte[] getCompoLayerBitMapData(){
		
		Bitmap bitmap = compoLayer.bitmap;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int widthBytes = bitmap.getRowBytes();
		int pixelBytes = height * widthBytes;
		int intSize = Integer.SIZE/8;
		
		ByteBuffer pixels = ByteBuffer.allocate(pixelBytes);
		ByteBuffer data = ByteBuffer.allocate(intSize * 3 + pixelBytes);
		
		pixels.clear(); 
		bitmap.copyPixelsToBuffer(pixels);
		pixels.rewind();
		
		data.clear();
		data.putInt(width); data.putInt(height); data.putInt(widthBytes);
		data.put(pixels);
		
		return data.array();
	}

	public class Layer {
		
		int gridX,gridY;
		Dots dots;
		public Bitmap bitmap;
		LayerGroup parentGroup;
		
		boolean isActivated;
		int alpha=255;
		
		public Layer(LayerGroup parentGroup){
			
			this.parentGroup=parentGroup;
			gridX=parentGroup.gridX;
			gridY=parentGroup.gridY;
			dots=new Dots(this,gridX,gridY);
			isActivated=true;
			
			createBitmap();
		}
	
		private void createBitmap() {
			
			bitmap = Bitmap.createBitmap(gridX, gridY, Bitmap.Config.ARGB_8888);
		}
	
		public void initialLayerDots(){
			
			dots.initialDots();
		}
		
		public boolean setDot(int x, int y, int color){
			
			if(!dots.setColor(x, y, color)) return false;
				
			return true;
		}
		
		public boolean setDot(int x, int y, int[] argb){
			
			if(!dots.setARGB(x, y, argb)) return false;
			
			return true;
		}
		
		public boolean setDotWithEnabledAlpha(int x, int y, int color){
			
			if(!dots.setColorWithEnabledAlpha(x, y, color)) return false;
				
			return true;
		}
		
		public boolean setDotWithEnabledAlpha(int x, int y, int[] argb){
			
			if(!dots.setARGBWithEnabledAlpha(x, y, argb)) return false;
			
			return true;
		}
		
		public boolean setDotWithBMP(int x, int y, int color){
			
			if(!dots.setColor(x, y, color)) return false;
			
			setBitmapPixel(x,y);
			
			return true;
		}

		public boolean setDotWithBMP(int x, int y, int[] argb){
			
			if(!dots.setARGB(x, y, argb)) return false;
			
			setBitmapPixel(x,y);
			
			return true;
		}
		
		public void setBitmapPixel(int x,int y){
			
			bitmap.setPixel(x, y, dots.getColor(x,y));
		}
		
		public void invalidateBitmap(){
			
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++)
					
					setBitmapPixel(x,y);
			}
		}
		
		public void setRangeDots(Dots srcDots, int left, int top){
			
			for(int x=0; x<srcDots.gridX; x++){
				for(int y=0; y<srcDots.gridY; y++)
					
					if (!srcDots.isMasked(x, y))
						setDot(left+x,top+y,srcDots.getColor(x, y));
			}
		}
		
		public void setRangeDotsEnabledTransparent(Dots srcDots, int left, int top){
			
			for(int x=0; x<srcDots.gridX; x++){
				for(int y=0; y<srcDots.gridY; y++){
					
					int[] argb = srcDots.getARGB(x, y);
					if(argb[0]!=0)
						if (!srcDots.isMasked(x, y))
							setDotWithEnabledAlpha(left+x,top+y,argb);
				}
			}
		}
		
		public void setRangeDotsIgnoreTransparentArea(Dots srcDots, int left, int top){
			
			for(int x=0; x<srcDots.gridX; x++){
				for(int y=0; y<srcDots.gridY; y++){
					
					int[] argb = dots.getARGB(left+x, top+y);
					if(argb == null) continue;
					if(argb[0]!=0)
						if (!srcDots.isMasked(x, y))
							setDot(left+x,top+y,srcDots.getColor(x, y));
				}
			}
		}

		public void setRangeDotsEnableTransAndIgnoreTrans(Dots srcDots, int left, int top){
			
			for(int x=0; x<srcDots.gridX; x++){
				for(int y=0; y<srcDots.gridY; y++){
					
					int[] argb = dots.getARGB(left+x, top+y);
					if(argb == null) continue;
					
					int[] argb2 = srcDots.getARGB(x, y);
					
					if(argb[0]!=0 && argb2[0]!=0)
						if (!srcDots.isMasked(x, y))
							setDotWithEnabledAlpha(left+x,top+y,srcDots.getColor(x, y));
				}
			}
		}
		
		public void renewalAlpha(){
			
			dots.renewalAlpha(alpha);
		}
		
		public void requestRenewalCompoLayer(int x,int y){
			
			parentGroup.renewalCompoLayer(x, y);
		}
		
		public void requestInvalidateCompoLayer(){
			
			parentGroup.invalidateCompoLayer();
		}
		
		public void requestInvalidateCompoLayerWithPrepared(){
			
			parentGroup.invalidateCompoLayerWithPrepared();
		}
		
		public void requestSetupPreparedCompoLayers(){
			
			parentGroup.setupPreparedCompoLayers();
		}
		
		public void copy(Layer srcLayer){
			
			parentGroup = srcLayer.parentGroup;
			gridX=parentGroup.gridX;
			gridY=parentGroup.gridY;
			alpha = srcLayer.alpha;
			isActivated = srcLayer.isActivated;
			dots.copy(srcLayer.dots);
			renewalAlpha();
			invalidateBitmap();
		}
		
		public void compo(Layer layer){

			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++){

					dots.compoDotWithBMP(layer, x, y);	
				}
			}
		}
		
	}
}
