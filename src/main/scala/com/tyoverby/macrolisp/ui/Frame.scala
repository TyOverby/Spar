package com.tyoverby.macrolisp.ui

import swing._
import com.tyoverby.macrolisp.parsers.generator.Rule
import com.tyoverby.macrolisp.pub.PublicProducer.{parseSourceSlurped, compile}
import com.tyoverby.macrolisp.pub.CompileAll

object LPFrame extends SimpleSwingApplication {
  var rules: Option[List[Rule]] = None


  def top = new MainFrame {
    title = "hello world"

    def updateCompile() {
      if (rules.isDefined) {
        try {
          val parsedSource = parseSourceSlurped(editor.peer.getText, "<ui>")
          val compiled = compile(parsedSource, rules.get)
          output.peer.setText(compiled)
        }
        catch {
          case x: Throwable => errors.peer.setText(x.toString)
        }
      }
      else {
        errors.peer.setText("You must choose a directory for rules.")
      }
    }

    lazy val editor = new TextArea {
      this.subscribe {
        case x => updateCompile()
      }
    }
    lazy val output = new TextArea {
      editable = false
    }
    lazy val errors = new TextArea {
      editable = false
    }

    lazy val editContainer = new ScrollPane {
      contents = editor
      preferredSize = new Dimension(500, 500)
    }
    lazy val outputContainer = new ScrollPane {
      contents = output
      preferredSize = new Dimension(500, 500)
    }
    lazy val errorsContainer = new ScrollPane {
      contents = errors
      preferredSize = new Dimension(100, 100)
    }

    lazy val editorSplit = new BorderPanel {
      add(editContainer, BorderPanel.Position.Center)
      add(outputContainer, BorderPanel.Position.East)
      add(errorsContainer, BorderPanel.Position.South)
    }

    lazy val menu = new MenuBar {
      lazy val patternText = new TextField(".rules")

      contents += new Menu("File") {
        contents += patternText
        contents += Button("Set Rules Directory") {
          val fc = new FileChooser
          fc.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly
          fc.multiSelectionEnabled = false

          val result = fc.showOpenDialog(this)
          if (result == FileChooser.Result.Approve){
            val allRules = CompileAll.allFiles(patternText.peer.getText)(fc.selectedFile)

          }
        }
      }
    }


    contents = editorSplit
    menuBar = menu
  }
}
