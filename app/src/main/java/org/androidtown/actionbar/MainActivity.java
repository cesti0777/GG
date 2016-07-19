package org.androidtown.actionbar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.neokree.materialtabs.MaterialTab;
import com.neokree.materialtabs.MaterialTabHost;
import com.neokree.materialtabs.MaterialTabListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 툴바에 탭을 설정하는 방법을 알 수 있습니다.
 * 
 * @author Mike
 */
public class MainActivity extends ActionBarActivity {
	private static final String ARG_PARAM1 = "온도";

	/**
	 * 설정 액티비티를 띄우기 위한 요청코드
	 */
	public static final int REQUEST_CODE_SETTINGS = 1001;

	ViewPager pager;
	ViewPagerAdapter pagerAdapter;
	MaterialTabHost tabhost;

	// 형준오빠--------------------
	static final int REQUEST_ENABLE_BT = 10;  // 사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 App으로 알려줄때 식별자로 사용됨 (0보다 커야함)
	int mPariedDeviceCount = 0;
	Set<BluetoothDevice> mDevices;
	// 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
	BluetoothAdapter mBluetoothAdapter;
	/**
	 BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
	 연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
	 현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
	 */
	BluetoothDevice mRemoteDevie;
	// 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
	BluetoothSocket mSocket = null;
	OutputStream mOutputStream = null;
	InputStream mInputStream = null;

	char mCharDelimiter =  '\n';

	int temperature, accdata;

	Thread mWorkerThread = null;
	byte[] readBuffer;
	int readBufferPosition;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void createNotification() {  //알람 만들어주는 녀석

		Intent intent = new Intent(MainActivity.this, MainActivity.class);
		TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
		taskStackBuilder.addNextIntent(intent);


		Intent actionIntent = new Intent(MainActivity.this, MainActivity.class);

		PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(123, PendingIntent.FLAG_UPDATE_CURRENT);


		PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 222, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this);
		nBuilder.setContentTitle("베이비시터");
		nBuilder.setContentText("아이가 타고있어요 (활활)");
		nBuilder.setSmallIcon(R.drawable.ic_stat_name);

		nBuilder.setContentIntent(pendingIntent);
		nBuilder.setAutoCancel(true);

		nBuilder.setDefaults(Notification.DEFAULT_SOUND);
		nBuilder.setVibrate(new long[] {100,2000,500,2000});
		nBuilder.setLights(Color.RED, 400, 400);

		nBuilder.addAction(R.drawable.ic_open, "Open", actionPendingIntent);

		Notification notification = nBuilder.build();
		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		nm.notify(0, notification);
	}
	//------------형준오빠

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
		checkBluetooth();
	}

	//형준 블루투스

	// 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
	BluetoothDevice getDeviceFromBondedList(String name) {
		// BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
		BluetoothDevice selectedDevice = null;
		// getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
		// Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
		for(BluetoothDevice deivce : mDevices) {
			// getName() : 단말기의 Bluetooth Adapter 이름을 반환
			if(name.equals(deivce.getName())) {
				selectedDevice = deivce;
				break;
			}
		}
		return selectedDevice;
	}



	//  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
	//        실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
	void connectToSelectedDevice(String selectedDeviceName) {
		// BluetoothDevice 원격 블루투스 기기를 나타냄.
		mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
		// java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
		UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

		try {
			// 소켓 생성, RFCOMM 채널을 통한 연결.
			// createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
			// 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
			mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
			mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

			// 데이터 송수신을 위한 스트림 얻기.
			// BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
			// 1. 데이터를 보내기 위한 OutputStrem
			// 2. 데이터를 받기 위한 InputStream
			mOutputStream = mSocket.getOutputStream();
			mInputStream = mSocket.getInputStream();

			// 데이터 수신 준비.
			beginListenForData();

		}catch(Exception e) { // 블루투스 연결 중 오류 발생
			Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
			finish();  // App 종료
		}
	}

	// 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
	void beginListenForData() {
		final Handler handler = new Handler();

		readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
		readBuffer = new byte[512];            // 수신 버퍼.

		// 문자열 수신 쓰레드.
		mWorkerThread = new Thread(new Runnable()
		{
			@Override
			public void run() {
				// interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
				// interrupt() 메소드는 하던 일을 멈추는 메소드이다.
				// isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
				while(!Thread.currentThread().isInterrupted()) {
					try {
						// InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
						int byteAvailable = mInputStream.available();   // 수신 데이터 확인
						if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
							byte[] packetBytes = new byte[byteAvailable];

							// read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
							mInputStream.read(packetBytes);
							for(int i=0; i<byteAvailable; i++) {
								byte b = packetBytes[i];
								if(b == mCharDelimiter) {
									//String str = "test";
									//Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
									byte[] encodedBytes = new byte[readBufferPosition];
									//  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
									//  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

									final String data = new String(encodedBytes, 0, encodedBytes.length-1);
									//StringTokenizer stdata = new StringTokenizer(data, "\n");
									//String testdata = stdata.nextToken();
									final int testdata = Integer.valueOf(data);

									if(testdata<100){
										temperature = testdata;
									}
									else if(testdata>100){
										accdata = testdata;
									}


									final String temperstr = "Temperature : ";
									final String accstr  = "Acc : ";
									readBufferPosition = 0;

									handler.post(new Runnable(){
										// 수신된 문자열 데이터에 대한 처리.
										@Override
										public void run() {
											// mStrDelimiter = '\n';
//											mEditReceive.setText(mEditReceive.getText().toString() +temperstr+ temperature+ mStrDelimiter);
//											mEditReceive.setText(mEditReceive.getText().toString() +accstr+ accdata+ mStrDelimiter);
											if(temperature>20){
												createNotification();
											}

										}

									});
								}
								else {
									readBuffer[readBufferPosition++] = b;
								}
							}
						}

					} catch (Exception e) {    // 데이터 수신 중 오류 발생.
						Toast.makeText(getApplicationContext(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
						finish();            // App 종료.
					}
				}
			}

		});
		mWorkerThread.start();
	}

	// 블루투스 지원하며 활성 상태인 경우.
	void selectDevice() {
		// 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
		// getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
		mDevices = mBluetoothAdapter.getBondedDevices();
		mPariedDeviceCount = mDevices.size();

		if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
			Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
			finish(); // App 종료.
		}
		// 페어링된 장치가 있는 경우.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("블루투스 장치 선택");

		// 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
		List<String> listItems = new ArrayList<String>();
		for(BluetoothDevice device : mDevices) {
			// device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
			listItems.add(device.getName());
		}
		listItems.add("취소");  // 취소 항목 추가.


		// CharSequence : 변경 가능한 문자열.
		// toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
		final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
		// toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
		listItems.toArray(new CharSequence[listItems.size()]);

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
					Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
					finish();
				}
				else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
					connectToSelectedDevice(items[item].toString());
				}
			}

		});

		builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
		AlertDialog alert = builder.create();
		alert.show();
	}


	void checkBluetooth() { //블루투스 지원하는지 확인함
		/**
		 * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
		 이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
		 */
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null ) {  // 블루투스 미지원
			Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
			finish();  // 앱종료
		}
		else { // 블루투스 지원
			/** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
			 *               true : 지원 ,  false : 미지원
			 */
			if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
				Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
				/**
				 startActivityForResult 함수 호출후 다이얼로그가 나타남
				 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
				 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
				 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
				 */
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
			else // 블루투스 지원하며 활성 상태인 경우.
				selectDevice();
		}
	}



	// onDestroy() : 어플이 종료될때 호출 되는 함수.
	//               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
	@Override
	protected void onDestroy() {
		try{
			mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
			mInputStream.close();
			mSocket.close();
		}catch(Exception e){}
		super.onDestroy();
	}


	// onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
	// RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
	// RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

	/**
	 사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
	 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
	 두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
	 세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch(requestCode) {
			case REQUEST_ENABLE_BT:
				if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
					selectDevice();
				}
				else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
					Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
					finish();
				}
				break;

			case  REQUEST_CODE_SETTINGS:
				Toast toast = Toast.makeText(getBaseContext(),
						"onActivityResult() 호출됨. 요청 코드 : " + requestCode + ", 결과 코드 : "
								+ resultCode, Toast.LENGTH_LONG);
				toast.show();

				if (resultCode == RESULT_OK) {

				}

		}
		super.onActivityResult(requestCode, resultCode, data);

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
				Bundle args = new Bundle();

				args.putInt(ARG_PARAM1, temperature);
				frag.setArguments(args);
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


}

