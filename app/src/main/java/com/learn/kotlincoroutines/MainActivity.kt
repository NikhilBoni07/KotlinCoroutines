package com.learn.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result_#1"
    private val RESULT_2 = "Result_#2"
    val JOB_TIMEOUT = 2050L
    private val CANCEL_MESSAGE = "Cancelling job... Job took longer than $JOB_TIMEOUT ms"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            // IO, Main, Default
            CoroutineScope(IO).launch {
                fakeAPIRequest()
            }

        }
    }

    private suspend fun fakeAPIRequest() {
        withContext(IO) {
            val job = withTimeoutOrNull(JOB_TIMEOUT) {
                val result1 = getResult1FromAPI() // wait
                println("result #1 $result1")
                setTextInMainThread("Got $result1")

                val result2 = getResult2FromAPI() // wait
                println("result #2 $result2")
                setTextInMainThread("Got $result2")
            }
            if (job == null) {
                println("debug: $CANCEL_MESSAGE")
                setTextInMainThread(CANCEL_MESSAGE)
            }
        }

    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    private suspend fun setTextInMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    /*private suspend fun fakeAPIRequest() {
        val result = getResult1FromAPI()
        println("debug: $result")
        setTextInMainThread(result)

        val result2 = getResult2FromAPI()
        setTextInMainThread(result2)
    }*/

    private suspend fun getResult1FromAPI(): String {
        logThread("getResult1FromAPI")
        delay(1000)     // -> will delay only this coroutine
        //Thread.sleep(1000) // -> Will Speep all the coroutines inside the thread
        return RESULT_1
    }

    private suspend fun getResult2FromAPI(): String {
        logThread("getResult2FromAPI")
        delay(1000)
        return RESULT_2
    }

    private fun logThread(methodName: String) {
        println("degub : $methodName : ${Thread.currentThread().name}")
    }
}