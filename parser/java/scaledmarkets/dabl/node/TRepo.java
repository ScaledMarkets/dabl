/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TRepo extends Token
{
    public TRepo()
    {
        super.setText("repo");
    }

    public TRepo(int line, int pos)
    {
        super.setText("repo");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TRepo(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTRepo(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TRepo text.");
    }
}