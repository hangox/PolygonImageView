#PolygonImageView
###介绍
一个可以自定义边框形状的ImageView
###演示
<video id="video" controls="controls" preload="auto" autoplay="autoplay" poster="https://raw.githubusercontent.com/hangox/PolygonImageView/master/document/screenshot.jpg" style="width: 60%">
    <source id="mp4" src="https://raw.githubusercontent.com/hangox/PolygonImageView/master/document/show.mov">
    Your browser does not support the video tag.
</video>

###使用

#####引用
```gradle
compile 'com.hangox:polygon-imageview:1.0'
```

#####属性说明
`app:piv_borderColor` 边界的颜色
`app:piv_borderWidth` 边界的宽度
`app:piv_clipImage` 边界形状的图片

#####例子

```
 <com.hangox.polygon.PolygonImageView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/guo_ye"
            app:piv_borderColor="@android:color/white"
            app:piv_borderWidth="18dp"
            app:piv_clipImage="@drawable/ic_mask_hex"/>
```

#CircleImageView
因为Circle 使用的频率比较高，所以我单独了一个Circle 控件

###引用
```gradle
compile 'com.hangox:circle-imageview:1.0' 
```

###使用
```xml
<com.hangox.circleimageview.CircleImageView
            app:piv_borderWidth="4dp"
            app:piv_borderColor="@color/colorAccent"
            android:src="@drawable/guo_ye"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
```



