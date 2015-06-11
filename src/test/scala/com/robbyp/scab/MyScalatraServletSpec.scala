package com.robbyp.scab

import org.scalatra.test.scalatest.ScalatraFlatSpec

class MyScalatraServletSpec extends ScalatraFlatSpec {

  addServlet(classOf[MyScalatraServlet], "/*")

  "MyScalatraServlet" should "return status 200 with GET /" in {
    get("/") {
      status should be(200)
    }
  }

}
