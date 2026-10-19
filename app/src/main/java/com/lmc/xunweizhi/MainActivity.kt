package com.lmc.xunweizhi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.lmc.xunweizhi.ui.XunWeiApp
import com.lmc.xunweizhi.ui.theme.XunWeiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XunWeiTheme {
                XunWeiApp()
            }
        }
    }
}
