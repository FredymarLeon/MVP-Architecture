package com.fredymarleon.mvparchitecture.mainModule

import com.fredymarleon.mvparchitecture.SportEvent

interface OnClickListener {
    fun onClick(result: SportEvent.ResultSuccess)
}