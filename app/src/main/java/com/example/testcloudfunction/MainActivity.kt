package com.example.testcloudfunction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.os.postDelayed
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.testcloudfunction.databinding.ActivityMainBinding
import org.json.JSONObject
import kotlin.concurrent.timer

const val URL : String = "https://us-central1-velvety-calling-395706.cloudfunctions.net/judgePrime"
// manifestにインターネット通信を許可するように記述してるから見てね。
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var grothendickExists: Boolean = false
    private lateinit var grothendickView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.judgeButton.setOnClickListener {
            onClickButton()
        }
    }

    private fun onClickButton() {
        val num = binding.inputNumber.text.toString()
        when(num){
            "" -> displayOnEmpty()
            else -> startAction(num)
        }
    }
    private fun startAction(num: String) {
        val jsonRequest = JSONObject()
        jsonRequest.put("num", num)

        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(
            Request.Method.POST, URL, jsonRequest,
            { response ->
                when(response.getString("result")) {
                    "true" -> displayIsPrime()
                    "false" -> displayIsNotPrime()
                    "Grothendieck" -> displayGrothendieck()
                }
            },
            { error ->
                // エラー処理
                binding.functionText.text = "エラーにゃ！！！！"
            }
        )
        queue.add(request)
    }
    private fun displayOnEmpty() {
        binding.functionText.text = "入力してにゃ！！！"
        changeButton()
    }
    private fun displayIsPrime() {
        binding.functionText.text = "素数にゃ！"
        changeButton()
    }
    private fun displayIsNotPrime() {
        binding.functionText.text = "素数じゃにゃいにゃ！"
        changeButton()
    }
    private fun displayGrothendieck() {
        binding.functionText.text = "素数にゃ(威圧)"
        changeButton()

        grothendickView = ImageView(this).apply{
            setImageResource(R.drawable.grothendieck)
            id = View.generateViewId()
            alpha=0.5f
        }

        binding.constraintLayout.addView(grothendickView, 350, 350)

        // ConstraintSetを生成してConstraintLayoutから制約を複製する
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.constraintLayout)

        // Grothendieck を中央にセット
        constraintSet.connect(grothendickView.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
        constraintSet.connect(grothendickView.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
        constraintSet.connect(grothendickView.id, ConstraintSet.TOP, binding.functionText.id, ConstraintSet.BOTTOM)
        constraintSet.connect(grothendickView.id, ConstraintSet.BOTTOM, binding.judgeButton.id, ConstraintSet.TOP)

        constraintSet.applyTo(binding.constraintLayout)

        grothendickExists = true
    }

    private fun changeButton() {
        binding.judgeButton.text ="り！"
        binding.judgeButton.setOnClickListener {
            binding.functionText.text = "素数かにゃ？"
            binding.judgeButton.text = "教えて素数猫"
            binding.judgeButton.setOnClickListener { onClickButton() }
            if (grothendickExists) { binding.constraintLayout.removeView(grothendickView) }
            grothendickExists = false
        }
    }
}