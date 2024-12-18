package com.gb.gbhelp;

import android.text.Editable;
import android.text.Html.TagHandler;

import org.xml.sax.XMLReader;

public class CustomTagHandler implements TagHandler{

	@Override
	public void handleTag(boolean opening, String tag, Editable output,
			XMLReader xmlReader) {
		// you may add more tag handler which are not supported by android here
		if("li".equals(tag)){
			if(opening){
				output.append(" \u2022 ");
			}else{
				output.append("\n");
			}
		}
	}
}
