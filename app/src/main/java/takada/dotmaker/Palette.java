package takada.dotmaker;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;

public class Palette {
	
	private class MyPalette {
		
		int A,R,G,B;
		int x1,y1,x2,y2;
		
		public MyPalette(int a,int r,int g,int b){
			setARGB(a,r,g,b);
		}
		
		public void setARGB(int a,int r,int g,int b){
			A=a;	R=r;	G=g;	B=b;
		}
		
		public int[] getRGB(){
			
			int[] rgb = new int[3];
			rgb[0]=R;	rgb[1]=G;	rgb[2]=B;
			return rgb;
		}
		
		public int[] getARGB(){
			
			int[] argb = new int[4];
			argb[0]=A;	argb[1]=R;	argb[2]=G;	argb[3]=B;
			return argb;
		}
		
		public void setColor(int color){
			
			int a=Color.alpha(color);
			int r=Color.red(color);
			int g=Color.green(color);
			int b=Color.blue(color);
			setARGB(a,r,g,b);
		}
		
		public int getColor(){
			
			return Color.argb(A,R,G,B);
		}
		
		public void setPosition(int x1,int y1,int x2,int y2){
			this.x1=x1; this.y1=y1; this.x2=x2; this.y2=y2;
		}
		
		public Rect getRect(int margin){
			
			return new Rect(x1+margin,y1+margin
						   ,x2-margin,y2-margin);
		}
		
		public RectF getRectF(int margin){
			
			return new RectF(x1+margin,y1+margin
							,x2-margin,y2-margin);
		}
		
		public void copy(MyPalette source){
			A=source.A; R=source.R; G=source.G; B=source.B;
		}
		
		public boolean equal(MyPalette source){
			
			if(A==source.A && R==source.R && G==source.G && B==source.B) return true;
			else return false;
		}
	}
	
	int Left=20,Top=5;
	int palSize=35;
	int palNum=42;
	
	static MyPalette clipPal,currentColor;
	static List<MyPalette> recordPalList = new ArrayList<MyPalette>();;
	private MyPalette[] pal = new MyPalette[palNum];
	
	public int selectedPal=0;
	boolean isBasicColor=false;
	
	public Palette(boolean isBasicColor){

		this.isBasicColor = isBasicColor;
		
		for(int i=0; i<palNum; i++){
			pal[i]=new MyPalette(255,0,0,0);
		}
		if(isBasicColor) initialPaletteBasicColor();
		else initialPaletteColor();
		
		initialPalettePosition();
		
		if(clipPal == null) clipPal= new MyPalette(255,0,0,0);
		if(currentColor == null) currentColor = new MyPalette(255,0,0,0);
	}
	
	private void initialPaletteColor(){
		
		/*for(int i=0; i<27; i++){
			int a=255;
			int r=(int)((i%3)*127.5);
			int g=(int)(((i/3)%3)*127.5);
			int b=(int)(((i/9)%3)*127.5);
			pal[i].setARGB(a, r, g, b);
		}*/
		
		pal[palNum-2].setARGB(0, 0, 0, 0);
	}
	
	private void initialPaletteBasicColor(){
		
		for(int i=0; i<6; i++){
			for(int j=0; j<3; j++){
				double h=i*60;
				double s=1-Math.sqrt((double)j)*0.5;
				double v=1;
				pal[i*6+j].setColor(HSVView.getColorFromHSV(h,s,v));
			}
		}
		for(int i=0; i<6; i++){
			for(int j=0; j<3; j++){
				double h=i*60;
				double s=1;
				double v=0.7-Math.sqrt((double)j)*0.3;
				pal[i*6+(j+3)].setColor(HSVView.getColorFromHSV(h,s,v));
			}
		}
		for(int i=0; i<5; i++){
			int dense=(int)(255/4*(double)i);
			pal[36+i].setARGB(255,dense,dense,dense);
		}
		pal[41].setARGB(0, 0, 0, 0);
	}
	
	private void initialPalettePosition(){
		int x1,y1,x2,y2;
		for(int i=0; i<palNum; i++){
			x1=Left+(palSize+10)*(i%6);
			y1=Top+ (palSize+10)*(i/6);
			x2=x1+palSize;
			y2=y1+palSize;
			pal[i].setPosition(x1,y1,x2,y2);
		}
	}
	
	static public int[] getCurrentRGB(){
		
		return currentColor.getRGB();
	}
	
	static public int getCurrentColor(){
		
		return currentColor.getColor();
	}
	
	static public void setCurrentRGB(int[] rgb){
		
		currentColor.setARGB(255, rgb[0], rgb[1], rgb[2]);
	}
	
	public void invalidateCurrentColor(){
		
		currentColor.copy(pal[selectedPal]);
	}

	public void setGridCursorColor(int color){
		
		pal[palNum-1].setColor(color);
	}
	
	public void clip(){
		
		clipPal.copy(pal[selectedPal]);
	}
	
	static public void setClipRGB(int[] rgb){
		
		clipPal.setARGB(255, rgb[0], rgb[1], rgb[2]);
	}
	public void paste(){
		
		if(!isBasicColor && selectedPal < palNum-6){
			pal[selectedPal].copy(clipPal);
			invalidateCurrentColor();
		}
	}

	public void copy(Palette srcPalette){
		
		for(int i=0; i<palNum; i++){
			pal[i].copy(srcPalette.pal[i]);
		}
	}
	
	public void draw(Canvas canvas){
		
		Paint paint = new Paint();
		
		paint.setStyle(Style.FILL);
		paint.setColor(Color.RED);
		canvas.drawRect(pal[selectedPal].getRect(-2),paint);
		
		if(!isBasicColor) setRecordColorsPal();
		
		for(int i=0; i<palNum; i++){
			
			if(pal[i].A==0) drawTransparentRect(i,canvas);
			else{
				paint.setColor(pal[i].getColor());
				canvas.drawRect(pal[i].getRect(0),paint);
			}
		}
		
		if(!isBasicColor){
			drawGridCursorPallete(canvas);
			drawRecordColorsGrid(canvas);
		}
	}
	
	private void drawTransparentRect(int a,Canvas canvas){
		
		Paint paint = new Paint();
		
		int col0=Color.argb(255,40,40,40);
		int col1=Color.argb(255,80,80,80);
		int xm=(pal[a].x1+pal[a].x2)/2;
		int ym=(pal[a].y1+pal[a].y2)/2;
		
		for(int x=0; x<2; x++)
			for(int y=0; y<2; y++){
				
				if((x+y)%2==0) paint.setColor(col0); else paint.setColor(col1);
				
				int x1=(x==0)? pal[a].x1 : xm;
				int y1=(y==0)? pal[a].y1 : ym;
				int x2=(x==0)? xm : pal[a].x2;
				int y2=(y==0)? ym : pal[a].y2;
				
				canvas.drawRect(new Rect(x1,y1,x2,y2),paint);
			}
	}
	
	private void drawGridCursorPallete(Canvas canvas){
		
		Paint paint = new Paint();
		int a = palNum-1;
		paint.setColor(Color.WHITE);
		paint.setTextSize(15);
		int x=pal[a].x1-10;
		int y=pal[a].y2+15;
		
		canvas.drawText("now grid", x, y, paint);
	}
	
	private void drawRecordColorsGrid(Canvas canvas){
		
		Paint paint = new Paint();
		int a = palNum - 5;
		paint.setColor(Color.WHITE);
		paint.setTextSize(15);
		int x=pal[a].x1-10;
		int y=pal[a].y2+15;
		
		canvas.drawText("recently used", x, y, paint);
		
		a = palNum - 6;
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.argb(255,150,150,150));
		Rect rect = new Rect(pal[a].x1-2, pal[a].y1-2, pal[a+3].x2+2, pal[a+3].y2+2);
		canvas.drawRect(rect,paint);
	}
	
	public boolean checkTouched(int i,int touchX,int touchY){
		
		if(pal[i].x1<touchX && pal[i].x2>touchX)
			if(pal[i].y1<touchY && pal[i].y2>touchY)
				return true;
			
		return false;
	}
	
	private void setRecordColorsPal(){
		
		int size = recordPalList.size();
		int dst_start_pal = palNum-6;
		
		for(int i=0; i<size; i++){
			
			pal[i+dst_start_pal].copy((MyPalette)recordPalList.get(i));
		}
	}
	
	public void addRecordColor(){
		
		MyPalette newPal = new MyPalette(0,0,0,0);
		newPal.copy(currentColor);
		
		for(int i=0; i<recordPalList.size(); i++)
			if(newPal.equal((MyPalette)recordPalList.get(i))){ 
				recordPalList.remove(i); 
				break;
			}
		
		recordPalList.add(newPal);
		if(recordPalList.size()==5) recordPalList.remove(0);
	}
	
	public byte[] getByteForSaveData(){
		
		int headerByte=1;
		int palleteByte=palNum*4;
		int totalByte=headerByte+palleteByte;
		
		byte[] data=new byte[totalByte];
		
		data[0]=(byte)palNum;
		
		int offset = headerByte;
		
		for(int i=0; i<palNum; i++){
				
			data[offset  ]=(byte)pal[i].A;
			data[offset+1]=(byte)pal[i].R;
			data[offset+2]=(byte)pal[i].G;
			data[offset+3]=(byte)pal[i].B;
			offset+=4;
		}
		return data;
	}
	
	public void setDataFromSaveData(byte[] data){
		
		palNum=data[0];
		
	    int offset=1;
		
	    for(int i=0; i<palNum; i++){
			
	    	pal[i].A=data[offset  ];
	    	pal[i].R=data[offset+1];
	    	pal[i].G=data[offset+2];
	    	pal[i].B=data[offset+3];
			offset+=4;
		}
	}
}
