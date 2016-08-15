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
//		int num = getArguments().getInt(ARG_PARAM1);
		int num=40;
		View view = inflater.inflate(R.layout.frag01, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text01);
//		tv.setText("움직임:"+num);
		ImageView iv = (ImageView) view.findViewById(R.id.iv1);
		if(num<= 37)
		{
			iv.setImageResource(R.drawable.happy);
			tv.setText("정상이예요. " +
					"체온:"+num+"℃");
		}
		else if(num> 37 && num <= 38)
		{
			iv.setImageResource(R.drawable.sad);
			tv.setText("미열이 있어요. " +
					"체온:"+num+"℃");
		}
		else
		{
			iv.setImageResource(R.drawable.cry);
			tv.setText("고열이 있어요. " +
					"체온:"+num+"℃");
		}
		return view;
	}

}