package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.metadata.old.HostDoc
import org.springframework.data.mongodb.repository.MongoRepository

trait HostCollection extends MongoRepository[HostDoc, String]
