### Getting Started
- 해당 앱은 기능적으로 문제가 되지 않음 타이머 설정을 하고 그에 맞게 Toast 메시지가 나옴, 이 부분을 Notification으로 변경할 예정

- 이 타이머 설정에 맞춰서 Notification 설정을 위해서 broadcast receiver를 활용해서 타이머 설정시 AlarmManager가 notification을 보내게 함

- 그리고 Notification에 대해 설정을 진행함

### Add Notifications to your app
- notification을 사용하는 것은 앱의 사용자의 이목을 끌기 가장 효과적인 방법임

- 앱이 실행중이던 아니던 notification은 소리와 진동을 포함하여 화면 상단에 팝업 윈도우를 보여줌

- Notification을 만들기 위해서는 notification builder를 사용해야 하고 제목, 내용, icon을 설정해야함

- 한 번 Builder를 통해 필요한 필드를 채우면 시스템 서비스 NotificationManager가 notification으로써 이 컨텐츠를 보여주는데 도움을 줌

- NotificationManager는 notification을 보내고, 내용을 업데이트하고 notification을 취소하는 역할을 맡음

- 이를 활용하기 위해 NotificationManager 확장 함수를 만들 것임

- 이 확장함수는 아래와 같이 제공됨, notification을 만들고 보낼 수 있음

```kotlin
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
```

- Notification Channel은 notification의 그룹임, 비슷한 타입의 notification을 함께 묶음으로써, 사용자와 개발자 모두 채널에 있는 notification을 사용할 수 있음

- 채널이 한 번 만들어지면 어떤 notification이라도 전달할 수 있음

```kotlin
//NotificationUtils.kt
// TODO: Step 1.2 get an instance of NotificationCompat.Builder
val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.egg_notification_channel_id)
)
```

- 그리고 유저에게 보여줄 내용을 위에서 언급한대로 icon, title, content를 채움, 다른 추가 옵션으로 커스텀 마이징이 가능함

```kotlin
//NotificationUtils.kt
   // TODO: Step 1.3 set title, text and icon to builder
   .setSmallIcon(R.drawable.cooked_egg)
   .setContentTitle(applicationContext.getString(R.string.notification_title))
   .setContentText(messageBody)
```

- 그리고 마지막으로 특정 ID와 함께 `notify`를 호출해서 notification을 만들어야함

- 이 ID는 현재 notification의 인스턴스를 나타내고, 해당 notification을 업데이트하고 취소하는데 필요로 함, 만약 주어진 상황에 하나의 notification을 활성화 시키고 싶다면, 모든 notification에 같은 ID를 적용시킬 수 있음

- 아래와 같이 ID를 상수로 넣고 진행함

```kotlin
//NotificationUtils.kt
   // TODO: Step 1.4 call notify to send the notification
    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
```

- 그런 다음 타이머가 시작될 때 notification을 시작할 수 있게끔 처리를 함, 이 때 이전에 만든 확장함수를 호출해서 적용함, 그래서 이 때 ViewModel에서 적용을 할 때, NotificationManager의 인스턴스가 필요함 이 System Service는 notification API를 모두 쓰고 확장함수도 쓸 수 있음

- 이 NotificationManager는 notification을 send, cancel, update할 때 시스템으로부터 인스턴스로 필요로 한 것임, 아래와 같이 만들 수 있음

```kotlin
// EggTimerViewModel.kt
// TODO: Step 1.5 get an instance of NotificationManager 
// and call sendNotification

val notificationManager = ContextCompat.getSystemService(
    app, 
    NotificationManager::class.java
) as NotificationManager
                notificationManager.sendNotification(app.getString(R.string.timer_running), app)
```

- 이 때 API 26 이상부터는 Notification은 모두 channel을 할당 받아야 함, 현재 위의 상태까지에서는 app이 어떠한 채널도 만들지 않음 

- Channel은 notification의 타입을 나타냄, 해당 앱 기준으로 egg timer에서 egg가 요리됐을 때, notification을 보내거나 아침으로 egg를 먹게 떠올리게 daily notification을 보낼 수 있음

- 채널에 있는 모든 Notification은 하나로 묶여서 유저도 모든 채널의 notification 설정을 조절할 수 있음, 이는 유저가 그들이 흥미가 있는 notification 종류에 기반하여 notification 설정을 개인화 할 수 있게함

- 개발자가 초기 세팅을 하겠지만 이처럼 유저가 개인 커스터 마이징을 할 수 있음

- 앞서 `egg_notification_channel_id`를 사용하였는데, 이제 이에 맞는 실제 notification setting을 커스텀하고 channel의 behavior를 만들어야 함

- NotificationChannel의 생성자를 만드는데 이때, id, name과 마지막 매개변수로 중요도를 보내줌

- 그리고 각각 notification이 보일 때 light과 해당 색깔 설정, 그리고 진동과 Channel의 설명 등을 설정함, 그리고 NotificationManager 인스턴스를 만들고 NotificationChannel을 만듬
```kotlin
//EggTimerFragment.kt
private fun createChannel(channelId: String, channelName: String) {
    // TODO: Step 1.6 START create a channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            // TODO: Step 2.4 change importance
            NotificationManager.IMPORTANCE_LOW
        )
        // TODO: Step 2.6 disable badges for this channel

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Time for breakfast"

        val notificationManager = requireActivity().getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
    // TODO: Step 1.6 END create channel
}
```

- 그런 당므 NotificationChannel을 만듬

```kotlin
// EggTimerFragment.kt
    // TODO: Step 1.7 call createChannel
    createChannel(
          getString(R.string.egg_notification_channel_id),
          getString(R.string.egg_notification_channel_name)
    )
```

- 그리고 이 Channel_Id를 넣음

```kotlin
// NotificationUtils.kt
val builder = NotificationCompat.Builder(
        applicationContext,
       // TODO: Step 1.8 verify the notification channel name
        applicationContext.getString(R.string.egg_notification_channel_id)
)
```

- 추가로 App-Info로 Notification을 열어서 Show notification 설정을 해줘야함

- 타이머 시작과 맞게 notification을 설정을 맞추기 위해서 alarm이 설정될 때 바로 AlarmManager에서 onReceive 메소드에서 Notification 인스턴스를 가져오고 `sendNotification`을 호출하고 Toast 메시지는 없앰

```kotlin
// AlarmReceiver.kt
   // TODO: Step 1.9 add call to sendNotification
   val notificationManager = ContextCompat.getSystemService(
       context, 
       NotificationManager::class.java
   ) as NotificationManager
             
   notificationManager.sendNotification(
       context.getText(R.string.eggs_ready).toString(), 
       context
   )

   // AlarmReceiver.kt
     // TODO: Step 1.10 [Optional] remove toast
//   Toast.makeText(
//       context, 
//       context.getText(R.string.eggs_ready),
//       Toast.LENGTH_SHORT
//   ).show()
```

- 그리고 AlarmReceiver에서 설정을 했기 때문에 굳이 ViewModel에서 설정한 Notification 보내는 것을 없앰

```kotlin
// EggTimeViewModel.kt

// TODO: Step 1.5 get an instance of NotificationManager 
// and call sendNotification
// val notificationManager = ContextCompat.getSystemService(
//      app,
//      NotificationManager::class.java
// ) as NotificationManager
// notificationManager.sendNotification(app.getString(R.string.eggs_ready), app)
```

- 지금까지 설정하면 Notification이 나오긴 하나 Notification을 누르면 아무일도 일어나지 않음, 그렇기 때문에 이 Notification을 눌렀을 때 intent 설정을 해서 다시 앱으로 돌아오게 해야함

- 이 상황에서 intent는 notification을 눌렀을 때 MainActivity로 오게끔 설정한다고 볼 수 있음, 현재는 앱이 단순해서 복잡한 설정이 필요 없지만 앱의 규모가 크다면 notification과 유저가 적절하게 상호작용하게끔 설정을 잘해야함

- 아래와 같이 눌렀을 경우 인텐트 설정을 하게끔 처리함

```kotlin
// NotificationUtils.kt

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // Create the content intent for the notification, which launches
    // this activity
   // TODO: Step 1.11 create intent
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
```

- 여기서 intent를 만들긴 했지만 notification은 앱 외부에서 보여짐, 그래서 intent가 앱 외부에서도 작동하게 하기 위해서 PendingIntent를 만들어야함

- `PendingIntent`는 앱의 행동에 대한 작업을 수행하는데 시스템이나 다른 앱에 대해서 권한을 승인해주는 것임, 이 `PendingIntent` 자체가 단순한 래퍼런스가 됨, 이 말은 application process 자체가 없어져도 `PendingIntent`가 다른 process로부터 사용가능한 정보로써 남겨질 수 있다는 것을 말함

- 이 경우 시스템은 notification을 누르고 intent를 통해 앱을 여는 과정은 시스템이 `PendingIntent`를 사용해서 처리하는 것임, 설령 타이머 앱이 실행중이지 않더라도

- 아래와 같이 만들 수 있음, 이 때 `PendingIntent` flag는 새로운 PendingIntent를 만들지 기존의 것을 만들지 특정할 수 있음, 현재 설정은 존재하고 있는 것이 있으면 굳이 새 것을 만들지 않고 업데이트 하는 방향으로 감

```kotlin
// NotificationUtils.kt
   // TODO: Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext, 
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
```

- 그리고 notification에 이 PendingIntent를 넘김, 그리고 자동 cancel도 추가함

```kotlin
// NotificationUtils.kt
    // TODO: Step 1.13 set content intent
    .setContentIntent(contentPendingIntent)
    .setAutoCancel(true)
```

- 아직 남은 문제는 notification에 대해서 이전 notification이 timer를 연속으로 시작하면 남아있는데 이를 취소하기 위해서 아래와 같이 확장함수를 만듬

```kotlin
// NotificationUtils.kt

// TODO: Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
```

- 그리고 `startTimer`에서 다시 설정을 함

```kotlin
//  EggTimerViewModel.kt
   //TODO Step 1.15 call cancel notification
    val notificationManager =
       ContextCompat.getSystemService(
            app,
            NotificationManager::class.java
        ) as NotificationManager
    notificationManager.cancelNotifications() 
```

### Customizing Notification
- notification framework 자체가 개발자에게 action과 style을 그들이 필요한 방향으로 커스텀하게 다양한 옵션을 줌

- `NotificationCompat`이 다양한 스타일을 제공하고 이를 활용해서 커스터마이징 할 수 있음

- 여기서는 `BigPictureStyle`을 아래와 같이 활용할 것임

```kotlin
// NotificationUtils.kt

// TODO: Step 2.0 add style
val eggImage = BitmapFactory.decodeResource(
     applicationContext.resources, 
     R.drawable.cooked_egg
)
val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(eggImage)
        .bigLargeIcon(null)
```

- 그리고 앞서 해왔던 방식과 같이 Builder를 통해서 style과 icon을 적용할 수 있음

```kotlin
// NotificationUtils.kt
// TODO: Step 2.1 add style to builder
.setStyle(bigPicStyle)
.setLargeIcon(eggImage)
```

- 또 다른 것으로 앞서 notification 클릭 시 바로 앱으로 돌아가게끔 기본 설정을 하였는데 여기서 action 역시 커스터마이징 할 수 있음

- 이러한 또다른 action 기능을 추가하기 위해서 PendingIntent를 builder에서 addAction에 추가를 하면 다양한 action을 추가하고 처리할 수 있음

- 이 때 `BroadcastReceiver`를 받아서 작업을 백그라운드에서 처리하기 때문에 앱이 이미 열린 경우에 이를 방해하진 않음

- 타이머에 따라서 snooze action button을 처리하기 위해서 이 역시 BroadcastReceiver로써 처리를 함, 그래서 아래와 같이 notificationManager 인스턴스를 만들 수 있음

```kotlin
// SnoozeReceiver.kt
        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
```

- 그리고 이를 위한 PendingIntent를 추가할 것인데, 이는 60초 후 snooze button을 눌렀다면 새로운 notification이 보내지게 새로운 아람을 설정하게 시스템에서 사용되는 pendingIntent임

- application context와 pending intent를 위한 context, 그리고 snoozeIntent 객체, 마지막으로 flag를 넘김

```kotlin
// NotificationUtils.kt

// TODO: Step 2.2 add snooze action
val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
    applicationContext, 
    REQUEST_CODE, 
    snoozeIntent, 
    FLAGS
)
```

- 그리고 기존 NotificationBuilder에서 `addAction`을 호출해서 처리함

```kotlin
// NotificationUtils.kt
// TODO: Step 2.3 add snooze action
.addAction(
    R.drawable.egg_icon, 
    applicationContext.getString(R.string.snooze),
    snoozePendingIntent
)
```

- notification 설정에 있어서 `NotificationChannel` 생성자에서 중요도 설정을 할 수 있음, 이는 0부터 4까지 할당을 할 수 있고 이는 channel의 모든 notification message에 적용됨

![one](/Android/img/fourtyfour.png)

- 이 중요도 설정은 Notification design guide를 참고하면 좋고, 이 때 이 중요도는 유저의 시간과 이목을 끄는 것이기 때문에 설정을 잘해둬야함, 불필요한 notification에 대해서는 급하게 할 이유가 없기 때문에

- 아래와 같이 해당 중요도를 높임, egg timer이기 때문에 높게 책정할 필요가 있음

```kotlin
// EggTimerFragment.kt
    val notificationChannel = NotificationChannel(
        channelId,
        channelName,
        // TODO: Step 2.4 change importance
        NotificationManager.IMPORTANCE_HIGH
    )
```

- 그리고 25버전 이하를 위해서 `Priority` 역시, 아래와 같이 설정해줘야함

```kotlin
// NotificationUtils.kt
   .addAction(
       R.drawable.common_google_signin_btn_icon_dark,
       applicationContext.getString(R.string.snooze),
       snoozePendingIntent
    )
   // TODO: Step 2.5 set priority
    .setPriority(NotificationCompat.PRIORITY_HIGH)
```

- 마지막으로 notification badge를 notification이 활성화 됐을 때 작은 표시로 나타나게끔 그리고 길게 눌렀을 때 notification 보이게끔 설정할 수 있음

- 이 작은 점으로 표시된 badges는 기본 설정이라서 건드릴 것은 없음, 하지만 이 badges가 필요 없는 경우도 있기 때문에 이와 같은 경우에 `NotificationChannel` 객체에서 값을 `false`처리할 수 있음

- 현재 만든 앱에서는 굳이 badges를 보여줄 필요가 없기 때문에 아래와 같이 진행함

```kotlin
// EggTimerFragment.kt

    ).apply {
        // TODO: Step 2.6 disable badges for this channel
        setShowBadge(false)
    }
```