---
layout: docs
title: Syntax DSL
permalink: /docs/optics/syntax/
---

## Syntax DSL

In some cases the full power of Optics is not required and a familiar property-like syntax to work with values of immutable structures is desired.
To avoid boilerplate Arrow can generate this property-like dsl using `@optics` annotation.

```kotlin:ank:ap
package com.example.domain

import arrow.optics.optics

@optics data class Street(val number: Int, val name: String)
@optics data class Address(val city: String, val street: Street)
@optics data class Company(val name: String, val address: Address)
@optics data class Employee(val name: String, val company: Company?)
```

The DSL will be generated in a syntax sub-package of your `data class` and can be used by invoking `setter()` on an instance. i.e. for a package `com.example.domain` the DSL will be generated in `com.example.domain.syntax`.

```kotlin:ank
package com.example.domain

import com.example.domain.syntax.*

val john = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

john.setter().company.address.street.name.modify(String::toUpperCase)
```

Arrow can also generate a dsl for a `sealed class` which can be helpful to reduce boilerplate code, or improve readability.

```kotlin:ank:ap
package com.example.domain

import arrow.optics.optics

@optics sealed class NetworkResult
@optics data class Success(val content: String): NetworkResult()
@optics sealed class NetworkError : NetworkResult()
@optics data class HttpError(val message: String): NetworkResult()
object TimeoutError: NetworkError()
```

Let's imagine we have a function `f` of type `(HttpError) -> HttpError` and we want to invoke it on the `NetworkResult`.

```kotlin:ank
package com.example.domain

val networkResult: NetworkResult = HttpError("boom!")
val f: (String) -> String = String::toUpperCase

when (networkResult) {
  is HttpError -> networkResult.copy(f(networkResult.message))
  else -> networkResult
}
```

We can rewrite this code with our generated dsl.

```kotlin
package com.example.domain

import com.example.domain.syntax.*

val networkResult: NetworkResult = HttpError("boom!")
val f: (String) -> String = String::toUpperCase

networkResult.setter().networkError.httpError.message.modify(f)
```
