package com.robbyp.scab


import org.scalatest.FunSuiteLike
import org.scalatra.test.scalatest._

class HelloWorldServletTests extends ScalatraSuite with FunSuiteLike {
  // `HelloWorldServlet` is your app which extends ScalatraServlet
  addServlet(classOf[MyScalatraServlet], "/*")

  test("simple get") {
    get("/") {
      status should equal(200)
      body should include("Hello, world!")
    }
  }
}
