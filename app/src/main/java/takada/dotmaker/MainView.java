package takada.dotmaker;

import android.content.Context;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends SurfaceView
		implements SurfaceHolder.Callback,Runnable{
	
	private SurfaceHolder holder;
	private Thread thread;
	public MainThread mainThread;
	
	
	public MainView(Context context,LayerDialog layerDialog) {
		
		super(context);
		
		holder=getHolder();
		holder.addCallback(this);
		
		mainThread=
			new MainThread(context,this,layerDialog);
	}
	
	public void setConsole(Layout_MainConsole console){
		
		mainThread.setConsole(console);
	}
	
	public void renewalLayerGroup(LayerGroup newGroup){
		
		mainThread.renewalLayerGroup(newGroup);
	}
	
	public void renewalCurrentLayer(){
		
		mainThread.renewalCurrentLayer();
	}

	@Override
	public void run() {
		
		while(thread!=null){
			
			mainThread.periodicProcess();
			mainThread.drawScreen();
			
			try{
				Thread.sleep(10);
			}catch (Exception e){
			}
			
		}	
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		thread=new Thread(this);
		thread.start();
		
		mainThread.setScreenSize(getWidth(), getHeight());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	
		thread=null;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		mainThread.onPad(event);
		
		return true;
	}

}
