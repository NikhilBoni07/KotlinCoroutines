package com.learn.kotlincoroutines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"
    val JOB_TIMEOUT = 2050L
    private val CANCEL_MESSAGE = "Cancelling job... Job took longer than $JOB_TIMEOUT ms"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()

        button.setOnClickListener {
            setNewText("Clicked!")

            fakeAPIRequest()

        }
    }

    private fun main() {
        CoroutineScope(IO).launch {
            println("Current Thread: ${Thread.currentThread().name}")
            /*for (i in 1..100_000) {
                launch {
                    doNetworkRequest()
                }
            }*/
            launch {
                doNetworkRequest()
            }
        }
    }

    private suspend fun doNetworkRequest() {
        println("Starting network request")
        delay(3000)
        println("Finished network request!")
    }

    /*Parralel Jobs in a coroutine*/
    /*private suspend fun fakeAPIRequest() {
        withContext(IO) {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromAPI()
                    setTextInMainThread("Got $result1")
                }
                println("debug: completed job in $time1 ms")
            }
            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult2FromAPI()
                    setTextInMainThread("Got $result2")
                }
                println("debug: completed job in $time2 ms")
            }
        }

    }*/

    //Parallel jobs Using Async and await
    /*private fun fakeAPIRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val result1: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromAPI()
                }
                val result2: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult2FromAPI()
                }
                setTextInMainThread("Got ${result1.await()}")
                setTextInMainThread("Got ${result2.await()}")
            }
            println("debug: total time elapsed: $executionTime")
        }
    }*/

    //Sequencial backgound thread
    private fun fakeAPIRequest() {
        CoroutineScope(IO).launch {
            val executionTime = measureTimeMillis {
                val resul1 = async {
                    println("debug: Launching job1: ${Thread.currentThread().name}")
                    getResult1FromAPI()
                }.await()

                val resul2 = async {
                    println("debug: Launching job2: ${Thread.currentThread().name}")
                    getResult2FromAPI(resul1)
                }.await()
                println("debug: got Result2: $resul2")
            }
            println("debug: Total elapsed time: $executionTime ms.")
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

    private suspend fun getResult1FromAPI(): String {
        logThread("getResult1FromAPI")
        delay(1000)     // -> will delay only this coroutine
        return RESULT_1
    }

    private suspend fun getResult2FromAPI(resul1: String): String {
        logThread("getResult2FromAPI")
        delay(1700)
        if (resul1.equals(RESULT_1)) {
            return RESULT_2
        }
        throw CancellationException("$RESULT_1 was incorrect")
    }

    private fun logThread(methodName: String) {
        println("degub : $methodName : ${Thread.currentThread().name}")
    }
}