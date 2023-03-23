package com.hwp.tv

import android.os.Bundle
import android.util.Log
import android.view.FocusFinder
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.AppUtils
import com.owen.tvrecyclerview.widget.TvRecyclerView
import com.owen.tvrecyclerview.widget.V7GridLayoutManager
import me.jessyan.autosize.utils.AutoSizeUtils
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private var mItems: MutableList<AppUtils.AppInfo>? = null
    var rvMainApps: TvRecyclerView? = null
    var oldPosition = -1
    var sortStatus = false
    var appAdapter: AppAdapter? = null
    private val columnsNum = 6
    val TAG = MainActivity::class.java.simpleName + "-------------"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        val appsInfo = initData()
        mItems = appsInfo
        appAdapter = AppAdapter()
        appAdapter?.setDatas(appsInfo)
        rvMainApps?.adapter = appAdapter
        setListener()
    }

    private fun setListener() {
        appAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                Log.d(TAG, "onItemRangeMoved---$fromPosition")
                Log.d(TAG, "onItemRangeMoved---$toPosition")

                if (sortStatus) {
                    rvMainApps?.postDelayed(
                        Runnable {
                            moveViewSet(rvMainApps?.layoutManager?.findViewByPosition(fromPosition),false)
                            moveViewSet(rvMainApps?.layoutManager?.findViewByPosition(toPosition),true)
                            rvMainApps?.clearFocus()
                            rvMainApps?.selectedPosition = toPosition
                            rvMainApps?.requestDefaultFocus()
                        },
                        100
                    )
                }
            }
        })
        appAdapter?.setOnItemKeyListener(object : AppAdapter.OnItemKeyListener {
            override fun onKey(var1: View, var2: Int, var3: Int) {
                val count = mItems?.size ?: 0
                if (count <= 0) {
                    return
                }
                if (var3 == KeyEvent.KEYCODE_MENU) {
                    if (!sortStatus) {
                        sortStatus = true
                        oldPosition = var2
                        moveViewSet(var1, hasFocus = true)
                    }
                    return
                }
                if (var3 == KeyEvent.KEYCODE_BACK) {
                    if (sortStatus) {
                        moveViewSet(var1, hasFocus = false)
                        sortStatus = false
                        return
                    }
                }
                if (var3 == KeyEvent.KEYCODE_DPAD_LEFT || var3 == KeyEvent.KEYCODE_DPAD_RIGHT ||
                    var3 == KeyEvent.KEYCODE_DPAD_UP || var3 == KeyEvent.KEYCODE_DPAD_DOWN
                ) {
                    if (sortStatus) {
                        when (var3) {
                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                Log.d(TAG, oldPosition.toString())
                                if (oldPosition.rem(columnsNum) == 0) {
                                    return
                                }
                                val newPosition = oldPosition - 1
                                Log.d(TAG, newPosition.toString())
                                if (newPosition < 0) {
                                    return
                                }
                                move(oldPosition, newPosition)
                                oldPosition = newPosition
                            }
                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                Log.d(TAG, oldPosition.toString())
                                val newPosition = oldPosition + 1
                                if (newPosition.rem(columnsNum) == 0) {
                                    return
                                }
                                Log.d(TAG, newPosition.toString())
                                if (newPosition > mItems?.size!! - 1) {
                                    return
                                }
                                move(oldPosition, newPosition)
                                oldPosition = newPosition
                            }
                            KeyEvent.KEYCODE_DPAD_UP -> {
                                Log.d(TAG, oldPosition.toString())
                                val newPosition = oldPosition - columnsNum
                                Log.d(TAG, newPosition.toString())
                                if (newPosition < 0) {
                                    return
                                }
                                move(oldPosition, newPosition)
                                oldPosition = newPosition
                            }
                            KeyEvent.KEYCODE_DPAD_DOWN -> {
                                Log.d(TAG, oldPosition.toString())
                                val newPosition = oldPosition + columnsNum
                                Log.d(TAG, newPosition.toString())
                                if (newPosition > mItems?.size!! - 1) {
                                    return
                                }
                                move(oldPosition, newPosition)
                                oldPosition = newPosition
                            }
                        }
                    }
                }
            }
        })
        rvMainApps?.setOnItemListener(object : TvRecyclerView.OnItemListener {
            override fun onItemPreSelected(
                parent: TvRecyclerView?,
                itemView: View?,
                position: Int
            ) {
                Log.d(TAG, "onItemPreSelected")
            }

            override fun onItemSelected(parent: TvRecyclerView?, itemView: View?, position: Int) {
                Log.d(TAG, "onItemSelected")
            }
            override fun onItemClick(parent: TvRecyclerView?, itemView: View?, position: Int) {
            }
        })
    }

    private fun initData(): MutableList<AppUtils.AppInfo> {
        var appsInfo = AppUtils.getAppsInfo()
        appsInfo = appsInfo.filterNot { it.isSystem }
        appsInfo = appsInfo.filterNot { it.icon == null }
        return appsInfo
    }

    private fun initView() {
        rvMainApps = findViewById(R.id.rv_main_apps)
        rvMainApps?.layoutManager = V7GridLayoutManager(this, columnsNum)
        rvMainApps?.setSpacingWithMargins(
            AutoSizeUtils.dp2px(this, 15F),
            AutoSizeUtils.dp2px(this, 36F)
        )
    }

    fun move(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) {
            // no-op
            return
        }
        val item: AppUtils.AppInfo? = mItems?.removeAt(fromPosition)
        item?.let { mItems?.add(toPosition, it) }
        appAdapter?.notifyItemMoved(fromPosition, toPosition)
    }
    private fun moveViewSet(focusView: View?, hasFocus: Boolean) {
        if (!sortStatus || focusView == null) {
            return
        }
        val ivMoveUp = focusView.findViewById<View>(R.id.m_sort_up_iv)
        val ivMoveDown = focusView.findViewById<View>(R.id.m_sort_down_iv)
        val ivMoveLeft = focusView.findViewById<View>(R.id.m_sort_left_iv)
        val ivMoveRight = focusView.findViewById<View>(R.id.m_sort_right_iv)
        val viewList = listOf(ivMoveUp, ivMoveDown, ivMoveLeft, ivMoveRight)
        if (hasFocus) {
            viewList.forEach { v ->
                val d = when (v?.id) {
                    R.id.m_sort_up_iv -> View.FOCUS_UP
                    R.id.m_sort_down_iv -> View.FOCUS_DOWN
                    R.id.m_sort_left_iv -> View.FOCUS_LEFT
                    R.id.m_sort_right_iv -> View.FOCUS_RIGHT
                    else -> {
                        null
                    }
                }
                d?.let {
                    val c = hasOtherNextFocus(focusView, d)
                    v?.visibility = c
                    v?.bringToFront()
                }
            }
        } else {
            viewList.forEach { it?.visibility = View.GONE }
        }
    }
    private fun hasOtherNextFocus(focusView: View, direction: Int): Int {
        try {
            val finder = FocusFinder.getInstance()
            val view = finder.findNextFocus(focusView.parent as ViewGroup, focusView, direction)
            return if (view == null) View.GONE else View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return View.GONE
    }
}
