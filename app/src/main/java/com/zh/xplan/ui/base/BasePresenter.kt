package com.zh.xplan.ui.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by zh on 2017/12/7.
 */

abstract class BasePresenter<T : BaseView> {
    protected var view: T? = null

    /**
     * 防止RxJava 内存泄露
     */
    private var mCompositeDisposable: CompositeDisposable? = null

    abstract fun onDestory()

    fun attachView(view: T) {
        this.view = view
    }

    fun detachView() {
        this.view = null
        unDisposable()
    }

    protected fun addDisposable(disposable: Disposable) {
        //如果已经解绑了的话再次添加disposable需要创建新的实例，否则绑定是无效的
        if (mCompositeDisposable == null || mCompositeDisposable?.isDisposed == true) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    /**
     * 在界面销毁后等需要解绑观察者的情况下调用此方法统一解绑，防止Rx造成的内存泄漏
     * CompositeDisposable.clear() 即可切断所有的水管
     */
    private fun unDisposable() {
        mCompositeDisposable?.apply {
            clear()
        }
    }
}
