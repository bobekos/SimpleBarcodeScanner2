package com.github.bobekos.simplebarcodescannerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        disposable = barcodeView
            .getObservable()
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    Log.e("MainActivity", "Barcode: ${it.displayValue}")
                },
                {
                    Log.e("MainActivity", "", it)
                })

    }

    override fun onStop() {
        super.onStop()

        disposable?.dispose()
    }
}
