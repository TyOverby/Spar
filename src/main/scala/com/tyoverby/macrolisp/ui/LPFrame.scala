package com.tyoverby.macrolisp.ui

import swing._
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.pub.PublicProducer.{parseSourceSlurped, parseRuleSlurped, compile}
import com.tyoverby.macrolisp.pub.{PublicProducer, CompileAll}
import java.io.File
import javax.swing.UIManager

object LPFrame extends SimpleSwingApplication {
  var rules: Option[List[Rule]] = None

  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName)


  def top = new MainFrame {
    title = "hello world"

    def updateCompile() {
      println("compiling " + System.currentTimeMillis)
      if (rules.isDefined) {
        try {
          val parsedSource = parseSourceSlurped(editor.peer.getText, "<ui>")
          val compiled = compile(parsedSource, rules.get)
          output.peer.setText(compiled)
          errors.peer.setText("");
        }
        catch {
          case x: Throwable => errors.peer.setText(x.toString)
        }
      }
      else {
        errors.peer.setText("You must choose a directory for rules.")
      }
    }

    def updateRuleCompile() {
      try {
        val rule = parseRuleSlurped(ruleEdit.peer.getText)
        rules = Some(rule)
        updateCompile()
      }
      catch {
        case e: Throwable => errors.peer.setText(e.toString)
      }
    }

    def openRules(patternText: TextField) {
      val fc = new FileChooser
      fc.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
      fc.peer.setCurrentDirectory(new File("."))
      fc.multiSelectionEnabled = false

      val result = fc.showOpenDialog(this.editor)
      if (result == FileChooser.Result.Approve) {
        val allRules = CompileAll.allFiles(patternText.peer.getText)(fc.selectedFile)
        val compiledRules = PublicProducer.parseRuleFiles(allRules: _*)
        rules = Some(compiledRules)
      }
    }

    def scrollableTextArea(width: Int, height: Int, edit: Boolean): (TextArea, ScrollPane) = {
      val tArea = new TextArea {
        this.editable = edit
        this.font = new swing.Font("Monospaced", 1, 13)
      }

      val container = new ScrollPane {
        contents = tArea
        preferredSize = new Dimension(width, height)
      }
      (tArea, container)
    }

    def split(leftTop: Component, rightBot: Component, vertical: Boolean): SplitPane = {
      val splitPane = new SplitPane()
      splitPane.orientation = if (vertical) Orientation.Vertical else Orientation.Horizontal
      splitPane.topComponent = leftTop
      splitPane.bottomComponent = rightBot

      leftTop.minimumSize = new Dimension(100, 100)

      splitPane.dividerSize = 6
      splitPane.dividerLocation = 0.5

      leftTop match {
        case x: SplitPane => x.dividerLocation = 0.5
        case _ =>
      }

      rightBot match {
        case x: SplitPane => x.dividerLocation = 0.5
        case _ =>
      }



      splitPane
    }

    lazy val (output, outputContainer) = scrollableTextArea(500, 500, edit = false)
    lazy val (editor, editContainer) = scrollableTextArea(500, 500, edit = true)
    lazy val (errors, errorsContainer) = scrollableTextArea(100, 100, edit = false)
    lazy val (ruleEdit, ruleEditContainer) = scrollableTextArea(500, 500, edit = true)

    editor.subscribe {
      case _ => updateCompile(); println("hi")
    }
    ruleEdit.subscribe {
      case _ => updateRuleCompile()
    }

    var editorSplit = split(split(editor, output, vertical = true), errors, vertical = false)
    editorSplit.preferredSize = new Dimension(600, 600)

    lazy val menu = new MenuBar {
      lazy val patternText = new TextField(".rules")
      patternText.maximumSize = new Dimension(300, 25)

      contents += new Menu("File") {
        contents += patternText
        contents += Button("Set Rules Directory") {
          openRules(patternText)
          editorSplit = split(split(editor, output, vertical = true), errors, vertical = false)
          c(editorSplit)
          minimumSize = new Dimension(300, 25)
        }
        contents += Button("User Rules") {
          editorSplit = split(split(split(editor, ruleEdit, vertical = false), output, vertical = true), errors, vertical = false)
          c(editorSplit)
          minimumSize = new Dimension(300, 25)
        }
      }
    }

    def c(c: Component) {
      contents = c
    }

    contents = editorSplit
    menuBar = menu
  }
}
