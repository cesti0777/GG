package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment03 extends Fragment {
	private static final String ARG_PARAM3 = "움직임";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int num = getArguments().getInt(ARG_PARAM3);
		View view = inflater.inflate(R.layout.frag03, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text03);
		tv.setText("움직임:"+num);
		return view;
	}

}