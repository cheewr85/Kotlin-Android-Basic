### Find view efficiently
- 일반적으로 그냥 계속 `findViewById`를 통해서 불러오는 작업이 작은 앱에서는 그렇게 큰 문제가 되지 않음

- 하지만, 어느정도 규모가 생기는 순간 계속해서 `findViewById`를 호출하는 것은 앱의 성능을 저하시킬 것임

- 가장 최적의 방법은 `findViewById`를 한 번 부르고 해당 `View`객체를 필드에 저장하는 것임

- 이때 단순하게 `val diceImage : ImageView = findViewById(R.id.dice_image)`를 할 것이 아니라 설명한대로 미리 필드에 정의를 하는 것임

- 가령 `var diceImage : ImageView? = null` 혹은 `lateinit var diceImage : ImageView`로 그렇게 한 뒤, `onCreate`에서 `diceImage = findViewById(R.id.dice_image)`로 해주는 것이 좋음

- 비교를 하면 이전에는 이렇게 단순하게 불러왔다면 아래와 같이 계속 불러오게 됨

```kotlin
package com.example.diceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener { rollDice() }


    }

    private fun rollDice() {
        val randomInt = (1..6).random()

        val diceImage: ImageView = findViewById(R.id.dice_image)
        val drawableResource = when (randomInt) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        diceImage.setImageResource(drawableResource)
    }
}
```

- 이를 개선해서 한 번 불러오고 이를 필드 값으로 저장해서 필요할 때 불러오게끔 아래와 같이 쓸 수 있음

```kotlin
package com.example.diceroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    
    lateinit var diceImage : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        diceImage = findViewById(R.id.dice_image)

        val rollButton: Button = findViewById(R.id.roll_button)
        rollButton.setOnClickListener { rollDice() }


    }

    private fun rollDice() {
        val randomInt = (1..6).random()
        val drawableResource = when (randomInt) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
        diceImage.setImageResource(drawableResource)
    }
}
```

- 이렇게 개선하면 확연한 속도 차이가 보임

### Understand API levels and compatibility
- 프로젝트 설정 시, Android API level을 가르키는데 이는 각각 OS 버전마다 새로운 feature와 functionality가 존재하고 있음

- 그래서 이 버전의 경우 유저가 업데이트를 할 수 있고 OS 버전을 다른 것을 실행할 수 있음, 그래서 이 최소 API 레벨 또한 설정을 함

- 이는 `build.gradle`에서 설정이 가능함, 여기서 compile, target, min sdk를 설정할 수 있음, 이렇게 설정한 버전보다 더 낮은 버전 활용은 불가능하고, 그리고 높은 버전에서의 기능도 활용을 못 함

- 이런 부분에 있어서 다른 API 레벨로 인한 문제가 생길 수 있음, 그래서 이런 문제를 해결하기 위해서 이전 버전에서 쓰는 라이브러리 등 다양한 라이브러리를 묶어서 Android Jetpack에 담음

- 이 Android Jetpack은 `androidx`라는 이름으로 `import` 되어 있음

- 이런식으로 처리와 더불어서 drawable에 대해서 vector drawable로 xml형태로 저장이 된 것을 알 수 있, 하지만 이는 안드로이드 버전이 다르면 문제가 될 수 있는데 낮은 버전에선 vector image가 `android:src`로 적용이 안될 수 있음

- 그때 `build.gradle`에서 해당 옵션을 추가하고 `vectorDrawables.useSupportLibrary = true` 그 다음 `android:src`에서 `app:srcCompat`로 바꾸어서 이런 호환성 문제를 해결할 수 있음, 그러면 이미지 파일 크기나 문제가 될 수 있는 요소가 해결이 됨

