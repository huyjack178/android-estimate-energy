package project.huyjack.traincpu.screenoff;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import project.huyjack.traincpu.Manager.ScreenManager;

/**
 * Listener class which detects phone state changes and locks the device when a
 * call is initiated or answered.
 */
public class ScreenOffPhoneListener extends PhoneStateListener {
	private Context context;
	private boolean incomingCall;

	public ScreenOffPhoneListener(Context context) {
		this.context = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		switch (state) {
		case TelephonyManager.CALL_STATE_RINGING:
			incomingCall = true;
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			turnScreenOff(incomingCall ? 400 : 1200);
			incomingCall = false;
			break;
		default:
			incomingCall = false;
			break;
		}
	}

	private void turnScreenOff(final long delay) {
		Thread t = new Thread() {
			public void run() {
				try {
					sleep(delay);
				} catch (InterruptedException e) {
					/* ignore this */
				}
				ScreenManager.turnScreenOff(context);
			}
		};
		t.start();
	}
}
