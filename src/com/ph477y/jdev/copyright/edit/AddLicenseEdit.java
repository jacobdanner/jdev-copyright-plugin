package com.ph477y.jdev.copyright.edit;

import oracle.ide.cmd.buffer.Edit;
import oracle.javatools.buffer.TextBuffer;

public class AddLicenseEdit
  extends Edit
{
  public AddLicenseEdit()
  {
    super();
  }

  @Override
  public void applyEdit(TextBuffer textBuffer)
  {

  }

  @Override
  public boolean isUndoable()
  {
    return false;
  }

  @Override
  public String getUndoName()
  {
    return null;
  }
}
