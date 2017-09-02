# Kotlin实战——LacWeather
>####简介

该APP是基于**Kotlin语言**以及**借鉴《Android第一行代码》中的酷欧天气**进行编写的，这也算是小猿学习Kotlin的时候，写的第一款Kotlin小demo。

>####主要功能

1. 我的城市：用户收藏的城市（正在实现中...）以及用户当前所在的城市的天气信息；

2. 查询城市：用户可以查询全国各地的城市的天气信息；

>####技术实现

1. LitePal：利用LitePal数据库来存储城市的省市县信息，实现简单的插入与查询；

2. SharePreference：用户存用户查询到的城市的天气信息以及有关的图片背景的Url；

3. Okhttp：用于网络请求接口信息；

4. Json：用于解析城市的省市县的数据；

5. Gson：用于解析城市的天气信息；

6. MaterialDesign：利用DraweLayout+NavigationView来用户展示有关的个性化设置；

7. Service+AlarmManager：利用后台的服务加上Alarm定时技术来实现天气APP在后台一直保持着天气数据的更新；

8. Glide：用于天气图片的加载；

>####依赖库

1. **kotlin**：

	compile 'org.jetbrains.kotlin:kotlin-stdlib:1.1.1'

    compile 'org.jetbrains.anko:anko-sdk25:0.10.0-beta-1'

    compile 'org.jetbrains.anko:anko-appcompat-v7:0.10.0-beta-1'

2. **MaterialDesign**：

	compile 'com.android.support:design:25.3.1'

3. **LitePal**：

	compile 'org.litepal.android:core:1.4.1'

4. **Okhttp**：

	compile 'com.squareup.okhttp3:okhttp:3.4.1'

5. **Gson**：

	compile 'com.google.code.gson:gson:2.7'

6. **Glide**：

	compile 'com.github.bumptech.glide:glide:3.7.0'

效果图：

![](https://i.imgur.com/zgTJHEq.png) ![](https://i.imgur.com/HLX3bIb.png)
