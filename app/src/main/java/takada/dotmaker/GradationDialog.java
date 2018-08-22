package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GradationDialog extends Dialog implements View.OnClickListener{
	
	RangeMenuDialog parentDialog;
	
	LinearLayout mainLayout,viewLayout,consoleLayout;
	static final int
	FP = LinearLayout.LayoutParams.FILL_PARENT,
	WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	GradationView mainView;
	TextView[] textView = new TextView[2];
	SeekBar[] seekBar = new SeekBar[2];
	Button[] button = new Button[3];
	
	public GradationDialog(Context context,RangeMenuDialog parentDialog) {
		super(context);
		
		this.parentDialog = parentDialog;
		
		setTitle("Gradation");
		
		setMainLayout(context);
		setContentView(mainLayout);
	}
	
	public Dots getDotsOfGradation(){
		
		return mainView.getDotsOfGradation();
	}
	
	public void renewalSampleDots(int gridX, int gridY){
		
		mainView.renewalSampleDots(gridX, gridY);
	}
	
	public void renewalStartColor(int color){
	
		mainView.renewalStartColor(color);
	}

	public void renewalEndColor(int color){
		
		mainView.renewalEndColor(color);
	}
	
	public void onClick(View v) {
		
		if(v==button[0]) { parentDialog.requestOpenColorDialogToGrid(2); hide();}
		if(v==button[1]) { parentDialog.requestOpenColorDialogToGrid(3); hide();}
		if(v==button[2]) { 
			parentDialog.setGradationDotsToSelectedDots(); 
			parentDialog.makeCompoLayerWithMovingRange();
			hide();
		}
	}
	
	public void onBackPressed(){
		
	}

	private void setMainLayout(Context context){
		
		mainLayout = new LinearLayout(context);
		mainLayout.setLayoutParams(getParam(FP,WC));
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		
		setViewLayout(context);
		setConsoleLayout(context);
		
		mainLayout.addView(viewLayout);
		mainLayout.addView(consoleLayout);
	}
	
	private void setViewLayout(Context context){
		
		viewLayout = new LinearLayout(context);
		LinearLayout.LayoutParams param = getParam(FP,0);
		param.weight = 1;
		viewLayout.setLayoutParams(param);
		
		mainView = new GradationView(context);
		viewLayout.addView(mainView);
	}
	
	private void setConsoleLayout(Context context){
		
		consoleLayout = new LinearLayout(context);
		consoleLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams param = getParam(FP,WC);
		consoleLayout.setLayoutParams(param);
		
		for(int i=0; i<2; i++){
			
			textView[i] = new TextView(context);
			setTextLayoutParam(i);
			consoleLayout.addView(textView[i]);
			
			seekBar[i] = new SeekBar(context);	
			seekBar[i].setThumbOffset(9);
			setBarLayoutParam(i);
			consoleLayout.addView(seekBar[i]);
		}
		
		textView[0].setText("Curvature");
		textView[1].setText("Direction");

		seekBar[0].setMax(100);
		seekBar[0].setProgress(0);
		setDistanceBarListener();
		
		seekBar[1].setMax(360);
		seekBar[1].setProgress(0);
		setDirectionBarListener();
		
		LinearLayout buttonLayout = new LinearLayout(context);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		param = getParam(WC,WC);
		param.gravity = Gravity.CENTER;
		buttonLayout.setLayoutParams(param);
		
		for(int i=0; i<3; i++){
			button[i] = new Button(context);
			setButtonLayoutParam(i);
			button[i].setOnClickListener(this);
			buttonLayout.addView(button[i]);
		}
		button[0].setText("Start Color");
		button[1].setText("End Color");
		button[2].setText("OK");
		
		consoleLayout.addView(buttonLayout);
	}
	
	private void setTextLayoutParam(int i){
		
		LinearLayout.LayoutParams param = getParam(WC,WC);
		param.gravity = Gravity.CENTER;
		textView[i].setLayoutParams(param);
	}
	private void setBarLayoutParam(int i){
		
		LinearLayout.LayoutParams param = getParam(FP,WC);
		param.setMargins(50, 10, 50, 10);
		seekBar[i].setLayoutParams(param);
	}
	
	private void setButtonLayoutParam(int i){
		
		LinearLayout.LayoutParams param = getParam(WC,WC);
		param.setMargins(10, 10, 10, 10);
		button[i].setLayoutParams(param);
	}
	
	private void setDistanceBarListener(){
		
		seekBar[0].setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			       
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			         
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			}
			          
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				double a = seekBar.getProgress();
				mainView.setDistance((int)Math.pow(a,a*0.012+1));
			}            
		});
	}
	
	private void setDirectionBarListener(){
		
		seekBar[1].setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			          
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			           
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
			}
			         
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				mainView.setDirection(seekBar.getProgress());
			}            
		});
	}
	
	private LinearLayout.LayoutParams getParam(int arg0,int arg1){
		
		return new LinearLayout.LayoutParams(arg0, arg1);
	}
}
