package takada.dotmaker;

import android.graphics.Point;
import android.graphics.Rect;

public class RotateRange {
	
	static final double radian = 3.1415926 / 180;

	double rotateAngle,c,s;
	double cx,cy;
	Point left_top,left_bottom,right_bottom,right_top;
	
	public RotateRange(){
		
		setAngle(rotateAngle);
	}
	
	public void setAngle(double angle){
		
		rotateAngle = angle;
		
		c = Math.cos(rotateAngle * radian);
		s = Math.sin(rotateAngle * radian);
	}

	public void setRangeRect(Rect rangeRect){
		
		cx = rangeRect.exactCenterX();
		cy = rangeRect.exactCenterY();
		
		left_top = mapToRotateCoord(new Point(rangeRect.left,rangeRect.top));
		left_bottom = mapToRotateCoord(new Point(rangeRect.left,rangeRect.bottom));
		right_bottom = mapToRotateCoord(new Point(rangeRect.right,rangeRect.bottom));
		right_top = mapToRotateCoord(new Point(rangeRect.right,rangeRect.top));
		
	}
	
	public Point mapToRotateCoord(Point point){
		
		int x = (int)(( c * (point.x - cx) + s * (point.y - cy)) + cx);
		int y = (int)((-s * (point.x - cx) + c * (point.y - cy)) + cy);
		
		return new Point(x, y);
	}
	
	public Point mapToBaseCoord(int x,int y){
		
		int rx = (int)(( c * (x - cx) + -s * (y - cy)) + cx);
		int ry = (int)(( s * (x - cx) +  c * (y - cy)) + cy);
		
		return new Point(rx, ry);
	}
	
	public Rect getRequiredRangeRect(int left, int top, int gridSize){
		
		Rect rect = new Rect();
		int[] x = new int[4];	int[] y = new int[4];
		
		x[0] = left_top.x;	x[1] = left_bottom.x;
		x[2] = right_top.x;	x[3] = right_bottom.x;
		
		y[0] = left_top.y;	y[1] = left_bottom.y;
		y[2] = right_top.y;	y[3] = right_bottom.y;
		
		rect.set((getMinValue(x)-1-left)/gridSize,(getMinValue(y)-1-top)/gridSize
				,(getMaxValue(x)+1-left)/gridSize,(getMaxValue(y)+1-top)/gridSize);
		
		return rect;
	}
	
	static private int getMinValue(int[] group){
		int min = group[0];
		for(int i=0; i<group.length; i++)	min = Math.min(min, group[i]);
		return min;
	}
	
	static private int getMaxValue(int[] group){
		int max = group[0];
		for(int i=0; i<group.length; i++)	max = Math.max(max, group[i]);
		return max;
	}
	
}
