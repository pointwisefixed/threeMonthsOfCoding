package bootstrap.liftweb.model

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{BooleanField, IntField, StringField}

/**
 * Created by hackgr on 5/1/2014.
 */
class TodoDoc private() extends MongoRecord[TodoDoc] with ObjectIdPk[TodoDoc]{
  def meta = TodoDoc

  object description extends StringField(this, 100)
  object priority extends IntField(this)
  object done extends BooleanField(this)
}

object TodoDoc extends TodoDoc with MongoMetaRecord[TodoDoc]