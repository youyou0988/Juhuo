package com.juhuo.welcome.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.juhuo.tool.JuhuoConfig;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/** ΢�ſͻ��˻ص�activityʾ�� */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	// IWXAPI �ǵ�����app��΢��ͨ�ŵ�openapi�ӿ�
    private IWXAPI api;
    private final String TAG = "WXEntryActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		api = WXAPIFactory.createWXAPI(this, JuhuoConfig.APP_ID_WECHAT, false);
		api.handleIntent(getIntent(), this);
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onReq(BaseReq arg0) { }

	@Override
	public void onResp(BaseResp resp) {
		Log.i(TAG, "resp.errCode:" + resp.errCode + ",resp.errStr:"
				+ resp.errStr);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			//����ɹ�
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Log.i(TAG, "canceled");
			//����ȡ��
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			//����ܾ�
			break;
		}
	}
}
