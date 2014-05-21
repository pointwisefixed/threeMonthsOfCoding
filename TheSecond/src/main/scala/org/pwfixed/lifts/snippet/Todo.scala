package org.pwfixed.lifts
package snippet

import net.liftweb._
import http._
import SHtml._
import util._
import Helpers._
import js._
import js.JsCmds._
import com.foursquare.rogue.LiftRogue._
import bootstrap.liftweb.model.TodoDoc
import net.liftweb.http.js.jquery.JqJsCmds.AppendHtml
import org.bson.types.ObjectId
import scala.xml.NodeSeq
import net.liftweb.http.js.JE.{JsRaw, Str}

/**
 * Created by hackgr on 4/29/2014.
 */

object AddTodo {
  def render = {
    var newTodo = ""

    def process(): JsCmd = {
      val itemCount = TodoDoc.count()
      val latestItem = TodoDoc.orderDesc(_.priority).fetch(1).headOption
      val todoItemPriority = if(itemCount > 0) latestItem.get.priority.value + 1 else 1
      val newRecord = TodoDoc.createRecord
      newRecord.priority(todoItemPriority)
      newRecord.description(newTodo)
      newRecord.done(false)
      val nr = newRecord.save
      SetHtml("todo-list", ShowTodo.list)
    }

    "@newTodo" #> text(newTodo, s => newTodo = s) & "type=submit" #> ajaxSubmit("Add", process)
  }
}

object DeleteTodos {
  def render = {

    def deleteItems(itemsToDelete: String) : JsCmd = {
      val idsToDelete = itemsToDelete.split(",").filterNot(_.isEmpty).map(new ObjectId(_))
      TodoDoc.where(_.id in idsToDelete).modify(_.done setTo true).updateMulti()
      SetHtml("todo-list", ShowTodo.list)
    }

    ".icon-delete [onclick]" #> ajaxCall(JsRaw("$('#itemsToDelete').val()"), deleteItems(_))
  }
}

object ShowTodo {


  def render = {
    "#todo-list *" #> ((ns:NodeSeq) => list)
  }

  def list = {
    val todoItems = TodoDoc.orderDesc(_.priority).fetch()
    val result = todoItems.flatMap(x => {
      if (x.done.value) {
        <li class="done">
          <input type="checkbox" id={x.id.toString()} disabled="" checked=""/>
          <label class="toggle" for={x.id.toString()}></label>{x.description}
        </li>
      }
      else{
        <li>
          <input type="checkbox" id={x.id.toString()} />
          <label class="toggle" onclick="toggleCheckbox(this)" for={x.id.toString()}></label>{x.description}
        </li>

      }
    })
    result
  }

}
