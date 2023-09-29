package org.dbpedia.ldr.core.errors

// indicate were this is thrown/catched
object AccessibilityIssues {

  // Experiment Issue
  class DomainTooSlow {}

  class ConnectException {}

  class IOException {}

  class RequestTimeout {}

  // Experiment Issue
  class MaxHostFailures {}

  class InvalidRedirectIRI {}

  class RedirectLoop {}

  class MaxRetryAfterTime {}

  class MaxResourceSize {}

  class PrivateIpSkipped {}

  class OtherJavaErrors {}
}
