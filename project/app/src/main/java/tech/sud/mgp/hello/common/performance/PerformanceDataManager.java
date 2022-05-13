package tech.sud.mgp.hello.common.performance;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.view.Choreographer;
import android.view.WindowManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * 性能检测管理类 包括 cpu、ram、fps等
 */
public class PerformanceDataManager {
    private static final String TAG = "PerformanceDataManager";
    private static final int MAX_FRAME_RATE = 60;
    /**
     * 信息采集时间 内存和cpu
     */
    private static final int NORMAL_SAMPLING_TIME = 500;
    /**
     * fps 采集时间
     */
    private static final int FPS_SAMPLING_TIME = 1000;
    private String memoryFileName = "memory.txt";
    private String cpuFileName = "cpu.txt";
    private String fpsFileName = "fps.txt";

    //private int mLastSkippedFrames;

    private Callback callback;

    private int mMaxFrameRate = MAX_FRAME_RATE;
    /**
     * cpu 百分比
     */
    private float mLastCpuRate;
    /**
     * 当前使用内存
     */
    private float mLastMemoryRate;
    /**
     * 当前的帧率
     */
    private int mLastFrameRate = mMaxFrameRate;
    private long mUpBytes;
    private long mDownBytes;
    private long mLastUpBytes;
    private long mLastDownBytes;
    /**
     * 默认的采集时间 通常为1s
     */
    private Handler mNormalHandler;
    private HandlerThread mHandlerThread;
    private float mMaxMemory;
    private Context mContext;
    private ActivityManager mActivityManager;
    private WindowManager mWindowManager;
    private RandomAccessFile mProcStatFile;
    private RandomAccessFile mAppStatFile;
    private Long mLastCpuTime;
    private Long mLastAppCpuTime;
    // 是否是8.0及其以上
    private boolean mAboveAndroidO;
    private static final int MSG_CPU = 1;
    private static final int MSG_MEMORY = 2;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private FrameRateRunnable mRateRunnable = new FrameRateRunnable();

    private void executeCpuData() {
        if (mAboveAndroidO) {
            mLastCpuRate = getCpuDataForO();
            writeCpuDataIntoFile();
        } else {
            mLastCpuRate = getCPUData();
            writeCpuDataIntoFile();
        }
    }

    /**
     * 获取内存数值
     */
    private void executeMemoryData() {
        mLastMemoryRate = getMemoryData();
        writeMemoryDataIntoFile();
    }


    /**
     * 8.0以上获取cpu的方式
     *
     * @return
     */
    private float getCpuDataForO() {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cpuIndex = -1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                int tempIndex = getCPUIndex(line);
                if (tempIndex != -1) {
                    cpuIndex = tempIndex;
                    continue;
                }
                if (line.startsWith(String.valueOf(Process.myPid()))) {
                    if (cpuIndex == -1) {
                        continue;
                    }
                    String[] param = line.split("\\s+");
                    if (param.length <= cpuIndex) {
                        continue;
                    }
                    String cpu = param[cpuIndex];
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"));
                    }
                    float rate = Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
                    return rate;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return 0;
    }

    private int getCPUIndex(String line) {
        if (line.contains("CPU")) {
            String[] titles = line.split("\\s+");
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].contains("CPU")) {
                    return i;
                }
            }
        }
        return -1;
    }


    private static class Holder {
        private static PerformanceDataManager INSTANCE = new PerformanceDataManager();
    }

    private PerformanceDataManager() {
    }

    public static PerformanceDataManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init() {
        mContext = Utils.getApp().getApplicationContext();
        mActivityManager = (ActivityManager) Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            mMaxFrameRate = (int) mWindowManager.getDefaultDisplay().getRefreshRate();
        } else {
            mMaxFrameRate = MAX_FRAME_RATE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
        }
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("handler-thread");
            mHandlerThread.start();
        }
        if (mNormalHandler == null) {
            //loop handler
            mNormalHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_CPU) {
                        if (AppUtils.isAppForeground()) {
                            executeCpuData();
                        }
                        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
                    } else if (msg.what == MSG_MEMORY) {
                        if (AppUtils.isAppForeground()) {
                            executeMemoryData();
                        }
                        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
                    }
                }
            };
        }
    }

    private String getFilePath(Context context) {
        return context.getCacheDir() + File.separator + "doraemon/";
    }

    public void startMonitorFrameInfo() {
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME);
        Choreographer.getInstance().postFrameCallback(mRateRunnable);
    }

    public void stopMonitorFrameInfo() {
        Choreographer.getInstance().removeFrameCallback(mRateRunnable);
        mMainHandler.removeCallbacks(mRateRunnable);
    }

    public void startMonitorCPUInfo() {
        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
    }

    public void destroy() {
        stopMonitorMemoryInfo();
        stopMonitorCPUInfo();
        stopMonitorFrameInfo();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        mHandlerThread = null;
        mNormalHandler = null;
    }


    public void stopMonitorCPUInfo() {
        mNormalHandler.removeMessages(MSG_CPU);
    }

    public void startMonitorMemoryInfo() {
        if (mMaxMemory == 0) {
            mMaxMemory = mActivityManager.getMemoryClass();
        }
        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
    }

    public void stopMonitorMemoryInfo() {
        mNormalHandler.removeMessages(MSG_MEMORY);
    }

    private void writeCpuDataIntoFile() {
        callbackChange(mLastCpuRate, PERFORMANCE_TYPE_CPU);
    }

    private void writeMemoryDataIntoFile() {
        callbackChange(mLastMemoryRate, PERFORMANCE_TYPE_MEMORY);
    }

    private void writeFpsDataIntoFile() {
        callbackChange(mLastFrameRate > 60 ? 60 : mLastFrameRate, PERFORMANCE_TYPE_FPS);
    }

    /**
     * 8.0一下获取cpu的方式
     *
     * @return
     */
    private float getCPUData() {
        long cpuTime;
        long appTime;
        float value = 0.0f;
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = new RandomAccessFile("/proc/stat", "r");
                mAppStatFile = new RandomAccessFile("/proc/" + Process.myPid() + "/stat", "r");
            } else {
                mProcStatFile.seek(0L);
                mAppStatFile.seek(0L);
            }
            String procStatString = mProcStatFile.readLine();
            String appStatString = mAppStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime;
                mLastAppCpuTime = appTime;
                return value;
            }
            value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
            mLastCpuTime = cpuTime;
            mLastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private float getMemoryData() {
        float mem = 0.0F;
        try {
            Debug.MemoryInfo memInfo = null;
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memInfo);
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.

                Debug.MemoryInfo[] memInfos = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (memInfos != null && memInfos.length > 0) {
                    memInfo = memInfos[0];
                }
            }
            int totalPss = 0;
            if (memInfo != null) {
                totalPss = memInfo.getTotalPss();
            }
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    private float parseMemoryData(String data) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.contains("Permission Denial")) {
                break;
            } else {
                String[] lineItems = line.split("\\s+");
                if (lineItems != null && lineItems.length > 1) {
                    String result = lineItems[0];
                    bufferedReader.close();
                    if (!TextUtils.isEmpty(result) && result.contains("K:")) {
                        result = result.replace("K:", "");
                        if (result.contains(",")) {
                            result = result.replace(",", ".");
                        }
                    }
                    // Mem in MB
                    return Float.parseFloat(result) / 1024;
                }
            }
        }
        return 0;
    }

    private float parseCPUData(String data) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.contains("Permission Denial")) {
                break;
            } else {
                String[] lineItems = line.split("\\s+");
                if (lineItems != null && lineItems.length > 1) {
                    bufferedReader.close();
                    return Float.parseFloat(lineItems[0].replace("%", ""));
                }
            }
        }
        return 0;
    }

    public String getCpuFilePath() {
        return getFilePath(mContext) + cpuFileName;
    }

    public String getMemoryFilePath() {
        return getFilePath(mContext) + memoryFileName;
    }

    public String getFpsFilePath() {
        return getFilePath(mContext) + fpsFileName;
    }


    public long getLastFrameRate() {
        return mLastFrameRate;
    }

    public float getLastCpuRate() {
        return mLastCpuRate;
    }

    public float getLastMemoryInfo() {
        return mLastMemoryRate;
    }

    public float getMaxMemory() {
        return mMaxMemory;
    }

    /**
     * 读取fps的线程
     */
    private class FrameRateRunnable implements Runnable, Choreographer.FrameCallback {
        private int totalFramesPerSecond;

        @Override
        public void run() {
            mLastFrameRate = totalFramesPerSecond;
            if (mLastFrameRate > mMaxFrameRate) {
                mLastFrameRate = mMaxFrameRate;
            }
            //保存fps数据
            if (AppUtils.isAppForeground()) {
                writeFpsDataIntoFile();
            }
            totalFramesPerSecond = 0;
            //1s中统计一次
            mMainHandler.postDelayed(this, FPS_SAMPLING_TIME);
        }

        //
        @Override
        public void doFrame(long frameTimeNanos) {
            totalFramesPerSecond++;
            Choreographer.getInstance().postFrameCallback(this);
        }

    }

    public long getLastUpBytes() {
        return mLastUpBytes;
    }

    public long getLastDownBytes() {
        return mLastDownBytes;
    }

    /**
     * cpu
     */
    public static final int PERFORMANCE_TYPE_CPU = 1;
    /**
     * memory
     */
    public static final int PERFORMANCE_TYPE_MEMORY = 2;
    /**
     * fps
     */
    public static final int PERFORMANCE_TYPE_FPS = 3;

    /**
     * 回调
     */
    private synchronized void callbackChange(float value, int performanceType) {
        if (performanceType == PERFORMANCE_TYPE_CPU) {
            LogUtils.d("cpu:" + value);
        } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
            LogUtils.d("memory:" + value);
        } else {
            LogUtils.d("fps:" + value);
        }
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onChange(value, performanceType);
                }
            }
        });
    }

    /** 设置回调监听 */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onChange(float value, int performanceType);
    }

}
