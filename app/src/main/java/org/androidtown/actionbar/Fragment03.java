package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment03 extends Fragment {
	private static final String ARG_PARAM4 = "Z축";
	private static final String ARG_PARAM5 = "Y축";
	private static final String ARG_PARAM6 = "Z축";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int  num1 = getArguments().getInt(ARG_PARAM4);
		//int  num2 = getArguments().getInt(ARG_PARAM5);
		//int  num3 = getArguments().getInt(ARG_PARAM6);
//		int num = 40;
		View view = inflater.inflate(R.layout.frag03, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text03);
		tv.setText("Z축 : "+num1);
		ImageView iv = (ImageView) view.findViewById(R.id.iv3);
//		if(num >= 1000)
//		{
//			iv.setImageResource(R.drawable.active2);
//			tv.setText(" 아이가 활동중이예요.");
//		}
//		else
//		{
//			iv.setImageResource(R.drawable.sleep2);
//			tv.setText(" 아이가 수면중이예요.");
//		}
		return view;
	}

}