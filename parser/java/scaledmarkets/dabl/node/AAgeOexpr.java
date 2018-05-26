/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class AAgeOexpr extends POexpr
{
    private POageExpr _oageExpr_;

    public AAgeOexpr()
    {
        // Constructor
    }

    public AAgeOexpr(
        @SuppressWarnings("hiding") POageExpr _oageExpr_)
    {
        // Constructor
        setOageExpr(_oageExpr_);

    }

    @Override
    public Object clone()
    {
        return new AAgeOexpr(
            cloneNode(this._oageExpr_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAgeOexpr(this);
    }

    public POageExpr getOageExpr()
    {
        return this._oageExpr_;
    }

    public void setOageExpr(POageExpr node)
    {
        if(this._oageExpr_ != null)
        {
            this._oageExpr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._oageExpr_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._oageExpr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._oageExpr_ == child)
        {
            this._oageExpr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._oageExpr_ == oldChild)
        {
            setOageExpr((POageExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}