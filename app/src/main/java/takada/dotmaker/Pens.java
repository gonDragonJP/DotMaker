package takada.dotmaker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;

public class Pens {
	
	int maxPenNumber=5;
	int selectedPen=0;
	
	int penSize=5;
	//byte penSize[] = new byte[maxPenNumber];
	
	byte pen[][][] ={ 
		{{0,0,0,0,0},{0,0,0,0,0},{0,0,1,0,0},{0,0,0,0,0},{0,0,0,0,0}},
		{{0,0,0,0,0},{0,0,1,0,0},{0,1,1,1,0},{0,0,1,0,0},{0,0,0,0,0}},
		{{0,0,0,0,0},{0,1,1,1,0},{0,1,1,1,0},{0,1,1,1,0},{0,0,0,0,0}},
		{{0,0,1,0,0},{0,1,1,1,0},{1,1,1,1,1},{0,1,1,1,0},{0,0,1,0,0}},
		{{0,1,1,1,0},{1,1,1,1,1},{1,1,1,1,1},{1,1,1,1,1},{0,1,1,1,0}}
		};
	
	public Pens(){}
	
	public boolean isActivePenPoint(int x,int y){
		
		if (pen[selectedPen][x][y]==1) return true;
		else return false;
	}
	
	public Bitmap getPenBitmap(int i){
		
		Paint paint = new Paint();
		Bitmap bitmap = Bitmap.createBitmap(64,64,Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		
		for(int x=0; x<5; x++){
			for(int y=0; y<5; y++){
				
				Rect rect = 
					new Rect(x*12,y*12,(x+1)*12,(y+1)*12);
			
				if(pen[i][x][y]==1){
				
					paint.setColor(Color.argb(255, 200, 100, 200));
					paint.setStyle(Style.FILL);
					canvas.drawRect(rect, paint);
				}
				
				paint.setColor(Color.argb(255, 200, 200, 200));
				paint.setStyle(Style.STROKE);
				canvas.drawRect(rect, paint);
			}			
		}
		
		return bitmap;
	}

}
