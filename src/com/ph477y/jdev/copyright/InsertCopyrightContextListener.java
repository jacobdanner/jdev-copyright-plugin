package com.ph477y.jdev.copyright;

import oracle.ide.Context;
import oracle.ide.controller.ContextMenu;
import oracle.ide.controller.ContextMenuListener;
import oracle.ide.controller.IdeAction;
import oracle.ide.model.TextNode;

public class InsertCopyrightContextListener
  implements ContextMenuListener
{
  public InsertCopyrightContextListener()
  {
    super();
  }

  @Override
  public void menuWillShow(ContextMenu contextMenu)
  {
    if(contextMenu.getContext().getNode() instanceof TextNode)
    {
      IdeAction action = IdeAction.find(InsertCopyrightActionController.INSERT_COPYRIGHT_CMD);
      contextMenu.add(contextMenu.createMenuItem(action));
    }
  }

  @Override
  public void menuWillHide(ContextMenu contextMenu)
  {
  }

  @Override
  public boolean handleDefaultAction(Context context)
  {
    return false;
  }
}
