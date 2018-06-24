package com.bonborunote

import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method

class StfApiClient {
  String stfHostUrl
  String stfAuthToken

  public void getDevices(Closure callback) {
    def http = new HTTPBuilder(stfHostUrl)
    http.ignoreSSLIssues()
    http.request(Method.GET, ContentType.JSON) { req ->
      uri.path = '/api/v1/devices'
      headers.'Authorization' = "Bearer ${this.stfAuthToken}"
      response.success = { res, json ->
        def devices = json.devices.collect {
          new DeviceResponse(serial: it.serial, name: it.name)
        }
        callback(devices)
      }
    }
  }

  public void connect(String serial, Closure callback) {
    def http = new HTTPBuilder(stfHostUrl)
    http.ignoreSSLIssues()
    http.request(Method.POST) { req ->
      uri.path = '/api/v1/user/devices'
      headers.'Authorization' = "Bearer ${this.stfAuthToken}"
      requestContentType = ContentType.JSON
      body = [serial: serial]
      response.success = { res, json ->
        callback(json.success)
      }
    }
  }

  public void remoteConnect(String serial, Closure callback) {
    def http = new HTTPBuilder(stfHostUrl)
    http.ignoreSSLIssues()
    http.request(Method.POST, ContentType.JSON) { req ->
      uri.path = "/api/v1/user/devices/${serial}/remoteConnect"
      headers.'Authorization' = "Bearer ${this.stfAuthToken}"
      body = [serial: serial]
      requestContentType = ContentType.JSON
      response.success = { res, json ->
        callback(json.success, json.remoteConnectUrl)
      }
    }
  }

  public void disconnect(String serial, Closure callback) {
    def http = new HTTPBuilder(stfHostUrl)
    http.ignoreSSLIssues()
    http.request(Method.DELETE, ContentType.JSON) { req ->
      uri.path = "/api/v1/user/devices/$serial"
      headers.'Authorization' = "Bearer ${this.stfAuthToken}"
      response.success = { res, json ->
        callback(json.success)
      }
    }
  }

  public void remoteDisconnect(String serial, Closure callback) {
    def http = new HTTPBuilder(stfHostUrl)
    http.ignoreSSLIssues()
    http.request(Method.DELETE, ContentType.JSON) { req ->
      uri.path = "/api/v1/user/devices/${serial}/remoteConnect"
      headers.'Authorization' = "Bearer ${this.stfAuthToken}"
      response.success = { res, json -> callback(json.success)
      }
    }
  }
}