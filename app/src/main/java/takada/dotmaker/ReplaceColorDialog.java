package takada.dotmaker;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ReplaceColorDialog extends Dialog implements View.OnClickListener{
	
	RangeMenuDialog parentDialog;
	
	LinearLayout mainLayout,viewLayout,consoleLayout;
	static final int
	FP = LinearLayout.LayoutParams.FILL_PARENT,
	WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	
	ReplaceColorView mainView;
	Button[] button = new Button[2];
	
	public ReplaceColorDialog(Context context,RangeMenuDialog parentDialog) {
		super(context);
		
		this.parentDialog = parentDialog;
		
		setTitle("Replace Color");
		
		setMainLayout(context);
		setContentView(mainLayout);
	}
	
	public Dots getDotsOfReplaceColor(){
		
		return mainView.getDotsOfReplaceColor();
	}
	
	public void renewalSampleDots(Dots newDots){
		
		mainView.renewalSampleDots(newDots);
	}
	
	public void renewalSrcColor(int color){
	
		mainView.renewalSrcColor(color);
	}

	public void renewalDstColor(int color){
		
		mainView.renewalDstColor(color);
	}
	
	public void onClick(View v) {
		
		if(v==button[0]) { parentDialog.requestOpenColorDialogToGrid(5); hide();}
		if(v==button[1]) { 
			parentDialog.setReplaceColorDotsToSelectedDots(); 
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
		
		mainView = new ReplaceColorView(context);
		viewLayout.addView(mainView);
	}
	
	private void setConsoleLayout(Context context){
		
		consoleLayout = new LinearLayout(context);
		consoleLayout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams param = getParam(FP,WC);
		consoleLayout.setLayoutParams(param);
	
		
		LinearLayout buttonLayout1 = new LinearLayout(context);
		buttonLayout1.setOrientation(LinearLayout.HORIZONTAL);
		param = getParam(WC,WC);
		param.gravity = Gravity.CENTER;
		buttonLayout1.setLayoutParams(param);
		
		for(int i=0; i<2; i++){
			button[i] = new Button(context);
			setButtonLayoutParam(i);
			button[i].setOnClickListener(this);
			buttonLayout1.addView(button[i]);
		}
		button[0].setText("Destination Color");
		button[1].setText("OK");
		
		consoleLayout.addView(buttonLayout1);
	}

	private void setButtonLayoutParam(int i){
		
		LinearLayout.LayoutParams param = getParam(WC,WC);
		param.setMargins(10, 10, 10, 10);
		button[i].setLayoutParams(param);
	}
	
	private LinearLayout.LayoutParams getParam(int arg0,int arg1){
		
		return new LinearLayout.LayoutParams(arg0, arg1);
	}
}
