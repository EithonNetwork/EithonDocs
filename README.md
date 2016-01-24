# EithonDocs

A documentation plugin for Minecraft.

## Functionality

## Release history

### 2.5 (2016-01-24)

* CHANGE: Now uses EithonCommand.

### 2.4 (2015-10-18)

* CHANGE: Refactoring EithonLibrary.

### 2.3.2 (2015-07-24)

* BUG: "/<command> <page> does no longer work (/<command> page <page> does though)

### 2.3.1 (2015-07-19)

* BUG: Null pointer exception, CommandHandler.java:41

### 2.3 (2015-07-19)

* NEW: Accepts the word "page" now between the file name and the page number.

### 2.2.1 (2015-07-13)

* BUG: Did not create the folder txt-files if it didn't exist already
* BUG: Did not read files from the folder txt-files.

### 2.2 (2015-07-08)

* CHANGE: The documents are now stored in the folder "txt-files"
* CHANGE: Now always shows the first page when the command is entered without a page number
* BUG: Now shows subcommands if no subcommand was given.

### 2.1 (2015-04-30)

* NEW: Configurable height
* NEW: Header and Footer is optional and has named parameters.
* NEW: Tab in a line means that line will be centered.

### 2.0 (2015-04-28)

* NEW: Complete rewrite of the mark up parsing and the pagination

### 1.2 (2015-04-27)

* NEW: Added configuration for "page i of n" message to be displayed above or below text.

### 1.1 (2015-04-27)

* NEW: Added the command next which moves to the next page of the current document.
* CHANGE: Now has color Yellow.
* CHANGE: Now goes to page 1 after the last page
* BUG: Now understands non-code brackets.

### 1.0 (2015-04-19)

* NEW: First proper EithonRelease
* NEW: Now by default goes to the next page if the same command is issued again.


