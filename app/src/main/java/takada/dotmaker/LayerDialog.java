package takada.dotmaker;

import java.util.List;
import takada.dotmaker.LayerGroup.Layer;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class LayerDialog extends Dialog
		implements AdapterView.OnItemClickListener, View.OnClickListener{
	
	Context context;
	
	SettingDialog settingDialog;
	MainView mainView;
	
	public UndoDialog undoDialog;
	
	int gridX,gridY;
	LayerGroup layerGroup;
	private boolean isConsoleAccessLimited = false;
	
	private ListView listView;
	private Button button[]=new Button[7];
	private static CheckBox check;
	private static ImageView image;
	private static TextView text;
	private static SeekBar bar;
	private final int bar_min=80;
	
	private static final int
		MAIN_CONSOLE = 0,
		SUB_CONSOLE = 1;
	
	private int consoleMode = MAIN_CONSOLE;
	private LinearLayout root,mainConsole,subConsole;
	
	public LayerDialog(Context context,SettingDialog settingDialog) {
		super(context);
		
		undoDialog = new UndoDialog(context, this);
		
		this.context=context;
		this.settingDialog=settingDialog;
		this.setTitle("Layer");
		
		createLayerGroup();
		
		setContentView(R.layout.layermainlayout);
		setButton();
		setListView();
		setStartLayout();
	}
	
	public void setMainView(MainView mainView){
		
		this.mainView=mainView;
	}
	
	public void setConsoleAccessLimit(boolean isLimited){
		
		this.isConsoleAccessLimited = isLimited;
		
		for(int i=0; i<7; i++){
			button[i].setEnabled(!isConsoleAccessLimited);
		}
		
		button[0].setEnabled(true);
		button[2].setEnabled(true);
	}

	public boolean hasUndoMemory(){
		
		return undoDialog.hasUndoMemory();
	}
	
	public void renewalLayerGroup(){
		
		createLayerGroup();
		refreshToChangeLayerGroup();
	}

	public LayerGroup createLayerGroup(){
		
		layerGroup=null;
		gridX=settingDialog.settingGridX;
		gridY=settingDialog.settingGridY;
		layerGroup=new LayerGroup(gridX,gridY);
		
		return layerGroup;
	}
	
	public void addLayer(int num){
		
		layerGroup.addLayer(num);
	}
	
	public void refreshToChangeLayerGroup(){
		
		layerGroup.invalidateCompoLayer();
		layerGroup.renewalAllLayersAlpha();
		layerGroup.invalidateAllLayersBitmap();
		
		setListView();
		mainView.renewalLayerGroup(layerGroup);
	}
	
	public void showDialog(){
		
		layerGroup.invalidateAllLayersBitmap();
		listView.invalidateViews();
		this.show();
	}
	
	private void hideDialog(){
		
		layerGroup.invalidateCompoLayer();
		this.hide();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	
		setConsole(SUB_CONSOLE);
		
		layerGroup.setCurrentLayer(position);
		mainView.renewalCurrentLayer();
		listView.invalidateViews();
		undoDialog.inclementMemory();
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		
		case R.id.layermainlayout_button1:
			
			layerGroup.addLayer(1);
			listView.invalidateViews();
			break;
			
		case R.id.layermainlayout_button2:
	
			layerGroup.createMergeLayer();
			mainView.renewalCurrentLayer();
			listView.invalidateViews();
			undoDialog.setupLayerMemory();
			break;
			
		case R.id.layermainlayout_button3:
			
			setConsole(MAIN_CONSOLE);
			hideDialog();
			break;
			
		case R.id.layermainlayout_button4:
			
			layerGroup.exchangeNextLayer();
			mainView.renewalCurrentLayer();
			listView.invalidateViews();
			undoDialog.setupLayerMemory();
			break;
			
		case R.id.layermainlayout_button5:
			
			layerGroup.removeCurrentLayer();
			mainView.renewalCurrentLayer();
			listView.invalidateViews();
			undoDialog.setupLayerMemory();
			break;
			
		case R.id.layermainlayout_button6:
			
			layerGroup.copyLayer();
			break;
			
		case R.id.layermainlayout_button7:
	
			layerGroup.pasteLayer();
			listView.invalidateViews();
			undoDialog.inclementMemory();
			break;
		}
	}
	
	@Override
	public void onBackPressed(){
    	
		switch(consoleMode){
		
		case MAIN_CONSOLE:
			hideDialog();
			break;
			
		case SUB_CONSOLE:
			setConsole(MAIN_CONSOLE);
			break;
		}
    }

	private void setConsole(int mode){
		
		if (mode == consoleMode) return;
		
		switch(mode){
		
		case MAIN_CONSOLE:
			root.removeView(subConsole);
			root.addView(mainConsole);
			break;
		case SUB_CONSOLE:
			root.removeView(mainConsole);
			root.addView(subConsole);
			break;
		}
		
		consoleMode = mode;
	}
	
	private void setStartLayout(){
		
		root  = (LinearLayout)this.findViewById(R.id.layermainlayout_root);
		mainConsole  = (LinearLayout)this.findViewById(R.id.layermainlayout_console);
		subConsole  = (LinearLayout)this.findViewById(R.id.layermainlayout_subconsole);
		
		root.removeView(subConsole);
	}
	
	private void setListView(){
		
		LayerViewAdapter adapter = 
			new LayerViewAdapter(context,layerGroup.layers);

		listView = (ListView)this.findViewById(R.id.layermainlayout_listview);
		listView.setOnItemClickListener(this);
		listView.setAdapter(adapter);	
	}
	
	private void setButton(){
		
		button[0]= (Button)this.findViewById(R.id.layermainlayout_button1);
		button[1]= (Button)this.findViewById(R.id.layermainlayout_button2);
		button[2]= (Button)this.findViewById(R.id.layermainlayout_button3);
		button[3]= (Button)this.findViewById(R.id.layermainlayout_button4);
		button[4]= (Button)this.findViewById(R.id.layermainlayout_button5);
		button[5]= (Button)this.findViewById(R.id.layermainlayout_button6);
		button[6]= (Button)this.findViewById(R.id.layermainlayout_button7);
		
		for(Button bt : button){
			
			bt.setOnClickListener(this);
		}
	}
	
	public class LayerViewAdapter extends BaseAdapter{
		
		List<Layer> layers;
		
		public LayerViewAdapter
			(Context context,List<Layer> layers){
		
			this.layers=layers;
		}
		
		public int getCount() {
			
			return layers.size();
		}
		
		public Object getItem(int position) {
			
			return layers.get(position);
		}
		
		public long getItemId(int position) {
			
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LayerGroup.Layer layer = (LayerGroup.Layer)getItem(position);
			
			if(convertView==null){
				
				LayoutInflater inflater = LayoutInflater.from(context);
				convertView = inflater.inflate(R.layout.layerviewunit_row,null);
			}
			
			check = (CheckBox)convertView.findViewById(R.id.layerviewunit_check);
			image = (ImageView)convertView.findViewById(R.id.layerviewunit_image);
			text = (TextView)convertView.findViewById(R.id.layerviewunit_text);
			bar = (SeekBar)convertView.findViewById(R.id.layerviewunit_bar);
		
			final int pos=position;
			
			setCheckListener(pos);	
			bar.setThumbOffset(9);
			bar.setMax(255-bar_min);
			bar.setProgress(layer.alpha-bar_min);
			setBarListener(pos);
			bar.setEnabled(!isConsoleAccessLimited);
			
			check.setChecked(layer.isActivated);
			check.setEnabled(!isConsoleAccessLimited);
			image.setImageBitmap(layer.bitmap);
			
			if (position==layerGroup.currentLayer)
				text.setTextColor(Color.RED);
			else text.setTextColor(Color.WHITE);
			text.setText("Layer"+position);
			
			return convertView;
		}
		
		private void setCheckListener(final int pos){
			
			check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				LayerGroup.Layer layer = (LayerGroup.Layer)getItem(pos);
				if(pos != layerGroup.currentLayer)
					layer.isActivated = isChecked;
				
				listView.invalidateViews();
				}
			});
		}
		private void setBarListener(final int pos){
			
			bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
				          
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				         
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
				}
				         
				public void onStopTrackingTouch(SeekBar seekBar) {
					
					if(isConsoleAccessLimited) return;
					
					LayerGroup.Layer layer = (LayerGroup.Layer)getItem(pos);
					layer.alpha = seekBar.getProgress() + bar_min;
					layer.renewalAlpha();
					layer.invalidateBitmap();
					listView.invalidateViews();
				}            
			});
		}
	}
	
}
