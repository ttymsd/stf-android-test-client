package com.bonborunote

class TestTarget {
  String serial
  String name
  private UnlockPattern unlockPattern
  private Process screenRecordProcess

  static void connect(String deviceId) {
    "adb connect $deviceId".execute().waitFor()
    "sleep 1s".execute().waitFor()
  }

  void unlock(String deviceId) {
    unlockPattern.unlock(deviceId)
  }

  static void install(String deviceId, String apkFilePath) {
    "adb -s $deviceId install -r $apkFilePath".execute().waitFor()
  }

  static void executeTest(String deviceId, String applicationId, String testRunner, long timeoutMillis) {
    def process = "adb -s $deviceId shell am instrument -w $applicationId/$testRunner".execute()
    def sout = new StringBuilder(), serr = new StringBuilder()
    process.consumeProcessOutput(sout, serr)
    process.waitForOrKill(timeoutMillis)
  }

  void screenRecord(String deviceId) {
    screenRecordProcess = "adb -s $deviceId shell screenrecord ${testPath(serial)}".execute()
  }

  void stopScreenRecord(String deviceId, String outputDir) {
    if (screenRecordProcess == null) return
    screenRecordProcess.waitForOrKill(200)
    "sleep 1s".execute().waitFor()
    "adb -s $deviceId pull ${testPath(serial)} ${outputDir.toString()}/".execute().waitFor()
    "adb -s $deviceId shell rm ${testPath(serial)}".execute().waitFor()
  }

  private static String testPath(String serial) {
    return "/sdcard/test_${serial}.mp4"
  }
}

abstract class UnlockPattern {
  abstract void unlock(String deviceSerial)
}

class SwipePattern extends UnlockPattern {
  Point start
  Point end

  @Override
  void unlock(String deviceId) {
    "adb -s $deviceId shell input swipe ${start.x} ${start.y} ${end.x} ${end.y}".execute().waitFor()
    println("adb -s $deviceId shell input swipe ${start.x} ${start.y} ${end.x} ${end.y}")
  }
}

class Point {
  int x
  int y
  Point(int x, int y) {
    this.x = x
    this.y = y
  }
}