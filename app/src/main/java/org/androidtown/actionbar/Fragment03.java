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


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int  num1 = getArguments().getInt(ARG_PARAM4);

		View view = inflater.inflate(R.layout.frag03, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text03);
		tv.setText("Z축 : "+num1);
		ImageView iv = (ImageView) view.findViewById(R.id.iv3);
		if(num1 >= 1000)
		{
			iv.setImageResource(R.drawable.babyupsidedown);
			tv.setText(" 아이가 뒤집힌거 같아요!.");
		}
		else
		{
			iv.setImageResource(R.drawable.active2);
			tv.setText(" 아이가 올바르게 누워있어요.");
		}
		return view;
	}

}