package com.ph477y.jdev.copyright;

import oracle.ide.Ide;
import oracle.ide.controller.Command;
import oracle.ide.extension.RegisteredByExtension;

@RegisteredByExtension("com.ph477y.jdev.copyright")
public class InsertCopyrightCommand
  extends Command
{
  private final static String NAME = InsertCopyrightCommand.class.getName();
  
  public InsertCopyrightCommand(int i, int i1, String string)
  {
    super(i, i1, string);
  }

  public InsertCopyrightCommand(int i, int i1)
  {
    super(i, i1);
  }

  public InsertCopyrightCommand(int i)
  {
    super(i);
  }
  
  private static int getCommandId()
  {
    Integer commandID = Ide.findOrCreateCmdID(NAME);
    if(commandID == null)
    {
      throw new IllegalStateException("Action "+NAME+" not found.");
    }
    return commandID;
  }
  

  @Override
  public int doit()
    throws Exception
  {
    return 0;
  }
}
