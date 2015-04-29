package net.eithon.plugin.docs;

import java.io.File;

import net.eithon.library.chat.Page;
import net.eithon.library.chat.Paginator;
import net.eithon.library.chat.SimpleMarkUp;

class PagedDocument {
	private Page[] _pages = null;
	private File _file;
	private int _widthInPixels = 320;

	public PagedDocument(File file, int widthInPixels) {
		this._file = file;
		this._widthInPixels = widthInPixels;
		reloadRules();
	}

	public int getNumberOfPages(){ return this._pages.length; }

	public String[] getPage(int pageNumber){
		if ((pageNumber < 1) || (pageNumber > getNumberOfPages())) return null;
		return this._pages[pageNumber-1].getLines();
	}

	public void reloadRules() {
		SimpleMarkUp parsedFile = new SimpleMarkUp(this._file);
		String[] allLines = parsedFile.getParsedLines();
		this._pages = Paginator.paginate(allLines, this._widthInPixels);
	}
}
