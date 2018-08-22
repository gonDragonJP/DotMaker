  package takada.dotmaker;

import android.content.Context;
import android.widget.LinearLayout;

public class Layout_MainConsole extends LinearLayout{
	
	public Layout_ConsoleUpper upper_layout;
	public Layout_ConsoleLower lower_layout;

	public Layout_MainConsole(Context context,MainView mainView) {
		super(context);
		
		mainView.setConsole(this);
		
		OnClickListener listener = mainView.mainThread;
		
		upper_layout=new Layout_ConsoleUpper(context,listener);
        lower_layout=new Layout_ConsoleLower(context,listener);
		
        LinearLayout.LayoutParams params;
   
        params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin=0;
        upper_layout.setLayoutParams(params);
       
        
        params = new LinearLayout.LayoutParams(
        		LinearLayout.LayoutParams.FILL_PARENT,
        		LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin=0;
        lower_layout.setLayoutParams(params);
        
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(upper_layout);
        this.addView(lower_layout);   
    }

}
