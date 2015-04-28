package net.eithon.plugin.docs;

import java.io.File;

import net.eithon.library.chat.Page;
import net.eithon.library.chat.Paginator;
import net.eithon.library.chat.SimpleMarkUp;

class PagedDocument {
	private SimpleMarkUp _parsedFile = null;
	private Page[] _chatPages = null;
	private File _file;
	private int _widthInPixels = 320;

	public PagedDocument(File file, int widthInPixels) {
		this._file = file;
		this._widthInPixels = widthInPixels;
		this._parsedFile = new SimpleMarkUp(this._file);
		reloadRules();
	}

	public int getNumberOfPages(){ return this._chatPages.length; }

	public String[] getPage(int pageNumber){
		if ((pageNumber < 1) || (pageNumber > getNumberOfPages())) return null;
		return this._chatPages[pageNumber-1].getLines();
	}

	public void reloadRules() {
		String[] allLines = this._parsedFile.getParsedLines();
		this._chatPages = Paginator.paginate(allLines, this._widthInPixels);
	}
}
