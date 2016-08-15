package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment03 extends Fragment {
	private static final String ARG_PARAM3 = "움직임";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int num = 100;
		View view = inflater.inflate(R.layout.frag03, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text03);
		tv.setText("움직임:"+num);
		ImageView iv = (ImageView) view.findViewById(R.id.iv);
		if(num >= 1000)
		{
			iv.setImageResource(R.drawable.active);
			tv.setText("아이는 활동중:"+num);
		}
		else
		{
			iv.setImageResource(R.drawable.sleep);
			tv.setText("아이는 수면중:"+num);
		}
		return view;
	}

}