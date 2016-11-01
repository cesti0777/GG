package org.androidtown.actionbar;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.neokree.materialtabs.MaterialTab;
import com.neokree.materialtabs.MaterialTabHost;
import com.neokree.materialtabs.MaterialTabListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    private static final String ARG_PARAM1 = "온도";
    private static final String ARG_PARAM2 = "정상온도 임계값";
    private static final String ARG_PARAM3 = "심박수";
    private static final String ARG_PARAM4 = "Z축";

    private static final String ARG_PARAM7 = "정상 심박수 임계값";
    private static final String ARG_PARAM8 = "거리값";
    private CountDownTimer timer;

    /**
     * 설정 액티비티를 띄우기 위한 요청코드
     */
    public static final int REQUEST_CODE_SETTINGS = 1001;

    ViewPager pager;
    ViewPagerAdapter pagerAdapter;
    MaterialTabHost tabhost;

    // 형준--------------------
    static final int REQUEST_ENABLE_BT = 10;  // 사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 App으로 알려줄때 식별자로 사용됨 (0보다 커야함)
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    /**
     * BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     * 연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     * 현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevie;
    BluetoothDevice mRemoteDevie2;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    BluetoothSocket mSocket2 = null;
    OutputStream mOutputStream2 = null;
    InputStream mInputStream2 = null;

    char mCharDelimiter = '\n';

    //온도값 변수들
    int tempertmp1; //정수형태 온도반영
    double tempertmp2; //소수형태 온도반영
    double temperature; //센싱된 온도값(가공전)
    boolean checkFever = true; //고열 알림주기 체크
    int fnotiCount = 0;
    boolean checkSlight = true; //미열 알림주기 체크
    boolean checkHypothermia = true; //저체온 알림주기 체크
    int hnotiCount = 0;

    Thread mWorkerThread = null;
    byte[] readBuffer; //아기용
    int readBufferPosition;

    Thread mWorkerThread2 = null;
    byte[] readBuffer2; //접근용
    int readBufferPosition2;

    Thread tCheckThread = null;
    Thread pCheckThread = null;


    //심박수 변수들
    int heartbeat;
    boolean checkHeart = true;
    int pnotiCount = 0;

    //움직임 변수들
    int movedata;
    boolean checkMove = true;

    //거리값 변수들
    int distance;
    boolean checkAccess = true; // 접근 알림주기 체크

    //움직임 알람에 대한 주기 설정
    int accdatas[] = new int[100];  //움직임 알람에 대한 갑 저장 배열
    int acccount = 0; //배열 인자값
    int accnoti; //움직임 인자값 계산 하기 위한 변수
    int acctemp;
    boolean accdanger = false;
    boolean accok = false; //중복 알람을 방지하기 위한 boolean값

    boolean alarmOnOff;
    boolean tAlarm;
    boolean pAlarm;
    boolean mAlarm;
    boolean aAlarm;
    double SEEKBAR_VALUE_T;
    int SEEKBAR_VALUE_P;

    String tAlarmPeriod = "1";
    String pAlarmPeriod = "1";
    String mAlarmPeriod = "1";
    int tAlarmPeriod_int = 1;
    int pAlarmPeriod_int = 1;
    int mAlarmPeriod_int = 1;
    boolean tAbnormal = false;
    int tAbnormalCnt = 0;
    boolean pAbnormal = false;
    int pAbnormalCnt = 0;
    boolean mAbnormal = false;
    int mAbnormalCnt = 0;


    final List<String> selectedItems = new ArrayList<String>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize a new LayoutParams object
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, // Width of TextView
                ActionBar.LayoutParams.WRAP_CONTENT // Height of TextView
        );

        // Get the action bar
        try {
            // Set the action bar background color
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(Color.WHITE)
            );
            getSupportActionBar().setLogo(R.drawable.icare);
            // Set the action bar display option
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            // Set the action bar custom view
            View mCustomView = LayoutInflater.from(this).inflate(R.layout.actionbar_main, null);
            getSupportActionBar().setCustomView(mCustomView);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        tAlarmPeriod = "1";
        pAlarmPeriod = "1";
        mAlarmPeriod = "1";
        tAlarmPeriod_int = 1;
        pAlarmPeriod_int = 1;
        mAlarmPeriod_int = 1;
        tAbnormal = false;
        tAbnormalCnt = 0;
        pAbnormal = false;
        pAbnormalCnt = 0;
        mAbnormal = false;
        mAbnormalCnt = 0;


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
        tabhost.setTextColor(Color.WHITE);
        // 탭의 배경색을 지정합니다.
        tabhost.setPrimaryColor(Color.rgb(69, 103, 227));

        // 탭을 추가합니다.
        for (int i = 0; i < 4; i++) {
            MaterialTab tab = tabhost.newTab();
            tab.setText(pagerAdapter.getPageTitle(i));
            tab.setTabListener(new ProductTabListener());
            tabhost.addTab(tab);
        }

        // 처음 선택된 탭을 지정합니다.
        tabhost.setSelectedNavigationItem(0);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
    // RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

    /**
     * 사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     * 첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     * 두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     * 세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) { // 블루투스 활성화 상태
            selectDevice();
        } else if (resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
            Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    /**
     * 뷰페이저 어댑터를 정의합니다.
     */


    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public Fragment getItem(int index) {
            Fragment frag = null;

            if (index == 0) {
                frag = new Fragment01();
                Bundle args = new Bundle();
                args.putDouble(ARG_PARAM1, temperature);
                args.putDouble(ARG_PARAM2, SEEKBAR_VALUE_T);
                frag.setArguments(args);
            } else if (index == 1) {
                frag = new Fragment02();
                Bundle args = new Bundle();
                args.putInt(ARG_PARAM3, heartbeat);
                args.putInt(ARG_PARAM7, SEEKBAR_VALUE_P);
                frag.setArguments(args);
            } else if (index == 2) {
                frag = new Fragment03();
                Bundle args = new Bundle();
                args.putInt(ARG_PARAM4, movedata);

                frag.setArguments(args);
            } else if (index == 3) {
                frag = new Fragment04();
                Bundle args = new Bundle();
                args.putInt(ARG_PARAM8, distance);
                frag.setArguments(args);
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

            int position = tab.getPosition();
            pagerAdapter.notifyDataSetChanged();
            pager.setCurrentItem(tab.getPosition());

        }

        @Override
        public void onTabReselected(MaterialTab tab) {
            int position = tab.getPosition();
            pagerAdapter.notifyDataSetChanged();
            pager.setCurrentItem(tab.getPosition());
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
        switch (curId) {
            case R.id.menu_settings:
                Intent intent = new Intent(getApplicationContext(), Setting.class);
                startActivity(intent);
                break;
            case R.id.bluetooth_button:
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                ActionMenuItemView a = (ActionMenuItemView) findViewById(R.id.bluetooth_button);
                Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Typo_SsangmunDongB.ttf");
                //현재 Bluetooth가 켜져 있는지, 혹은 켜는 중인지 확인 한다.
                if (adapter.getState() == BluetoothAdapter.STATE_TURNING_ON ||
                        adapter.getState() == BluetoothAdapter.STATE_ON) {
                    adapter.disable();   // Bluetooth Off
                    a.setText("OFF");
                    a.setTextColor(Color.BLACK);
                    a.setTextSize(20);
                    a.setTypeface(typeface);
                } else {
                    adapter.enable();     // Bluetooth On
                    a.setText("ON");
                    a.setTextColor(Color.BLUE);
                    a.setTextSize(20);
                    a.setTypeface(typeface);
                    checkBluetooth();
                }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification(int i) {  //알람 만들어주는 녀석

        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, Fragment01.class), PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getService(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        //TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
        //taskStackBuilder.addNextIntent(intent);

        NotificationCompat.Builder nBuilder;
        nBuilder = new NotificationCompat.Builder(this);
        Notification notification;
        NotificationManager nm;
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nBuilder.setContentTitle("아이케어");
        nBuilder.setSmallIcon(R.drawable.babycrying);
        nBuilder.setContentIntent(pendingIntent);
        nBuilder.setAutoCancel(true);
        nBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        nBuilder.setVibrate(new long[]{100, 2000, 500, 2000});
        nBuilder.setLights(Color.RED, 400, 400);

        switch (i) {
            case 1: //고온 알람
                // pendingIntent = taskStackBuilder.getPendingIntent(111, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이 체온이 높아요!");
                notification = nBuilder.build();
                nm.notify(0, notification);
                break;
            case 2: //미열 알람
                //  pendingIntent = taskStackBuilder.getPendingIntent(222, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이에게 미열이 있는거 같아요!");
                notification = nBuilder.build();
                nm.notify(1, notification);
                break;
            case 3: //저온 알람
                // pendingIntent = taskStackBuilder.getPendingIntent(333, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이 체온이 낮아요!");
                notification = nBuilder.build();
                nm.notify(2, notification);
                break;
            case 4: //심박수알람
                //pendingIntent = taskStackBuilder.getPendingIntent(444, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이 심박수가 이상해요!");
                notification = nBuilder.build();
                nm.notify(3, notification);
                break;
            case 5: //자세알람
                // pendingIntent = taskStackBuilder.getPendingIntent(555, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이 자세가 이상한거 같아요!");
                notification = nBuilder.build();
                nm.notify(4, notification);
                break;
            case 6: //접근알람
                //pendingIntent = taskStackBuilder.getPendingIntent(666, PendingIntent.FLAG_NO_CREATE);
                nBuilder.setContentIntent(pendingIntent);
                nBuilder.setContentText("아이 위험구역에 접근한거 같아요!");
                notification = nBuilder.build();
                nm.notify(5, notification);
                break;
        }
    }

    public void chagePrefValue() {
        //기본 SharedPreference를 가져옴. (PreferenceActivity에서 설정한 pref)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Preference 자료 수정을 위하여 editor 생성
        alarmOnOff = prefs.getBoolean("alarmOnOff", false);
        tAlarm = prefs.getBoolean("tAlarm", false);
        pAlarm = prefs.getBoolean("pAlarm", false);
        mAlarm = prefs.getBoolean("mAlarm", false);
        aAlarm = prefs.getBoolean("aAlarm", false);
        SEEKBAR_VALUE_T = prefs.getInt("SEEKBAR_VALUE_T", 36);
        SEEKBAR_VALUE_P = prefs.getInt("SEEKBAR_VALUE_P", 70);
        tAlarmPeriod = prefs.getString("tAlarmPeriod", "1");
        pAlarmPeriod = prefs.getString("pAlarmPeriod", "1");
        mAlarmPeriod = prefs.getString("mAlarmPeriod", "1");
        tAlarmPeriod_int = Integer.valueOf(tAlarmPeriod);
        pAlarmPeriod_int = Integer.valueOf(pAlarmPeriod);
        mAlarmPeriod_int = Integer.valueOf(mAlarmPeriod);
//      Toast.makeText(getApplicationContext(),
//            "값 : " + alarmOnOff
//                  + " " + tAlarm
//                  + " " + pAlarm
//                  + " " + mAlarm
//                  + " " + aAlarm
//                  + " " + SEEKBAR_VALUE_T
//                  + " " + SEEKBAR_VALUE_P
//					+ " " + tAlarmPeriod
//            , Toast.LENGTH_SHORT).show();

    }

/////----------------통신------------------////

    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
    BluetoothDevice getDeviceFromBondedList(String name) {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for (BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if (name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }


    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
    //        실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
    void connectToSelectedDevice(String selectedDeviceName, String selectedDeviceName2) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);  //아기용
        mRemoteDevie2 = getDeviceFromBondedList(selectedDeviceName2);  //접근용
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        UUID uuid2 = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            mSocket2 = mRemoteDevie2.createRfcommSocketToServiceRecord(uuid2);
            mSocket2.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

            mOutputStream2 = mSocket2.getOutputStream();
            mInputStream2 = mSocket2.getInputStream();

            // 데이터 수신 준비.
            beginListenForData();
            beginListenForData2();

        } catch (Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }


    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() {
        final Handler handler = new Handler();
        final Handler handler2 = new Handler();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.

        readBufferPosition2 = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer2 = new byte[1024];            // 수신 버퍼.

        // 문자열 수신 쓰레드.
        mWorkerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
                // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
                // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
                        int byteAvailable = mInputStream.available();   // 수신 데이터 확인
                        //아기용
                        if (byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes = new byte[byteAvailable];

                            // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                            mInputStream.read(packetBytes);
                            for (int i = 0; i < byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == mCharDelimiter) {
                                    //String str = "test";
                                    //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
                                    //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, 0, encodedBytes.length - 1);

                                    int testdata = Integer.valueOf(data);

                                    if (testdata > 10000 && testdata < 20000) {  //온도값 가공
                                        testdata = testdata - 10000;
                                        tempertmp1 = testdata / 10;
                                        tempertmp2 = (testdata % 10) * (0.1);
                                        temperature = tempertmp1 + tempertmp2;
                                    } else if (testdata > 20000 && testdata < 30000) { //심박수값
                                        heartbeat = testdata - 20000;
                                    } else if (testdata > 90000 && testdata < 110000) { //z값
                                        if (testdata < 100000) {
                                            movedata = testdata - 100000;
                                        } else {
                                            movedata = testdata - 100000;
                                        }
                                    }

                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        // 수신된 문자열 데이터에 대한 처리.

                                        @Override
                                        public void run() {
                                            chagePrefValue();

                                            double minNormal_t = SEEKBAR_VALUE_T - 1;
                                            double maxNormal_t = SEEKBAR_VALUE_T + 1;
                                            int minNormal_p = SEEKBAR_VALUE_P - 5;
                                            int maxNormal_p = SEEKBAR_VALUE_P + 5;

                                            pagerAdapter.notifyDataSetChanged();
                                            Log.v("tabn", "" + tAbnormal);
                                            if (tAlarm == true) {
                                                if (tAbnormal == false) {
                                                    if (temperature < minNormal_t || temperature > maxNormal_t) {
                                                        tAbnormal = true;
                                                        if (temperature > maxNormal_t + 1) {
                                                            createNotification(1);
                                                        } else if (temperature > maxNormal_t && temperature < maxNormal_t + 1) {
                                                            createNotification(2);
                                                        } else if (temperature < minNormal_t) {
                                                            createNotification(3);
                                                        }
                                                    }
                                                } else {
                                                    if (tAbnormalCnt == tAlarmPeriod_int * 3 * 60) {
                                                        tAbnormalCnt = 0;
                                                        tAbnormal = false;

                                                    }
                                                    Log.v("period값", "" + tAlarmPeriod_int);
                                                    Log.v("CNT값", "" + tAbnormalCnt);
                                                    tAbnormalCnt++;
                                                }
                                            } else {
                                                tAbnormalCnt = 0;
                                                tAbnormal = false;
                                            }

                                            if (pAlarm == true) {
                                                if (pAbnormal == false) {
                                                    if (heartbeat > 10) {
                                                        if (heartbeat < minNormal_p || heartbeat > maxNormal_p) {
                                                            pAbnormal = true;
                                                            createNotification(4);
                                                        }
                                                    }
                                                } else {
                                                    if (pAbnormalCnt == pAlarmPeriod_int * 3 * 60) {
                                                        pAbnormalCnt = 0;
                                                        pAbnormal = false;
                                                    }
                                                    pAbnormalCnt++;
                                                }
                                            } else {
                                                pAbnormalCnt = 0;
                                                pAbnormal = false;
                                            }


                                            if (mAlarm == true) {

                                                if (movedata > 1000) {
                                                    mAbnormal = true;
                                                    createNotification(5);
                                                }
                                            }


                                        }
                                    });
                                } else {
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

    void beginListenForData2() {


        readBufferPosition2 = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer2 = new byte[512];            // 수신 버퍼.
        mWorkerThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
                // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
                // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.

                        int byteAvailable2 = mInputStream2.available();   // 수신 데이터 확인


                        //접근용
                        if (byteAvailable2 > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes2 = new byte[byteAvailable2];

                            // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                            mInputStream2.read(packetBytes2);
                            for (int i = 0; i < byteAvailable2; i++) {
                                byte b2 = packetBytes2[i];
                                if (b2 == mCharDelimiter) {
                                    //String str = "test";
                                    //Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                                    byte[] encodedBytes2 = new byte[readBufferPosition2];
                                    //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
                                    //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
                                    System.arraycopy(readBuffer2, 0, encodedBytes2, 0, encodedBytes2.length);

                                    final String data2 = new String(encodedBytes2, 0, encodedBytes2.length - 1);

                                    int testdata2 = Integer.valueOf(data2);

                                    distance = testdata2;
                                    if (aAlarm == true) {
                                        if (distance < 15)
                                            createNotification(6);
                                    }
                                    readBufferPosition2 = 0;

                                } else {
                                    readBuffer2[readBufferPosition2++] = b2;
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
        mWorkerThread2.start();


    }


    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if (mPariedDeviceCount == 0) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        final List<String> listItems = new ArrayList<String>();
        for (BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }

        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.

        builder.setMultiChoiceItems(items,
                new boolean[]{false, false, false, false, false, false, false, false},
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(items[which].toString());
                        } else {
                            selectedItems.remove(items[which]);
                        }
                    }
                });
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedItems.size() == 0) {
                    Toast.makeText(MainActivity.this, "선택된 블루투스가 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    connectToSelectedDevice(selectedItems.get(1), selectedItems.get(0));
                }
            }
        });
        // 우측(Negative Button) 에 출력될 버튼을 설정한다.
        // 해당 버튼을 클릭했을 때 처리할 문장을 리스너로 구성해준다.
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // 대화상자를 취소하여 닫는다.
                dialog.cancel();
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
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
            finish();  // 앱종료
        } else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
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
            } else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }


    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try {
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
            mWorkerThread2.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream2.close();
            mSocket2.close();

        } catch (Exception e) {
        }
        super.onDestroy();
    }


}