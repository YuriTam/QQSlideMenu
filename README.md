#SlideMenu

这是一个Android项目

----------

**名称：侧滑菜单**

**开发环境：Android Studio**

----------

##应用截图

![image](https://github.com/AlionSSS/QQSlideMenu/blob/master/Screenshot_1.png)
![image](https://github.com/AlionSSS/QQSlideMenu/blob/master/Screenshot_2.png)

----------

##详细

本项目仿照QQ的侧滑效果，使用Android Studio制作了滑划菜单控件view。项目包括了一系列侧滑效果、文字与图像动画效果等。

##build.gradle
	apply plugin: 'com.android.application'
	
	android {
	    compileSdkVersion 23
	    buildToolsVersion "24.0.1"
	    defaultConfig {
	        applicationId "com.skey.qqslidemenu"
	        minSdkVersion 15
	        targetSdkVersion 23
	        versionCode 1
	        versionName "1.0"
	        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
	    }
	    buildTypes {
	        release {
	            minifyEnabled false
	            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
	        }
	    }
	}
	
	dependencies {
	    compile fileTree(dir: 'libs', include: ['*.jar'])
	    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
	        exclude group: 'com.android.support', module: 'support-annotations'
	    })
	    compile 'com.android.support:appcompat-v7:23.4.0'
	    testCompile 'junit:junit:4.12'
	}
