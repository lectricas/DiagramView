package com.polusov.infinitediagram

import android.os.Handler
import android.os.Looper
import android.os.Message
import org.junit.Test
import timber.log.Timber

class SimpleTest {

    @Test
    fun testSomething() {
        class LooperThread : Thread() {
            lateinit var mHandler: Handler

            override fun run() {
                // Initialize the current thread as a Looper
                // (this thread can have a MessageQueue now)
                Looper.prepare()

                mHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        Timber.d("Do Stuff")
                    }
                }

                // Run the message queue in this thread
                Looper.loop()
            }
        }

        val thread = LooperThread()
        thread.start()
    }
}