/*
 * Copyright 2022 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.killerror.mlkit.geekbrainscan.codescanner.kotlin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Html
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.common.MlKitException
import com.google.mlkit.samples.codescanner.R
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.util.Locale


/** Demonstrates the code scanner powered by Google Play Services. */
class MainActivity : AppCompatActivity() {

  private var allowManualInput = false
  private var enableAutoZoom = false
  private var barcodeResultView: TextView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    barcodeResultView = findViewById(R.id.barcode_result_view)

    val img = findViewById<ImageView>(R.id.imageView3)
    img.setOnTouchListener { view, motionEvent ->
      when (motionEvent.action) {
        MotionEvent.ACTION_DOWN -> {
          val scaleDownX = ObjectAnimator.ofFloat(img, "scaleX", 0.9f)
          val scaleDownY = ObjectAnimator.ofFloat(img, "scaleY", 0.9f)
          scaleDownX.setDuration(100)
          scaleDownY.setDuration(100)

          val scaleDown = AnimatorSet()
          scaleDown.play(scaleDownX).with(scaleDownY)

          scaleDown.start()

          false
        }
        MotionEvent.ACTION_UP -> {
          val scaleDownX = ObjectAnimator.ofFloat(img, "scaleX", 1.0f)
          val scaleDownY = ObjectAnimator.ofFloat(img, "scaleY", 1.0f)
          scaleDownX.setDuration(100)
          scaleDownY.setDuration(100)

          val scaleDown = AnimatorSet()
          scaleDown.play(scaleDownX).with(scaleDownY)

          scaleDown.start()

          false
        }
        else -> false
      }
    }
    img.setOnClickListener {
//      Toast.makeText(this@MainActivity, "You clicked on ImageView.", Toast.LENGTH_SHORT).show()
      val optionsBuilder = GmsBarcodeScannerOptions.Builder()
      if (allowManualInput) {
        optionsBuilder.allowManualInput()
      }
      if (enableAutoZoom) {
        optionsBuilder.enableAutoZoom()
      }
      val gmsBarcodeScanner = GmsBarcodeScanning.getClient(this, optionsBuilder.build())
      gmsBarcodeScanner
        .startScan()
        .addOnSuccessListener { barcode: Barcode ->
          //barcodeResultView!!.text = getSuccessfulMessage(barcode)
          barcodeResultView!!.setText(Html.fromHtml(getSuccessfulMessage(barcode)))
        }
        .addOnFailureListener { e: Exception -> barcodeResultView!!.text = getErrorMessage(e) }
        .addOnCanceledListener {
          barcodeResultView!!.text = getString(R.string.error_scanner_cancelled)
        }
    }


  }


  fun onAllowManualInputCheckboxClicked(view: View) {
    allowManualInput = (view as CheckBox).isChecked
  }

  fun onEnableAutoZoomCheckboxClicked(view: View) {
    enableAutoZoom = (view as CheckBox).isChecked
  }

  fun onScanButtonClicked(view: View) {
    val optionsBuilder = GmsBarcodeScannerOptions.Builder()
    if (allowManualInput) {
      optionsBuilder.allowManualInput()
    }
    if (enableAutoZoom) {
      optionsBuilder.enableAutoZoom()
    }
    val gmsBarcodeScanner = GmsBarcodeScanning.getClient(this, optionsBuilder.build())
    gmsBarcodeScanner
      .startScan()
      .addOnSuccessListener { barcode: Barcode ->
        barcodeResultView!!.text = getSuccessfulMessage(barcode)
      }
      .addOnFailureListener { e: Exception -> barcodeResultView!!.text = getErrorMessage(e) }
      .addOnCanceledListener {
        barcodeResultView!!.text = getString(R.string.error_scanner_cancelled)
      }
  }

  override fun onSaveInstanceState(savedInstanceState: Bundle) {
    savedInstanceState.putBoolean(KEY_ALLOW_MANUAL_INPUT, allowManualInput)
    savedInstanceState.putBoolean(KEY_ENABLE_AUTO_ZOOM, enableAutoZoom)
    super.onSaveInstanceState(savedInstanceState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    allowManualInput = savedInstanceState.getBoolean(KEY_ALLOW_MANUAL_INPUT)
    enableAutoZoom = savedInstanceState.getBoolean(KEY_ENABLE_AUTO_ZOOM)
  }

  private fun getSuccessfulMessage(barcode: Barcode): String {
    var barcodeFormat = ""
    when (barcode.format) {
      Barcode.FORMAT_UNKNOWN -> {
        barcodeFormat = "Неизвестный формат"
      }
      Barcode.FORMAT_CODE_128 -> {
        barcodeFormat = "Штрихкод стандарта <b>Code 128</b>"
      }
      Barcode.FORMAT_CODE_39 -> {
        barcodeFormat = "Штрихкод стандарта <b>Code 39</b>"
      }
      Barcode.FORMAT_CODE_93 -> {
        barcodeFormat = "Штрихкод стандарта <b>Code 93</b>"
      }
      Barcode.FORMAT_CODABAR -> {
        barcodeFormat = "Штрихкод стандарта <b>CODABAR</b>"
      }
      Barcode.FORMAT_DATA_MATRIX -> {
        barcodeFormat = "Матричный штрихкод <b>DataMatrix</b>"
      }
      Barcode.FORMAT_EAN_13 -> {
        barcodeFormat = "Европейский код товара <b>EAN-13</b>"
      }
      Barcode.FORMAT_EAN_8 -> {
        barcodeFormat = "Европейский код товара <b>EAN-8</b>"
      }
      Barcode.FORMAT_ITF -> {
        barcodeFormat = "Штрихкод стандарта <b>ITF-14</b>"
      }
      Barcode.FORMAT_QR_CODE -> {
        barcodeFormat = "Матричный штрихкод <b>QR</b>"
      }
      Barcode.FORMAT_UPC_A -> {
        barcodeFormat = "Universal Product Code тип <b>UPC-A</b>"
      }
      Barcode.FORMAT_UPC_E -> {
        barcodeFormat = "Universal Product Code тип <b>UPC-E</b>"
      }
      Barcode.FORMAT_PDF417 -> {
        barcodeFormat = "Штрихкод стандарта <b>Portable Data File</b>"
      }
      Barcode.FORMAT_AZTEC -> {
        barcodeFormat = "Штрихкод стандарта <b>Aztec Code</b>"
      }
      else -> { barcodeFormat = "Ошибка определения формата" }
    }
    var barcodeType = ""
    when (barcode.valueType) {
      Barcode.TYPE_UNKNOWN -> {
        barcodeType = "Неизвестный тип данных"
      }
      Barcode.TYPE_CONTACT_INFO -> {
        barcodeType = "Контактная информация"
      }
      Barcode.TYPE_EMAIL -> {
        barcodeType = "Адрес электронной почты"
      }
      Barcode.TYPE_ISBN -> {
        barcodeType = "Код ISBN"
      }
      Barcode.TYPE_PHONE -> {
        barcodeType = "Номер телефона"
      }
      Barcode.TYPE_PRODUCT -> {
        barcodeType = "Код продукта"
      }
      Barcode.TYPE_SMS -> {
        barcodeType = "Данные SMS"
      }
      Barcode.TYPE_TEXT -> {
        barcodeType = "Простой текст"
      }
      Barcode.TYPE_URL -> {
        barcodeType = "Веб-ссылка"
      }
      Barcode.TYPE_WIFI -> {
        barcodeType = "Данные для подключения к Wi-Fi"
      }
      Barcode.TYPE_GEO -> {
        barcodeType = "Геоданные"
      }
      Barcode.TYPE_CALENDAR_EVENT -> {
        barcodeType = "События календаря"
      }
      Barcode.TYPE_DRIVER_LICENSE -> {
        barcodeType = "Водительские права"
      }
      else -> { barcodeType = "Ошибка определения типа данных" }
    }
    val barcodeValue =
      String.format(
        Locale.US,
        "<br/><br/> Данные штрихкода:<br/> <b>%s</b> <br/><br/>  Формат: <b>%s</b> <br/> Тип данных: <b>%s</b>",
        barcode.displayValue,
        barcodeFormat,
        barcodeType
      )
    return getString(R.string.barcode_result, barcodeValue)
  }

  private fun getErrorMessage(e: Exception): String? {
    return if (e is MlKitException) {
      when (e.errorCode) {
        MlKitException.CODE_SCANNER_CAMERA_PERMISSION_NOT_GRANTED ->
          getString(R.string.error_camera_permission_not_granted)
        MlKitException.CODE_SCANNER_APP_NAME_UNAVAILABLE ->
          getString(R.string.error_app_name_unavailable)
        else -> getString(R.string.error_default_message, e)
      }
    } else {
      e.message
    }
  }

  companion object {
    private const val KEY_ALLOW_MANUAL_INPUT = "allow_manual_input"
    private const val KEY_ENABLE_AUTO_ZOOM = "enable_auto_zoom"
  }
}
