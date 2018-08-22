package takada.dotmaker;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UndoDialog extends Dialog implements View.OnClickListener{

	private Context context;
	private LayerDialog parentDialog;

	private class LayerMemory{
		LayerGroup.Layer layer;
		int layerNumber;
		boolean isLayerChanger=false;
		
		public LayerMemory(LayerGroup layerGroup, int layerNumber){
			
			layer = layerGroup.getLayerClone(layerNumber);
			this.layerNumber = layerNumber;
		}
	}
	private List<LayerMemory> memoryList;
	private int currentMemoryIndex=0;
	
	private int selectedMenu;
	private boolean hasDrawableStep;
	
	static final int
	MENU_UNDO = 0,
	MENU_REDO = 1;
	
	static final int
	UNDO_SIZE = 20;
	
	public UndoDialog(Context context, LayerDialog layerDialog) {
		super(context);
		
		this.context = context;
		this.parentDialog = layerDialog;
		
		memoryList = new ArrayList<LayerMemory>();
		
		setTitle("Undo & Redo");
		setContentView(R.layout.undodialog);
		setMenu();
		setButton();
	}
	
	public void setupLayerMemory(){
		
		memoryList.clear();
		currentMemoryIndex = 0;
		addLayerMemory();
	}
	
	public void inclementMemory(){
		
		currentMemoryIndex++;
		killOverMemory();
		addLayerMemory();
	}
	
	private void killOverMemory(){//記憶途中部からの描画にはそれ以降の記憶が不要となる
		
		int nowLayer = parentDialog.layerGroup.currentLayer;
		int preMemoryLayer = memoryList.get(currentMemoryIndex-1).layerNumber;
		int surviveMemory = -1;
		
		if (nowLayer!=preMemoryLayer){
			
			for(int i=currentMemoryIndex; i<memoryList.size(); i++){
				LayerMemory lm = memoryList.get(i);
				
				if(lm.layerNumber == nowLayer && lm.isLayerChanger == true){
					
					surviveMemory = i;
				}
			}
		}//描画中レイヤーの最も古いメモリーだけ残しておく
		
		for(int i = memoryList.size()-1; i>=currentMemoryIndex; i--){
			if(i!= surviveMemory) memoryList.remove(i);
		}
		
		if(surviveMemory != -1) currentMemoryIndex++;
	}

	private void addLayerMemory(){
		
		LayerGroup layerGroup = parentDialog.layerGroup;
		int layerNumber = layerGroup.currentLayer;
		memoryList.add(new LayerMemory(layerGroup, layerNumber));
		
		int nowPos = memoryList.size()-1;
		int prePos = nowPos-1;	
		if(nowPos>0){
			int preLayer = memoryList.get(prePos).layerNumber;
			if(preLayer != layerNumber)
				memoryList.get(nowPos).isLayerChanger = true;
		}
		
		if(memoryList.size()>UNDO_SIZE){
			memoryList.remove(0);
			currentMemoryIndex--;
		}
	}
	
	public void undo(){
		
		if(currentMemoryIndex==0) return;
		
		do{
		currentMemoryIndex--;
		setLayerMemory(currentMemoryIndex);
		
		if(currentMemoryIndex == 0) break;
		}while(memoryList.get(currentMemoryIndex).isLayerChanger == true);
	}
	
	public void redo(){
		
		if(currentMemoryIndex==memoryList.size()-1) return;
		
		do{
		currentMemoryIndex++;
		setLayerMemory(currentMemoryIndex);
		
		if(currentMemoryIndex == memoryList.size()-1) break;
		}while(memoryList.get(currentMemoryIndex).isLayerChanger == true);
	}
	
	public void setLayerMemory(int memoryIndex){
		
		LayerMemory memory = memoryList.get(memoryIndex);
		parentDialog.layerGroup.setLayerClone(memory.layer, memory.layerNumber);
	}
	
	private void dumpLog(){
		
		for(int i=0; i<memoryList.size(); i++){
			
			int layer = memoryList.get(i).layerNumber;
			boolean layerchanger = memoryList.get(i).isLayerChanger;
			String ck;
			
			if(i==currentMemoryIndex) ck=" <-"; else ck="";
			android.util.Log.e("","id:"+i+"  layer:"+layer+"  LC:"+layerchanger+ck);
		}
	}
	
	public boolean hasUndoMemory(){
		
		return (currentMemoryIndex>0);
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		switch(id){
		
		case R.id.undodialog_buttonok:
			
			switch(selectedMenu){
			
			case MENU_UNDO:
				undo();
				break;
				
			case MENU_REDO:
				redo();
				break;
			}
			hide();
			
		case R.id.undodialog_buttoncancel:
			
			hide(); 
			break;
		}
	}
	
	private void setMenu(){
		
		RadioGroup radioGroup=(RadioGroup)findViewById(R.id.undodialod_radiogroup);
		radioGroup.check(R.id.undodialog_radioundo);
		
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch(checkedId){
				
				case R.id.undodialog_radioundo: selectedMenu = MENU_UNDO; break;
				case R.id.undodialog_radioredo: selectedMenu = MENU_REDO; break;
				}
			}
		});
	}
	
	private void setButton(){
		
		Button ok = (Button)findViewById(R.id.undodialog_buttonok);
		Button cancel = (Button)findViewById(R.id.undodialog_buttoncancel);
		
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
}
