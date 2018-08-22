package takada.dotmaker;

import java.nio.ByteBuffer;
import java.util.List;

import takada.dotmaker.LayerGroup.Layer;

public class StrageDataFormat {
	
	private LayerDialog layerDialog;
	private LayerGroup layerGroup;
	private int gridX,gridY;
	private List<Layer> layers;
	private Palette palette;
	
	int totalByte;
	byte[] dataByte;
	
	public StrageDataFormat(LayerDialog layerDialog){
		
		this.layerDialog=layerDialog;
		this.layerGroup=layerDialog.layerGroup;
		this.layers=layerGroup.layers;
		this.gridX=layerGroup.gridX;
		this.gridY=layerGroup.gridY;
		this.palette = layerDialog.layerGroup.palette;
	}
	
	int groupHeaderByte=4;
	int layerHeader=2;
	
	public void createSaveData(){
		
		byte[] thumbnailData = layerGroup.getCompoLayerBitMapData();
		int thumbnailByte = thumbnailData.length;
		
		byte[] paletteData = palette.getByteForSaveData();
		int paletteByte=paletteData.length;
		
		int dotsByte=gridX*gridY*4;
		int layerNumber=layers.size();
		int currentLayer=layerGroup.currentLayer;
		
		totalByte = thumbnailByte + paletteByte
					+ groupHeaderByte + (dotsByte+layerHeader) * layerNumber;	
		dataByte = new byte[totalByte];
			
		int offset=0;
		
		for(int i=0; i<thumbnailByte; i++){
			
			dataByte[offset]=thumbnailData[i];
			offset++;
		}
		
		for(int i=0; i<paletteByte; i++){
			
			dataByte[offset]=paletteData[i];
			offset++;
		}
		
		dataByte[offset  ]=(byte)gridX;
		dataByte[offset+1]=(byte)gridY;
		dataByte[offset+2]=(byte)layerNumber;
		dataByte[offset+3]=(byte)currentLayer;
			
		offset += groupHeaderByte;
			
		for(int i=0; i<layerNumber; i++){
			
			dataByte[offset  ] = (byte)(layers.get(i).alpha-128);
			boolean isActivated = layers.get(i).isActivated;
			dataByte[offset+1] = isActivated ? (byte)1 : 0;
			
			offset += layerHeader;
			
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++){
					
				int[] argb=new int[4];
				argb = layers.get(i).dots.getARGB(x, y);
					
				dataByte[offset  ]=(byte)(argb[0]-128);
				dataByte[offset+1]=(byte)(argb[1]-128);
				dataByte[offset+2]=(byte)(argb[2]-128);
				dataByte[offset+3]=(byte)(argb[3]-128);
				
				offset += 4;
				}
			}
		}
	}
	
	public LayerGroup setLoadData(int length,byte[] dataByte){
		
		int offset=0;
		
		int thumbnailHeaderByte=Integer.SIZE/8 * 3;
		ByteBuffer header = ByteBuffer.allocate(thumbnailHeaderByte);
		header.put(dataByte, 0, thumbnailHeaderByte);
		header.rewind();
		int thumbnailWidth = header.getInt();
		int thumbnailHeight = header.getInt();
		int thumbnailWidthByte = header.getInt();
		int thumbnailPixelByte = thumbnailHeight * thumbnailWidthByte;
		
		if((thumbnailWidth * 4 == thumbnailWidthByte) && (thumbnailWidth !=0)){
			
			offset += thumbnailHeaderByte + thumbnailPixelByte;
		}
		
		int palNum=(int)dataByte[offset];
		int paletteByte=1 + palNum*4;
		int paletteOffset=offset;
		
		offset += paletteByte;
		
		int gridX =(int)dataByte[offset  ];
		int gridY =(int)dataByte[offset+1];
		int layerNumber =(int)dataByte[offset+2];
		int currentLayer = (int)dataByte[offset+3];
		
		this.gridX = layerDialog.settingDialog.settingGridX = gridX;
		this.gridY = layerDialog.settingDialog.settingGridY = gridY;
		layerGroup = layerDialog.createLayerGroup();
		layers = layerGroup.layers;
		palette = layerGroup.palette;
		
		layerGroup.addLayer(layerNumber-1);
		
		byte[] sendData = new byte[paletteByte];
		for(int i=0; i<paletteByte; i++){
			sendData[i] = dataByte[paletteOffset++];
		}
		palette.setDataFromSaveData(sendData);
		
		offset += groupHeaderByte;
			
		for(int i=0; i<layerNumber; i++){
			
			layers.get(i).alpha = (int)dataByte[offset]+128;
			layers.get(i).isActivated = 
				(dataByte[offset+1] == 1)? true : false;
			
			offset += layerHeader;
			
			for(int x=0; x<gridX; x++){
				for(int y=0; y<gridY; y++){
					
				int[] argb=new int[4];
				
				argb[0]=(int)dataByte[offset  ]+128;
				argb[1]=(int)dataByte[offset+1]+128;
				argb[2]=(int)dataByte[offset+2]+128;
				argb[3]=(int)dataByte[offset+3]+128;
			
				layers.get(i).dots.setARGB(x,y,argb);
					
				offset += 4;
				}
			}
		}
		layerGroup.setCurrentLayer(currentLayer);
		
	return layerGroup;
	}
}
