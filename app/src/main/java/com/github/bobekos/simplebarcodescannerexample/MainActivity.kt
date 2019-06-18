package com.github.bobekos.simplebarcodescannerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.bobekos.simplebarcodescanner2.overlay.BarcodeRectOverlay
import com.github.bobekos.simplebarcodescanner2.utils.CameraFacing
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
            .setFacing(CameraFacing.BACK)
            .getObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Log.e("MainActivity", "Barcode: ${it.displayValue}")
                    Toast.makeText(this, "Jo jo", Toast.LENGTH_SHORT).show()
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
