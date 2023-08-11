package org.dbpedia.ldr.core.metadata.old

import org.dbpedia.ldr.core.metadata.old.ParseResultDoc
import org.springframework.data.mongodb.repository.MongoRepository

trait ParseResultCollection extends MongoRepository[ParseResultDoc, String] {

}
