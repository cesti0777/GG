package org.androidtown.actionbar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class Fragment02 extends Fragment {
	private static final String ARG_PARAM3 = "심박수";
	private static final String ARG_PARAM7 = "정상 심박수 임계값";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		int  num = getArguments().getInt(ARG_PARAM3);
		int  SEEKBAR_VALUE_P = getArguments().getInt(ARG_PARAM7);
		int minNormal_p = SEEKBAR_VALUE_P-5;
		int maxNormal_p = SEEKBAR_VALUE_P+5;

		View view = inflater.inflate(R.layout.frag02, container, false);
		TextView tv = (TextView) view.findViewById(R.id.text02);
		ImageView iv = (ImageView) view.findViewById(R.id.iv2);
		iv.setImageResource(R.drawable.heartbeat);
		if(num==0){
			tv.setText("  심박을 제고있어요. \n");
		}

		else if(num>minNormal_p && num<maxNormal_p)
		{
			iv.setImageResource(R.drawable.heartbeat);
			tv.setText("  심박이 정상이예요. \n" +
					"   심박수 : "+num+" bpm");
		}
		else if(num>maxNormal_p)
		{
			iv.setImageResource(R.drawable.heartbeat);
			tv.setText("  심박이 빨라요. \n" +
					"   심박수 : "+num+" bpm");
		}
		else
		{
			iv.setImageResource(R.drawable.heartbeat);
			tv.setText("  심박이 느려요. \n" +
					"   심박수 : "+num+" bpm");
		}
		return view;
	}

}