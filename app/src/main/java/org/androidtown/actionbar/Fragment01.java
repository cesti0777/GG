package org.androidtown.actionbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Fragment01 extends Fragment {
	private static final String ARG_PARAM1 = "온도";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int num = getArguments().getInt(ARG_PARAM1);

		View view = inflater.inflate(R.layout.frag01, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text01);
		tv.setText("온도:"+num);
		return view;
	}

}