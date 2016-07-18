package org.androidtown.actionbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.neokree.materialtabs.MaterialTab;
import com.neokree.materialtabs.MaterialTabHost;
import com.neokree.materialtabs.MaterialTabListener;


/**
 * 툴바에 탭을 설정하는 방법을 알 수 있습니다.
 * 
 * @author Mike
 */
public class MainActivity extends ActionBarActivity {

	/**
	 * 설정 액티비티를 띄우기 위한 요청코드
	 */
	public static final int REQUEST_CODE_SETTINGS = 1001;

	ViewPager pager;
	ViewPagerAdapter pagerAdapter;
	MaterialTabHost tabhost;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tabhost = (MaterialTabHost) this.findViewById(R.id.tabhost);
		pager = (ViewPager) this.findViewById(R.id.pager);

		// 뷰페이저 어댑터를 만듭니다.
		pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tabhost.setSelectedNavigationItem(position);
			}
		});

		// 탭의 글자색을 지정합니다.
		tabhost.setTextColor(Color.RED);

		// 탭의 배경색을 지정합니다.
		tabhost.setPrimaryColor(Color.CYAN);

		// 탭을 추가합니다.
		for (int i = 0; i < pagerAdapter.getCount(); i++) {
			MaterialTab tab = tabhost.newTab();
			tab.setText(pagerAdapter.getPageTitle(i));
			tab.setTabListener(new ProductTabListener());

			tabhost.addTab(tab);
		}

		// 처음 선택된 탭을 지정합니다.
		tabhost.setSelectedNavigationItem(0);
	}

	/**
	 * 뷰페이저 어댑터를 정의합니다.
	 */
	private class ViewPagerAdapter extends FragmentStatePagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public Fragment getItem(int index) {
			Fragment frag = null;

			if (index == 0) {
				frag = new Fragment01();
			} else if (index == 1) {
				frag = new Fragment02();
			} else if (index == 2) {
				frag = new Fragment03();
			} else if (index == 3) {
				frag = new Fragment04();
			}

			return frag;
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "온도";
				case 1:
					return "심박수";
				case 2:
					return "움직임";
				case 3:
					return "접근금지";
				default:
					return null;
			}
		}

	}

	/**
	 * 탭을 선택했을 때 처리할 리스너 정의
	 */
	private class ProductTabListener implements MaterialTabListener {

		public ProductTabListener() {

		}

		@Override
		public void onTabSelected(MaterialTab tab) {
			pager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabReselected(MaterialTab tab) {

		}

		@Override
		public void onTabUnselected(MaterialTab tab) {

		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		int curId = item.getItemId();
		switch(curId){
			case R.id.menu_settings:
				Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
				startActivityForResult(intent, REQUEST_CODE_SETTINGS);
				break;
		}
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_SETTINGS) {
			Toast toast = Toast.makeText(getBaseContext(),
					"onActivityResult() 호출됨. 요청 코드 : " + requestCode + ", 결과 코드 : "
							+ resultCode, Toast.LENGTH_LONG);
			toast.show();

			if (resultCode == RESULT_OK) {

			}

		}

	}
}

