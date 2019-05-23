```
tips:如果您在电脑上直接打开，推荐使用Typora客户端打开该文档
     如果您在AndroidStudio打开，推荐下载markdown插件，Preferences-Plugins-Markdown-Navgitor
```
# Android TV 开发工具XTvLibs使用说明

## 1 简介

总结整理AndroidTv开发中常用的组件，工具类等

> - Multkey 组合键监听，默认实现"上+下+左+右+上"组合键打印版本信息。
> - keyboard 自定义九宫格键盘，预览图见sreenshot文件夹
> - Pickerview 自定义滚动选择器，预览图见sreenshot文件夹
> - HScrollMenuTv 自定义水平菜单选择器，预览图见sreenshot文件夹
> - FlyBoraderTv 自定义焦点移动飞框
> - 集成TV开发常见屏幕分辨率（基准1920*1080），使用时直接@dimen/w_x 或@dimen/h_x 
> - 更多功能待整理 可参考[Android TV 开源社区](<https://gitee.com/kumei>)



## 2 集成说明
可选择aar集成或者私服maven依赖集成
### 2.1 aar集成
将离线xtvlibs.aar copy至app模块lib目录下

app模块build.gradle中声明如下



```
repositories{
		flatDir{dirs 'libs'}
}
dependencies {
    implementation (name:'xtvlibs',ext:'aar')
}
```


### 2.2 私服maven依赖

工程buid.grade

```Dart
...
allprojects {
    repositories {
        maven { url "http://192.168.3.163:8081/repository/maven-public-android/" }
        google()
        jcenter()
        
    }
}
...
```

app模块 build.grade

```Dart
...
dependencies {
    ...
    implementation'com.avit.xtvlibs:xtvlibs:+'
}
```

## 3 使用说明

待整理...
可参考益阳IPTV相关代码