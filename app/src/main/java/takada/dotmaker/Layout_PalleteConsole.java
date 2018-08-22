package takada.dotmaker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Layout_PalleteConsole extends LinearLayout{
	
	public Button[] button=new Button[5];
	private String name[] = {" OK ","Basic Sample","HSV","copy","paste"};

	public Layout_PalleteConsole(Context context,ColorDialog parentDialog) {
		super(context);
		
		this.setOrientation(LinearLayout.VERTICAL);
        setParams(this);
        
        LinearLayout upperConsole = new LinearLayout(context);
        LinearLayout lowerConsole = new LinearLayout(context);
        
        setLayout(upperConsole);
        setLayout(lowerConsole);
		
		for(int i=0; i<2; i++){
			button[i]=new Button(context);
			button[i].setOnClickListener(parentDialog);
			button[i].setText(name[i]);
			setParams(button[i]);
			upperConsole.addView(button[i]);
		}
		
		for(int i=2; i<5; i++){
			button[i]=new Button(context);
			button[i].setOnClickListener(parentDialog);
			button[i].setText(name[i]);
			setParams(button[i]);
			lowerConsole.addView(button[i]);
		}
		
		this.addView(upperConsole);
		this.addView(lowerConsole);
	}
    
	private static void setLayout(LinearLayout layout){
		
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT);
    	layout.setLayoutParams(params);
	}
	
	private static void setParams(View view){
    	
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT);
    	view.setLayoutParams(params);	
    }
}

