/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TScheme extends Token
{
    public TScheme()
    {
        super.setText("scheme");
    }

    public TScheme(int line, int pos)
    {
        super.setText("scheme");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TScheme(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTScheme(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TScheme text.");
    }
}