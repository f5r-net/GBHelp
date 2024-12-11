package com.gb.gbhelp.translate;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import com.gb.gbhelp.GB;

public class TranslateItems implements DialogInterface.OnClickListener{

	private final Context a;
	private final TextView title;
	private final TextView summery;

	public TranslateItems(Context gbActivity, TextView title, TextView summery) {
		this.a = gbActivity;
		this.title = title;
		this.summery = summery;
	}

	@Override
    public void onClick(DialogInterface arg0, int which) {
		switch (which) {
		case 0:
			GB.translate(a, title, summery,"ar");
			break;
		case 1:
			GB.translate(a, title, summery,"en");
			break;
		case 2:
			GB.translate(a, title, summery,"es");
			break;
		case 3:
			GB.translate(a, title, summery,"pt");
			break;
		case 4:
			GB.translate(a, title, summery,"de");
			break;
		case 5:
			GB.translate(a, title, summery,"fr");
			break;
	    case 6:
			GB.translate(a, title, summery,"hi");
		break;
	    case 7:
			GB.translate(a, title, summery,"it");
		break;
		case 8:
		    GB.translate(a, title, summery,"tr");
			break;
		case 9:
			GB.translate(a, title, summery,"fa");
		break;
	}
}
}