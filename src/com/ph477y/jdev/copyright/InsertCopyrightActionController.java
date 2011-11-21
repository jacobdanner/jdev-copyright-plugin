package com.ph477y.jdev.copyright;

import oracle.ide.Context;
import oracle.ide.controller.Controller;
import oracle.ide.controller.IdeAction;
import oracle.ide.extension.RegisteredByExtension;

import oracle.ide.model.Element;
import oracle.ide.model.TextNode;
import java.util.ArrayList;

import java.util.List;
import java.util.logging.Logger;

import oracle.ide.Ide;
import oracle.ide.controller.TriggerController;


@RegisteredByExtension("com.ph477y.jdev.copyright")
public final class InsertCopyrightActionController
  implements TriggerController
{
  public static final String INSERT_COPYRIGHT_CMD = "com.ph477y.jdev.InsertCopyrightActionId";
  //public static final IdeAction INSERT_COPYRIGHT_CMD_IA = IdeAction.find(INSERT_COPYRIGHT_CMD);
  public static final int INSERT_COPYRIGHT_CMD_ID = Ide.findCmdID(INSERT_COPYRIGHT_CMD);
  private final Logger LOG = Logger.getLogger(InsertCopyrightActionController.class.getName());

  //private ArrayList<String> XML_FILETYPES = new ArrayList<String>();

  public boolean update(IdeAction action, Context context)
  {
    if (action.getCommandId() == INSERT_COPYRIGHT_CMD_ID)
    {
      // TODO: add a check to see if we have text nodes
      action.setEnabled(context.getSelection() != null);
      return true;
    }
    return false;
  }

  public boolean handleEvent(IdeAction action, Context context)
  {
    if (action.getCommandId() == INSERT_COPYRIGHT_CMD_ID)
    {
      Element[] elements = context.getSelection();

      List<TextNode> nodes = new ArrayList<TextNode>();
      for (Element elem: elements)
      {
        if (elem instanceof TextNode)
        {
          TextNode node = (TextNode) elem;
          nodes.add(node);
        }
      }

      AddCopyrightCommand addCmd = new AddCopyrightCommand(nodes, context);
      try
      {
        addCmd.doit();
      } catch (Exception e)
      {
        e.printStackTrace();
        return false;
      }
      return true;
    }
    return false;
  }

  public Object getInvalidStateMessage(IdeAction ideAction, Context context)
  {
    return null;
  }
}
