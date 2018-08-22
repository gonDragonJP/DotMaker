package takada.dotmaker;

import android.graphics.Color;

public class Dots {
	
	protected class MyDot{
		int A,R,G,B;
		
		public MyDot(int a,int r,int g,int b){
			A=a; R=r; G=g; B=b;
		}
		
		public int[] getARGB(){
			
			int[] argb = new int[4];
			argb[0]=A;	argb[1]=R;	argb[2]=G;	argb[3]=B;
			return argb;
		}
		
		public int getColor(){
			
			return Color.argb(A,R,G,B);
		}
		
		public void setARGB(int a,int r,int g,int b){
			A=a; R=r; G=g; B=b;
		}
		
		public void setColor(int color){
			
			A=Color.alpha(color);
			R=Color.red(color);
			G=Color.green(color);
			B=Color.blue(color);
		}
		
		public void setARGBWithEnabledAlpha(int a, int r, int g, int b){
			
			int Cd = a;
			int Cs = 255-Cd;
			
			A=(Cd*a+Cs*A)/255;
			R=(Cd*r+Cs*R)/255;
			G=(Cd*g+Cs*G)/255;
			B=(Cd*b+Cs*B)/255;
		}
		
		public void setColorWithEnabledAlpha(int color){
			
			setARGBWithEnabledAlpha
			(Color.alpha(color),Color.red(color),Color.green(color),Color.blue(color));
		}
		
		public void copy(MyDot myDot){
			
			A=myDot.A; R=myDot.R; G=myDot.G; B=myDot.B;
		}
		
	}
	
	protected class MyDotWithDoubleARGB extends MyDot{

		double da,dr,dg,db;
		boolean isMasked = false;//rotateDots“à‚Ì–³Œøˆæ—p
		
		public MyDotWithDoubleARGB(int a, int r, int g, int b) {
			super(a, r, g, b);
			
			resetEffect(); //ˆø”‚Ìargb–³Ž‹
		}
		
		public void resetEffect(){
			
			A = R = G = B = 0;
			da = dr = dg = db =0;
		}
	
		public void addEffect(double effect, MyDot dot){
			
			isMasked = false;
			
			da += effect * 2 * dot.A; // ‚`‚ÍŠ„‘‰ÁŽZ(Œ©‚¦•û‚ª‹}Œƒ‚É’á‰º‚·‚é‚Ì‚ð–h‚®j
			dr += effect * dot.R;
			dg += effect * dot.G;
			db += effect * dot.B;
			
			A=(int)da; R=(int)dr; G=(int)dg; B=(int)db;
			if(A>255) A=255;
			if(R>255) R=255;
			if(G>255) G=255;
			if(B>255) B=255;
		}
	}
	
	static MyDot clipDot;
	protected MyDot[][] dot;
	protected MyDotWithDoubleARGB[][] dot2;
	LayerGroup.Layer parentLayer;
	
	int gridX,gridY;
	
	public Dots(LayerGroup.Layer parentLayer, int gridX ,int gridY){
		
		this.parentLayer = parentLayer;
		
		clipDot=new MyDot(255,0,0,0);
		
		this.gridX=gridX; this.gridY=gridY;
		
		initialDots();	
	}
	
	public Dots(int gridX ,int gridY){
		
		this.parentLayer = null;
		
		clipDot=new MyDot(255,0,0,0);
		
		this.gridX=gridX; this.gridY=gridY;
		
		initialDots();	
	}

	public void initialDots(){
		
		if(parentLayer != null){
		
			dot = null;
			dot = new MyDot[gridX][gridY];
		
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++)
					dot[x][y] = new MyDot(0,0,0,0);
				
			}
		}
		else{
			
			dot2 = null;
			dot2 = new MyDotWithDoubleARGB [gridX][gridY];
		
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++)
					dot2[x][y] = new MyDotWithDoubleARGB(0,0,0,0);
			}
		
			dot = (MyDot[][])dot2;
		
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++)
					dot[x][y] = (MyDot)dot2[x][y];
			}
		}
	}
	
	public void maskALL(){
		
		for(int x=0; x<gridX; x++)
			for(int y=0; y<gridY; y++)
				dot2[x][y].isMasked = true;
	}

	public boolean isMasked(int x, int y){
		
		return dot2[x][y].isMasked;
	}

	public int getColor(int x,int y){
		
		if(!checkPosRange(x,y)) return -1;
		
		return dot[x][y].getColor();
	}
	
	public int[] getARGB(int x,int y){
		
		if(!checkPosRange(x,y)) return null;
		
		int[] argb = new int[4];
		argb[0]=dot[x][y].A;
		argb[1]=dot[x][y].R;
		argb[2]=dot[x][y].G;
		argb[3]=dot[x][y].B;
		
		return argb;
	}
	
	public void copy(Dots srcDots){
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++)
				 dot[x][y].copy(srcDots.dot[x][y]);
		}
	}

	public boolean setARGB(int x, int y, int[] argb){
		
		if(!checkPosRange(x,y)) return false;
		
		dot[x][y].setARGB(argb[0], argb[1], argb[2], argb[3]);
		
		return true;
	}
	
	public boolean setColor(int x,int y,int color){
		
		if(!checkPosRange(x,y)) return false;
		
		dot[x][y].setColor(color);
		
		return true;
	}
	
	public boolean setARGBWithEnabledAlpha(int x, int y, int[] argb){
		
		if(!checkPosRange(x,y)) return false;
		
		dot[x][y].setARGBWithEnabledAlpha(argb[0], argb[1], argb[2], argb[3]);
		
		return true;
	}
	
	public boolean setColorWithEnabledAlpha(int x,int y,int color){
		
		if(!checkPosRange(x,y)) return false;
		
		dot[x][y].setColorWithEnabledAlpha(color);
		
		return true;
	}
	
	public boolean clip(int x,int y){
		
		if(!checkPosRange(x,y)) return false;
		
		clipDot.copy(dot[x][y]);
		
		return true;
	}
	
	public boolean paste(int x,int y){
		
		if(!checkPosRange(x,y)) return false;
		
		dot[x][y].copy(clipDot);
		
		return true;
	}
	
	public void renewalAlpha(int alpha){
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++)
				
				if(dot[x][y].A!=0){
					dot[x][y].A=alpha;
				}
		}
	}
	
	public void requestRenewalCompoLayer(int x,int y){
		
		parentLayer.requestRenewalCompoLayer(x, y);
	}
	
	public void compoDotWithBMP(LayerGroup.Layer sourceLayer, int x, int y){
				
		int Cd = dot[x][y].A;
		int Cs = 255-Cd;
		
		int Ad=dot[x][y].A;
		int Rd=dot[x][y].R;
		int Gd=dot[x][y].G;
		int Bd=dot[x][y].B;
				
		int[] sourceARGB = sourceLayer.dots.getARGB(x, y);
		int[] resultARGB = new int[4];
		
		int As=sourceARGB[0];
		int Rs=sourceARGB[1];
		int Gs=sourceARGB[2];
		int Bs=sourceARGB[3];
		
		resultARGB[0]=(Cd*Ad+Cs*As)/255;
		resultARGB[1]=(Cd*Rd+Cs*Rs)/255;
		resultARGB[2]=(Cd*Gd+Cs*Gs)/255;
		resultARGB[3]=(Cd*Bd+Cs*Bs)/255;
		
		sourceLayer.setDotWithBMP(x, y, resultARGB);
	}
	
	public boolean checkPosRange(int x, int y){
		
		if(x<0 | y<0 | x>=gridX | y>=gridY ) return false;
	
		return true;
	}
	
	public void verticalMirroring(){
		
		for(int x=0; x<gridX/2; x++){
			for(int y=0; y<gridY; y++){
				
				clip(x,y);
				dot[x][y].copy(dot[gridX-1-x][y]);
				paste(gridX-1-x,y);
			}
		}
	}
	
	public void horizontalMirroring(){
	
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY/2; y++){
				
				clip(x,y);
				dot[x][y].copy(dot[x][gridY-1-y]);
				paste(x,gridY-1-y);
			}
		}
	}
	
	public void fill(int color){
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++){
				
				setColor(x,y,color);
			}
		}
	}
	
	public void expandTo(Dots dots){
		
		double rateX = (double)dots.gridX/(double)gridX;
		double rateY = (double)dots.gridY/(double)gridY;
		
		for(int x=0; x<gridX; x++){
			for(int y=0; y<gridY; y++){
				
				double left = x * rateX;
				double right = (x+1) * rateX;
				
				int minXInt,maxXInt;
				if((int)left == left) minXInt=(int)left;
				else minXInt = (int)(left + 1);
				maxXInt = (int)right;
				
				double leftSpare=0,rightSpare=0;
				leftSpare = minXInt-left;
				rightSpare = right - maxXInt;
				
				double top = y * rateY;
				double bottom = (y+1) * rateY;
				
				int minYInt,maxYInt;
				if((int)top == top) minYInt=(int)top;
				else minYInt = (int)(top + 1);
				maxYInt = (int)bottom;
				
				double topSpare=0,bottomSpare=0;
				topSpare = minYInt-top;
				bottomSpare = bottom - maxYInt;
				
				for(int x2=minXInt-1; x2<maxXInt+1; x2++)
					for(int y2=minYInt-1; y2<maxYInt+1; y2++){
						
						double effect = 1;
						
						if(x2==minXInt-1) 
							if(minXInt>maxXInt) effect *= (rightSpare+leftSpare-1);
							else effect *=leftSpare;
						else if(x2==maxXInt) effect *=rightSpare;
						
						if(y2==minYInt-1) 
							if(minYInt>maxYInt) effect *= (topSpare+bottomSpare -1);
							else effect *=topSpare;
						else if(y2==maxYInt) effect *=bottomSpare;
						
						if(dots.checkPosRange(x2, y2))
							dots.dot2[x2][y2].addEffect(effect, dot[x][y]);
						
					}
			}
		}
	}
	
	public void rotateTo(Dots rotateDots, int rotateAngle){
		
		double radian = 3.1415926 / 180;
		int c = (int)(Math.cos(rotateAngle * radian) * 1024);
		int s = (int)(Math.sin(rotateAngle * radian) * 1024);
		
		int resolution = 8;
		double rec2 = (double)1 / (resolution * resolution);
		
		int dst_cx8192 = rotateDots.gridX * 4 * 1024;
		int dst_cy8192 = rotateDots.gridY * 4 * 1024;
		int src_cx8 = gridX * 4;
		int src_cy8 = gridY * 4;
		
		for(int x=0; x<gridX*resolution; x++){
			for(int y=0; y<gridY*resolution; y++){
				
				int srcX8 = x + 4 - src_cx8;
				int srcY8 = y + 4 - src_cy8;
				
				int dstX = (srcX8 *  c + srcY8 * s + dst_cx8192) >>13;
				int dstY = (srcX8 * -s + srcY8 * c + dst_cy8192) >>13;
				
				if(rotateDots.checkPosRange(dstX, dstY))
					rotateDots.dot2[dstX][dstY].addEffect
									(rec2, dot[x/resolution][y/resolution]);
			}
		}
	}
	
	public void rotate2To(Dots rotateDots, int rotateAngle, boolean isInterpolated){
		
		rotateAngle *=-1;
		
		double radian = 3.1415926 / 180;
		int c = (int)(Math.cos(rotateAngle * radian)*1024);
		int s = (int)(Math.sin(rotateAngle * radian)*1024);
		
		int dst_cx = rotateDots.gridX / 2;
		int dst_cy = rotateDots.gridY / 2;
		int src_cx = gridX / 2;
		int src_cy = gridY / 2;
		
		for(int x=0; x<rotateDots.gridX; x++){
			for(int y=0; y<rotateDots.gridY; y++){
				
				int dstX = x - dst_cx; 
				int dstY = y - dst_cy;
				
				int srcX1024 = dstX *  c + dstY * s + (src_cx << 10);
				int srcY1024 = dstX * -s + dstY * c + (src_cy << 10);
				
				int dx256 = (srcX1024 & 0x3FF) >> 2;
				int dy256 = (srcY1024 & 0x3FF) >> 2;
				
				int x2 = srcX1024 >> 10;
				int y2 = srcY1024 >> 10;
				
				if(checkPosRange(x2,y2) && dx256>=0 && dy256>=0){
					if(isInterpolated)
						rotateDots.dot2[x][y].copy(interpolate(x2, y2, dx256, dy256));
					else rotateDots.dot2[x][y].copy(dot[x2][y2]);
					rotateDots.dot2[x][y].isMasked = false;
				}
			}
		}
	}

	private MyDot interpolate(int x, int y, int dx, int dy){
		
		int A,R,G,B;
		int A0,R0,G0,B0;
		int A1,R1,G1,B1;
		int A2,R2,G2,B2;
		int A3,R3,G3,B3;
		
		A0 = dot[x][y].A;
		R0 = dot[x][y].R;
		G0 = dot[x][y].G;
		B0 = dot[x][y].B;
		
		if(checkPosRange(x+1,y)){
			A1 = dot[x+1][y].A;
			R1 = dot[x+1][y].R;
			G1 = dot[x+1][y].G;
			B1 = dot[x+1][y].B;
		}
		else{
			A1=R1=G1=B1=0;
			dx=0;
		}
		
		if(checkPosRange(x,y+1)){
			A2 = dot[x][y+1].A;
			R2 = dot[x][y+1].R;
			G2 = dot[x][y+1].G;
			B2 = dot[x][y+1].B;
		}
		else{
			A2=R2=G2=B2=0;
			dy=0;
		}
		
		if(checkPosRange(x+1,y+1)){
			A3 = dot[x+1][y+1].A;
			R3 = dot[x+1][y+1].R;
			G3 = dot[x+1][y+1].G;
			B3 = dot[x+1][y+1].B;
		}
		else{
			A3=R3=G3=B3=0;
		}
		
		A=interpolateCalc(A0,A1,A2,A3,dx,dy);
		R=interpolateCalc(R0,R1,R2,R3,dx,dy);
		G=interpolateCalc(G0,G1,G2,G3,dx,dy);
		B=interpolateCalc(B0,B1,B2,B3,dx,dy);
		
		return new MyDot(A,R,G,B);
	}
	
	private int interpolateCalc(int C0, int C1, int C2, int C3, int dx, int dy){
		
		int D = ((255-dy) * ((255-dx)*C0 + dx*C1) + dy * ((255-dx)*C2 + dx*C3)) >>16;
		
		return D;
	}
}
