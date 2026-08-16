package com.fredymarleon.mvparchitecture.mainModule.view

import com.fredymarleon.mvparchitecture.common.SportEvent

interface OnClickListener {
    fun onClick(result: SportEvent.ResultSuccess)
}