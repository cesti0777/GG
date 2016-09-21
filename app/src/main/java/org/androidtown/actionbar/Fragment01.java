package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment01 extends Fragment {
	private static final String ARG_PARAM1 = "온도";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int num = getArguments().getInt(ARG_PARAM1);
//		int num=40;
		View view = inflater.inflate(R.layout.frag01, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text01);
//		tv.setText("움직임:"+num);
		ImageView iv = (ImageView) view.findViewById(R.id.iv1);
		if(num<= 37 && num>=36)
		{
			iv.setImageResource(R.drawable.babysmile);
			tv.setText(" 정상이예요. \n" +
					"     체온 : "+num+" ℃");
		}
		else if(num> 37 && num <= 38)
		{
			iv.setImageResource(R.drawable.babysad);
			tv.setText(" 미열이 있어요. \n" +
					"     체온 : "+num+" ℃");
		}
		else if(num<=36 && num>=35)
		{
			iv.setImageResource(R.drawable.babycold);
			tv.setText(" 저체온이예요. \n" +
					"     체온 : "+num+" ℃");
		}
		else
		{
			iv.setImageResource(R.drawable.babycrying);
			tv.setText("   고열이 있어요. \n" +
					"     체온 : "+num+" ℃");
		}
		return view;
	}

}