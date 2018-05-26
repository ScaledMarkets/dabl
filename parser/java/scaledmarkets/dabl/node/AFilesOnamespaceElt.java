/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class AFilesOnamespaceElt extends POnamespaceElt
{
    private POfilesDeclaration _ofilesDeclaration_;

    public AFilesOnamespaceElt()
    {
        // Constructor
    }

    public AFilesOnamespaceElt(
        @SuppressWarnings("hiding") POfilesDeclaration _ofilesDeclaration_)
    {
        // Constructor
        setOfilesDeclaration(_ofilesDeclaration_);

    }

    @Override
    public Object clone()
    {
        return new AFilesOnamespaceElt(
            cloneNode(this._ofilesDeclaration_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFilesOnamespaceElt(this);
    }

    public POfilesDeclaration getOfilesDeclaration()
    {
        return this._ofilesDeclaration_;
    }

    public void setOfilesDeclaration(POfilesDeclaration node)
    {
        if(this._ofilesDeclaration_ != null)
        {
            this._ofilesDeclaration_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._ofilesDeclaration_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._ofilesDeclaration_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._ofilesDeclaration_ == child)
        {
            this._ofilesDeclaration_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._ofilesDeclaration_ == oldChild)
        {
            setOfilesDeclaration((POfilesDeclaration) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}