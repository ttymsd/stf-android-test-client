package com.bonborunote

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class StfClientPlugin implements Plugin<Project> {
  @Override
  void apply(Project target) {
    def extension = target.extensions.create("stfConfig", StfClientExtension)

    target.task("showStfDeviceList").doLast { showDeviceList(extension) }

    target.android.applicationVariants.all { v ->
      if (v.testVariant == null) return
      def connectedStfTest = target.task("connectedStf${v.name.capitalize()}AndroidTest") {
        doLast {
          info("finish test")
        }
      }
      extension.testTargets.each { testTarget ->
        def deviceTestTask = target.task(
            "connectedStfDevice${testTarget.serial.capitalize()}${v.name.capitalize()}AndroidTest") {
          doLast { executeTest(v, extension, testTarget) }
        }
        connectedStfTest.dependsOn deviceTestTask
      }

      def assembleAndConnectedStfTest = target.task(
          "assembleAndConnectedStf${v.name.capitalize()}AndroidTest") {
        doLast {
          info("finish test")
        }
      }
      extension.testTargets.each { testTarget ->
        def deviceTestTask = target.task(
            "assembleAndConnectedStfDevice${testTarget.serial.capitalize()}${v.name.capitalize()}AndroidTest") {
          doLast { executeTest(v, extension, testTarget) }
        }

        def assembleTask = target.tasks.findByName("assemble${v.name.capitalize()}")
        def assembleTestTask = target.tasks.findByName("assemble${v.testVariant.name.capitalize()}")

        deviceTestTask.dependsOn assembleTask
        deviceTestTask.dependsOn assembleTestTask
        assembleAndConnectedStfTest.dependsOn deviceTestTask
      }
    }
  }

  void executeTest(ApplicationVariant v, StfClientExtension extension, TestTarget target) {
    extension.outputDir.mkdirs()
    def serial = target.serial
    def apkFile = v.outputs[0].outputFile.toString()
    def testApkFile = v.testVariant.outputs[0].outputFile.toString()
    def timeoutMillis = extension.testTimeoutMillis
    try {
      connect(extension, serial) { remoteUrl ->
        info("start")
        target.connect(remoteUrl)
        info("connected")
        target.unlock(remoteUrl)
        info("unlocked")
        target.install(remoteUrl, apkFile)
        info("install apk")
        target.install(remoteUrl, testApkFile)
        info("install test apk")
        if (extension.testWithScreenRecord) {
          info("screen record")
          target.screenRecord(remoteUrl)
        }
        info("execute")
        def applicationId = [v.testVariant.mergedFlavor.applicationId, v.testVariant.buildType.applicationIdSuffix, ".test"].
            findAll().
            join()
        target.executeTest(remoteUrl, applicationId,
            v.testVariant.mergedFlavor.testInstrumentationRunner, timeoutMillis)
        info("executed")
        target.stopScreenRecord(remoteUrl, extension.outputDir.toString())
      }
    } catch (Exception e) {
      error("fail test: " + e)
    } finally {
      try {
        disconnect(extension, serial)
        info("disconnect device:$serial")
      } catch (Exception e) {
        error("fail disconnect: " + e)
      }
    }
  }

  void showDeviceList(StfClientExtension extension) {
    def stfClient = new StfApiClient(stfHostUrl: extension.stfHostUrl,
        stfAuthToken: extension.stfAuthToken)
    stfClient.getDevices {
      it.forEach {
        printf("Device: %s \t\t Serial: %s\n", it.name, it.serial)
      }
    }
  }

  void connect(StfClientExtension extension, String serial, Closure callback) {
    def stfClient = new StfApiClient(stfHostUrl: extension.stfHostUrl,
        stfAuthToken: extension.stfAuthToken)

    stfClient.connect(serial) { result ->
      if (result) {
        stfClient.remoteConnect(serial) { connected, remoteUrl ->
          if (connected) {
            callback(remoteUrl)
          }
        }
      }
    }
  }

  void disconnect(StfClientExtension extension, String serial) {
    def stfClient = new StfApiClient(stfHostUrl: extension.stfHostUrl,
        stfAuthToken: extension.stfAuthToken)
    stfClient.remoteDisconnect(serial) { result ->
      if (result) {
        stfClient.disconnect(serial) {
          info("disconnected")
        }
      }
    }
  }

  void info(String message) {
    //    project.logger.info(message)
    println(message)
  }

  void error(String message) {
    println(message)
    //    System.err.println(message)
  }
}
