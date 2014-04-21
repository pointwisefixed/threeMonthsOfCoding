import java.sql.Date
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta.MTable

/**
 * Created by pwfixed on 4/11/2014.
 */
object Todo {

  object Command extends Enumeration{
    type Command = Value
    val Done, New, Update, Show, Quit, NotRecognized = Value
  }


  import Command._

// Fire up the todo list
// A todo list has priorities
// for instance this is a todo list
// #1 Finish X
// #2 Finish 2
// Done #1
// #1 Finish - DONE
// so if you start the program it loads up your todo list
// -- console
// show todo
// #1 finish x
// #2 finish y
// #3 finish z
// ----console -finish job
// Done #1
//-----console - show new todo
// show todo
// #2 finish y
// #3 finish z
//-----console - add
// Pick up dog
// #4 Pick up Dog
//-----console - update
// #4 Fix Lamp
  //

  val dbDriver = Database.forURL("jdbc:h2:~/todo", driver = "org.h2.Driver")

  def main(args:Array[String]) = {
    configureDb()
    var continue = true
    while(continue){
      val command = readLine()
      val cmd = doCommand(command)
      continue = cmd != Quit
      if(cmd == NotRecognized)
        println("Entry was not recognized, please try again")
    }
  }

  class TodoTb(tag: Tag) extends Table[(Int, String, Boolean, Date, Date)](tag, "TODO"){
    def priority = column[Int]("Priority")
    def task = column[String]("Task")
    def done = column[Boolean]("Done")
    def createdDate = column[Date]("CreatedDate")
    def modifiedDate = column[Date]("ModifiedDate")
    def * = (priority, task, done, createdDate, modifiedDate)
  }
  val todos = TableQuery[TodoTb]



  def configureDb() = {
    dbDriver withSession {
     implicit session =>
     val tableList = MTable.getTables.list
     val tableMap = tableList.map{t => (t.name.name, t)}.toMap
     if(!tableMap.contains(todos.baseTableRow.tableName)){
       todos.ddl.create
     }

   }
  }

  def done(priority: Int): Todo.Command.Command = {
    dbDriver withSession{
      implicit session =>
      val curDate = new java.util.Date()
      val task = for {t <- todos if t.priority === priority} yield (t.done, t.modifiedDate)
      task.update((true, new Date(curDate.getTime)))
    }
    println("Task marked as done")
    Command.Done
  }

  def updateOrAdd(priority: Int, taskVal: String): Todo.Command.Command = {
    dbDriver withSession{
      implicit session =>
      val curDate = new java.util.Date()
      val taskCount = (for {t <- todos if t.priority === priority && t.done == false} yield t.*).length.run
      if(taskCount  == 0){
        todos += (priority, taskVal, false, new Date(curDate.getTime), new Date(curDate.getTime))
        println("New Task Created")
        Command.New
      }
      else{
        val task = for {t <- todos if t.priority === priority && t.done == false} yield (t.task, t.modifiedDate)
        task.update((taskVal, new Date(curDate.getTime)))
        println("Task updated")
        Command.Update
      }
    }
  }

  def showTasks: Todo.Command.Command = {
    dbDriver withSession{
      implicit session =>
      val q = for(t <- todos if t.done === false) yield (t.priority, t.task)
      q.foreach{ case (priority, task) =>
        println(s"#$priority $task" )
      }
    }
    Command.Show
  }

  def doCommand(command: String): Command = {
    command match {
      case r"Done\s+#([0-9]+)$num"  => done(num.toInt)
      case r"#([0-9]+)$num\s+(.*)$task" => updateOrAdd(num.toInt, task)
      case r"show\s*" => showTasks
      case r"[Qq]uit\s*" => Quit
      case _ => Command.NotRecognized
    }

  }

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }



}
