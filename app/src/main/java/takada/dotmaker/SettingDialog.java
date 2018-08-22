package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingDialog extends Dialog 
					implements View.OnClickListener{
	
	Context context;
	
	Pens pen = new Pens();
	
	int settingGridX=64,settingGridY=64;
	
	boolean isGridLineVisible = true;
	boolean isIndicatorActive = true;
	boolean isInterpolatedRotation = false;
	
	private int[] penImageId = new int [pen.maxPenNumber];
	private ImageView[] penImage = new ImageView[pen.maxPenNumber];
	
	public SettingDialog(Context context) {
		super(context);
		
		this.context = context;
		
		setTitle("Setting");
		
		setContentView(R.layout.settingdialog);
		
		setPictureSizeSpinner(context);
		setPenSelectScrollView(context);
		setGridLineCheckBox();
		setIndicatorCheckBox();
		setInterpolatedRotCheckBox();
	}
	
	private void setPictureSizeSpinner(Context context){
		
		Spinner sizeXSpinner = (Spinner)findViewById(R.id.settingdialog_sizexspinner);
		Spinner sizeYSpinner = (Spinner)findViewById(R.id.settingdialog_sizeyspinner);
	
		String[] value = {"16","20","24","28","32","40","48","56","64"};
		ArrayAdapter<String> adapter 
			= new ArrayAdapter<String>(context,R.layout.picturesizespinner_row,value);
		sizeXSpinner.setAdapter(adapter);
		sizeYSpinner.setAdapter(adapter);
		
		sizeXSpinner.setEnabled(false);
	}
	
	public void setPenSelectScrollView(Context context){
		
		LinearLayout penLayout 
		= (LinearLayout)findViewById(R.id.settingdialog_penlayout);
		
		for(int i=0; i<pen.maxPenNumber; i++){
			penImage[i] = new ImageView(context);
			penImage[i].setImageBitmap(pen.getPenBitmap(i));
		
			penLayout.addView(penImage[i]);
			penImage[i].setOnClickListener(this);
			penImageId[i] = i;
			penImage[i].setId(i); 
		}
		renewalPenImageAlpha();
	}
	
	private void renewalPenImageAlpha(){
		
		for(int i=0; i<pen.maxPenNumber; i++){
			if(i==pen.selectedPen)
				penImage[i].setAlpha(255);
			else 
				penImage[i].setAlpha(128);
		}
	}
	
	private void setGridLineCheckBox(){
		
		CheckBox check = (CheckBox)findViewById(R.id.settingdialog_gridlinecheck);
		check.setChecked(isGridLineVisible);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				isGridLineVisible = isChecked;
			}
		});
	}
	
	private void setIndicatorCheckBox(){
		
		CheckBox check = (CheckBox)findViewById(R.id.settingdialog_indicatorcheck);
		check.setChecked(isIndicatorActive);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				isIndicatorActive = isChecked;
			}
		});
	}
	
	private void setInterpolatedRotCheckBox(){
		
		CheckBox check = (CheckBox)findViewById(R.id.settingdialog_interpolatedrotcheck);
		check.setChecked(isInterpolatedRotation);
		check.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				isInterpolatedRotation = isChecked;
			}
		});
	}

	@Override
	public void onClick(View v) {
		
		for(int i=0; i<pen.maxPenNumber; i++){
		
			if(penImageId[i] == v.getId()){
				pen.selectedPen = i;
				renewalPenImageAlpha();
			}
		}
	}

}
