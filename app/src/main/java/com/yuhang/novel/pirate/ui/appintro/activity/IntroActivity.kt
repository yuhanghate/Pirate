package com.yuhang.novel.pirate.ui.appintro.activity

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.yuhang.novel.pirate.R
import com.yuhang.novel.pirate.ui.appintro.fragment.SampleSlide


class IntroActivity: AppIntro2() {
    init {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFadeAnimation()

        val sliderPage1 = SliderPage()
        sliderPage1.title = "Welcome!"
        sliderPage1.description = "欢迎使用追书app"
        sliderPage1.imageDrawable = R.drawable.ic_launch_pirate
        sliderPage1.bgColor = Color.parseColor("#F44336")
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = "简洁, 免费"
        sliderPage2.description = "大量的热门小说,免费提供给那些喜欢小说的人"
        sliderPage2.imageDrawable = R.drawable.ic_launch_free
        sliderPage2.bgColor = Color.parseColor("#2196F3")
        addSlide(AppIntroFragment.newInstance(sliderPage2))

        val sliderPage3 = SliderPage()
        sliderPage3.title = "更新快"
        sliderPage3.description =
            "网络小说更新章节快如闪电"
        sliderPage3.imageDrawable = R.drawable.ic_launch_update
        sliderPage3.bgColor = Color.parseColor("#FF5722")
        addSlide(AppIntroFragment.newInstance(sliderPage3))

        val sliderPage4 = SliderPage()
        sliderPage4.title = "探索"
        sliderPage4.description = "我们将会申请相关权限,这可能是一件乏味的事情"
        sliderPage4.imageDrawable = R.drawable.ic_launch_novel
        sliderPage4.bgColor = Color.BLACK
        addSlide(AppIntroFragment.newInstance(sliderPage4))

        // Here we load a string array with a camera permission, and tell the library to request permissions on slide 2
//        askForPermissions(arrayOf(Manifest.permission.CAMERA), 2)

//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
//        showSkipButton(true);
//        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);

    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Do something when users tap on Skip button.
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finish()
    }

}