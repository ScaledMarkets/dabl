/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TFiles extends Token
{
    public TFiles()
    {
        super.setText("files");
    }

    public TFiles(int line, int pos)
    {
        super.setText("files");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TFiles(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTFiles(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TFiles text.");
    }
}