package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment02 extends Fragment {
	private static final String ARG_PARAM3 = "온도";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
//		int  num = getArguments().getInt(ARG_PARAM3);
		int num=65;
		View view = inflater.inflate(R.layout.frag02, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text02);
		ImageView iv = (ImageView) view.findViewById(R.id.iv2);
		iv.setImageResource(R.drawable.heartbeat);
		tv.setText("  심박이 정상이예요. \n" +
				"   심박수 : "+num+" bpm");
		return view;
	}

}