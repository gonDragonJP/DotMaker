package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.View;

public class RangeMenuDialog extends Dialog implements View.OnClickListener{

	Context context;
	MainGrid parentGrid;
	SelectedArea selectedArea;
	GradationDialog gradationDialog;
	ReplaceColorDialog replaceColorDialog;
	
	LinearLayout root,premenu,postmenu,console;
	
	boolean isDonePreMenu = false;
	
	static final int
		COPY = 0,
		CUT = 1,
		FILL_BOX=2,
		GRADATION=3,
		REPLACE_COLOR=4;
	int selectedPreMenu = COPY;
	

	CheckBox checkVMirror,checkHMirror,checkTransPaste,checkIgnoreTrans;
	boolean isVMirrorChecked;
	boolean isHMirrorChecked;
	boolean isTransPasteChecked;
	boolean isIgnoreTransChecked;
	
	int dstX,dstY;
	Rect selectedRange = new Rect();
	
	public RangeMenuDialog(Context context,MainGrid parentGrid) {
		super(context);
		
		this.context = context;
		this.parentGrid = parentGrid;
		
		selectedArea = new SelectedArea();
		gradationDialog = new GradationDialog(context,this);
		replaceColorDialog = new ReplaceColorDialog(context,this);
		
		setTitle("Range Operation");
		setContentView(R.layout.rangemenudialog);
		
		root=(LinearLayout)findViewById(R.id.rangemenudialog_root);
		premenu=(LinearLayout)findViewById(R.id.rangemenudialog_console1);
		postmenu=(LinearLayout)findViewById(R.id.rangemenudialog_console2);
		console=(LinearLayout)findViewById(R.id.rangemenudialog_console3);
		
		setPreMenu();
		setPostMenu();
		setButton();
		
		root.removeView(postmenu);
	}
	
	public void setRange(LayerGroup.Layer layer,Rect range){
		
		selectedArea.setRange(layer,range);
		selectedRange.set(range);
	}

	public void setDstPosition(int left,int top){
		
		dstX = left;	dstY = top;
	}
	
	public void renewalMovingRangeLayer(LayerGroup.Layer newLayer){
		
		selectedArea.renewalMovingRangeLayer(newLayer);
		makeCompoLayerWithMovingRange();
	}

	public void makeCompoLayerWithMovingRange(){
	
		selectedArea.resetMovingRangeLayerDots();
		
		selectedArea.pasteRangeDotsToMovingRangeLayer
			(dstX, dstY, isTransPasteChecked,isIgnoreTransChecked);
		
		selectedArea.selectedLayer.requestInvalidateCompoLayerWithPrepared();
	}

	private void doPreMenu(){
		
		int gridX,gridY;
		
		switch(selectedPreMenu){
		
		case COPY: 
			selectedArea.preCopyProcess();
			break;
			
		case CUT: 
			selectedArea.preCutProcess();
			break;
			
		case FILL_BOX: 
			selectedArea.preFillBoxProcess();
			requestOpenColorDialogToGrid(1);
			break;
			
		case GRADATION:
			selectedArea.preGradationProcess();
			gridX = selectedArea.selectedDots.gridX;
			gridY = selectedArea.selectedDots.gridY;
			gradationDialog.renewalSampleDots(gridX,gridY);
			gradationDialog.show();
			break;
			
		case REPLACE_COLOR:
			selectedArea.preReplaceColorProcess();
			replaceColorDialog.renewalSampleDots(selectedArea.selectedDots);
			replaceColorDialog.show();
			break;
		}
		
		if(isVMirrorChecked==true)
			selectedArea.verticalMirroringSelectedDots();
		if(isHMirrorChecked==true)
			selectedArea.horizontalMirroringSelectedDots();
	}
	
	public void requestOpenColorDialogToGrid(int finalCallProcess){
		
		parentGrid.onColorDialog(finalCallProcess);
	}

	public void setFillBoxDotsToSelectedDots(int color){
		
		selectedArea.setFillBoxDots(color);
		selectedArea.setDotsToSelectedDots(selectedArea.fillBoxDots);
	}
	
	public void setGradationDotsToSelectedDots(){
		
		selectedArea.setDotsToSelectedDots
						(gradationDialog.getDotsOfGradation());
	}
	
	public void setReplaceColorDotsToSelectedDots(){
		
		selectedArea.setDotsToSelectedDots
						(replaceColorDialog.getDotsOfReplaceColor());
	}
	
	public void setGradationStartColor(int color){
		
		gradationDialog.renewalStartColor(color);
	}
	
	public void setGradationEndColor(int color){
		
		gradationDialog.renewalEndColor(color);
	}
	
	public void setReplaceSrcColor(int color){
		
		replaceColorDialog.renewalSrcColor(color);
	}
	
	public void setReplaceDstColor(int color){
		
		replaceColorDialog.renewalDstColor(color);
	}
	
	
	public void expandSelectedDots(int x,int y){
		
		selectedArea.expandSelectedDots(x, y);
	}
	
	public void rotateSelectedDots(Rect requiredRotateRange,int rotateAngle){
		
		boolean isInterpolated = parentGrid.settingDialog.isInterpolatedRotation;
		selectedArea.rotateSelectedDots(requiredRotateRange, rotateAngle, isInterpolated);
	}
	
	private void doPostMenu(){
		
		makeCompoLayerWithMovingRange();
		selectedArea.movingRangeLayer.invalidateBitmap();
	}
	
	public void onBackPressed(){
		
	}
	
	public void show(){
		super.show();
		
		if(!isDonePreMenu){
			checkVMirror.setChecked(false);
			checkHMirror.setChecked(false);
			checkTransPaste.setChecked(false);
			checkIgnoreTrans.setChecked(false);
			if(selectedPreMenu != REPLACE_COLOR) checkTransPaste.setChecked(true);
		}
	}
	
	public void onClick(View v) {
		
		int id = v.getId();
		
		switch(id){
		
		case R.id.rangemenudialog_ok:
			
			if(!isDonePreMenu){
				
				doPreMenu();
				isDonePreMenu = true;
			}
			else{
				doPostMenu();
				isDonePreMenu = false;
				parentGrid.endRangeSelect();
			}
			setConsoleAlignment();
			hide(); break;
			
		case R.id.rangemenudialog_cancel:
			
			parentGrid.cancelRangeSelect();
			cancelRangeSelect();
			hide(); break;
			
		}
	}
	
	private void setConsoleAlignment(){
		
		if(isDonePreMenu){
			
			root.removeView(premenu);
			root.addView(postmenu);
		}
		else{
			
			root.removeView(postmenu);
			root.addView(premenu);
		}
		root.removeView(console);
		root.addView(console);
	}
	
	public void cancelRangeSelect(){
		
		if(isDonePreMenu){
			
			isDonePreMenu = false;
			setConsoleAlignment();
			
			selectedArea.resetMovingRangeLayerDots();
			if(selectedPreMenu==CUT)
				selectedArea.resetCutDotsToSelectedLayer();
			
			parentGrid.rangeRect.set(selectedRange);
		}
		parentGrid.layerGroup.invalidateCompoLayer();
	}

	private void setPreMenu(){
		
		checkVMirror = (CheckBox)findViewById(R.id.rangemenudialog_checkvmirror);
		checkHMirror = (CheckBox)findViewById(R.id.rangemenudialog_checkhmirror);
		checkTransPaste = (CheckBox)findViewById(R.id.rangemenudialog_checktranscolor);
		checkIgnoreTrans = (CheckBox)findViewById(R.id.rangemenudialog_checkignoretrans);
		
		checkVMirror.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isVMirrorChecked = arg1;
			}
		});
		checkHMirror.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isHMirrorChecked = arg1;
			}
		});
		checkTransPaste.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isTransPasteChecked = arg1;
			}
		});
		checkIgnoreTrans.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				
				isIgnoreTransChecked = arg1;
			}
		});
		
		RadioGroup preRadioGroup=(RadioGroup)findViewById(R.id.rangemenudialog_radiogroup1);
		
		preRadioGroup.check(R.id.rangemenudialog_radiocopy);
		
		preRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				checkVMirror.setEnabled(true);
				checkHMirror.setEnabled(true);
				checkTransPaste.setEnabled(true);
				checkIgnoreTrans.setEnabled(true);
				
				checkVMirror.setChecked(false);
				checkHMirror.setChecked(false);
				checkTransPaste.setChecked(true);
				checkIgnoreTrans.setChecked(false);
				
				switch(checkedId){
				
				case R.id.rangemenudialog_radiocopy:
					selectedPreMenu = COPY; 
					break;
				case R.id.rangemenudialog_radiocut:
					selectedPreMenu = CUT; 
					break;
				case R.id.rangemenudialog_radiofillbox:
					selectedPreMenu = FILL_BOX; 
					checkVMirror.setEnabled(false);
					checkHMirror.setEnabled(false);
					break;
				case R.id.rangemenudialog_radiogradation:
					selectedPreMenu = GRADATION; 
					break;
				case R.id.rangemenudialog_radioreplace:
					selectedPreMenu=REPLACE_COLOR;
					checkVMirror.setEnabled(false);
					checkHMirror.setEnabled(false);
					checkTransPaste.setEnabled(false);
					checkIgnoreTrans.setEnabled(false);
					checkTransPaste.setChecked(false);
					break;
				}
			}
		});
	}
	
	private void setPostMenu(){
		
		RadioButton radio_paste = (RadioButton)findViewById(R.id.rangemenudialog_radiopaste);
		radio_paste.setChecked(true);
	}

	private void setButton(){
		
		Button ok = (Button)findViewById(R.id.rangemenudialog_ok);
		Button cancel = (Button)findViewById(R.id.rangemenudialog_cancel);
		
		ok.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

}
