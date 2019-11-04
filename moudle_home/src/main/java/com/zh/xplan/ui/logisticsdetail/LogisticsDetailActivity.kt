package com.zh.xplan.ui.logisticsdetail

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.zh.xplan.R
import com.zh.xplan.ui.base.BaseActivity
import com.zh.xplan.ui.logisticsdetail.adapter.LogisticsInfoAdapter
import com.zh.xplan.ui.logisticsdetail.bean.LogisticsInfoBean
import kotlinx.android.synthetic.main.activity_logistics_detail.*
import java.util.*

/**
 * 仿物流流程详情效果
 */
class LogisticsDetailActivity : BaseActivity() {

    private val data: List<LogisticsInfoBean>
        get() {
            val data = ArrayList<LogisticsInfoBean>()
            data.add(LogisticsInfoBean("2018-05-20 13:37:57", "客户 签收人: 已签收 感谢使用圆通速递，期待再次为您服务"))
            data.add(LogisticsInfoBean("2018-05-20 09:03:42", "【广东省深圳市宝安区公司】 派件人: 快递员 派件中 派件员电话133111111"))
            data.add(LogisticsInfoBean("2018-05-20 08:27:10", "【广东省深圳市宝安区公司】 已收入"))
            data.add(LogisticsInfoBean("2018-05-20 04:38:32", "【深圳转运中心】 已收入"))
            data.add(LogisticsInfoBean("2018-05-19 01:27:49", "【北京转运中心】 已发出 下一站 【深圳转运中心】"))
            data.add(LogisticsInfoBean("2018-05-19 01:17:19", "【北京转运中心】 已收入"))
            data.add(LogisticsInfoBean("2018-05-18 18:34:28", "【河北省北戴河公司】 已发出 下一站 【北京转运中心】"))
            data.add(LogisticsInfoBean("2018-05-18 18:33:23", "【河北省北戴河公司】 已打包"))
            data.add(LogisticsInfoBean("2018-05-18 18:27:21", "【河北省北戴河公司】 已收件"))
            return data
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logistics_detail)
        initTitle()
        rv_logistics.layoutManager = LinearLayoutManager(this)
        rv_logistics.isFocusable = false
        //解决ScrollView嵌套RecyclerView出现的系列问题
        rv_logistics.isNestedScrollingEnabled = false
        rv_logistics.setHasFixedSize(true)
        rv_logistics.adapter = LogisticsInfoAdapter(this, R.layout.item_logistics, data)
    }

    private fun initTitle() {
        title_bar_back.setOnClickListener{
            finish()
        }
        setStatusBarColor(resources.getColor(R.color.colorPrimaryDark), 0)
    }

}
