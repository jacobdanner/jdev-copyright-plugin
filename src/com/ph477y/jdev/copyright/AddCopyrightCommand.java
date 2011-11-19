package com.ph477y.jdev.copyright;

import oracle.ide.controller.Command;

public class AddCopyrightCommand
  extends Command
{
  public AddCopyrightCommand(int i, int i1, String string)
  {
    super(i, i1, string);
  }

  public AddCopyrightCommand(int i, int i1)
  {
    super(i, i1);
  }

  public AddCopyrightCommand(int i)
  {
    // can we call TemplateInserter?
    super(i);
  }

  @Override
  public int doit()
    throws Exception
  {
    return 0;
  }
  
}
