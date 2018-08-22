package takada.dotmaker;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class FileUtility implements OnItemClickListener{

	private Context context;
	private FileDialog parentDialog;
	private ListView listView;
	
	private ArrayAdapter<String> adapter;
	private VisualDirectoryAdapter visualAdapter;
	private ArrayList<VisualDirData> visualDirList;
	private File myDir;
	
    private int finalCallKind;
    
    public FileUtility(Context context,FileDialog parentDialog){
        
    	this.context = context;
    	this.parentDialog = parentDialog;
    	this.listView = parentDialog.listView;
    	
    	createAdapter(parentDialog);
        setupDirectory();
    }
    
    public void createAdapter(FileDialog parent){
    	
    	visualDirList = new ArrayList<VisualDirData>();
    	visualAdapter = new VisualDirectoryAdapter(context, visualDirList);
    }
    
    public void setFinalCallKind(int finalCallKind){
    	
    	this.finalCallKind = finalCallKind;
    }
    
    public void setupDirectory(){
    	
    	String state = Environment.getExternalStorageState();
		if(!Environment.MEDIA_MOUNTED.equals(state)){
			showAlertDialog(context,"Error","No Mounted External Storage");
			return;
		}
    	
    	File sdDir = Environment.getExternalStorageDirectory();
    	if(!sdDir.exists()|!sdDir.canWrite()){
    		showAlertDialog(context,"Error","Not Ready For External Storage");
    		return;
    	}
    	
    	myDir = new File(sdDir.toString()+"/tmp/DotMaker");
    	myDir.mkdirs();
    }
    
    public String getNewFileFullPath(){
    	
    	String fileName = getNewFileNameWithoutSuffixFromCalendar();
    	
    	return myDir.toString()+"/"+fileName+".dtmk";
    }
    
    public String getNewExportFileFullPath(){
    	
    	String fileName = getNewFileNameWithoutSuffixFromCalendar();
    	
    	return myDir.getParent().toString()+"/"+fileName+".png";
    }
    
    private String getNewFileNameWithoutSuffixFromCalendar(){
    	
    	Calendar date = Calendar.getInstance();
    	int year = date.get(Calendar.YEAR);
    	int month = date.get(Calendar.MONTH)+1;
    	int day = date.get(Calendar.DAY_OF_MONTH);
    	int hour = date.get(Calendar.HOUR_OF_DAY);
    	int min= date.get(Calendar.MINUTE);
    	int sec = date.get(Calendar.SECOND);
    	
    	String fileName = String.format("%4d-%02d-%02d-%02d-%02d-%02d",
    			year,month,day,hour,min,sec);
    	
    	return fileName;
    }
    
    public void setDirList(){
    	
    	visualAdapter.clear();
    
    	//FileFilter filter = new FileFilter(adapter);
    	//String[] list=myDir.list(filter);
    	String[] list=myDir.list();
    	
    	for(int i=0; i<list.length; i++){
    		
    		String fullPathName = myDir.toString()+"/"+list[i];
    		String name = list[i].replace(".dtmk", "");
    		
    		visualAdapter.add(new VisualDirData(name, fullPathName));
    	}
    	
    	//adapter.sort(new FileNameComparator());
    	visualAdapter.removeDir(myDir);
    	visualAdapter.sort();
    	
    	for(int i=0; i<list.length; i++){
    		
    		String fullPathName = visualAdapter.getItem(i).fullPathName;
    		new Thread(new LoadThumbnailTask(i,fullPathName)).start();	
    	}
    	
    	listView.setAdapter(visualAdapter);
    	
    	listView.setOnItemClickListener(this);
    	listView.setOnTouchListener(new View.OnTouchListener() {
    		
			public boolean onTouch(View v, MotionEvent event) {
				
				return false;
			}
		});
    }
    
    private class LoadThumbnailTask implements Runnable{

    	String fullPathName;
    	int pos;
    	
    	public LoadThumbnailTask(int pos, String fullPathName){
    		
    		this.fullPathName = fullPathName;
    		this.pos = pos;
    	}
    	
		@Override
		public void run() {
			
			
			visualAdapter.setBitmap
    		(pos, parentDialog.getThumbnailBitmap(fullPathName));
		}
    	
    }
    
    public void deleteData(String fullPathName){
    	
    	File file = new File(fullPathName);
    	file.delete();
    }
    
    @Override
    public void onItemClick(AdapterView<?> listview, View layout, int position, long id) {
 	
    	VisualDirData data = (VisualDirData)listview.getItemAtPosition(position);
    	String name = data.textName;
    	String fullPathName = myDir.toString()+"/"+name+".dtmk";
 	
    	switch(finalCallKind){
 	
    	case FileDialog.LOAD_DATA:
    		parentDialog.requestLoadData(fullPathName);
    		break;
    	case FileDialog.DELETE_DATA:
    		parentDialog.requestDeleteData(fullPathName);
    		break;
    	}
    }
   static public void showAlertDialog(final Context context, String title, String text){
    	
    	AlertDialog.Builder ad = new AlertDialog.Builder(context);
    	ad.setTitle(title);
    	ad.setMessage(text);
    	ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
    	ad.show();
    }

   private class VisualDirData{
	   
	   Bitmap thumbnail;
	   String textName;
	   String fullPathName;
	   
	   public VisualDirData(String textName, String fullPathName){
		   
		   this.textName = textName;
		   this.fullPathName = fullPathName;
	   }
   }

   private class VisualDirectoryAdapter extends BaseAdapter{

	   private LayoutInflater inflater;
	   private List<VisualDirData> list;
	   
	   public VisualDirectoryAdapter
	   	(Context context, ArrayList<VisualDirData> list){
		   
		   inflater = LayoutInflater.from(context);
		   this.list = list;
	   }
	   
	   public void add(VisualDirData newData){
		
		   list.add(newData);
	   }
	
	   public void replace(int arg0, int arg1){
		
		   VisualDirData s = list.get(arg0);
		   list.set(arg0, list.get(arg1));
		   list.set(arg1, s);
	   }
	
	   public void clear(){
		
		   list.clear();
	   }
	   
	   public void setBitmap(int i, Bitmap bitmap){
		   
		   list.get(i).thumbnail = bitmap;
	   }
	   
	   @Override
	   public int getCount() {
		
		   return list.size();
	   }

	   @Override
	   public VisualDirData getItem(int arg0) {
		
		   return list.get(arg0);
	   }

		@Override
		public long getItemId(int arg0) {
		
			return arg0;
		}
	
		public void sort(){
		
			for(int i=0; i<getCount(); i++){
				for(int j=0; j<getCount(); j++){
				
					String NameI = getItem(i).textName;
					String NameJ = getItem(j).textName;
				
					if(NameI.compareTo(NameJ)>0) replace(i, j);
				}
			}
		}
	
		public void removeDir(File dir){
		
			for(int i=getCount()-1; i>=0; i--){
			
				File file = new File (dir.toString() + "/" + getItem(i).textName);
   		
				if(file.isDirectory()) list.remove(i);
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
		
			convertView = inflater.inflate(R.layout.filevisualselector_row, parent, false);
		
			ImageView image=
				(ImageView)convertView.findViewById(R.id.filevisualselector_image);
			image.setImageBitmap(getItem(position).thumbnail);
		
			TextView text = 
				(TextView)convertView.findViewById(R.id.filevisualselector_textview);
			text.setText(getItem(position).textName);
		
			return convertView;
		}
	   
   	}
   
   public class FileFilter implements FilenameFilter{

	   ArrayAdapter<String> adapter;
   	
	   public FileFilter(ArrayAdapter<String> adapter){
   		
		   this.adapter = adapter;
	   }
   	
	   @Override
   		public boolean accept(File dir, String filename) {
   		
		   File file = new File (dir.toString() + "/" + filename);
   		
		   if(file.isDirectory()) return false;
   		
		   return true;
	   }

   }
   
   public class FileNameComparator implements Comparator<String>{

	   @Override
	   public int compare(String arg0, String arg1) {
		
		   String Name0 = arg0;
			String Name1 = arg1;
		
			return -Name0.compareTo(Name1);
		}   
   }
}
