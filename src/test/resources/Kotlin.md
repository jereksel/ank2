```kotlin
val a = 1
a
```

```kotlin:ank:ap
package com.example.domain

@optics
data class Street(val number: Int, val name: String)
@optics
data class Address(val city: String, val street: Street)
@optics
data class Company(val name: String, val address: Address)
@optics
data class Employee(val name: String, val company: Company?)
```

```kotlin:ank
import com.example.domain.*
import com.example.domain.syntax.*

val employee = Employee("John Doe", Company("Kategory", Address("Functional city", Street(42, "lambda street"))))

employee.setter().company.address.street.name.modify(String::toUpperCase)
```