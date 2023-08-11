package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.metadata.old.IdResultDoc
import org.springframework.data.mongodb.repository.MongoRepository

trait IdResultCollection extends MongoRepository[IdResultDoc, String]
