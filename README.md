Spar 
====

(S-expression PARser)
---------------------

A domain specific language that allows the creation of a lisp compiler to any target language.


This project aims to allow compilation from a markup consisting of s-expressions to any target language or markup 
via intermediate rules.  

The basic data flow operates like this:

    Rules File => |
                  | => compiler => Output program
       Program => |
       
For example, if you don't like the way that XML looks, you could define the following for compilation to XML:

Rules File
----------
The rules file defines the transformations that occur when encountered in the program.
If the first rule doesn't match, it will try the seccond, and so on until it runs out of rules,
in which case it will error.

    # An element
    (:name :children...) => {
        "< "  :name " >"
            :children...
        "</ " :name " >"
    # Raw text
    :text => { :text }
        
Program
-------
    
    (html 
      (head (title my page))
      (body 
        (ul
          (li this is some text)
          (li more text here)
          (li an unordered list))))
                  
Result
------

    <html>
        <head>
            <title>
                my page 
            </title>
        </head>
        <body>
            <ul>
                <li> this is some text </li>
                <li> more text here </li>
                <li> an unordered list </li>
            </ul>
        </body>
    </html>
    
Future
------

As of now, I have the following compilers in beta:
* Lisp -> Javascript
* S-Expression -> Html
* S-Expression -> LaTeX
 
I plan on adding more feature in the near future such as:
* Macros
* Quote and UnQuote for otherwise un-parsable grammers
