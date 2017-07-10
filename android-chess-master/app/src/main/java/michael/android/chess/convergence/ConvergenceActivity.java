package michael.android.chess.convergence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import michael.android.chess.*;

public class ConvergenceActivity extends Activity  {
	
	//private ListView _lvStart;
	private RestServer _restServer;
	public static final String TAG = "start";
	protected TextView _tvIpPort;
	protected Button _butStartStop;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.convergence); 

        Log.i(TAG, "onCreate");
        _restServer = null;
        
        _tvIpPort = (TextView)findViewById(R.id.TextViewServerIpPort);
       
        _butStartStop = (Button)findViewById(R.id.ButtonStartStopServer);
        _butStartStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				toggleServer();
			}
        	
        });
        
        Button butTestConvergence = (Button)findViewById(R.id.ButtonTestConvergence);
        butTestConvergence.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Connection con = new Connection();
				con.searchDevice(Connection.TYPE_CONVERGENCE);
			}
        	
        });
        
        Button butTestDial = (Button)findViewById(R.id.ButtonTestDial);
        butTestDial.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Connection con = new Connection();
				con.searchDevice(Connection.TYPE_DIAL);
			}
        	
        });
        
        ((Button)findViewById(R.id.ButtonHelpConvergence)).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(ConvergenceActivity.this, HtmlActivity.class);
				i.putExtra(HtmlActivity.HELP_MODE, "convergence");
				startActivity(i);
			}
        	
        });
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	if(isAlive()){
	        	Intent intent = new Intent(this, start.class);
	        	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
	        	startActivity(intent);
	            return true;
	        }
	    }
        return super.onKeyDown(keyCode, event);
    }
    
    protected String ipPart(String ip){
    	
    	String[] arrTmp = ip.split("\\.");
    	
    	if(arrTmp.length > 0){
    		String res = "";
	    	for(int i = 0; i < arrTmp.length; i++){
	    		if(arrTmp[i].length() == 1){
	    			res += "00" + arrTmp[i];  
	    		} else if(arrTmp[i].length() == 2){
	    			res += "0" + arrTmp[i];
	    		} else {
	    			res += arrTmp[i];
	    		}
	    	}
	    	return res;
    	}
    	return ip;
    }
    
    protected void toggleServer(){
    	try{
			Log.i(TAG, "toggleServer");
			//SharedPreferences prefs = getSharedPreferences("ChessPlayer", MODE_PRIVATE);
			if(isAlive()){
				_restServer.stop();
				_restServer = null;
				_tvIpPort.setText(getString(R.string.msg_server_stopped));
			} else {
				//String sPort = prefs.getString("restServerPort", "8092");
				//_restServer = new RestServer(Integer.parseInt(sPort));
				_restServer = new RestServer(8092);
				if(_restServer.start()){
					String sAddr = RestServer.getIpAddress();
					String sCode = "";
					if(sAddr == null){
						
					} else {
						if(sAddr.startsWith("192.168.1.")){
							sCode += "9" + sAddr.substring(10);
						} else if(sAddr.startsWith("192.168.")){
							sCode += "9" + ipPart(sAddr.substring(8));
						} else if(sAddr.startsWith("10.0.1.")){
							sCode += "1" + sAddr.substring(7);
						} else if(sAddr.startsWith("10.0.")){
							sCode += "2" + ipPart(sAddr.substring(5));
						} else if(sAddr.startsWith("172.16.1.")){
							sCode += "7" + sAddr.substring(9);
						} else if(sAddr.startsWith("172.16.")){
							sCode += "8" + ipPart(sAddr.substring(7));
						}
					}
					if(sCode.length() > 0){
						_tvIpPort.setText(getString(R.string.msg_your_code) + sCode + "\nIP: " + sAddr);
					} else {
						_tvIpPort.setText(getString(R.string.msg_not_valid_ip) + sAddr);
						_restServer.stop();
						_restServer = null;
					}
				} else {
					_restServer = null;
					_tvIpPort.setText(getString(R.string.msg_server));
				}
			}
			
			isAlive();
			
     	} catch(Exception ex){}
    }
    
    protected boolean isAlive(){
    	if(_restServer == null){
    		Log.i(TAG, "isAlive -> restServer = null");
    		_butStartStop.setText(getString(R.string.menu_start));
    		return false;
    	}
    	if(_restServer.isAlive()){
    		Log.i(TAG, "isAlive -> restServer.isAlive = true");
    		_butStartStop.setText(getString(R.string.menu_stop));
    		return true;
    	} else {
    		Log.i(TAG, "isAlive -> restServer.isAlive = false");
    		_butStartStop.setText(getString(R.string.menu_start));
    		return false;
    	}
    }
    
    @Override
    protected void onResume() {
    	
    	Log.i(TAG, "onResume");
    	isAlive();
    	
    	super.onResume();
    }
    
    @Override protected void onPause() {
    	Log.i(TAG, "onPause");
    	super.onPause();
    }
    
    @Override protected void  onDestroy(){
    	Log.i(TAG, "onDestroy");
    	super.onDestroy();
    }
}
