package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment04 extends Fragment {
	private static final String ARG_PARAM8 = "거리값";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		//int  num = 0; // 움직임은 아직 세팅 전
		int  num0 = getArguments().getInt(ARG_PARAM8);
		View view = inflater.inflate(R.layout.frag04, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text04);
		ImageView iv = (ImageView) view.findViewById(R.id.iv4);
		if(num0==0){
			iv.setImageResource(R.drawable.safe);
			tv.setText(" 기기와 연결중이에요!");
		}

		else if(num0 < 20)
		{
			iv.setImageResource(R.drawable.warning);
			tv.setText(" 아이가 접근금지 구역에 접근중이에요!");
		}
		else
		{
			iv.setImageResource(R.drawable.safe);
			tv.setText(" 아이가 안전해요.");
		}
		return view;
	}

}