package takada.dotmaker;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class DotMaker extends Activity{
	
	private MainView mainView;
	private FileDialog fileDialog;
	private LayerDialog layerDialog;
	private SettingDialog settingDialog;
	
	private boolean isGoneTitle=false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.titlelayout);
        
       settingDialog=new SettingDialog(this);
       layerDialog=new LayerDialog(this,settingDialog); 
       fileDialog=new FileDialog(this,layerDialog);     
       mainView=new MainView(this,layerDialog);
      
       fileDialog.setActivity(this);//			終了処理のために必要
       layerDialog.setMainView(mainView);//	レイヤー更新処理のために必要
       
       final Handler handler = new Handler();
       
       new Thread(new Runnable(){

		@Override
		public void run() {
			
			try {
				Thread.currentThread().sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			handler.post(new Runnable(){

				@Override
				public void run() {
					
					setMainLayout();
					isGoneTitle=true;
				}
				
			});
		}
    	   
       }).start();
       
       //setMainLayout();	
    }
   
    private void setMainLayout(){
    	
    	int fillParent=LinearLayout.LayoutParams.FILL_PARENT;
    	int wrapContent=LinearLayout.LayoutParams.WRAP_CONTENT;
    
    	RelativeLayout mainLayout=new RelativeLayout(this);
    	mainLayout.setLayoutParams(getLParam(fillParent,fillParent));
        setContentView(mainLayout);
        
        mainView.mainThread.setMainLayout(mainLayout);
    }
    
    private LinearLayout.LayoutParams getLParam(int arg0,int arg1){
    
    	LinearLayout.LayoutParams param 
    					= new LinearLayout.LayoutParams(arg0,arg1);
    	return param;
    }
    
    private LinearLayout.LayoutParams addWeight
    				(LinearLayout.LayoutParams param, int i){
    	
    	param.weight = i;
    	return param;
    }
    
    private static final int
    	MENU_FILE = 0,
    	MENU_LAYER = 1,
    	MENU_SETTING = 2,
    	MENU_HELP = 3;
    
    public boolean onCreateOptionsMenu(Menu menu){
		
    	MenuItem file = menu.add(0,MENU_FILE,0,"File");
    	MenuItem layer= menu.add(0,MENU_LAYER,0,"Layer");
    	MenuItem setting = menu.add(0,MENU_SETTING,0,"Setting");
    	MenuItem help=menu.add(0,MENU_HELP,0,"Help");
    	
    	file.setIcon(android.R.drawable.ic_menu_save);
    	layer.setIcon(android.R.drawable.ic_menu_gallery);
    	setting.setIcon(android.R.drawable.ic_menu_manage);
    	help.setIcon(android.R.drawable.ic_menu_help);
    	
    	return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	
    	switch(item.getItemId()){
    	
    	case MENU_FILE:
    		fileDialog.show();
    		return true;
    	
    	case MENU_LAYER:
    		layerDialog.showDialog();
    		return true;
    	
    	case MENU_SETTING:
    		settingDialog.show();
    		return true;
    	}
    	
    	return true;
    }
    
    public void onBackPressed(){
    	
    	if(isGoneTitle)
    		mainView.mainThread.onBackPressed();
    }

}