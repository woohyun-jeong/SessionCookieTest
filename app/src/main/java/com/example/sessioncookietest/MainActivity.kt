package com.example.sessioncookietest

import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sessioncookietest.ui.theme.SessionCookieTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SessionCookieTestTheme {
                Column(modifier = Modifier.fillMaxSize()) {
                    WebViewWithHeaders(url = "http://localhost:8080/")
//                    WebViewWithHeaders(url = "file:///android_asset/index.html")
                }

            }
        }
    }
}

@Composable
fun WebViewWithHeaders(url: String) {
    val headers = mapOf(
        "Authorization" to "Bearer TestTest",
        "Custom-Header" to "CustomValue"
    )

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                // JavaScript 허용
                settings.javaScriptEnabled = true
                // HTTP와 HTTPS 혼합 허용
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.domStorageEnabled = true // DOM 스토리지 활성화
                // 쿠키 관리 설정
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                webViewClient = object : WebViewClient() {
                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: SslErrorHandler?,
                        error: SslError?
                    ) {
                        Log.e("onReceivedSslError", "${error}")

                        // SSL 인증서 오류를 무시하고, 계속 로드하도록 설정
                        handler?.proceed() // 오류 무시하고 계속 진행
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        Log.e("onReceivedError", "${error?.errorCode}")

                        super.onReceivedError(view, request, error)
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        Log.e("onReceivedHttpError", "${errorResponse}")
                        super.onReceivedHttpError(view, request, errorResponse)
                    }
                }
                setWebContentsDebuggingEnabled(true)
                loadUrl(url, headers) // 헤더를 포함해 URL 로드
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    WebViewWithHeaders(url = "http://localhost:8080/")
}