package com.example.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.*
import android.net.Uri
import android.os.BatteryManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = javaClass.simpleName

//    val saioService = SaioService(this)

    val MIN_BRIGHTNESS = 30
    val MAX_BRIGHTNESS = 255

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButtonCollection()

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //讓螢幕不關閉休眠

        //是否開啟修改權限
        if (Settings.System.canWrite(this)) {
            brightness()
            setSound()
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.setData(Uri.parse("package:$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /**按鈕應用集合*/
    private fun ButtonCollection() {
        btn.setOnClickListener(this)
        btn11.setOnClickListener(this)
        btn12.setOnClickListener(this)
        btn21.setOnClickListener(this)
        btn22.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(button: View?) {
        when (button) {
            btn -> {
                val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
                val percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

                tv.text = "Battery Percentage is $percentage %"
            }

            btn11 -> {
//                saioService.sleepTime = SaioService.PM_SLEEP_TIME_30_MIN
            }

            btn12 -> {
//                saioService.sleepTime = SaioService.PM_SLEEP_TIME_10_MIN
            }

            btn21 -> {
                if (Settings.System.canWrite(this)) {
                    setAutoBrightnessOpen(this)
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    intent.setData(Uri.parse("package:$packageName"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }

            btn22 -> {
                if (Settings.System.canWrite(this)) {
                    setAutoBrightnessClose(this)
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                    intent.setData(Uri.parse("package:$packageName"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }


            btn3 -> {
                val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

//                mAudioManager.setStreamVolume(STREAM_MUSIC,4,0)

                //媒體音樂音量
                val musicSoundmMax = mAudioManager.getStreamMaxVolume(STREAM_MUSIC)
                val musicSoundcurrent = mAudioManager.getStreamVolume(STREAM_MUSIC)

                //鬧鐘音量
                val systemSoundMax = mAudioManager.getStreamMaxVolume(STREAM_ALARM)
                val systemSoundcurrent = mAudioManager.getStreamVolume(STREAM_ALARM)

                //鈴聲音量
                val ringSoundMax = mAudioManager.getStreamMaxVolume(STREAM_SYSTEM)
                val ringSoundcurrent = mAudioManager.getStreamVolume(STREAM_SYSTEM)

                tv3.text = "Music max $musicSoundmMax  curremt $musicSoundcurrent" + "\n" +
                        "Alarm max $systemSoundMax curremt $systemSoundcurrent" + "\n" +
                        "SystemRing max $ringSoundMax curremt $ringSoundcurrent"

                Log.e("Music", "max $musicSoundmMax curremt $musicSoundcurrent")
                Log.e("Alarm ring", "max $systemSoundMax curremt $systemSoundcurrent")
                Log.e("SystemRing", "max $ringSoundMax curremt $ringSoundcurrent")
            }

            btn4 -> {
                startActivity(Intent(this, SaioSysInfo::class.java))
            }

            btn5 -> {
                startActivity(Intent(this, WifiScanner::class.java))
            }
        }
    }

    /**設定音效*/
    private fun setSound() {
        val mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        //媒體音樂音量
        val musicSoundmMax = mAudioManager.getStreamMaxVolume(STREAM_MUSIC)
        val musicSoundcurrent = mAudioManager.getStreamVolume(STREAM_MUSIC)
        setSoundSeekBar(mAudioManager, seekBar1, musicSoundmMax, musicSoundcurrent, STREAM_MUSIC)

        //鬧鐘音量
        val systemSoundMax = mAudioManager.getStreamMaxVolume(STREAM_ALARM)
        val systemSoundcurrent = mAudioManager.getStreamVolume(STREAM_ALARM)
        setSoundSeekBar(mAudioManager, seekBar2, systemSoundMax, systemSoundcurrent, STREAM_ALARM)

        //鈴聲音量
        val ringSoundMax = mAudioManager.getStreamMaxVolume(STREAM_SYSTEM)
        val ringSoundcurrent = mAudioManager.getStreamVolume(STREAM_SYSTEM)
        setSoundSeekBar(mAudioManager, seekBar3, ringSoundMax, ringSoundcurrent, STREAM_SYSTEM)
    }

    /**音量控制SeekBar*/
    private fun setSoundSeekBar(audioManager: AudioManager, seekBar: SeekBar, soundMax: Int, soundOrigin: Int, streamType: Int){
        val originMusicSound: Int = soundOrigin

        seekBar.progress = originMusicSound
        seekBar.max = soundMax

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seeBar: SeekBar?, progress: Int, fromUser: Boolean) {

                audioManager.setStreamVolume(streamType, progress, FLAG_PLAY_SOUND)//當前模式改變聲音大小
            }

            override fun onStartTrackingTouch(seeBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seeBar: SeekBar?) {
            }
        })
    }

    /**SeekBar螢幕亮度調節*/
    private fun brightness() {
        val oldBrightness: Int = getSysScreenBrightness() //當前系統亮度
        val progress = oldBrightness //seekbar範圍值 0-255 代表的亮度值30-255

        seekBar.setProgress(if (progress < 0) 0 else progress)
        seekBar.max = MAX_BRIGHTNESS - MIN_BRIGHTNESS // 最大值：225

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val brightness = progress + MIN_BRIGHTNESS

                setSysScreenBrightness(brightness)
            }

            override fun onStartTrackingTouch(progress: SeekBar?) {
            }

            override fun onStopTrackingTouch(progress: SeekBar?) {
            }
        })
    }

    /**設定螢幕亮度*/
    fun setBrightness(activity: Activity, brightness: Int) {
        val lp = activity.window.attributes
        lp.screenBrightness = java.lang.Float.valueOf(brightness.toFloat()) * (1f / 255f)
        activity.window.attributes = lp
    }

    /**獲取螢幕亮度*/
    fun getScreenBrightness(activity: Activity): Int {
        var nowBrightnessValue = 0
        val resolver = activity.contentResolver

        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return nowBrightnessValue
    }

    /**
     * 獲得當前系統的亮度模式
     * SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1為自動調節屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL = 0為手動調節屏幕亮度
     */
    fun getBrightnessMode(): Int {
        var brightnessMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        val resolver = this.contentResolver

        try {
            brightnessMode = Settings.System.getInt(
                resolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
        } catch (e: Exception) {
            Log.e(TAG, "獲取當前螢幕失敗：$e")
        }

        return brightnessMode
    }

    /**設定螢幕自動亮度調節-開*/
    fun setAutoBrightnessOpen(activity: Activity) {
        Settings.System.putInt(
            activity.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        )
    }

    fun setAutoBrightnessClose(activity: Activity) {
        Settings.System.putInt(
            activity.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
    }

    /**
     * 獲取當前系統亮度 0-255
     */
    fun getSysScreenBrightness(): Int {
        var screenBrightness = MAX_BRIGHTNESS

        try {
            val resolver = this.contentResolver

            screenBrightness = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            Log.e(TAG, "獲取當前系統亮度：$e")
        }

        return screenBrightness
    }

    /**設定系統螢幕亮度*/
    fun setSysScreenBrightness(brightness: Int) {
        try {
            val resolver = this.contentResolver
            val uri = Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS)
            Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness)

            resolver.notifyChange(uri, null) //通知改變
        } catch (e: Exception) {
            Log.e(TAG, "設置當前系統亮度值失敗：$e")
        }
    }
}