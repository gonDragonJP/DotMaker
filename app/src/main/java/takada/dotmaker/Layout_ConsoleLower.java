package takada.dotmaker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Layout_ConsoleLower extends LinearLayout{
	
	public Button[] button=new Button[4];
	private String name[] = {"figure","palette","range","move"};

	public Layout_ConsoleLower(Context context,OnClickListener listener) {
		super(context);
		
		this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
		
		for(int i=0; i<4; i++){
			button[i]=new Button(context);
			button[i].setOnClickListener(listener);
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
