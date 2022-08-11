### Adding Firebase to your Android project
- Firebase를 쓰기전에 먼저 App을 연결해야함, 이를 위해서 Project를 만들고 절차대로 따라해서 연결하면됨, 공식문서대로 따라하면 됨

### Starting with Firebase Cloud Messaging
- 앞서 Notification에 대해서 알아보았는데 이번에는 Notifications composer로 Firebase를 통해서 message를 구성하고 보내는 것을 사용할 것임([공식문서](https://firebase.google.com/docs/cloud-messaging/ios/send-with-console?hl=ko))

- 이 때, 해당 서비스를 쓰기 위한 별도의 클래스를 만듬 각 메소드를 본다면

- `onNewToken`의 경우, service를 manifest에 등록하면 자동으로 호출되는 함수임, 이 함수는 앱을 처음 실행할 때와 앱을 위해 Firebase가 새 토큰을 발행할 때마다 호출됨, 이 token의 경우 Firebase backend project에 access key임, 이것은 특정 client device를 정함, 이 token을 통해 Firebase는 backend가 어떤 client에 message를 보내야 하는지 알게됨, 그리고 Firebase project에 유효한지도 알 수 있음

- `onMessageReceived`는 앱이 실행 중이고 앱에게 Firebase가 message를 보낼 때 호출됨, 이 함수는 `RemoteMessage` 객체를 받음, 이 `RemoteMessage`에는 notification과 data message payload가 담겨있음

- 먼저 Firebase에서 Notifications console에서 notification을 테스트로 보내기 위해서 기기의 등록 토큰을 알아야함, 이는 테스트를 위한 단일 기기에 대해서 토큰 값에 대한 확인과 생성을 위해서 등록을 해줘야함

- 그러기 위해 아래와 같이 Android manifest에서 `MyFirebaseMessagingService`를 허용하게끔 등록을 해야함, 그리고 해당 service의 meta-data 역시 등록해서 intent filter를 통해서 FCM이 보낸 message를 받을 수 있게도 해야함
, 그리고 마지막으로 channel_id도 등록함

```xml
<!-- AndroidManifest.xml -->
<!-- TODO: Step 3.0 uncomment to start the service  -->

        <service
                android:name=".MyFirebaseMessagingService"
                android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>
        <!-- [START fcm_default_icon] -->
        <!--
 Set custom default icon. This is used when no icon is set for incoming notification messages.
             See README(https://goo.gl/l4GJaQ) for more.
        -->
        <meta-data
                android:name="com.google.firebase.messaging.default_notification_icon"
                android:resource="@drawable/common_google_signin_btn_icon_dark"/>
        <!--
 Set color used with incoming notification messages. This is used when no color is set for the incoming
             notification message. See README(https://goo.gl/6BKBk7) for more.
        -->
        <meta-data
                android:name="com.google.firebase.messaging.default_notification_color"
                android:resource="@color/colorAccent"/> <!-- [END fcm_default_icon] -->
        <!-- [START fcm_default_channel] -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="@string/breakfast_notification_channel_id" />
        <!-- [END fcm_default_channel] -->
```

- 이 때, 기존 egg timer 알림과 FCM 푸시 알림을 분리해서 사용하게 해주기 위해서 아래와 같이 새로운 채널을 만드는게 좋음

```kotlin
// EggTimerFragment.kt

   // TODO: Step 3.1 create a new channel for FCM
    createChannel(
        getString(R.string.breakfast_notification_channel_id),
        getString(R.string.breakfast_notification_channel_name)
    )
```

- 그리고 `onNewToken`함수에서 새로운 토큰이 생성될 때 호출되도록 추가를 함

```kotlin
// MyFirebaseMessagingService.kt

   // TODO: Step 3.2 log registration token
    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the     
     * InstanceID token is initially generated so this is where you would retrieve     
     * the token.
     */
    override fun onNewToken(token: String?) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]
```

- 실행을 하여 Logcat을 본다면 해당 토큰값이 넘어와서 처리됨

- 이때, Firebase 말고 별도의 서버가 존재한다면 이 client token을 저장해서 활용하려고 할텐데, 이 때 새로운 토큰을 받고 난 후 바로 저장을 해야하는데 `sendRegistrationToServer` 메소드를 통해서 다른 서버에 저장을 할수도 있음

```kotlin
/**
     * Persist token to third-party servers.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
    }
```

- 그리고 Firebase cnosole에서 Cloud Messaging을 선택하여 아래와 같이 쓰고 test message를 보내면 됨, 이때 토큰 값을 넣어주면 해당 기기로 감

![one](/Android/img/fourtyfive.png)

![one](/Android/img/fourtysix.png)

- 또 다른 것으로 FCM topic messaging이 존재함, 이는 publish/subscribe 모델을 기반함, 이는 client device가 앱을 통해 새로운 메시지가 보내질 때 알려지게끔 subscribe 할 수 있게 처리되는 모델이라고 볼 수 있음

- Topics는 특정 topic을 선택하면 다양한 기기에 대해서 메시지를 보내는 걸 허용해줌, 이렇게 한다면 유저가 흥미있는 data source에 대해 특정지어서 할 수 있음, 서버에서도 특정 data source에 대해서 업데이트를 진행하게 할 수 있음

- Topics는 뉴스나 기상예보, 경기 결과등 notifications의 현재 카테고리에 사용될 수 있음, topic을 subscribe하기 위해 FCM은 `subscribeToTopic`이라는 함수를 호출함, 이 함수는 호출이 성공하면 `OnCompleteListener` 콜백을 통해서 subscribe한 메시지를 받고 실패하면 error message를 받게됨

- 일단 구현은 자동화를 시켰지만, 일반적으로 유저에게 subscribe에 대한 선택권을 줌

```kotlin
// EggTimerFragment.kt

   // TODO: Step 3.3 subscribe to breakfast topic
    private fun subscribeTopic() {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        // [END subscribe_topics]
    }
```

- 그리고 위의 함수를 `onCreateView`에서 호출해서 해당 topic을 subscribe하게 함
```kotlin
// EggTimerFragment.kt

   // TODO: Step 3.4 call subscribe topics on start
    subscribeTopic()

    return binding.root
```

- 그리고 아래와 같이 notification을 만들고 단순히 테스트로 보내는게 아닌, Topic과 Target을 정해서 단일 기기가 아닌 아예 `breakfast`라는 topic으로 정해서 저장함

![one](/Android/img/fourtyseven.png)

- 스케줄링은 `Now`로 하고 볼 수 있음, 이 때 앱 세부사항에 들어가면 Notification에 대해서 기존에 만든 것외에 Breakfast라는 것이 추가됨, 이 채널을 구독할지 안할지 정할 수 있음

### Sending data with FCM
- FCM message는 앱에서 담고 있는 message에 대한 data payload를 포함함, 이를 통해 notification message 말고 data message를 활용할 수 있음

- 이를 사용하기 위해서 `onMessageReceived` 함수를 통해서 data payload를 다룰 수 있음, 이 때 `remoteMessage` 객체에서 `data` 요소를 저장하고 있음

```kotlin
// MyFirebaseMessagingService.kt

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: ${remoteMessage?.from}")
        
       // TODO: Step 3.5 check messages for data
        // Check if the message contains a data payload.
        remoteMessage?.data?.let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

    }
    // [END receive_message]
```

- 그리고 아래와 같이 추가 옵션을 통해서 custom data를 추가할 수 있음

![one](/Android/img/fourtyeight.png)

- data를 추가하고 함수를 추가했기 때문에 앱이 실행시 백그라운드에서는 유저가 notification을 클릭시 `remoteMessage` 객체에 대해 정보를 받을 수 있음

- 이는 앱이 foreground에 있으면 자동으로 하지 않고 위와 같이 해당 함수를 통해서 notification을 어떻게 다룰지 정할 수 있지만 background에 있다면 자동으로 notification tray를 보여줌, 그리고 유저와 상호작용 시 해당 정보를 처리함(이는 notification payload가 있다면 둘 다 있다는 전제가 있음)

- 현재 앱에서는 앱이 실행중이지 않거나 background라면 notification message가 자동으로 나타나지만 app이 foreground에 있다면 자동으로 보여지지 않음, 대신 message와 무엇을 할 지 코드로 정했음, 이 때 이미 쓴 `onMessageRceived` 함수를 통해서 foreground에서의 상태처리를 할 수 있음

- 이 때 foreground에 있을 때, 유저에게 상기시키기 위해서 아래와 같이 코드를 쓸 수 있음

```kotlin
// MyFirebaseMessagingService.kt

    // TODO: Step 3.6 check messages for notification and call sendNotification
    // Check if the message contains a notification payload.
    remoteMessage.notification?.let {
        Log.d(TAG, "Message Notification Body: ${it.body}")
        sendNotification(it.body as String)
    }
```