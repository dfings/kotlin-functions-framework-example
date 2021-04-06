package io.github.dfings.example

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse

class App : HttpFunction {
    val greeting = "Hello, World!"
    override fun service(request: HttpRequest, response: HttpResponse) {
        response.writer.write(greeting)
    }
}
