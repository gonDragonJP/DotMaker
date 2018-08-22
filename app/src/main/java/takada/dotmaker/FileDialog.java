package takada.dotmaker;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileDialog extends Dialog 
			implements OnItemClickListener,DialogInterface.OnClickListener{
	
	private static final String[] 
	 mMenu={"Save Data","Load Data","Delete Data","New Dots","Export DotsPicture","Exit Application"};
	
	private Context context;
	private DotMaker activity;
	private LayerDialog layerDialog;
	private LayerGroup layerGroup;
	
	public ListView listView;
	private OriginalAdapter adapter;
	private FileUtility fileUtil;
	
	private int finalCallKind;
	private String fullPathName;
	
	public static final int
	SAVE_DATA =0,
	LOAD_DATA =1,
	DELETE_DATA =2,
	RENEWAL_DATA =3,
	EXPORT_DATA =4,
	APPLICATION_EXIT=5;

	
	public FileDialog(Context context,LayerDialog layerDialog) {
		super(context);
		
		this.context=context;
		this.layerDialog=layerDialog;
		layerGroup=layerDialog.layerGroup;

		this.setTitle("File Menu");
		setLayout(context);
	}
	
	public void setActivity(DotMaker rootActivity){ //èIóπèàóùÇÃÇΩÇﬂÇ…activityïKóv
		
		this.activity = rootActivity;
	}

	public void setAdapterForMenuSelect(){
		
		listView.setAdapter(adapter);
		listView.setOnTouchListener(new View.OnTouchListener() {
		
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getAction()==MotionEvent.ACTION_MOVE)
				  return true;
				return false;
			}
		});
		listView.setOnItemClickListener(this);
	}
	
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		if (parent != listView) return;
		
		finalCallKind = position;
		
		switch(position){
		
		case SAVE_DATA:
			saveData();
			hide();
			break;
		
		case LOAD_DATA:
			showDirectoryList(LOAD_DATA);
			break;	
			
		case DELETE_DATA:
			showDirectoryList(DELETE_DATA);
			break;
			
		case RENEWAL_DATA:
			renewalData();
			hide();
			break;
		
		case EXPORT_DATA:
			exportDotsPicture();
			break;
			
		case APPLICATION_EXIT:
			exitApplication();
			break;
		}
	}

	public void onBackPressed(){
		
		if(listView.getAdapter()!=adapter) setAdapterForMenuSelect();
		hide();
	}
	
	private void saveData(){
		
		StrageDataFormat strageData = new StrageDataFormat(layerDialog);
		strageData.createSaveData();
		
		byte[] dataByte=strageData.dataByte;
		int dataLength=strageData.totalByte;
		
		try{
		
			String fullPathName = fileUtil.getNewFileFullPath();
			FileOutputStream output = new FileOutputStream(fullPathName);
			
			output.write(dataByte);
			output.close();
		
			Toast.makeText(context, "Saving Data is succeed", 5).show();
		}
		catch(Exception e){
			FileUtility.showAlertDialog(context,"Error","Saving Data is failed");
		}
	}
	
	private void exportDotsPicture(){
		
		Bitmap bitmap = layerDialog.layerGroup.compoLayer.bitmap;
		
		try{
			String fullPathName = fileUtil.getNewExportFileFullPath();
			FileOutputStream output = new FileOutputStream(fullPathName);
			
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
			
			android.util.Log.e("",fullPathName);
			
			output.close();
			
			Toast.makeText(context, "Export Picture is succeed", 5).show();
		}catch(Exception e){
			FileUtility.showAlertDialog(context, "Error", "Export Picture is failed");
		}
		
	}
	
	private void showDirectoryList(int finalCallKind){
		
		fileUtil.setFinalCallKind(finalCallKind);
		fileUtil.setDirList();
	}
	
	public void requestLoadData(String fullPathName){
		
		getThumbnailBitmap(fullPathName);
		loadData(fullPathName);
		setAdapterForMenuSelect();
		hide();
	}
	
	public Bitmap getThumbnailBitmap(String fullPathName){
		
		int size;
		Bitmap bitmap =null;
		ByteBuffer header = ByteBuffer.allocate(12);
		header.clear();
		ByteArrayOutputStream out=null;
		try{
			FileInputStream input = new FileInputStream(fullPathName);
			out = new ByteArrayOutputStream();
			
			input.read(header.array());
			
			int width    = header.getInt(0);
			int height   = header.getInt(4);
			int widthByte= header.getInt(8);
			int pixelByte= height * widthByte;
			
			if((width==0)|(width * 4 != widthByte)) throw new Exception();
			
			bitmap = Bitmap.createBitmap(width, height ,Bitmap.Config.ARGB_8888);
			byte[] w=new byte[pixelByte];
			
			size = input.read(w);
			out.write(w,0,size);
			
			out.close();
			input.close();
			
			byte[] pixelData=out.toByteArray();
			
			ByteBuffer src = ByteBuffer.allocate(height * widthByte);
			src.clear();
			src.put(pixelData); 
			src.rewind();
			bitmap.copyPixelsFromBuffer(src);
		}
		catch(Exception e){
			//FileUtility.showAlertDialog(context,"Error","Loading Thumbnail is failed");
			try{
				if(out!=null) out.close();
			}
			catch(Exception e2){}
		}
		
		return bitmap;
	}
	
	public void loadData(String fullPathName){
		
		int size;
		byte[] w=new byte[1024];
		ByteArrayOutputStream out=null;
		try{
			FileInputStream input = new FileInputStream(fullPathName);
			out = new ByteArrayOutputStream();
			while(true){
				size = input.read(w);
				if (size<=0) break;
				out.write(w,0,size);
			}
			out.close();
			input.close();
			
			byte[] dataByte=out.toByteArray();
			int dataLength=out.size();
			
			setupLoadingData(dataLength,dataByte);
			
			Toast.makeText(context, "Data Loading is succeed", 5).show();
		}
		catch(Exception e){
			FileUtility.showAlertDialog(context,"Error","Loading Data is failed");
			try{
				if(out!=null) out.close();
			}
			catch(Exception e2){}
		}
	}
	
	private void setupLoadingData(int length,byte[] data){
			
		StrageDataFormat strageData = new StrageDataFormat(layerDialog);
		layerGroup = strageData.setLoadData(length,data);
		
		layerDialog.refreshToChangeLayerGroup();
	}
	
	public void requestDeleteData(String fullPathName){
		
		this.fullPathName = fullPathName;
		showAlertOKCancelDialog("Delete Data","Are You Sure?");
	}
	
	private void deleteData(String fullPathName){
		
		fileUtil.deleteData(fullPathName);
	}
	
	private void renewalData(){
		
		layerDialog.renewalLayerGroup();
		layerGroup=layerDialog.layerGroup;
	}
	
	private void exitApplication(){
		
    	showAlertOKCancelDialog("Application Exit","Are You Sure?");
	}
	
	private void showAlertOKCancelDialog (String title, String text){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
    	.setTitle(title)
    	.setMessage(text)
    	.setPositiveButton("OK",this)
    	.setNegativeButton("Cancel",this);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void onClick(DialogInterface dialog, int which) {
		
		if(which==DialogInterface.BUTTON_POSITIVE) {
			switch(finalCallKind){
			case DELETE_DATA:
				deleteData(fullPathName);
				setAdapterForMenuSelect();
				hide();
				break;
			case APPLICATION_EXIT:
				activity.finish();
				break;
			}
		}
	}
	
	static final int
		wc = LinearLayout.LayoutParams.WRAP_CONTENT,
		fp = LinearLayout.LayoutParams.FILL_PARENT;

	private void setLayout(Context context){
	
		LinearLayout layout = new LinearLayout(context);
		layout.setLayoutParams(getParams(fp,wc));
	
		layout.setOrientation(LinearLayout.VERTICAL);
		setContentView(layout);
		setListViewToLayout(context,layout);
		
	}
	private void setListViewToLayout(Context context,LinearLayout layout){
	
		adapter = new OriginalAdapter(context, R.layout.fileselector_row,mMenu);
		listView = new ListView(context);
	
		listView.setLayoutParams(getParams(fp,wc));
		layout.addView(listView);
	
		fileUtil = new FileUtility(context,this);
	
		setAdapterForMenuSelect();
	}

	private static LinearLayout.LayoutParams getParams(int arg0, int arg1){
	
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(arg0,arg1);
		return params;
	}

	private static LinearLayout.LayoutParams setGravity(LinearLayout.LayoutParams param, int arg){
	
		param.gravity = arg;
		return param;
	}
	
	public class OriginalAdapter extends ArrayAdapter<String>{

		private LayoutInflater inflater;
		String[] objects;
		
		public OriginalAdapter(Context context, int textViewResourceId,
				String[] objects) {
			super(context, textViewResourceId, objects);
			
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.objects = objects;
		}
		
		public OriginalAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position,View convertView, ViewGroup parent){
		
			convertView = inflater.inflate(R.layout.fileselector_row, parent, false);
			TextView textView = (TextView)convertView.findViewById(R.id.fileselector_textview);
			textView.setText((String)getItem(position));
			
			return convertView;
		}
		
	}
}
