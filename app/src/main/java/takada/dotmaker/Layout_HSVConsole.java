package takada.dotmaker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Layout_HSVConsole extends LinearLayout{
	
	public Button[] button=new Button[2];
	private String name[] = {" OK ","Copy & Return Palette"};

	public Layout_HSVConsole(Context context,View.OnClickListener mainThread) {
		super(context);
	 
        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        
		for(int i=0; i<2; i++){
			button[i]=new Button(context);
			button[i].setOnClickListener(mainThread);
			button[i].setText(name[i]);
			setParams(button[i]);
			this.addView(button[i]);
		}
	}
        
	private static void setParams(View view){
    	
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT);
    	view.setLayoutParams(params);	
    }
}