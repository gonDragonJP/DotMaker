package takada.dotmaker;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class TextDialog extends Dialog{

	Context context;
	
	ImageView sampleImage;
	EditText editText;
	CheckBox[] checkBox = new CheckBox[4];
	CheckBox checkBold;
	CheckBox checkLinear;
	CheckBox checkAntiAlias;
	CheckBox checkSubPixel;
	SeekBar skewBar;
	Button buttonOK, buttonCancel;

	boolean isCheckedBold, isCheckedLinear, isCheckedAntiAlias, isCheckedSubPixel;
	float skew;
	static final int skewBarMax = 100;
	
	Paint paint = new Paint();
	
	public TextDialog(Context context) {
		super(context);
		
		this.context = context;
		this.setTitle("Text");
		
		setContentView(R.layout.textdialog);
		setLayoutMembers(context);	
	}
	
	public String getText(){
		
		return editText.getText().toString();
	}
	
	private void drawSampleText(){
		
		Canvas canvas = new Canvas();
		canvas.setBitmap(sampleBitmap);
		sampleBitmap.eraseColor(Color.BLACK);
		
		String sampleText = editText.getText().toString();
		setTextAttribute();
		
		int width = sampleBitmap.getWidth();
		int height = sampleBitmap.getHeight();
		float textWidth = paint.measureText(sampleText);
		float textShift= -(paint.ascent() + paint.descent())/2; 
		
		canvas.drawText(sampleText, width/2-textWidth/2, height/2+textShift, paint);
		
		sampleImage.setImageBitmap(sampleBitmap);
	}
	
	private void setTextAttribute(){
		
		paint.setTextSize(25);
		paint.setColor(Color.WHITE);
		
		modifyPaintAttribure(paint);
	}
	
	public void modifyPaintAttribure(Paint paint){
		
		paint.setFakeBoldText(isCheckedBold);
		paint.setLinearText(isCheckedLinear);
		paint.setAntiAlias(isCheckedAntiAlias);
		paint.setSubpixelText(isCheckedSubPixel);
		paint.setTextSkewX(skew);
	}
	
	@Override
	public void show(){
		super.show();
		
		editText.setText("Text");
		editText.selectAll();
		skew = 0;
		skewBar.setProgress(skewBarMax/2);
		drawSampleText();
	}
	
	private void setLayoutMembers(Context context){
		
		sampleImage = (ImageView)findViewById(R.id.textdialog_sampleimage);
		editText = (EditText)findViewById(R.id.textdialog_edittext);
		checkBox[0] = (CheckBox)findViewById(R.id.textdialog_boldcheck);
		checkBox[1] = (CheckBox)findViewById(R.id.textdialog_linearcheck);
		checkBox[2] = (CheckBox)findViewById(R.id.textdialog_antialiascheck);
		checkBox[3] = (CheckBox)findViewById(R.id.textdialog_subpixelcheck);
		skewBar = (SeekBar)findViewById(R.id.textdialog_skewbar);
		buttonOK     = (Button)findViewById(R.id.textdialog_buttonok);
		buttonCancel = (Button)findViewById(R.id.textdialog_buttoncancel);
		
		setEditText();
		setSampleImage();
		for(CheckBox cb : checkBox) setCheckBox(cb);
		setSkewBar();
		setButton();
		
	}
	
	Bitmap sampleBitmap;
	
	private void setSampleImage(){
		
		sampleBitmap = Bitmap.createBitmap(200, 60, Config.ARGB_8888);
		sampleBitmap.eraseColor(Color.BLACK);
		sampleImage.setImageBitmap(sampleBitmap);
	}
	
	private void setEditText(){
		
		editText.selectAll();
		editText.setSingleLine();
		editText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable arg0) {
				
				drawSampleText();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}});
	}
	
	private void setCheckBox(CheckBox checkBox){
		
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				onCheckedBox(buttonView.getId(), isChecked);
			}
		});
	}
	
	private void onCheckedBox(int id, boolean isChecked){
		
		switch(id){
		
		case R.id.textdialog_boldcheck:		isCheckedBold      = isChecked; break;
		case R.id.textdialog_linearcheck:	isCheckedLinear    = isChecked; break;
		case R.id.textdialog_antialiascheck:isCheckedAntiAlias = isChecked; break;
		case R.id.textdialog_subpixelcheck: isCheckedSubPixel  = isChecked; break;
			
		}
		
		drawSampleText();
	}
	
	private void setSkewBar(){
		
		skewBar.setMax(skewBarMax);
		skewBar.setProgress(skewBarMax/2);
		skewBar.setThumbOffset(9);
		skewBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
				int a = seekBar.getProgress();
				skew = -(a - skewBarMax/2)/ (float)skewBarMax;
				
				drawSampleText();
			}});
	}
	
	private void setButton(){
		
		buttonOK.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				hide();
			}
		});
		
		buttonCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				hide();
			}
		});
	}
	
}
