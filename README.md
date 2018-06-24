# STFAndroidTestClient

STFAndroidTestClient is a Gradle plugin. Run Android instrumental test using [STF](https://github.com/openstf/stf) and screen record.

## Getting Started

### Installing

add dependencies
```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.com.bonborunote:stf-android-test-client:${latestVersion}"
  }
}

apply plugin: "com.bonborunote.stf-android-test-client"
```

add config to module build.gradle

#### example 
```
import com.bonborunote.TestTarget
import com.bonborunote.SwipePattern
import com.bonborunote.Point

stfConfig {
  stfHostUrl = "http://192.168.0.99:7100"
  stfAuthToken = "19abc84ed0a24ac5a59333b39eeeec78c3fdf8642a9a48eb98e76d4241a01ec4"
  testTargets = [new TestTarget(serial:"5b8bccac", name:"Nexus5", unlockPattern:new SwipePattern(start:new Point(400, 800), end:new Point(400, 200)))]
  testTimeoutMillis = 30000
  outputDir = new File(project.buildDir, "stf")
  testWithScreenRecord = true
}

```

* **stfHostUrl:** <span style="color:red">*Required*</span> Running STF Host url
* **stfAuthToken:** <span style="color:red">*Required*</span> STF Auth Token
* **testTargets:** <span style="color:red">*Required*</span> Run AndroidTest this devices.
* **testTimeoutMills:** <span style="color:red">*Optional* default value is 600000</span> all tests finished in this duration.
* **outputDir:** <span style="color:red">*Optional* default value is project root</span> screen record file(mp4) output dir.
* **testWithScreenRecord:** <span style="color:red">*Optional* default value is true</span> test with screen record.

## add below tasks by plugin

* **showStfDeviceList:** Show devices info

* **connectedStf{ProductFlavor}AndroidTest:** Run AndroidTest on configured devices.

* **assembleAndConnectedStf{ProductFlavor}AndroidTest:** assemble apks and run AndroidTest on configured devices.

## Authors

* **ttymsd**

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
