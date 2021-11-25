package com.kudashov.url_comparison

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.*
import java.io.IOException
import kotlin.math.min

class UrlInteractor {

    private val client = OkHttpClient.Builder()
        .build()

    fun getTheDifference(
        url1: String,
        url2: String
    ): Observable<String> {
        val url1Request = getHtmlForUrl(url1)
        val url2Request = getHtmlForUrl(url2)
        val subject = PublishSubject.create<String>()

        Observable.zip(
            url1Request,
            url2Request,
            { htmlPage1, htmlPage2 ->
                compare(htmlPage1, htmlPage2)
            })
            .flatMap { formatPair(it) }
            .subscribeOn(Schedulers.io())
            .subscribe { subject.onNext(it) }

        return subject
    }

    private fun getHtmlForUrlStub(url: String) = Observable.just(url)

    private fun getHtmlForUrl(url: String): Observable<String> {
        val subject: PublishSubject<String> = PublishSubject.create()

        val request: Request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw e
                }

                override fun onResponse(call: Call, response: Response) {
                    val string = response.body?.string()
                    subject.onNext(string)
                    Log.d("TAG", "onResponse: $string")
                }
            })
        return subject
    }

    private fun compare(
        str1: String,
        str2: String
    ): List<Pair<String, String>> {
        val list1 = str1.toCharArray().map { it.toString() }
        val list2 = str2.toCharArray().map { it.toString() }

        val result: MutableList<Pair<String, String>> = ArrayList()

        (0..min(list1.size - 1, list2.size - 1)).forEach { i ->
            if (list1[i] != list2[i])
                result.add(Pair(list1[i], list2[i]))
        }
        return result
    }

    private fun formatPair(listPair: List<Pair<String, String>>): Observable<String> {
        var res = ""

        for (i in listPair) {
            res += "{${i.first}} -> {${i.second}} \n"
        }
        return Observable.just(res)
    }
}