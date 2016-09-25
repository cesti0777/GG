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
	private static final String ARG_PARAM2 = "정상온도 임계값";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		//double num = getArguments().getInt(ARG_PARAM1);
		double num = getArguments().getDouble(ARG_PARAM1);
		double SEEKBAR_VALUE = getArguments().getDouble(ARG_PARAM2);
		double minNormal = SEEKBAR_VALUE-0.5;
		double maxNormal = SEEKBAR_VALUE+0.5;
//		int num=40;
		View view = inflater.inflate(R.layout.frag01, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text01);
//		tv.setText("움직임:"+num);
		ImageView iv = (ImageView) view.findViewById(R.id.iv1);
		if(num>minNormal && num<maxNormal)
		{
			iv.setImageResource(R.drawable.babysmile);
			tv.setText(" 정상이예요. \n" +
					"     체온 : "+num+" ℃");
		}
		else if(num>maxNormal && num<maxNormal+1)
		{
			iv.setImageResource(R.drawable.babysad);
			tv.setText(" 미열이 있어요. \n" +
					"     체온 : "+num+" ℃");
		}
		else if(num<maxNormal)
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