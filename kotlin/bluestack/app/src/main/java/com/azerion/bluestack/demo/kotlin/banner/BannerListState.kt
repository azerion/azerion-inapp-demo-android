package com.azerion.bluestack.demo.kotlin.banner

sealed class BannerListState {
    data class Idle(val autoRefreshEnabled: Boolean = true) : BannerListState()
    data class Loading(val autoRefreshEnabled: Boolean = true) : BannerListState()
    data class Loaded(val visible: Boolean = true, val autoRefreshEnabled: Boolean = true) :
        BannerListState()
}