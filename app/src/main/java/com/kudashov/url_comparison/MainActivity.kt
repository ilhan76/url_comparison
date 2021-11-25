package com.kudashov.url_comparison

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val urlInteractor = UrlInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
    }

    private fun initListeners() {
        btn.setOnClickListener {
            placeholder.isVisible = true
            urlInteractor.getTheDifference(
                "https://google.com",
                "https://market.yandex.ru/"
                /*first_url.text.toString(),
                second_url.text.toString()*/
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    text.text = it
                    placeholder.isVisible = false
                }
        }
    }
}