package com.juhuo.tool;

import android.os.AsyncTask;

/**
 * �����getStop setStop �� AsyncTask��
 *
 * @author Yang Yu
 */
abstract public class CheckStopAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
    private boolean isStop = false;

    synchronized public void setStop()
    {
        isStop = true;
    }

    synchronized public boolean getStop()
    {
        return isStop;
    }
}
