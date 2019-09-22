package com.zh.xplan.ui.menuvideo.kaiyanonlinevideo

import com.module.common.net.rx.NetManager
import com.zh.xplan.ui.base.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by zh on 2017/12/6.
 */
class OnlineVideoPresenter : BasePresenter<OnlineVideoView>() {

    override fun onDestory() {
        detachView()
    }

    fun getOnlineVideos(url: String, date: String, isPullDownRefresh: Boolean?) {
        val disposableObserver = NetManager.get()
                .url(url)
                .params("date", date)
                .build()!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onNext(response: String) {
                            view?.updateOnlineData(true, response, isPullDownRefresh)
                    }

                    override fun onError(e: Throwable) {
                            view?.updateOnlineData(false, e.toString(), isPullDownRefresh)
                    }

                    override fun onComplete() {}
                })
        addDisposable(disposableObserver)
    }
}
