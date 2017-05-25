# PolygonImageView

### 介绍
一个可以自定义边框形状的ImageView

### 演示

![](https://raw.githubusercontent.com/hangox/PolygonImageView/master/document/show.gif)

### 使用

##### 引用
```gradle
compile 'com.hangox:polygon-imageview:1.0'
```

##### 属性说明
`app:piv_borderColor` 边界的颜色
`app:piv_borderWidth` 边界的宽度
`app:piv_clipImage` 边界形状的图片

##### 例子

```xml
 <com.hangox.polygon.PolygonImageView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:src="@drawable/guo_ye"
            app:piv_borderColor="@android:color/white"
            app:piv_borderWidth="18dp"
            app:piv_clipImage="@drawable/ic_mask_hex"/>
```

# CircleImageView
因为Circle 使用的频率比较高，所以我单独了一个Circle 控件

### 引用
```gradle
compile 'com.hangox:circle-imageview:1.0' 
```

### 使用
```xml
<com.hangox.circleimageview.CircleImageView
            app:piv_borderWidth="4dp"
            app:piv_borderColor="@color/colorAccent"
            android:src="@drawable/guo_ye"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
```


License
=======

    Copyright 2016 hangox 

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

