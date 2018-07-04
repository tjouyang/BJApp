package cn.edu.hnust.bjapp.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ImageButtonWithText extends LinearLayout {
	
	private  CircleImageView   imageViewbutton;
	
	private  TextView   textView;

	public ImageButtonWithText(Context context,AttributeSet attrs) {
		super(context,attrs);
		
		imageViewbutton = new CircleImageView(context, attrs);
		
		imageViewbutton.setPadding(0, 0, 0, 0);

		textView = new TextView(context, attrs);

		textView.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
		
		textView.setPadding(0, 0, 0, 0);
		
		setClickable(true);
		
		setFocusable(true);
		
//		setBackgroundResource(android.R.drawable.btn_default);
		
		setOrientation(LinearLayout.VERTICAL);
		
		addView(imageViewbutton);
		
		addView(textView);
		
	}

	public CircleImageView getImageViewbutton (){
		return imageViewbutton;
	}

	public TextView getTextView(){
		return textView;
	}
}